package fr.skitou.kanei.lavautils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MusicManager {
    public static final HashMap<Long, GuildMusic> guildMusics = new HashMap<>();
    private static final Timer timer = new Timer();

    public static void scheduleForRemoval(long guildId) {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (guildMusics.containsKey(guildId)) {
                    GuildMusic guildMusic = guildMusics.get(guildId);
                    if (guildMusic.scheduler.getQueue().isEmpty() && guildMusic.player.getPlayingTrack() == null) {
                        guildMusic.destroy();
                        guildMusics.remove(guildId);
                    }
                }

            }
        }, Date.from(Instant.now().plus(Duration.ofMinutes(10))));

    }
}
