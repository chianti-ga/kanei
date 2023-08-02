package fr.skitou.kanei.commands.slash.music;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.skitou.botcore.commands.slash.ISlashCommand;
import io.sentry.Sentry;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Lyrics implements ISlashCommand {
    private static final Logger logger = LoggerFactory.getLogger(Lyrics.class);

    @Override
    public @NotNull String getName() {
        return "lyrics";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        /*if(MusicManager.guildMusics.containsKey(event.getGuild().getIdLong()) && MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.getPlayingTrack() != null) {
            EmbedBuilder builder = new EmbedBuilder();
            String title = MusicManager.guildMusics.get(event.getGuild().getIdLong()).player.getPlayingTrack().getInfo().title;
            builder.setTitle("Lyrics for "+title)
                    .setDescription(getLyrics(title))
                    .setColor(QuickColors.LIGHT_GREEN);
            event.replyEmbeds(builder.build()).queue();
        } else event.reply(KaneiMain.getLangBundle().getString("music.nothingplaying")).queue();*/

        event.reply("Command disabled").queue();
    }

    @Override
    public @NotNull String getHelp() {
        return "Return the lyrics of the current playing track";
    }

    private String getLyrics(String title) {
        Gson gson = new Gson();
        String response, artist;
        try {
            InputStreamReader searchReader = new InputStreamReader(new URL("https://api.lyrics.ovh/suggest/" + title).openStream());
            JsonObject search = gson.fromJson(searchReader, JsonObject.class).get("data").getAsJsonArray().get(0).getAsJsonObject();
            artist = search.get("artist").getAsJsonObject().get("name").getAsString();
            InputStreamReader responseReader = new InputStreamReader(new URL("https://api.lyrics.ovh/v1/" + artist + "/" + title).openStream());

            response = gson.fromJson(responseReader, JsonObject.class).get("lyrics").getAsString();

        } catch(IOException e) {
            logger.error("Unable to retrieve lyrics, {}: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            Sentry.captureException(e);
            response = "Error";
        }

        return response;
    }
}
