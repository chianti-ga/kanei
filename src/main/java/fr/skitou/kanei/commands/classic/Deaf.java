package fr.skitou.kanei.commands.classic;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import fr.skitou.botcore.utils.IsSenderAllowed;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class Deaf extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "deaf";
    }

    @Override
    public @NotNull String getName() {
        return "deaf";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().getFirst().equals("un")){
            event.getArgs().stream().skip(1).forEach(s -> event.getGuild().getMemberById(s).mute(false).queue());
        }else event.getArgs().forEach(s -> event.getGuild().getMemberById(s).mute(true).queue());
    }

    @Override
    public Predicate<Member> isSenderAllowed() {
        return IsSenderAllowed.BotAdmin.or(member -> member.getId().equalsIgnoreCase("588381876989853697"));
    }
}
