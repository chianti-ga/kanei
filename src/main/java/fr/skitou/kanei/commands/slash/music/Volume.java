package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.databaseentities.GuildMusicSettings;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings("unused")
public class Volume implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "volume";
    }

    @Override
    public @NotNull String getHelp() {
        return "Set audio volume (0 to 150)";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.INTEGER, "volume", "0 to 300", true));
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        if(event.getOption("volume").getAsInt() < 0 || event.getOption("volume").getAsInt() > 300) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.invalidvolume")).queue();
            return;
        }

        if(MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.setVolume(event.getOption("volume").getAsInt());
        }
        Database.saveOrUpdate(new GuildMusicSettings(event.getGuild().getIdLong(), event.getOption("volume").getAsInt()));
        event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.volumeSet") + " " + event.getOption("volume").getAsInt()).queue();
    }
}
