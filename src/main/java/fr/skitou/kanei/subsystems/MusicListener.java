/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.subsystems;

import fr.skitou.botcore.subsystems.AbstractSubsystem;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
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
        if (event.getChannelLeft() == null) return;

        // If only one member remains in the voice channel (the bot itself), schedule for removal of music data
        if (event.getChannelLeft().asVoiceChannel().getMembers().size() == 1) {
            MusicManager.scheduleForRemoval(event.getGuild().getIdLong());
        }

        // If the bot is not in an audio channel and has associated music data, destroy it
        if (!event.getGuild().getSelfMember().getVoiceState().inAudioChannel() && MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            MusicManager.guildMusics.get(event.getGuild().getIdLong()).destroy();
        }
    }
}
