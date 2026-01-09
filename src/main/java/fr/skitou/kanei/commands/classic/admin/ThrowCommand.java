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
import fr.skitou.kanei.utils.IsSenderAllowed;
import org.jetbrains.annotations.NotNull;

/**
 * Throw an error
 *
 * @author Uku
 */

@SuppressWarnings("unused") //Automatically discovered via reflection. See CommandAdapter.
public class ThrowCommand extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "throw";
    }

    @Override
    public @NotNull String getName() {
        return "ThrowCommand";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        throw new NullPointerException();
    }

}
