/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.utils.QuickColors;
import fr.skitou.kanei.utils.lava.GuildMusic;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class Sources implements ISlashCommand {
    private static final String HTTP_FORMAT = """
            - MP3
            - FLAC
            - WAV
            - Matroska/WebM (AAC, Opus or Vorbis codecs)
            - MP4/M4A (AAC codec)
            - OGG streams (Opus, Vorbis and FLAC codecs)
            - AAC streams
            - Stream playlists (M3U and PLS)
            """;

    @Override
    public @NotNull String getName() {
        return "sources";
    }

    @Override
    public @NotNull String getHelp() {
        return "Display all available media sources";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Available Sources:")
                .setColor(QuickColors.CYAN);
        GuildMusic.getPlayerManager().getSourceManagers().forEach(audioSourceManager -> {
            if (audioSourceManager.getSourceName().equalsIgnoreCase("http")) {
                builder.addField(audioSourceManager.getSourceName(), HTTP_FORMAT, true);
            } else if (audioSourceManager.getSourceName().equalsIgnoreCase("spotify")) {
                builder.addField(audioSourceManager.getSourceName(), "Only FR region available track", true);
            } else builder.addField(audioSourceManager.getSourceName(), "", true);
        });

        event.getHook().editOriginalEmbeds(builder.build()).queue();

    }
}