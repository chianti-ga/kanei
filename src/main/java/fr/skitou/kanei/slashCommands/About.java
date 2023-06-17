package fr.skitou.kanei.slashCommands;

import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.botcore.utils.QuickColors;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.time.Duration;

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
        Duration duration = Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime());
        long HH = duration.toHours();
        long MM = duration.toMinutesPart();
        long SS = duration.toSecondsPart();
        String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(BotInstance.getJda().getSelfUser().getName() + "Music Bot Infos")
                .setDescription("This is a music bot made for me and my friends using JDA and lavaplayer.\n Type /help to see all commands available.")
                .addField("Versions:", "JDA5\nCore:" + BotInstance.getCoreVersion() + "\n" + BotInstance.getJda().getSelfUser().getName() + ":" + KaneiMain.getVersion(), true)
                .addField("Total servers:", String.valueOf(BotInstance.getJda().getGuilds().size()), true)
                .addField("Total stream:", String.valueOf(MusicManager.guildMusics.size()), true)
                .setColor(QuickColors.LIGHT_BLUE)
                .setFooter("Running on Java " + Runtime.version() + " | Uptime:" + timeInHHMMSS);
        event.replyEmbeds(builder.build()).queue();
    }
}
