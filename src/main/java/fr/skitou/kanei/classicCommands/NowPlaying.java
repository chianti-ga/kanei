package fr.skitou.kanei.classicCommands;

import fr.skitou.botcore.commands.AbstractCommand;
import fr.skitou.botcore.commands.CommandReceivedEvent;
import fr.skitou.kanei.lavautils.MusicManager;
import org.jetbrains.annotations.NotNull;

public class NowPlaying extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "nowplaying";
    }

    @Override
    public @NotNull String getName() {
        return "nowplaying";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.nowPlaying()).queue();
    }
}
