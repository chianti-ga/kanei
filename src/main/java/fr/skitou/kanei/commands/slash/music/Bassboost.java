package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings("unused")
public class Bassboost implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "bassboost";
    }

    @Override
    public @NotNull String getHelp() {
        return "Set the bassboost effect value (up to 200, 0 to disable)";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.INTEGER, "bassboostlevel", "level", true));
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel() || event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }

        if (!MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            event.reply(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
            return;
        }
        event.deferReply().queue();
        if (event.getOption("bassboostlevel").getAsInt() < 0 || event.getOption("bassboostlevel").getAsInt() > 200) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.invalidbassboost")).queue();
            return;
        }

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            MusicManager.guildMusics.get(event.getGuild().getIdLong()).bassBoost(event.getOption("bassboostlevel").getAsInt());
        }
        event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.bassbooset") + " " + event.getOption("bassboostlevel").getAsInt()).queue();
    }
}
