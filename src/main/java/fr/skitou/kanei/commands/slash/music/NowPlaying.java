/*
 * Copyright (c) Chianti Gally 2024 - 2026.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.kanei.commands.slash.ISlashCommand;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.lava.MusicManager;
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
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null && event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }


        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            if (MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.getPlayingTrack() != null) {
                event.getHook().sendMessageEmbeds(MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.nowPlaying()).queue();
            } else
                event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
        } else
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
    }
}
