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

package fr.skitou.kanei.subsystems.internal;

import io.sentry.Sentry;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom listener that extends {@link ListenerAdapter} to handle exceptions thrown by JDA (Java Discord API) events.
 * When an exception occurs, it logs the error and captures the exception using Sentry for error tracking.
 */
public class ErrorListener extends ListenerAdapter {

    /**
     * The logger instance used for logging error messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(ErrorListener.class);

    /**
     * This method is called when JDA encounters an uncaught exception in any event.
     * It logs the error with relevant information and captures the exception using Sentry for error tracking.
     *
     * @param event The {@link ExceptionEvent} representing the exception thrown by JDA.
     */
    @Override
    public void onException(@NotNull ExceptionEvent event) {
        logger.error("JDA threw a {}: {}", event.getCause().getClass().getSimpleName(), event.getCause().getMessage());
        Sentry.captureException(event.getCause());
    }
}

