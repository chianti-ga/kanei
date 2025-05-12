/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.classic;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Clean extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "clean";
    }

    @Override
    public @NotNull String getName() {
        return "clean";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        List<Message> msgs = event.getChannel().getHistory().retrievePast(Integer.parseInt(event.getArgs().getFirst())).complete();
        event.getChannel().purgeMessages(msgs);

    }
}
