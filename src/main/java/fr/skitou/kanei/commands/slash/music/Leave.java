package fr.skitou.kanei.commands.slash.music;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.lava.MusicManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class Leave implements ISlashCommand {

    @Override
    public @NotNull String getName() {
        return "leave";
    }

    @Override
    public @NotNull String getHelp() {
        return "Disconnect the bot.";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        if(!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }

        if(MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            MusicManager.guildMusics.get(event.getGuild().getIdLong()).destroy();
            event.getHook().sendMessage(":white_check_mark:").queue();
        } else {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();
        }
    }
}
