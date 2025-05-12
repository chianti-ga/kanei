/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.lava.GuildMusic;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class Pause implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "pause";
    }

    @Override
    public @NotNull String getHelp() {
        return "Pause/resume the player.";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null) {
            if (event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
                event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
                return;
            }
        }
        GuildMusic guildMusic;

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
            return;
        }

        if (guildMusic.player.getPlayingTrack() == null) {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
            return;
        }

        if (guildMusic.player.isPaused()) {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.resumed")).queue();
            guildMusic.player.setPaused(false);
        } else {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.paused")).queue();
            guildMusic.player.setPaused(true);
        }
    }
}
