package fr.skitou.kanei.commands.slash;

import fr.skitou.botcore.commands.classic.ICommand;
import fr.skitou.botcore.commands.slash.ISlashCommand;
import fr.skitou.botcore.core.BotInstance;
import fr.skitou.botcore.utils.QuickColors;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.utils.TimeFormater;
import fr.skitou.kanei.utils.lava.MusicManager;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;


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

    @SneakyThrows
    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(BotInstance.getJda().getSelfUser().getName() + " Music Bot Infos")
                .setDescription("This is a music bot made for me and my friends using JDA and lavaplayer.\n Type `" + ICommand.PREFIX + "help` to see classic commands list!.\n **Bot made by " + User.fromId("374283393799553036").getAsMention() + "**")
                .addField("Versions:", "JDA5\nCore:" + BotInstance.getCoreVersion() + "\n" + BotInstance.getJda().getSelfUser().getName() + ":" + KaneiMain.getVersion(), true)
                .addField("Total servers:", String.valueOf(BotInstance.getJda().getGuilds().size()), true)
                .addField("Total users:", String.valueOf(BotInstance.getJda().getUsers().size()), true)
                .addField("Total streams:", String.valueOf(MusicManager.guildMusics.size()), true)
                .addField("RAM:", Math.round((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) * Math.pow(2, -20)) + "/" + Math.round(Runtime.getRuntime().totalMemory() * Math.pow(2, -20)), true)
                .addField("Host:", SystemUtils.getHostName() + " | " + SystemUtils.OS_NAME + " " + SystemUtils.OS_VERSION + " " + SystemUtils.OS_ARCH, true)
                .setColor(QuickColors.LIGHT_BLUE)
                .setFooter("Running on Java " + Runtime.version() + " | Uptime: " + TimeFormater.milisToFormatedDuration(ManagementFactory.getRuntimeMXBean().getUptime()));
        event.replyEmbeds(builder.build()).queue();
    }
}
