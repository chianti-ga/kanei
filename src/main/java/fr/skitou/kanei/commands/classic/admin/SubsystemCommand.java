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
import fr.skitou.kanei.commands.classic.CommandReceivedEvent;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.subsystems.ISubsystem;
import fr.skitou.kanei.subsystems.SubsystemAdapter;
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
public class SubsystemCommand extends AbstractCommand {
    public final Logger logger = LoggerFactory.getLogger(SubsystemCommand.class);

    @Override
    public @NotNull String getCommand() {
        return "subsystem";
    }

    @Override
    public @NotNull String getName() {
        return "Subsystem";
    }

    @Override
    public @NotNull String getHelp() {
        return PREFIX + getCommand() + " (list|enable|disable) (subsystem)";
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
            case "list" -> listSubsystemSubCommand(event);
            case "enable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    SubsystemAdapter.getSubsystems().stream()
                            .peek(iSubsystem -> sendMessage(event, "Enabling " + iSubsystem.getName()))
                            .forEach(ISubsystem::enable);
                    return;
                }
                SubsystemAdapter.getSubsystems().stream()
                        .filter(iSubsystem -> iSubsystem.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .peek(iSubsystem -> logger.info("Enabling " + iSubsystem.getName()))
                        .forEach(ISubsystem::enable);
                listSubsystemSubCommand(event);
            }
            case "disable" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage(getHelp()).queue();
                    return;
                }
                if (event.getArgs().get(1).equalsIgnoreCase("all")) {
                    SubsystemAdapter.getSubsystems().stream()
                            .peek(iSubsystem -> logger.info("Disabling " + iSubsystem.getName()))
                            .forEach(ISubsystem::disable);
                    listSubsystemSubCommand(event);
                    return;
                }
                SubsystemAdapter.getSubsystems().stream()
                        .filter(iSubsystem -> iSubsystem.getName().equalsIgnoreCase(event.getArgs().get(1)))
                        .peek(iSubsystem -> sendMessage(event, "Disabling " + iSubsystem.getName()))
                        .forEach(ISubsystem::disable);
                listSubsystemSubCommand(event);
            }
            case "reload" ->
                    sendMessage(event, "It would simply be useless, as we are reusing the same instance anyway.");
            default -> sendHelp(event);
        }
    }

    private void listSubsystemSubCommand(CommandReceivedEvent event) {
        StringBuilder jdaBuilder = new StringBuilder();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Subsystems").setDescription("Subsystem status");
        SubsystemAdapter.getSubsystems().stream().sorted(Comparator.comparing(ISubsystem::getName)).forEach(iSubsystem -> builder.addField((iSubsystem.isEnabled() ? "🟢 " : "🔴 ") + iSubsystem.getName(), iSubsystem.getDescription(), false)
        );
        BotInstance.getJda().getRegisteredListeners().forEach(o -> jdaBuilder.append(o.getClass().getSimpleName()).append("\n"));
        builder.addField("JDA Actual Listeners:", jdaBuilder.toString(), true);

        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
