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

package fr.skitou.kanei.commands.slash;

import fr.skitou.kanei.commands.classic.AbstractCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Define common methods for Slash Commands that are called from Discord.
 * <br/>
 * For most purposes, {@link AbstractCommand} should be used instead.
 *
 * @author Skitou
 * @see AbstractSlashCommand
 */
public interface ISlashCommand {

    /**
     * @return The name of the command.
     */
    @NotNull
    String getName();

    /**
     * @return The displayed help of the command. <br>
     * By default returns "description".
     */
    @NotNull
    default String getHelp() {
        return "No description";
    }

    void onCommandReceived(SlashCommandInteractionEvent event);

    /**
     * see <a href="https://jda.wiki/using-jda/interactions/">Discord JDA</a>
     *
     * @return Return {@link Set} containing all {@link OptionData}.
     */
    default Set<OptionData> getOptionData() {
        return Set.of();
    }

    /**
     * @return Return {@link Set} containing all {@link SubcommandData}.
     */
    @NotNull
    default Set<SubcommandData> getSubcommandDatas() {
        return Set.of();
    }
}
