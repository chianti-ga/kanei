/*
 * Copyright (C) 2021-2024 Ruben Rouvière, Chianti Gally, uku3lig, Rayan Malloul, and Maxandre Rochefort.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package fr.skitou.kanei.commands.classic;

import fr.skitou.kanei.commands.slash.ISlashCommand;
import fr.skitou.kanei.core.Config;
import fr.skitou.kanei.hibernate.Database;
import fr.skitou.kanei.hibernate.entities.MembersBlacklist;
import fr.skitou.kanei.utils.IsSenderAllowed;
import fr.skitou.kanei.utils.ReflectionUtils;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandAdapter extends ListenerAdapter {
    private static CommandAdapter instance;
    private final Logger logger = LoggerFactory.getLogger(CommandAdapter.class);
    @Getter
    private final HashSet<ISlashCommand> slashcommands = new HashSet<>();
    @Getter
    private HashSet<fr.skitou.kanei.commands.classic.ICommand> commands = new HashSet<>();

    /**
     * Automatically add all detected commands as commands.
     */
    public CommandAdapter() {
        StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Detected commands: ");
        commands.addAll(ReflectionUtils.getSubTypesInstance(ICommand.class));
        commands.forEach(command -> infoBuilder.append("\n").append(command.getName()));
        infoBuilder.append("Total: ").append(commands.size());
        logger.info(infoBuilder.toString());

        slashcommands.addAll(ReflectionUtils.getSubTypesInstance(ISlashCommand.class, "fr.skitou.kanei.commands.slash"));

        infoBuilder.setLength(0);
        infoBuilder.append("Detected Slash commands: ");
        infoBuilder.append("Total: ").append(slashcommands.size());
        logger.info(infoBuilder.toString());
    }

    /**
     * Create an instance of CommandAdapter without automatically populating it.
     *
     * @param listeners The commands to be registered.
     */
    public CommandAdapter(fr.skitou.kanei.commands.classic.ICommand... listeners) {
        this.commands = new HashSet<>();
        this.commands.addAll(Set.of(listeners));
    }

    public static CommandAdapter getInstance() {
        if (instance == null) instance = new CommandAdapter();
        return instance;
    }

    /**
     * Detect commands from {@link MessageReceivedEvent messages}, and create {@link MessageReceivedEvent} if needed.
     *
     * @param event A raw {@link MessageReceivedEvent}.
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        //PERF: PLEASE CONSIDER THE ORDER OF EXECUTION OF THOSE CHECKS AS TO RETURN AS QUICKLY AS POSSIBLE FOR THE MOST COMMON CASES.

        // Since CommandReceivedEvent is a subtype of GuildMessageReceivedEvent,
        // we unfortunately receive our own generated event.
        // This is bad and WILL create an infinite loop if we don't catch it.
        if (event instanceof fr.skitou.kanei.commands.classic.CommandReceivedEvent) {
            return;
        }

        // Only consider messages starting with the PREFIX.
        if (!(event.getMessage().getContentDisplay().startsWith(fr.skitou.kanei.commands.classic.ICommand.PREFIX)))
            return;

        // Avoid self-loops
        if (event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) return;

        // Avoid bots loops.
        if (event.getAuthor().isBot()
                && Objects.equals(Config.CONFIG.getProperty("commands.allowNonUser").orElse("false"), "true")) {
            return;
        }

        logger.debug("Received command.");
        onCommandReceivedEvent(new fr.skitou.kanei.commands.classic.CommandReceivedEvent(event)); //Generate our custom event.
    }

    public void onCommandReceivedEvent(fr.skitou.kanei.commands.classic.CommandReceivedEvent event) {
        //PERF: Can be replaced with a for loop by IntelliJ IDEA if needed.
        //Please avoid to do so unless we really need it to preserve readability.
        commands.stream()
                .filter(fr.skitou.kanei.commands.classic.ICommand::isEnabled)
                .filter(command -> doesCommandMatchString(command, event.getCommand()))
                .filter(matchedCommand ->
                        matchedCommand.isSenderAllowed().test(event.getMember()) ||
                                IsSenderAllowed.BotAdmin.test(event.getMember())) //Hardcoded bypass for Bot admins.
                .limit(1) // Limit to one matched commands
                .forEach(command -> dispatchEvent(command, event));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();
        slashcommands.stream()
                .filter(iSlashCommand -> iSlashCommand.getName().equalsIgnoreCase(event.getName()))
                .limit(1) // Limit to one matched commands
                .forEach(iSlashCommand -> {
                    Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                            .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                            .collect(Collectors.toSet());

                    if (!membersBlacklists.isEmpty()) {
                        boolean isBlacklisted = membersBlacklists.stream().findFirst().get().getBlacklistedMembers().contains(event.getMember().getIdLong());

                        if (isBlacklisted) {
                            event.getHook().editOriginal("You are blacklisted from using the bot on this server!").queue();
                            return;
                        }
                    }

                    try {
                        iSlashCommand.onCommandReceived(event);
                    } catch (Exception exception) {
                        event.getChannel().sendMessage("Command failed!\n`The error has been reported!`").queue();

                        logger.error("Command {} threw a {}: {}", iSlashCommand.getName(),
                                exception.getClass().getSimpleName(), exception.getMessage());

                        exception.printStackTrace();

                        Sentry.captureException(exception);
                    }
                });
    }

    private boolean doesCommandMatchString(fr.skitou.kanei.commands.classic.ICommand commandToTest, String stringToTest) {
        try {
            boolean matchesCommand = commandToTest.getCommand().equalsIgnoreCase(stringToTest);
            if (!matchesCommand)
                return commandToTest.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(stringToTest));
            else return true;
        } catch (NullPointerException npe) {
            logger.error("Error: " + commandToTest.getClass() + " #getCommand or #getAliases returns null ! THIS IS A CONTRACT VIOLATION!!!");
            return false;
        }
    }

    public void dispatchEvent(ICommand command, CommandReceivedEvent event) {
        try {
            command.onCommandReceived(event);
            logger.info(event.getAuthor().getName() + "(" + event.getAuthor().getId() + ")" + " issued the " + event.getCommand() + " command.");
        } catch (Exception exception) {
            event.getChannel().sendMessage("Command failed!\n`The error have been reported!`").queue();

            logger.error("Command {} threw a {}: {}", command.getCommand(),
                    exception.getClass().getSimpleName(), exception.getMessage());

            exception.printStackTrace();

            Sentry.captureException(exception);
        }
    }
}
