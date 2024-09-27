/*
 * Copyright (c) Skitou 2024.
 */

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

@SuppressWarnings({"DuplicatedCode", "unused"})
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
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.INTEGER, "track_number", getHelp(), false));
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

        GuildMusic guildMusic;

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
            return;
        }

        if (event.getOption("track_number") != null) {
            if (guildMusic.scheduler.getQueue().isEmpty() || event.getOption("track_number").getAsInt() <= 0 || event.getOption("track_number").getAsInt() > guildMusic.scheduler.getQueue().size()) {
                event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.invalidskip")).queue();
            } else {
                guildMusic.scheduler.skiptoIndex(event.getOption("track_number").getAsInt() - 1);
                guildMusic.scheduler.nextTrack();
                event.getHook().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
            }
        } else {
            guildMusic.scheduler.nextTrack();
            if (guildMusic.player.getPlayingTrack() != null) {
                event.getHook().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
            } else {
                event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.emptyqueue")).queue();
            }
        }
    }
}
