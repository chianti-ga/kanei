package fr.skitou.kanei.classicCommands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.skitou.botcore.commands.AbstractCommand;
import fr.skitou.botcore.commands.CommandReceivedEvent;
import fr.skitou.kanei.music.GuildMusic;
import fr.skitou.kanei.music.MusicManager;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public class Play extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "play";
    }

    @Override
    public @NotNull String getName() {
        return "play";
    }

    @Override
    public @NotNull String getHelp() {
        return getCommand() + " <music link or id>";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(getHelp()).queue();
            return;
        }

        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getChannel().sendMessage("notinchannel").queue(); //TODO : TEXT
            return;
        }

        GuildMusic guildMusic;

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else
            guildMusic = new GuildMusic(event.getGuild().getAudioManager(), event.getMember().getVoiceState().getChannel(), event.getGuild().getIdLong());

        StringJoiner search = new StringJoiner(" ");

        event.getArgs().stream().forEach(search::add);

        guildMusic.playerManager.loadItemOrdered(guildMusic, "ytsearch:" + search, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusic.scheduler.queueTrack(track);
                event.getChannel().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                guildMusic.scheduler.queueTrack(playlist.getTracks().get(0));
                event.getChannel().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
            }

            @Override
            public void noMatches() {
                event.getChannel().sendMessage("Nothing found for " + search).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }
}
