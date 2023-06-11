package fr.skitou.kanei.lavautils;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import fr.skitou.botcore.core.BotInstance;
import fr.skitou.kanei.TimeFormater;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


// TODO: DOCUMENTATION
public class TrackScheduler extends AudioEventAdapter {
    final long guildId;

    private final AudioPlayer player;
    @Getter
    private final LinkedBlockingQueue<AudioTrack> queue;
    private AudioTrack lastTrack;
    private boolean repeating = false;

    /**
     * @param guildId Id of the guild
     * @param player  The audio player this scheduler uses
     */
    public TrackScheduler(long guildId, AudioPlayer player) {
        this.guildId = guildId;
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
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
        BotInstance.logger.warn(String.valueOf(player.getPlayingTrack()));

        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void foreward(String formatedForewardPosition) {
        long forewardPosition = TimeFormater.formatedDurationToMilis(formatedForewardPosition);

        if (!(forewardPosition > player.getPlayingTrack().getDuration() || forewardPosition < player.getPlayingTrack().getDuration())) {
            player.getPlayingTrack().setPosition(forewardPosition);
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    public MessageEmbed nowPlaying() {
        AudioTrackInfo info = player.getPlayingTrack().getInfo();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(info.author)
                .setDescription(info.title)
                .setUrl(info.uri)
                .setThumbnail("https://img.youtube.com/vi/" + player.getPlayingTrack().getIdentifier() + "/mqdefault.jpg")
                .setFooter(TimeFormater.milisToFormatedDuration(info.length));

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
        this.lastTrack = track;
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if (repeating) {
                player.startTrack(lastTrack.makeClone(), false);
            } else if (queue.isEmpty()) {
                MusicManager.scheduleForRemoval(guildId);
            } else {
                nextTrack();
            }
        }

    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void shuffle() {
        Collections.shuffle((List<?>) queue);
    }
}
