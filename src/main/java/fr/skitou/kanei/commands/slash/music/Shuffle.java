package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
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
        event.deferReply().queue();
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            MusicManager.guildMusics.get(event.getGuild().getIdLong()).scheduler.shuffle();
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.queueshuffled")).queue();
        } else event.reply(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
    }
}
