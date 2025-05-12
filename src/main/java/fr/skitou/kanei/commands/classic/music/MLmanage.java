/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.classic.music;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import fr.skitou.botcore.utils.IsSenderAllowed;
import fr.skitou.botcore.utils.QuickColors;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

public class MLmanage extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "mlmanage";
    }

    @Override
    public @NotNull String getName() {
        return "mlmanage";
    }

    @Override
    public @NotNull String getHelp() {
        return "mlmanage (list|destroy) [guildId]";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(this.getHelp()).queue();
        } else {
            switch (event.getArgs().get(0).toLowerCase()) {
                case "list" -> MusicManager.guildMusics.forEach((aLong, guildMusic) -> {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("guildMusics ConcurrentHashMap status")
                            .addField("guildId", Objects.requireNonNullElse(event.getJDA().getGuildById(aLong).getName(), "null"), false)
                            .addField("playingTrack", Objects.requireNonNullElse(guildMusic.scheduler.nowPlaying().getTitle(), "null"), false)
                            .addField("queue", String.valueOf(guildMusic.scheduler.getQueue().size()), false)
                            .addField("volume", String.valueOf(guildMusic.player.getVolume()), false)
                            .setColor(QuickColors.DARK_YELLOW);
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                });
                case "destroy" -> {
                    if (event.getArgs().size() < 2) {
                        event.getChannel().sendMessage(this.getHelp()).queue();
                    } else {
                        MusicManager.guildMusics.get(Long.valueOf(event.getArgs().get(1))).destroy();
                        event.getChannel().sendMessage(":thumbsup:").queue();
                    }
                }
                default -> event.getChannel().sendMessage(this.getHelp()).queue();
            }
        }
    }

    @Override
    public Predicate<Member> isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }
}
