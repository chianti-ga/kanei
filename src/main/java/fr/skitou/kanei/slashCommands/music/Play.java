package fr.skitou.kanei.slashCommands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.GuildMusic;
import fr.skitou.kanei.lavautils.MusicManager;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

@SuppressWarnings({"DuplicatedCode", "unused"})
public class Play implements ISlashCommand {

    @Override
    public @NotNull String getName() {
        return "play";
    }

    @Override
    public @NotNull String getHelp() {
        return "Play a track based on URL or name";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.STRING, "track", getHelp(), true));
    }

    @Override
    public void onCommandReceived(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.notinchanel")).queue();
            return;
        }


        GuildMusic guildMusic;

        if (MusicManager.guildMusics.containsKey(event.getGuild().getIdLong())) {
            guildMusic = MusicManager.guildMusics.get(event.getGuild().getIdLong());
        } else
            guildMusic = new GuildMusic(event.getGuild().getAudioManager(), event.getMember().getVoiceState().getChannel(), event.getGuild().getIdLong());

        String search = event.getOption("track").getAsString().startsWith("http") ? event.getOption("track").getAsString() : "ytsearch:" + event.getOption("track").getAsString();

        guildMusic.playerManager.loadItemOrdered(guildMusic, search, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusic.scheduler.queueTrack(track);
                event.getHook().sendMessageEmbeds(guildMusic.scheduler.embedTracInfo(track.getInfo())).queue();

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (search.startsWith("ytsearch:")) {
                    guildMusic.scheduler.queueTrack(playlist.getTracks().get(0));
                    event.getHook().sendMessageEmbeds(guildMusic.scheduler.nowPlaying()).queue();
                } else {
                    playlist.getTracks().forEach(guildMusic.scheduler::queueTrack);
                    List<MessageEmbed> queueEmbeds = guildMusic.scheduler.displayQueue();
                    event.getHook().sendMessage("").addEmbeds(queueEmbeds.get(0)).queue();
                    queueEmbeds.remove(0);
                    if (!queueEmbeds.isEmpty())
                        queueEmbeds.forEach(messageEmbed -> event.getChannel().sendMessageEmbeds(messageEmbed).queue());
                }
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.noresult") + search).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.cantplay") + exception.getMessage()).queue();
            }
        });
    }
}
