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

package fr.skitou.kanei.commands.classic.other;

import fr.skitou.kanei.commands.classic.AbstractCommand;
import fr.skitou.kanei.commands.classic.CommandAdapter;
import fr.skitou.kanei.commands.classic.CommandReceivedEvent;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.utils.IsSenderAllowed;
import fr.skitou.kanei.utils.NotWorking;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
@NotWorking
public class HelpCommand extends AbstractCommand {
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().size() == 1) {
            BotInstance.getEventListeners().stream().filter(CommandAdapter.class::isInstance)
                    .forEach(eventListener -> ((CommandAdapter) eventListener).getCommands()
                            .stream().filter(iCommand -> iCommand.getClass().getSimpleName().equalsIgnoreCase(event.getArgs().get(0)) || iCommand.getCommand().equalsIgnoreCase(event.getArgs().get(0)))
                            .findFirst().ifPresentOrElse(
                                    iCommand -> event.getChannel().sendMessage(iCommand.getHelp()).queue(),
                                    () -> event.getChannel().sendMessage("Error: Command not found").queue()));
            return;
        }
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Commands");
        Map<String, List<String>> fields = new HashMap<>();
        BotInstance.getEventListeners().stream().filter(CommandAdapter.class::isInstance)
                .forEach(eventListener -> ((CommandAdapter) eventListener).getCommands()
                        .forEach(command -> {
                            if (command.isSenderAllowed().test(event.getMember())) {
                                String[] pkg = command.getClass().getPackage().getName().split("\\.");
                                String lastPkg = pkg[pkg.length - 1];
                                if (!fields.containsKey(lastPkg))
                                    fields.put(lastPkg, new ArrayList<>());
                                String commandAndHelp = command.getClass().getSimpleName() +
                                        ": " +
                                        command.getHelp() +
                                        "\n";
                                fields.get(lastPkg).add(commandAndHelp);
                            }
                        }));
        fields.forEach((commandType, commands) -> {
            StringBuilder sb = new StringBuilder();
            commands.forEach(sb::append);
            builder.addField(commandType, sb.toString(), false);
        });
        event.getChannel().sendMessageEmbeds(builder.build()).queue();
    }

    @Override
    public @NotNull String getCommand() {
        return "help";
    }

    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public @NotNull String getHelp() {
        return "^help *[command]*";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.Default;
    }
}
