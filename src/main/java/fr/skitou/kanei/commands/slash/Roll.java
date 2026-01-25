/*
 * Copyright (c) Chianti Gally 2024 - 2026.
 */

package fr.skitou.kanei.commands.slash;

import fr.skitou.kanei.core.BotInstance;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Roll implements ISlashCommand {

    private static ResourceBundle bundle = null;
    private final Random random = new Random();

    @NotNull
    @Override
    public String getName() {
        return "roll";
    }

    @NotNull
    @Override
    public String getHelp() {
        return "Roll a dice.";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(
                new OptionData(OptionType.STRING, "roll", "<number_of_dices>d<number_of_sides>", true)
        );
    }

    @Override
    public void onCommandReceived(net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent event) {
        String roll = Objects.requireNonNull(event.getOption("roll")).getAsString();
        if (bundle == null) {
            bundle = BotInstance.getBundleFromGuild(Objects.requireNonNull(event.getGuild()));
        }

        if (!roll.matches("^\\d+d\\d+$")) {
            event.getHook().sendMessage(bundle.getString("roll.wrongformat")).queue();
            return;
        }

        String[] split = roll.split(Pattern.quote("d"));
        long numberOfDices;
        long numberOfSides;
        try {
            numberOfDices = Long.parseLong(split[0]);
            numberOfSides = Long.parseLong(split[1]);
        } catch (NumberFormatException e) {
            event.getHook().sendMessage("values must be between " + Long.MIN_VALUE + " and " + Long.MAX_VALUE + ".").queue();
            return;
        }

        if ((numberOfDices <= 0 || numberOfSides <= 0) && (numberOfDices > 10000 || numberOfSides <= 10000)) {
            event.getHook().sendMessage(bundle.getString("roll.invalid")).queue();
            return;
        }

        long sum = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("**Roll ").append(roll).append("**").append('\n');
        sb.append("```").append('\n');
        sb.append(String.format("%-6s | %-5s%n", bundle.getString("roll.dicenumber"), bundle.getString("roll.result")));
        sb.append("-----------------").append('\n');

        for (int i = 0; i < numberOfDices; i++) {
            long result = random.nextLong(1, numberOfSides);
            sum += result;
            sb.append(String.format("%-6d | %-5d%n", i + 1, result));
        }

        sb.append("-----------------").append('\n');
        sb.append(String.format("%-6s | %-5d%n", bundle.getString("roll.sum"), sum));
        sb.append("```").append('\n');
        sb.append("Lancé par ").append(event.getUser().getAsMention());

        event.getHook().sendMessage(sb.toString()).queue();
    }
}
