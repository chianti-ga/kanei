package fr.skitou.kanei.commands.slash.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.GuildMusic;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class Remove implements ISlashCommand {

    @Override
    public @NotNull String getName() {
        return "remove";
    }

    @Override
    public @NotNull String getHelp() {
        return "remove track from queue with specified index";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.STRING, "index", getHelp(), true));
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

        if (guildMusic.scheduler.getQueue().isEmpty()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.emptyqueue")).queue();
            return;
        }

        int selectedIndex = event.getOption("index").getAsInt() - 1;

        if (selectedIndex >= 0 && selectedIndex <= guildMusic.scheduler.getQueue().size()) {

            String title = ((AudioTrack) ((List<?>) guildMusic.scheduler.getQueue()).get(selectedIndex)).getInfo().title;
            guildMusic.scheduler.removeFromQueueWithIndex(selectedIndex);

            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.trackremoved") + " " + title).queue();
        }
    }
}
