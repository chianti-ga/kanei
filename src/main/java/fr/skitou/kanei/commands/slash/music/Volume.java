/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.databaseentities.GuildMusicSettings;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Volume implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "volume";
    }

    @Override
    public @NotNull String getHelp() {
        return "Set audio volume (0 to 300)";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null) {
            if (event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
                event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
                return;
            }
        }

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            if (event.getOption("volume").getAsInt() < 0 || event.getOption("volume").getAsInt() > 300) {
                event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.invalidvolume")).queue();
            } else {
                MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.setVolume(event.getOption("volume").getAsInt());

                Set<GuildMusicSettings> playerSettings = Database.getAll(GuildMusicSettings.class).stream()
                        .filter(settings -> settings.getGuild() == event.getGuild().getIdLong())
                        .collect(Collectors.toSet());
                if (playerSettings.isEmpty()) {
                    new GuildMusicSettings(event.getGuild().getIdLong(), event.getOption("volume").getAsInt(), "en");
                } else
                    playerSettings.forEach(guildMusicSettings -> guildMusicSettings.setVolume(event.getOption("volume").getAsInt()));


                event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.volumeSet") + " " + event.getOption("volume").getAsInt()).queue();
            }
        } else
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.INTEGER, "volume", "0 to 300", true));
    }
}
