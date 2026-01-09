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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract implementation of {@link fr.skitou.kanei.commands.classic.ICommand}, providing features such as:
 * <ul>
 *     <li>A default implementation of {@link fr.skitou.kanei.commands.classic.ICommand#enable()}, {@link fr.skitou.kanei.commands.classic.ICommand#disable()}, and {@link fr.skitou.kanei.commands.classic.ICommand#isEnabled()}, backed by a boolean.</li>
 *     <li>Convenience methods for the common scenario of sending a message to the channel in which the command was received.</li>
 *     <li>Convenience methods for sending the command's {@linkplain fr.skitou.kanei.commands.classic.ICommand#getHelp() help} in a {@link #sendHelp(TextChannel) TextChannel}, or {@link #sendHelp(fr.skitou.kanei.commands.classic.CommandReceivedEvent) directly to the channel in which the command was received.}</li>
 * </ul>
 *
 * @author U_Bren
 * @see fr.skitou.kanei.commands.classic.ICommand
 */
public abstract class AbstractCommand implements ICommand {
    /**
     * The boolean that defines the state of a command.
     * <br>
     * True by default.
     *
     * @see #isEnabled()
     */
    private boolean isEnabled = true;

    @Override
    public final boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * A convenience method to send a message in the form of a {@link String} to a {@link TextChannel}.
     */
    public void sendMessage(@NotNull TextChannel textChannel, @NotNull String message) {
        textChannel.sendMessage(message).queue();
    }

    /**
     * A convenience method to send a message in the form of a {@link String} to the {@link TextChannel} in which the event was received.
     * <br/>
     * This method is a wrapper around {@link #sendMessage(TextChannel, String)} using {@link fr.skitou.kanei.commands.classic.CommandReceivedEvent#getChannel() event.getChannel()} as the {@link TextChannel}.
     *
     * @param event The {@link fr.skitou.kanei.commands.classic.CommandReceivedEvent} from which to get the {@link TextChannel} where the message will be sent.
     * @author U_Bren
     */
    public void sendMessage(@NotNull fr.skitou.kanei.commands.classic.CommandReceivedEvent event, @NotNull String message) {
        sendMessage(event.getChannel().asTextChannel(), message);
    }

    /**
     * Sends this command's {@link #getHelp()} formatted as an {@link net.dv8tion.jda.api.entities.MessageEmbed embed} to the provided {@link TextChannel}.
     *
     * @param textChannel The {@link TextChannel}
     */
    public void sendHelp(TextChannel textChannel) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Error: wrong syntax")
                .setDescription(getHelp())
                .setFooter("If this is a bug, please contact us.");
        textChannel.sendMessageEmbeds(builder.build()).queue();
    }

    /**
     * A convenience method to send this command's {@link #getHelp()} formatted as an {@link net.dv8tion.jda.api.entities.MessageEmbed embed} to the {@link TextChannel} in which the event was received.
     * <br/>
     * This method is a wrapper around {@link #sendHelp(TextChannel)} using {@link fr.skitou.kanei.commands.classic.CommandReceivedEvent#getChannel() event.getChannel()} as the {@link TextChannel}.
     *
     * @param event The {@link fr.skitou.kanei.commands.classic.CommandReceivedEvent} from which to get the {@link TextChannel} where the message will be sent.
     * @author U_Bren
     */
    public void sendHelp(CommandReceivedEvent event) {
        sendHelp(event.getChannel().asTextChannel());
    }
}
