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

package fr.skitou.kanei.commands.classic.admin;

import fr.skitou.kanei.commands.classic.AbstractCommand;
import fr.skitou.kanei.commands.classic.CommandAdapter;
import fr.skitou.kanei.commands.classic.CommandReceivedEvent;
import fr.skitou.kanei.commands.classic.ICommand;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.utils.IsSenderAllowed;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * Command to manage others commands
 *
 * @author Unknown, reworked by Skitou
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
public class CommandManager extends AbstractCommand {
    public final Logger logger = LoggerFactory.getLogger(CommandManager.class);

    @Override
    public @NotNull String getCommand() {
        return "command";
    }

    @Override
    public @NotNull String getName() {
        return "CommandManager";
    }

    @Override
    public @NotNull String getHelp() {
        return ICommand.PREFIX + "command (list|enable|disable) (command|all)";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(getHelp()).queue();
            return;
        }

        switch (event.getArgs().get(0).toLowerCase()) {
            case "list" -> listCommand(event);
            case "enable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    CommandAdapter.getInstance().getCommands().stream()
                            .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                            .peek(e -> logger.info("Enabling " + e.getName()))
                            .forEach(ICommand::enable);
                    listCommand(event);
                    return;
                }
                CommandAdapter.getInstance().getCommands().stream()
                        .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                        .filter(e -> e.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .peek(e -> logger.info("Enabling " + e.getName()))
                        .forEach(ICommand::enable);
                listCommand(event);
            }
            case "disable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    CommandAdapter.getInstance().getCommands().stream()
                            .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                            .forEach(iCommand -> {
                                iCommand.disable();
                                logger.info("Disabling " + iCommand.getName());
                            });
                    listCommand(event);
                    return;
                }
                CommandAdapter.getInstance().getCommands().stream()
                        .filter(iCommand -> !iCommand.getName().equalsIgnoreCase(getName()))
                        .filter(e -> e.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .forEach(iCommand -> {
                            iCommand.disable();
                            logger.info("Disabling " + iCommand.getName());
                        });
                listCommand(event);
            }
            case "refresh" -> BotInstance.updateGuildCommands();
            default -> sendHelp(event);
        }
    }

    private void listCommand(CommandReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Subsystems").setDescription("subsystem status");
        CommandAdapter.getInstance().getCommands().stream().sorted(Comparator.comparing(ICommand::getName)).forEach(iCommand ->
                builder.addField((iCommand.isEnabled() ? "🟢 " : "🔴 ") + iCommand.getName(), iCommand.getHelp(), false)
        );
        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
