package fr.skitou.kanei.subsystems;

import fr.skitou.botcore.subsystems.AbstractSubsystem;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

public class VoiceLeave extends AbstractSubsystem {
    @Override
    public @NotNull String getName() {
        return "voiceleave";
    }

    @Override
    public @NotNull String getDescription() {
        return "Mark GuildMusic for removal when a voice channel is empty.";
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getChannelLeft().asVoiceChannel().getMembers().isEmpty()) {
            MusicManager.scheduleForRemoval(event.getGuild().getIdLong());
        }
    }
}
