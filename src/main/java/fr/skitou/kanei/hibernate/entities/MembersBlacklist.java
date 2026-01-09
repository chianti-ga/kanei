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

package fr.skitou.kanei.hibernate.entities;

import fr.skitou.kanei.hibernate.Database;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@Entity
@Table(name = "membersBlacklist")
public class MembersBlacklist {
    @Id
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private long guild;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> blacklistedMembers;

    public MembersBlacklist(long guild, Set<Long> blacklistedMembers) {
        this.guild = guild;
        this.blacklistedMembers = blacklistedMembers;

        Database.saveOrUpdate(this);
    }

    protected MembersBlacklist() {
    }
}
