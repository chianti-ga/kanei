/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.lava.GuildMusic;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class Clear implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "clear";
    }

    @Override
    public @NotNull String getHelp() {
        return "Clear the current queue.";
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

        if (!guildMusic.scheduler.getQueue().isEmpty()) {
            guildMusic.scheduler.clearQueue();
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.clearedqueue")).queue();
        } else
            event.getHook().sendMessage(KaneiMain.getBundleFromGuild(event.getGuild()).getString("music.emptyqueue")).queue();
    }
}
