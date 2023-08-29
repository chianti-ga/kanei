package fr.skitou.kanei.utils.lava;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MusicManager {
    public static final ConcurrentMap<Long, GuildMusic> guildMusics = new ConcurrentHashMap<>();
    private static final Timer timer = new Timer();

    private MusicManager() {
        throw new IllegalStateException("Utility class");
    }

    public static void scheduleForRemoval(long guildId) {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (guildMusics.containsKey(guildId)) {
                    GuildMusic guildMusic = guildMusics.get(guildId);
                    if (guildMusic.scheduler.getQueue().isEmpty() && guildMusic.player.getPlayingTrack() == null) {
                        guildMusic.destroy();
                    }
                }

            }
        }, Date.from(Instant.now().plus(Duration.ofMinutes(10))));

    }
}
