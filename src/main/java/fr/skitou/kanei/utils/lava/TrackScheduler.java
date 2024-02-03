/*
 * Copyright (c) Skitou 2024.
 */

package fr.skitou.kanei.utils.lava;

import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fr.skitou.botcore.utils.QuickColors;
import fr.skitou.kanei.utils.TimeFormater;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: DOCUMENTATION
public class TrackScheduler extends AudioEventAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);
    final long guildId;

    private final AudioPlayer player;
    @Getter
    private final Queue<AudioTrack> queue;
    @Getter
    private boolean repeating = false;

    /**
     * @param guildId Id of the guild
     * @param player  The audio player this scheduler uses
     */
    public TrackScheduler(long guildId, AudioPlayer player) {
        this.guildId = guildId;
        this.player = player;
        this.queue = new LinkedList<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queueTrack(AudioTrack track) {

        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In the case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void removeFromQueueWithIndex(int index) {
        ((List<?>) queue).remove(index);
    }

    public void skiptoIndex(int index) {
        ((List<?>) queue).subList(0, index).clear();
    }

    public void foreward(String formatedForewardPosition) {
        long forewardPosition = TimeFormater.formatedDurationToMilis(formatedForewardPosition);

        if (!(forewardPosition > player.getPlayingTrack().getDuration() || forewardPosition < 0)) {
            player.getPlayingTrack().setPosition(forewardPosition);
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    public MessageEmbed embedTracInfo(AudioTrack track) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(track.getInfo().title)
                .setDescription("**Duration : " + TimeFormater.milisToFormatedDuration(track.getInfo().length) + "**")
                .setUrl(track.getInfo().uri)
                .setThumbnail(getThumb(track))
                .setColor(QuickColors.LIGHT_BLUE)
                .setFooter(track.getInfo().author);
        return builder.build();
    }

    private String getThumb(AudioTrack track) {
        if (track.getSourceManager().getSourceName().equalsIgnoreCase("spotify")) {
            try {
                final JsonBrowser jsonBrowser = ((SpotifySourceManager) track.getSourceManager()).getJson("https://api.spotify.com/v1/tracks/" + track.getIdentifier());
                return jsonBrowser.get("album").get("images").index(0).get("url").text();
            } catch (IOException e) {
                logger.error("Unable to retrieve spotify image {}: {}",
                        e.getClass().getSimpleName(), e.getMessage());
                Sentry.captureException(e);
            }
        }
        return "https://img.youtube.com/vi/" + track.getIdentifier() + "/mqdefault.jpg";
    }

    public MessageEmbed nowPlaying() {
        AudioTrackInfo info = player.getPlayingTrack().getInfo();


        StringBuilder sb = new StringBuilder();
        sb.append("**[");

        float curentPosition = player.getPlayingTrack().getPosition();
        float percentage = curentPosition / info.length;
        int progress = Math.round(30 * percentage);

        sb.append("#".repeat(progress))
                .append("-".repeat(30 - progress))
                .append("]** `").append(Math.round(percentage * 100)).append("% | ")
                .append(TimeFormater.milisToFormatedDuration(player.getPlayingTrack().getPosition())).append("/")
                .append(TimeFormater.milisToFormatedDuration(info.length))
                .append("`");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(":speaker: Now Playing : " + info.title)
                .setDescription(sb)
                .setUrl(info.uri)
                .setColor(QuickColors.LIGHT_BLUE)
                .setFooter(info.author)
                .setThumbnail(getThumb(player.getPlayingTrack()));
        return builder.build();
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if (repeating) {
                player.startTrack(track.makeClone(), false);
            } else if (queue.isEmpty()) {
                MusicManager.scheduleForRemoval(guildId);
            } else {
                nextTrack();
            }
        }

    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void shuffle() {
        if (!queue.isEmpty()) Collections.shuffle((List<?>) queue);
    }

    public List<MessageEmbed> displayQueue() {
        List<String> trackList = new ArrayList<>();
        List<MessageEmbed> queueEmbeds = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger();

        queue.forEach(audioTrack -> {
            i.getAndIncrement();
            sb.append("**`").append(String.format("%02d", i.get())).append("`** | ")
                    .append("`[").append(TimeFormater.milisToFormatedDuration(audioTrack.getDuration()))
                    .append("]` ")
                    .append(audioTrack.getInfo().title.length() > 40 ? audioTrack.getInfo().title.substring(0, 40).concat("...") : audioTrack.getInfo().title)
                    .append("\n");

            trackList.add(sb.toString());
            sb.setLength(0);
        });

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(":speaker: Now playing: " + player.getPlayingTrack().getInfo().title)
                .setFooter("Queue size: " + queue.size() + (isRepeating() ? " :repeat:" : ""))
                .setColor(QuickColors.CYAN);


        trackList.forEach(s -> {
            if (builder.getDescriptionBuilder().length() + s.length() > 4096 || builder.length() + s.length() > MessageEmbed.EMBED_MAX_LENGTH_BOT) {
                queueEmbeds.add(builder.build());
                builder.setDescription(null).setTitle("Queue");
            }
            builder.appendDescription(s);
        });


        queueEmbeds.add(builder.build()); // Add last embed
        return queueEmbeds;

    }
}
