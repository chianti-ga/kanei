/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.utils.IsSenderAllowed;
import fr.skitou.kanei.utils.lava.GuildMusic;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Quality implements ISlashCommand {
    @NotNull
    @Override
    public String getName() {
        return "musicquality";
    }

    @NotNull
    @Override
    public String getHelp() {
        return "ChangeOpus quality";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!IsSenderAllowed.BotAdmin.test(event.getMember())) {
            event.getHook().sendMessage("You are not allowed to use this command.").queue();
            return;
        }

        int quality = event.getOption("quality").getAsInt();

        if (quality < 0 || quality > 10) {
            event.getHook().sendMessage("Quality must be between 0 and 10.").queue();
            return;
        }

        GuildMusic.setOpusQuality(quality);

        event.getHook().sendMessage("Music quality set to " + quality + ".").queue();
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(
                new OptionData(OptionType.INTEGER, "quality", "0 to 10")
                        .setRequired(true)
                        .setMinValue(0)
                        .setMaxValue(10)
        );
    }
}
