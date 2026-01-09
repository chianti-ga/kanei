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
import fr.skitou.kanei.hibernate.Database;
import fr.skitou.kanei.hibernate.entities.MembersBlacklist;
import fr.skitou.kanei.utils.IsSenderAllowed;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ManageCommand extends AbstractCommand {

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    @Override
    public @NotNull String getCommand() {
        return "manage";
    }

    @Override
    public @NotNull String getName() {
        return "manage";
    }

    @Override
    public @NotNull String getHelp() {
        return "^manage restrict/unrestrict {user}";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().size() < 2) {
            event.getChannel().sendMessage("Invalid syntax").queue();
            sendHelp(event.getChannel().asTextChannel());
            return;
        }
        switch (event.getArgs().get(0)) {
            case "restrict" -> {
                if (event.getArgs().size() < 2) {
                    event.getChannel().sendMessage("Invalid syntax").queue();
                    sendHelp(event.getChannel().asTextChannel());
                    return;
                }
                if (event.getGuild().getMemberById(event.getArgs().get(1)) == null) {
                    event.getChannel().sendMessage("User doesn't exist").queue();
                    return;
                }

                Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                        .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                        .collect(Collectors.toSet());

                if (membersBlacklists.isEmpty()) {
                    new MembersBlacklist(event.getGuild().getIdLong(), Set.of(Long.valueOf(event.getArgs().get(1))));
                } else {
                    Set<Long> blacklistedMembers = new HashSet<>(membersBlacklists.stream().findFirst().get().getBlacklistedMembers());
                    blacklistedMembers.add(Long.valueOf(event.getArgs().get(1)));
                    new MembersBlacklist(event.getGuild().getIdLong(), blacklistedMembers);

                }

                event.getChannel().sendMessage(event.getGuild().getMemberById(event.getArgs().get(1)).getAsMention() + " has been restricted from using the bot").queue();
            }
            case "unrestrict" -> {

                if (event.getGuild().getMemberById(event.getArgs().get(1)) == null) {
                    event.getChannel().sendMessage("User doesn't exist");
                    return;
                }

                Set<MembersBlacklist> membersBlacklists = Database.getAll(MembersBlacklist.class).stream()
                        .filter(blacklist -> blacklist.getGuild() == event.getGuild().getIdLong())
                        .collect(Collectors.toSet());


                Set<Long> blacklistedMembers = new HashSet<>(membersBlacklists.stream().findFirst().get().getBlacklistedMembers());

                blacklistedMembers.remove(Long.valueOf(event.getArgs().get(1)));

                new MembersBlacklist(event.getGuild().getIdLong(), blacklistedMembers);

                event.getChannel().sendMessage(":white_check_mark:").queue();
            }
            case "list" -> {

            }
        }
    }
}
