package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NowPlaying implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "nowplaying";
    }

    @Override
    public @NotNull String getHelp() {
        return "Show current track";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if(MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            if(MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.getPlayingTrack() != null) {
                event.replyEmbeds(MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.nowPlaying()).queue();
            } else event.reply(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
        } else event.reply(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
    }
}