/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.kanei.commands.slash.ISlashCommand;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.utils.lava.GuildMusic;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class Skip implements ISlashCommand {

    @Override
    public @NotNull String getName() {
        return "skip";
    }

    @Override
    public @NotNull String getHelp() {
        return "skip";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null && event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }


        GuildMusic guildMusic;

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
            return;
        }

        if (event.getOption("track_number") != null) {
            int trackNumber = Objects.requireNonNull(event.getOption("track_number")).getAsInt();

            if (guildMusic.scheduler.getQueue().isEmpty() || trackNumber <= 0 || trackNumber > guildMusic.scheduler.getQueue().size()) {
                event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.invalidskip")).queue();
            } else {
                if ((trackNumber - 1) <= 0) {
                    event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.invalidskip")).queue();
                    return;
                }
                guildMusic.scheduler.skiptoIndex(trackNumber - 1);
                guildMusic.scheduler.nextTrack();
                event.getHook().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
            }
        } else {
            guildMusic.scheduler.nextTrack();
            if (guildMusic.player.getPlayingTrack() != null) {
                event.getHook().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
            } else {
                event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.emptyqueue")).queue();
            }
        }
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.INTEGER, "track_number", getHelp(), false));
    }
}
