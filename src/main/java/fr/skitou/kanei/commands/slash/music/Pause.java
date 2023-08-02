package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.GuildMusic;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class Pause implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "pause";
    }

    @Override
    public @NotNull String getHelp() {
        return "Pause/resume the player.";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }

        GuildMusic guildMusic;

        if(MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
            return;
        }

        if(guildMusic.player.getPlayingTrack() == null) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
            return;
        }

        if(guildMusic.player.isPaused()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.resumed")).queue();
            guildMusic.player.setPaused(false);
        } else {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.paused")).queue();
            guildMusic.player.setPaused(true);
        }
    }
}
