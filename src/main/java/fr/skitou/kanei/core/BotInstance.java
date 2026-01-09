/*
 * Copyright (C) 2021-2024 Ruben Rouvière, Chianti Gally, uku3lig, Rayan Malloul, and Maxandre Rochefort.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package fr.skitou.kanei.core;

import fr.skitou.kanei.commands.classic.CommandAdapter;
import fr.skitou.kanei.commands.slash.ISlashCommand;
import fr.skitou.kanei.hibernate.Database;
import fr.skitou.kanei.hibernate.entities.GuildMusicSettings;
import fr.skitou.kanei.subsystems.SubsystemAdapter;
import fr.skitou.kanei.utils.FilesCache;
import fr.skitou.kanei.utils.reporter.SentryManager;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class BotInstance {
    public static final Logger logger = LoggerFactory.getLogger(BotInstance.class);
    public static final ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    private static final Set<Runnable> runnables = new HashSet<>();
    @Getter
    private static final FilesCache filesCache = new FilesCache();
    @Getter
    private static Set<EventListener> eventListeners = null;
    @Getter
    private static JDA jda = null;

    private static String[] botArgs = null;
    @Getter
    private static String coreVersion;


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        botArgs = args;

        Database.getFactory(); // Init db BEFORE adapters
        eventListeners = Set.of(CommandAdapter.getInstance(), SubsystemAdapter.getInstance());

        logger.warn(isTestMode() ? "Bot Starting in TESTMODE" : "Bot Starting");

        // Create instances of SlashCommands when JDA is ready
        runWhenReady(BotInstance::updateGuildCommands);

        try {
            jda = JDABuilder.createDefault(getToken())
                    .addEventListeners(eventListeners.toArray())
                    .setActivity(Activity.listening("some music!"))
                    .enableCache(Set.of(CacheFlag.VOICE_STATE))
                    .disableCache(Collections.singleton(CacheFlag.MEMBER_OVERRIDES))
                    .enableIntents(Set.of(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES))
                    .disableIntents(Set.of())
                    .build();
        } catch (NullPointerException e) {
            e.getCause();
            logger.error("ERROR: Login failed: " + e.getMessage() + ":" + Arrays.toString(e.getStackTrace()) + "\n Check the token or retry later.");
            Runtime.getRuntime().exit(2);
        }
        SentryManager.getInstance();

        //Run once injection point
        //Only used for test purposes.
        try {
            jda.awaitReady();
            runWhenReady();
        } catch (InterruptedException e) {
            logger.error("Runnable threw a {}: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            Sentry.captureException(e);
            Thread.currentThread().interrupt();
        }

        long end = System.currentTimeMillis();
        logger.info("Start time : {}ms", (end - start));
    }

    public static ResourceBundle getBundleFromGuild(Guild guild) {
        long guildId = guild.getIdLong();
        List<GuildMusicSettings> settings = Database.getAll(GuildMusicSettings.class)
                .stream().filter(guildMusicSettings -> guildMusicSettings.getGuild() == guildId)
                .limit(1).toList();

        if (settings.isEmpty()) {
            new GuildMusicSettings(guildId, 100, "en");
            return ResourceBundle.getBundle("lang", Locale.ENGLISH);
        } else return ResourceBundle.getBundle("lang", Locale.of(settings.get(0).getLang()));
    }

    public static void updateGuildCommands() {
        logger.info("Updating guild commands!");
        HashSet<ISlashCommand> slashCommands = CommandAdapter.getInstance().getSlashcommands();

        jda.getGuilds().forEach(guild -> {
            Set<SlashCommandData> a = slashCommands.stream()
                    .map(iSlashCommand ->
                            Commands.slash(iSlashCommand.getName().toLowerCase(), iSlashCommand.getHelp())
                                    .addOptions(iSlashCommand.getOptionData())
                                    .addSubcommands(iSlashCommand.getSubcommandDatas())).collect(Collectors.toSet());
            guild.updateCommands().addCommands(a).queue();
        });
    }

    /**
     * Add a {@link Runnable} to the {@link Set} of {@link Runnable Runnables} to execute when {@link JDA#awaitReady() JDA is ready}.
     *
     * @param runnable A {@link Runnable} to execute when JDA is ready.
     */
    public static void runWhenReady(Runnable runnable) {
        runnables.add(runnable);
    }

    /**
     * Run queued {@link Runnable Runnables}.
     *
     * @see #runWhenReady(Runnable)
     */
    private static void runWhenReady() {
        runnables.forEach(Runnable::run);
    }

    private static String getToken() {
        String token = "";
        if (token.isEmpty()) {
            //Config
            Optional<String> opToken = Config.CONFIG.getProperty("bot.token");
            if (opToken.isPresent() && !opToken.get().isEmpty()) {
                logger.info("Using config as the token provider.");
                return opToken.get();
            } else {
                logger.warn("No token found 😕.");
            }
        }
        return token;
    }

    /**
     * Checks whether the application is running in test mode.
     * Test mode is enabled with the command line argument "-Dtest".
     *
     * @return {@code true} if the application is running in test mode, {@code false} otherwise.
     */
    public static boolean isTestMode() {
        List<String> args = Arrays.asList(botArgs);
        Optional<String> optional = args.stream().filter(arg -> arg.replaceFirst("-", "").equalsIgnoreCase("Dtest")).findFirst();
        return optional.isPresent();
    }
}
