/*
 * Copyright (c) Skitou 2024.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NowPlaying implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "nowplaying";
    }

    @Override
    public @NotNull String getHelp() {
        return "Show current track";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null) {
            if (event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
                event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
                return;
            }
        }

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            if (MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.getPlayingTrack() != null) {
                event.getHook().sendMessageEmbeds(MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.nowPlaying()).queue();
            } else event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
        } else event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
    }
}
