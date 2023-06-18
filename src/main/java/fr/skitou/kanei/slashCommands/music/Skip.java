package fr.skitou.kanei.slashCommands.music;

import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.GuildMusic;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class Skip implements ISlashCommand {

    @Override
    public @NotNull String getName() {
        return "skip";
    }

    @Override
    public @NotNull String getHelp() {
        return "skip";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }

        GuildMusic guildMusic;

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
            return;
        }
        guildMusic.scheduler.nextTrack();
        if (guildMusic.player.getPlayingTrack() != null) {
            event.getHook().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
        } else guildMusic.destroy();
        event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.emptyqueue")).queue();
    }
}
