/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash;

import fr.skitou.kanei.hibernate.Database;
import fr.skitou.kanei.hibernate.entities.GuildMusicSettings;
import fr.skitou.kanei.subsystems.internal.ComponentInteractionListener;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class Language implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "language";
    }

    @Override
    public @NotNull String getHelp() {
        return "Language";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        StringSelectMenu languageSelect = StringSelectMenu.create("language-select")
                .addOption("English", "en")
                .addOption("Français", "fr")
                .build();
        event.getHook().editOriginal("Language:").setComponents(ActionRow.of(ComponentInteractionListener.createStringSelectMenuInteraction(languageSelect, interactionEvent -> {
            List<GuildMusicSettings> settingsSingleton = Database.getAll(GuildMusicSettings.class)
                    .stream().filter(guildMusicSettings -> guildMusicSettings.getGuild() == event.getGuild().getIdLong())
                    .limit(1).toList();

            if (settingsSingleton.isEmpty()) {
                new GuildMusicSettings(event.getGuild().getIdLong(), 100, "en");
            } else {
                settingsSingleton.get(0).setLang(interactionEvent.getSelectedOptions().get(0).getValue());
                Database.saveOrUpdate(settingsSingleton.get(0));
            }
            interactionEvent.reply(":thumbsup:").queue();
        }))).queue();
    }
}