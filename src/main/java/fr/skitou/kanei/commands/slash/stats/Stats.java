package fr.skitou.kanei.commands.slash.stats;

import fr.skitou.botcore.commands.slash.ISlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.XYChart;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Stats implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "stats";
    }

    @Override
    public @NotNull String getHelp() {
        return "stats";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        for (double i = 0; i <50; i += 0.01) {
            xData.add(new BigDecimal(i).setScale(2, RoundingMode.HALF_UP).doubleValue());
            yData.add(Math.sin(i));
        }

        XYChart chart = QuickChart.getChart("TEST", "X", "Y", "sin(x)", xData, yData);

        try {
            BitmapEncoder.saveBitmapWithDPI(chart, "data/cache/testchart.png", BitmapEncoder.BitmapFormat.PNG, 300);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        event.getHook().sendFiles(FileUpload.fromData(new File("data/cache/testchart.png"))).queue();
    }
}
