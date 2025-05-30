/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class Queue implements ISlashCommand {

    @Override
    public @NotNull String getName() {
        return "queue";
    }

    @Override
    public @NotNull String getHelp() {
        return "queue";
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

        if (!MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
            return;
        }
        if (MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.getQueue().isEmpty()) {
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.emptyqueue")).queue();
            if (MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.getPlayingTrack() != null)
                event.getChannel().sendMessageEmbeds(MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.nowPlaying()).queue();
            return;
        }

        List<MessageEmbed> queueEmbeds = MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.displayQueue();
        event.getHook().sendMessageEmbeds(queueEmbeds.get(0)).queue();
        queueEmbeds.remove(0);
        if (!queueEmbeds.isEmpty())
            queueEmbeds.forEach(messageEmbed -> event.getChannel().sendMessageEmbeds(messageEmbed).queue());
    }
}
