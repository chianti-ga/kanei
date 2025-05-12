/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.classic;

import fr.skitou.botcore.commands.classic.AbstractCommand;
import fr.skitou.botcore.commands.classic.CommandReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class Del extends AbstractCommand {
    @Override
    public @NotNull String getCommand() {
        return "del";
    }

    @Override
    public @NotNull String getName() {
        return "del";
    }

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        event.getArgs().forEach(s -> event.getChannel().deleteMessageById(s).queue());
    }
}
