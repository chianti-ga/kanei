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


import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.core.Config;
import fr.skitou.kanei.utils.Children;
import fr.skitou.kanei.utils.IsSenderAllowed;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Define common methods for commands that are called from Discord.
 * <br/>
 * For most purposes, {@link AbstractCommand} should be used instead.
 *
 * @author U_Bren
 * @see AbstractCommand
 */
@Children(targetPackages = {
        "fr.skitou.kanei.commands.classic",
        "fr.skitou.kanei.subsystems"
})
public interface ICommand {
    String PREFIX = BotInstance.isTestMode() ? "?" : Config.CONFIG.getPropertyOrDefault("bot.prefix");

    /**
     * @return The command, without its prefix.
     */
    @NotNull
    String getCommand();

    /**
     * @return The name of the command.
     */
    @NotNull
    String getName();

    /**
     * @return The displayed help of the command. <br>
     * By default returns {@link ICommand#PREFIX} + {@link ICommand#getCommand()}.
     */
    @NotNull
    default String getHelp() {
        return PREFIX + getCommand();
    }

    /**
     * @return If the command is enabled, true.
     */
    boolean isEnabled();

    /**
     * Sets the state for the command. <br>
     * <code>true</code> to enable and <code>false</code> to disable.
     *
     * @param isEnabled State of the command.
     */
    void setEnabled(boolean isEnabled);

    /**
     * Enables the command. Uses {@link ICommand#setEnabled(boolean) setEnabled(true)}
     */
    default void enable() {
        setEnabled(true);
    }

    /**
     * Disables the command. Uses {@link ICommand#setEnabled(boolean) setEnabled(false)}
     */
    default void disable() {
        setEnabled(false);
    }

    /**
     * The entry point of a command. Is executed when the command is detected by {@link CommandAdapter}.
     *
     * @param event An {@link CommandReceivedEvent event} extending {@link net.dv8tion.jda.api.events.message.MessageReceivedEvent}.
     */
    void onCommandReceived(CommandReceivedEvent event);

    /**
     * A list of other strings which are valid for triggering this command
     */
    @NotNull
    default List<String> getAliases() {
        return Collections.emptyList();
    }

    /**
     * A {@link Predicate} returning {@literal true} if the {@link Member} calling this command is authorised to do so.
     * <br/>
     * If {@literal false}, {@link #onCommandReceived(CommandReceivedEvent)} will not be called.
     *
     * @return {@literal true} if the {@link Member} calling this command is authorised to do so, {@literal false} otherwise.
     */
    default Predicate<Member> isSenderAllowed() {
        return IsSenderAllowed.Default;
    }
}