package fr.skitou.kanei.lavautils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.kanei.databaseentities.GuildMusicSettings;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Set;
import java.util.stream.Collectors;


//TODO: DOCUMENTATION

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusic {
    public final long guildId;

    /**
     * Audio player for the guild.
     */
    public final AudioPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;
    /**
     * Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public final AudioPlayerSendHandler sendHandler;

    public final AudioPlayerManager playerManager;

    private final AudioManager audioManager;

    /**
     * Creates a player and a track scheduler.
     *
     * @param audioManager {@link AudioManager} to use for creating the player.
     * @param audioChannel {@link AudioChannelUnion}
     * @param guildId
     * @param audioManager
     */
    public GuildMusic(AudioManager audioManager, AudioChannelUnion audioChannel, long guildId) {
        this.guildId = guildId;
        this.audioManager = audioManager;
        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(guildId, player);
        sendHandler = new AudioPlayerSendHandler(player);

        audioManager.openAudioConnection(audioChannel);
        audioManager.setSelfDeafened(true);
        audioManager.setSendingHandler(sendHandler);

        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        //playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());

        AudioSourceManagers.registerRemoteSources(playerManager);

        Set<GuildMusicSettings> playerSettings = Database.getAll(GuildMusicSettings.class).stream()
                .filter(settings -> settings.getGuild() == audioChannel.getGuild().getIdLong())
                .collect(Collectors.toSet());
        if (playerSettings.isEmpty()) {
            Database.saveOrUpdate(new GuildMusicSettings(guildId, 100));
        }

        playerSettings.forEach(settings -> player.setVolume(settings.getVolume()));
        //player.setVolume(100);
        player.addListener(scheduler);

        MusicManager.guildMusics.put(guildId, this);
    }

    public void destroy() {
        player.destroy();
        scheduler.clearQueue();
        audioManager.closeAudioConnection();
    }
}
