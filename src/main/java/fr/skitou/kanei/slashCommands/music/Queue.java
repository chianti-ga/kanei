package fr.skitou.kanei.slashCommands.music;

import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

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
        if (!MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            event.reply(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
        }
        event.replyEmbeds(MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.displayQueue()).queue();
    }
}
