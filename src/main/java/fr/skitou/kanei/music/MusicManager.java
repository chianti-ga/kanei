package fr.skitou.kanei.music;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class MusicManager {
    public static final Set<GuildMusic> guildMusics = new HashSet<>();
    private static final Timer timer = new Timer();

    public static void scheduleForRemoval(TrackScheduler trackScheduler) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                guildMusics.removeIf(guildMusic -> guildMusic.scheduler == trackScheduler);
            }
        }, Date.from(Instant.now().plus(Duration.ofMinutes(10))));
    }
}
