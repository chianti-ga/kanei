package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.lava.GuildMusic;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Loop implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "loop";
    }

    @Override
    public @NotNull String getHelp() {
        return "Enable or disable track repetition.";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.BOOLEAN, "loop", getHelp(), true));
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        //noinspection DuplicatedCode
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
        guildMusic.scheduler.setRepeating(event.getOption("loop").getAsBoolean());
        if (event.getOption("loop").getAsBoolean()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.loopon")).queue();
        } else {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.loopoff")).queue();
        }
    }
}
