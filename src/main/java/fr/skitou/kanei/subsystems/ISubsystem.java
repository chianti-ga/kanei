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

package fr.skitou.kanei.subsystems;

import fr.skitou.kanei.commands.classic.ICommand;
import fr.skitou.kanei.utils.Children;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * An interface representing a subsystem in a modular application architecture.
 * Subsystems are components that can be enabled or disabled independently to control their behavior.
 * This interface extends {@link EventListener} to allow handling of events related to the subsystem.
 */
@Children(targetPackages = "fr.skitou.kanei.subsystems.internal")
public interface ISubsystem extends EventListener {

    /**
     * Gets the name of the subsystem.
     *
     * @return The name of the subsystem as a {@link String}.
     */
    @NotNull
    String getName();

    /**
     * Gets the description of the subsystem.
     *
     * @return The description of the subsystem as a {@link String}.
     */
    @NotNull
    String getDescription();

    /**
     * Gets the list of commands declared by this subsystem.
     * By default, it returns an empty list
     *
     * @return A list of {@link ICommand} associated with this subsystem.
     */
    @NotNull
    default List<ICommand> getDeclaredCommands() {
        return Collections.emptyList();
    }

    /**
     * Enables this subsystem.
     * This is a convenient wrapper for {@link #setEnabled(boolean) setEnabled(true)}.
     *
     * @see #setEnabled(boolean)
     */
    default void enable() {
        setEnabled(true);
    }

    /**
     * Disables this subsystem.
     * This is a convenient wrapper for {@link #setEnabled(boolean) setEnabled(false)}.
     *
     * @see #setEnabled(boolean)
     */
    default void disable() {
        setEnabled(false);
    }

    /**
     * Checks whether the subsystem is currently enabled.
     *
     * @return {@code true} if the subsystem is enabled, {@code false} otherwise.
     */
    boolean isEnabled();

    /**
     * Enables or disables this subsystem. This operation is idempotent.
     * The {@link #enable()} and {@link #disable()} methods internally use this method.
     *
     * @param enabled The state to which to set this subsystem.
     */
    void setEnabled(boolean enabled);
}

