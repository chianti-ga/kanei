/*
 * Copyright (c) Chianti Gally 2024 - 2026.
 */

package fr.skitou.kanei.commands.slash.music;

import com.google.common.net.UrlEscapers;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.skitou.kanei.commands.slash.ISlashCommand;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.lava.GuildMusic;
import fr.skitou.kanei.lava.MusicManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class Play implements ISlashCommand {

    private static void sendQueue(SlashCommandInteractionEvent event, GuildMusic guildMusic) {
        List<MessageEmbed> queueEmbeds = guildMusic.scheduler.displayQueue();
        event.getHook().sendMessage("").addEmbeds(queueEmbeds.getFirst()).queue();
        queueEmbeds.removeFirst();
        if (!queueEmbeds.isEmpty())
            queueEmbeds.forEach(messageEmbed -> event.getChannel().sendMessageEmbeds(messageEmbed).queue());
    }

    @Override
    public @NotNull String getName() {
        return "play";
    }

    @Override
    public @NotNull String getHelp() {
        return "Play a track based on URL or name, type /sources to get all media source available";
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }

        if (event.getGuild().getSelfMember().getVoiceState().getChannel() != null && event.getMember().getVoiceState().getChannel().asVoiceChannel() != event.getGuild().getSelfMember().getVoiceState().getChannel().asVoiceChannel()) {
            event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.notinchanel")).queue();
            return;
        }


        GuildMusic guildMusic;

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else
            guildMusic = new GuildMusic(event.getGuild().getAudioManager(), event.getMember().getVoiceState().getChannel(), event.getGuild().getIdLong());

        String search = Objects.requireNonNull(event.getOption("track")).getAsString().startsWith("http") ? Objects.requireNonNull(event.getOption("track")).getAsString() : "ytsearch:" + Objects.requireNonNull(event.getOption("track")).getAsString();

        if (search.endsWith(".sklist")) {
            try {
                OkHttpClient client = new OkHttpClient();
                try (Response response = client.newCall(new Request.Builder().url(search).build()).execute()) {
                    if (!response.isSuccessful()) {
                        event.getHook().sendMessage("can't connect to url").queue();
                        return;
                    }

                    if (response.body() == null) {
                        event.getHook().sendMessage("url response's body is empty").queue();
                        return;
                    }

                    for (String line : response.body().string().split("\n")) {
                        HttpUrl httpUrl = HttpUrl.parse(line);
                        if (httpUrl == null) continue;

                        String baseUrl = httpUrl.scheme() + "://" + httpUrl.host() + "/";
                        String path = UrlEscapers.urlPathSegmentEscaper().escape(line.substring(baseUrl.length()));
                        guildMusicLoadMusic(event, guildMusic, baseUrl + path, true);
                    }
                }
                event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.playlist.added")).queue();
            } catch (IOException e) {
                event.getHook().sendMessage(e.getMessage()).queue();
            }
        } else {
            guildMusicLoadMusic(event, guildMusic, search, false);
        }
    }

    private void guildMusicLoadMusic(SlashCommandInteractionEvent event, GuildMusic guildMusic, String search, boolean isPlaylist) {
        GuildMusic.playerManager.loadItemOrdered(guildMusic, search, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusic.scheduler.queueTrack(track);
                if (!isPlaylist) {
                    event.getHook().sendMessageEmbeds(guildMusic.scheduler.embedTracInfo(track)).queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (search.startsWith("ytsearch:")) {
                    guildMusic.scheduler.queueTrack(playlist.getTracks().getFirst());
                    event.getHook().sendMessageEmbeds(guildMusic.scheduler.embedTracInfo(playlist.getTracks().getFirst())).queue();
                } else {
                    playlist.getTracks().forEach(guildMusic.scheduler::queueTrack);
                    sendQueue(event, guildMusic);
                }
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.noresult") + search).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessage(BotInstance.getBundleFromGuild(event.getGuild()).getString("music.cantplay") + "\n_**" + exception.getMessage() + "**_").queue();
            }
        });
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.STRING, "track", getHelp(), true));
    }
}
