package fr.skitou.kanei.subsystems;

import fr.skitou.botcore.subsystems.AbstractSubsystem;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

public class MusicListener extends AbstractSubsystem {
    @Override
    public @NotNull String getName() {
        return "musiclistener";
    }

    @Override
    public @NotNull String getDescription() {
        return "music management related stuff";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
                MusicManager.guildMusics.get(event.getGuild().getIdLong()).destroy();
            }
        }
    }
}
