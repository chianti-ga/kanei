package fr.skitou.kanei.commands.slash;

import fr.skitou.botcore.commands.classic.ICommand;
import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.utils.QuickColors;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.MusicManager;
import fr.skitou.kanei.utils.TimeFormater;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("unused")
public class About implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "about";
    }

    @Override
    public @NotNull String getHelp() {
        return "About";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(BotInstance.getJda().getSelfUser().getName() + " Music Bot Infos")
                .setDescription("This is a music bot made for me and my friends using JDA and lavaplayer.\n Type `" + ICommand.PREFIX + "help` to see classic commands list!.")
                .addField("Versions:", "JDA5\nCore:" + BotInstance.getCoreVersion() + "\n" + BotInstance.getJda().getSelfUser().getName() + ":" + KaneiMain.getVersion(), true)
                .addField("Total servers:", String.valueOf(BotInstance.getJda().getGuilds().size()), true)
                .addField("Total stream:", String.valueOf(MusicManager.guildMusics.size()), true)
                .addField("RAM:", Math.round((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) * Math.pow(2, -20)) + "/" + Math.round(Runtime.getRuntime().totalMemory() * Math.pow(2, -20)), false)
                .setColor(QuickColors.LIGHT_BLUE)
                .setFooter("Running on Java " + Runtime.version() + " | Uptime:" + TimeFormater.milisToFormatedDuration(System.currentTimeMillis()));
        event.replyEmbeds(builder.build()).queue();
    }
}
