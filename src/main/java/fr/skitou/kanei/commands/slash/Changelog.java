package fr.skitou.kanei.commands.slash;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.hibernate.Database;
import fr.skitou.kanei.databaseentities.ChangelogEntity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class Changelog implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "changelog";
    }

    @Override
    public @NotNull String getHelp() {
        return "Get the changelog";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.STRING, "version", "Specific changelog version", false));
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        List<ChangelogEntity> changeloglist = Database.getAll(ChangelogEntity.class).stream().toList();

        if (changeloglist.isEmpty()){
            event.getHook().editOriginal("No changelog found").queue();
        }

        ChangelogEntity changelog;
        if (event.getOption("version") != null) {
             Optional<ChangelogEntity> specificChangelog = changeloglist.stream().filter(changelogEntity -> changelogEntity.getVersion().equalsIgnoreCase(event.getOption("version").getAsString()))
                     .findFirst();
             if (specificChangelog.isEmpty()){
                 event.reply("Specific changelog found, getting latest").setEphemeral(true).queue();
                 changelog = changeloglist.get(changeloglist.size()-1);
             }else changelog = specificChangelog.get();
        }else changelog = changeloglist.get(changeloglist.size()-1);

        User skitou = event.getJDA().getUserById("374283393799553036");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Changelog")
                .setColor(skitou.retrieveProfile().complete().getAccentColor())
                .setDescription(changelog.getContent())
                .setAuthor(skitou.getGlobalName(), "https://skitou.fr/", skitou.getAvatarUrl())
                .setFooter("@leskitou | "+ DateTimeFormatter.ofPattern("dd/MM/yyyy").format(changelog.getTime())
                        , skitou.getAvatarUrl());
        event.getHook().editOriginalEmbeds(builder.build()).queue();
    }
}


