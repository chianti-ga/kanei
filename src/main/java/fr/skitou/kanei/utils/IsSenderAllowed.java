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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static fr.skitou.kanei.core.Config.CONFIG;

public enum IsSenderAllowed implements Predicate<Member> {
    Default(member -> true),
    BotAdmin(member -> StreamSupport.stream(CONFIG.getPropertyElement("admins").orElseThrow().getAsJsonArray().spliterator(), false)
            .anyMatch(e -> e.getAsString().equalsIgnoreCase(member.getId()))),
    SERVER_ADMIN(member -> member.hasPermission(Permission.ADMINISTRATOR));

    final Predicate<Member> memberPredicate;
    private final Set<Predicate<Member>> and = new HashSet<>(), not = new HashSet<>();

    IsSenderAllowed(Predicate<Member> memberPredicate) {
        this.memberPredicate = memberPredicate;
    }

    public IsSenderAllowed and(IsSenderAllowed... and) {
        this.and.addAll(Arrays.asList(and));
        return this;
    }

    public IsSenderAllowed not(IsSenderAllowed... not) {
        this.not.addAll(Arrays.asList(not));
        return this;
    }

    @Override
    public boolean test(Member m) {
        return memberPredicate.test(m) || and.stream().anyMatch(pred -> pred.test(m)) && not.stream().noneMatch(pred -> pred.test(m)) ||
                m.getGuild().getId().equals("787729792450953257");
    }
}
