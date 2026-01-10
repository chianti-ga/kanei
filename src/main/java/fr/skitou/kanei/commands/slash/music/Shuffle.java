/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.kanei.commands.slash.ISlashCommand;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Shuffle implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "shuffle";
    }

    @Override
    public @NotNull String getHelp() {
        return "Shuffle the queue content";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null && event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }


        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.shuffle();
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.queueshuffled")).queue();
        } else
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.nothingplaying")).queue();
    }
}
