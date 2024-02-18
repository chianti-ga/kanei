package fr.skitou.kanei.commands.classic;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import fr.skitou.botcore.utils.IsSenderAllowed;
import fr.skitou.botcore.utils.QuickColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class BotManage extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "botmanage";
    }

    @Override
    public @NotNull String getName() {
        return "botmanage";
    }

    @Override
    public @NotNull String getHelp() {
        return "botmanage (listguild)";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(this.getHelp()).queue();
        } else {
            switch (event.getArgs().get(0).toLowerCase()) {
                case "listguild" -> event.getJDA().getGuilds().forEach(guild -> {
                    guild.retrieveOwner().queue();
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Guild list")
                            .addField("guildId", guild.getId(), false)
                            .addField("guildName", guild.getName(), false)
                            .addField("Members", String.valueOf(guild.getMemberCount()), false)
                            .addField("ownerId",guild.getOwnerId() , false)
                            .setColor(QuickColors.DARK_YELLOW);
                    event.getChannel().sendMessageEmbeds(builder.build()).queue();
                });
                case "leaveguild" ->{
                    if (event.getArgs().size() < 2) {
                        event.getChannel().sendMessage(this.getHelp()).queue();
                    }else {
                        event.getJDA().getGuildById(event.getArgs().get(1)).leave().queue();
                        event.getChannel().sendMessage(":thumbsup:").queue();
                    }
                }
                default -> event.getChannel().sendMessage(this.getHelp()).queue();
            }
        }
    }

    @Override
    public Predicate<Member> isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }
}

