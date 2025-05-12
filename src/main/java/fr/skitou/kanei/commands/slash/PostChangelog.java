/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.utils.IsSenderAllowed;
import fr.skitou.kanei.databaseentities.ChangelogEntity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")

public class PostChangelog implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "postchangelog";
    }

    @Override
    public @NotNull String getHelp() {
        return "Post changelog";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!IsSenderAllowed.BotAdmin.test(event.getMember())) {
            event.getHook().editOriginal("Only botadmin can post a changelog").queue();
            return;
        }

        if (!event.getOption("attachment").getAsAttachment().getFileExtension().contains("txt")) {
            event.getHook().editOriginal("txt invalid").queue();
            return;
        }

        try {
            InputStream inputStream = event.getOption("attachment").getAsAttachment().getProxy().download().get();
            String content = new String(inputStream.readAllBytes());
            inputStream.close();

            new ChangelogEntity(event.getOption("version").getAsString(), content);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.ATTACHMENT, "attachment", "txt of the changelog", true),
                new OptionData(OptionType.STRING, "version", "version", true));
    }
}
