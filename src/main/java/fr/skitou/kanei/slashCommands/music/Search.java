package fr.skitou.kanei.slashCommands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.skitou.botcore.slashcommand.ISlashCommand;
import fr.skitou.botcore.utils.QuickColors;
import fr.skitou.kanei.KaneiMain;
import fr.skitou.kanei.lavautils.GuildMusic;
import fr.skitou.kanei.lavautils.MusicManager;
import fr.skitou.kanei.subsystems.ComponentInteractionListener;
import fr.skitou.kanei.utils.TimeFormater;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Search implements ISlashCommand {
    @Override
    public @NotNull String getName() {
        return "search";
    }

    @Override
    public @NotNull String getHelp() {
        return "search";
    }

    @Override
    public Set<OptionData> getOptionData() {
        return Set.of(new OptionData(OptionType.STRING, "search", "Search a track on Youtube.", true));
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

        guildMusic.playerManager.loadItemOrdered(guildMusic, "ytsearch:" + event.getOption("search").getAsString(), new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                StringSelectMenu.Builder selectMenu = StringSelectMenu.create("selectMenu");
                AtomicInteger i = new AtomicInteger();

                playlist.getTracks().subList(0, 4).forEach(audioTrack -> {
                    i.getAndIncrement();
                    selectMenu.addOption(String.valueOf(i.get()), String.valueOf(i.get()), audioTrack.getInfo().title);
                });

                event.getHook().sendMessageEmbeds(displaySearchResult(playlist.getTracks().subList(0, 4)))
                        .addActionRow(ComponentInteractionListener.createStringSelectMenuInteraction(selectMenu.build(), interactionEvent -> {
                            AudioTrack track = playlist.getTracks().get(Integer.parseInt(interactionEvent.getSelectedOptions().get(0).getLabel()) - 1);
                            guildMusic.scheduler.queueTrack(track);
                            interactionEvent.replyEmbeds(guildMusic.scheduler.embedTracInfo(track.getInfo())).queue();
                        })).queue();
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.noresult") + event.getOption("search").getAsString()).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getHook().sendMessage(KaneiMain.getLangBundle().getString("music.cantplay") + exception.getMessage()).queue();
            }
        });
    }

    private MessageEmbed displaySearchResult(List<AudioTrack> searchTracks) {
        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger();

        searchTracks.forEach(audioTrack -> {
            i.getAndIncrement();
            sb.append("**`").append(i.get()).append("`** | ")
                    .append("`[").append(TimeFormater.milisToFormatedDuration(audioTrack.getDuration()))
                    .append("]` ")
                    .append(audioTrack.getInfo().title.length() > 40 ? audioTrack.getInfo().title.substring(0, 40).concat("`...`") : audioTrack.getInfo().title)
                    .append("\n");
        });

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(KaneiMain.getLangBundle().getString("music.searchresult"))
                .setDescription(sb.toString())
                .setColor(QuickColors.CYAN);
        return builder.build();
    }
}