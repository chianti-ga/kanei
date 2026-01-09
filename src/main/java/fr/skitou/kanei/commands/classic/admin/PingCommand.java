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
import fr.skitou.kanei.utils.QuickColors;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Simple ping command
 *
 * @author Skitou
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
public class PingCommand extends AbstractCommand {
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        long startTime = System.nanoTime();

        EmbedBuilder builder = new EmbedBuilder()
                .addField("WS:", event.getJDA().getGatewayPing() + "ms", false)
                .setColor(QuickColors.MOUNAK_HIGHLIGHT_DARKER);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        event.getChannel().sendMessageEmbeds(builder.addField("Typing:", duration + "ms", false).build()).queue();

    }

    @Override
    public @NotNull String getCommand() {
        return "ping";
    }

    @Override
    public @NotNull String getName() {
        return "ping";
    }
}
