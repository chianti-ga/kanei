package fr.skitou.kanei.classicCommands;

import fr.skitou.botcore.commands.AbstractCommand;
import fr.skitou.botcore.commands.CommandReceivedEvent;
import fr.skitou.kanei.music.MusicManager;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public class Queue extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "queue";
    }

    @Override
    public @NotNull String getName() {
        return "queue";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        StringJoiner sj = new StringJoiner("\n");
        MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.getQueue().forEach(audioTrack -> sj.add(audioTrack.getInfo().title));
        event.getChannel().sendMessage(sj.toString()).queue();
    }
}
