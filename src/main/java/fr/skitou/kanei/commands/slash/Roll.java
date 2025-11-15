/*
 * Copyright (c) Chianti Gally 2024 - 2025.
 */

package fr.skitou.kanei.commands.slash;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Roll implements ISlashCommand {

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

        if (!roll.matches("^\\d+d\\d+$")) {
            event.getHook().sendMessage("Invalid roll format.\n`<number_of_dices>d<number_of_sides>`").queue();
            return;
        }

        String[] split = roll.split(Pattern.quote("d"));
        int numberOfDices = Integer.parseInt(split[0]);
        int numberOfSides = Integer.parseInt(split[1]);

        if (numberOfDices <= 0 || numberOfSides <= 0) {
            event.getHook().sendMessage("Number of dices and sides must be greater than 0.").queue();
            return;
        }

        int sum = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("**Roll ").append(roll).append("**").append('\n');
        sb.append("```").append('\n');
        sb.append(String.format("%-6s | %-5s%n", "Dé n°", "Résultat"));
        sb.append("-----------------").append('\n');

        for (int i = 0; i < numberOfDices; i++) {
            int result = random.nextInt(numberOfSides) + 1;
            sum += result;
            sb.append(String.format("%-6d | %-5d%n", i + 1, result));
        }

        sb.append("-----------------").append('\n');
        sb.append(String.format("%-6s | %-5d%n", "Somme", sum));
        sb.append("```").append('\n');
        sb.append("Lancé par ").append(event.getUser().getAsMention());

        event.getHook().sendMessage(sb.toString()).queue();
    }
}
