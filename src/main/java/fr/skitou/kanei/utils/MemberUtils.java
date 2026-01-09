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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;


public class MemberUtils {
    private MemberUtils() {
        throw new IllegalStateException("Utility class");
    }

    @NotNull
    public static Member resolveMember(Guild guild, String string) throws NullPointerException {
        string = string.replaceAll("<*@*!*>*", "");
        Member member = guild.retrieveMemberById(string).complete();
        if (member == null) {
            member = guild.retrieveMemberById(string).complete();
        }
        if (member == null) {
            member = guild.retrieveMembersByPrefix(string, 1).get().get(0);
        }
        if (member == null) {
            member = guild.retrieveMemberById(Message.MentionType.USER.getPattern().matcher(string).group()).complete();
        }
        if (member == null) {
            throw new NullPointerException("The member cannot be resolved!");
        }
        return member;
    }
}
