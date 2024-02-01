/*
 * Copyright (c) Skitou 2024.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.databaseentities.GuildMusicSettings;
import fr.skitou.kanei.utils.lava.MusicManager;
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
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null) {
            if (event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
                event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
                return;
            }
        }

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.setVolume(event.getOption("volume").getAsInt());
        }
        new GuildMusicSettings(event.getGuild().getIdLong(), event.getOption("volume").getAsInt());
        event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.volumeSet") + " " + event.getOption("volume").getAsInt()).queue();
    }
}
