package fr.skitou.kanei.slashCommands.music;

import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            return;
        }
        List<MessageEmbed> queueEmbeds = MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.displayQueue();
        event.getHook().sendMessage("").addEmbeds(queueEmbeds.get(0)).queue();
        queueEmbeds.remove(0);
        if (!queueEmbeds.isEmpty())
            queueEmbeds.forEach(messageEmbed -> event.getChannel().sendMessageEmbeds(messageEmbed).queue());
    }
}
