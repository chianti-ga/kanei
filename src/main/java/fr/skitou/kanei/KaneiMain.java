/*
 * Copyright (c) Skitou 2024.
 */

package fr.skitou.kanei;

import fr.skitou.botcore.core.BotInstance;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

public class KaneiMain {
    @Getter
    private static final ResourceBundle langBundle = ResourceBundle.getBundle("lang");
    @Getter
    private static final String version = KaneiMain.class.getPackage().getImplementationVersion();
    public static BotInstance botInstance;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BotInstance.BotInstanceBuilder builder = new BotInstance.BotInstanceBuilder(args);
        builder.setCMDPackage("fr.skitou.kanei.commands.classic")
                .setSlashCMDPackage("fr.skitou.kanei.commands.slash")
                .setSubsystemPackage("fr.skitou.kanei.subsystems")
                .setEntitiesPackagePackage("fr.skitou.kanei.databaseentities")
                .setEnabledintents(Set.of(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES))
                .setEnabledcacheFlags(Set.of(CacheFlag.VOICE_STATE))
                .setDisabledcacheFlags(Collections.singleton(CacheFlag.MEMBER_OVERRIDES))
                .setDisabledintents(Set.of())
                .setActivity(Activity.listening("some music!"));
        botInstance = builder.build();
        long end = System.currentTimeMillis();
        BotInstance.logger.info("Start time : {}ms", (end - start));
    }
}

