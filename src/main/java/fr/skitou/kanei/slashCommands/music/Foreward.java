package fr.skitou.kanei.slashCommands.music;

import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.GuildMusic;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Foreward implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "foreward";
    }

    @Override
    public @NotNull String getHelp() {
        return "Foreward to a specific position of the track";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.STRING, "position", "Position in [HH:MM:SS] format"));
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

        guildMusic.scheduler.foreward(event.getOption("position").getAsString());

    }
}
