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

package fr.skitou.kanei.utils;

import fr.skitou.kanei.core.BotInstance;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SubCommandListener extends ListenerAdapter {
    private final User user;
    private final MessageChannel channel;
    private final Consumer<Message> action;

    public SubCommandListener(User user, MessageChannel channel, Consumer<Message> action) {
        this.user = user;
        this.channel = channel;
        this.action = action;

        BotInstance.getJda().addEventListener(this);
    }

    public SubCommandListener() {
        this.user = null;
        this.channel = null;
        this.action = null;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(channel.getId())) return;
        if (!event.getAuthor().getId().equals(user.getId())) return;
        action.accept(event.getMessage());
        BotInstance.getJda().removeEventListener(this);

    }
}
