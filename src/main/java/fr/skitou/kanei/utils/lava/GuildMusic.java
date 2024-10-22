/*
 * Copyright (c) Skitou 2024.
 */

package fr.skitou.kanei.utils.lava;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import fr.skitou.botcore.core.Config;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.kanei.databaseentities.GuildMusicSettings;
import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusic {
    @Getter
    public static final AudioPlayerManager playerManager = initPlayerManager();
    private static final float[] BASS_BOOST = {
            0.2f,
            0.15f,
            0.1f,
            0.05f,
            0.0f,
            -0.05f,
            -0.1f,
            -0.1f,
            -0.1f,
            -0.1f,
            -0.1f,
            -0.1f,
            -0.1f,
            -0.1f,
            -0.1f
    };
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
    private final AudioManager audioManager;

    private final EqualizerFactory equalizer = new EqualizerFactory();

    /**
     * Creates a player and a track scheduler.
     *
     * @param audioManager {@link AudioManager} to use for creating the player.
     * @param audioChannel {@link AudioChannelUnion}
     * @param guildId      id of the guild
     */
    public GuildMusic(AudioManager audioManager, AudioChannelUnion audioChannel, long guildId) {
        this.guildId = guildId;
        this.audioManager = audioManager;

        Set<GuildMusicSettings> playerSettings = Database.getAll(GuildMusicSettings.class).stream()
                .filter(settings -> settings.getGuild() == audioChannel.getGuild().getIdLong())
                .collect(Collectors.toSet());

        if (playerSettings.isEmpty()) {
            playerSettings.add(new GuildMusicSettings(guildId, 100, "en"));
        }

        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(guildId, player);
        sendHandler = new AudioPlayerSendHandler(player);
        playerSettings.forEach(settings -> player.setVolume(settings.getVolume()));
        player.addListener(scheduler);
        player.setFilterFactory(equalizer);

        audioManager.openAudioConnection(audioChannel);
        audioManager.setSelfDeafened(true);
        audioManager.setSendingHandler(sendHandler);


        MusicManager.guildMusics.put(guildId, this);
    }

    /**
     * Initializes and configures the AudioPlayerManager with audio source managers. THIS PREVENT DUPLICATION OF INSTANCES
     *
     * @return An instance of AudioPlayerManager configured
     */
    public static AudioPlayerManager initPlayerManager() {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        //new YouTube source manager
        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true, new WebWithThumbnail(), new WebEmbeddedWithThumbnail(), new AndroidTestsuiteWithThumbnail(), new MusicWithThumbnail()));

        playerManager.registerSourceManager(new SpotifySourceManager(null, Config.CONFIG.getPropertyOrDefault("spotify.id"), Config.CONFIG.getPropertyOrDefault("spotify.secret"), "FR", playerManager));
        playerManager.getConfiguration().setOpusEncodingQuality(AudioConfiguration.OPUS_QUALITY_MAX);
        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
        playerManager.getConfiguration().setFilterHotSwapEnabled(true);
        AudioSourceManagers.registerRemoteSources(playerManager);
        return playerManager;
    }

    public void destroy() {
        player.destroy();
        scheduler.clearQueue();
        audioManager.closeAudioConnection();
        MusicManager.guildMusics.remove(guildId);
    }

    public void bassBoost(float percentage) {
        final float multiplier = percentage / 100.00f;

        for (int i = 0; i < BASS_BOOST.length; i++) {
            equalizer.setGain(i, BASS_BOOST[i] * multiplier);
        }
    }
}
