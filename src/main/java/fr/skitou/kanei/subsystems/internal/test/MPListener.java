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

package fr.skitou.kanei.subsystems.internal.test;

import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.subsystems.AbstractSubsystem;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * For debug only
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See SubsystemAdapter.
public class MPListener extends AbstractSubsystem {
    private final Logger logger = LoggerFactory.getLogger(MPListener.class);

    @Override
    public @NotNull String getName() {
        return this.getClass().getSimpleName();
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Transfer all private messages to the logs.";
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (event.getAuthor().getId().equalsIgnoreCase(BotInstance.getJda().getSelfUser().getId()))
                logger.info("Sent dm to " + Objects.requireNonNull(event.getChannel().asPrivateChannel().getUser()).getName() + ": " + event.getMessage().getContentDisplay());
            else
                logger.info("Received dm from " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());
        }
    }
}
