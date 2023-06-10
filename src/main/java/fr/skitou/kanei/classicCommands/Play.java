package fr.skitou.kanei.classicCommands;

import fr.skitou.botcore.commands.AbstractCommand;
import fr.skitou.botcore.commands.CommandReceivedEvent;
import fr.skitou.kanei.music.GuildMusic;
import fr.skitou.kanei.music.MusicManager;
import org.jetbrains.annotations.NotNull;

public class Play extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "play";
    }

    @Override
    public @NotNull String getName() {
        return "play";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        //TODO: Add verification
        GuildMusic guildMusic = new GuildMusic(event.getGuild().getAudioManager(), event.getMember().getVoiceState().getChannel());
        MusicManager.guildMusics.add(guildMusic);
    }
}
