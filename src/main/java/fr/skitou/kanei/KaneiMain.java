package fr.skitou.kanei;

import fr.skitou.botcore.core.BotInstance;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Set;

public class KaneiMain {
    public static BotInstance botInstance;
    @Getter
    private static String version;
    @Getter
    private static final ResourceBundle langBundle = ResourceBundle.getBundle("lang");

    public static void main(String[] args) {
        try {
            version = Files.readAllLines(Path.of(ClassLoader.getSystemResource("kaneiversion.txt").toURI())).get(0);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

        BotInstance.BotInstanceBuilder builder = new BotInstance.BotInstanceBuilder(args);
        builder.setCMDPackage("fr.skitou.kanei.classicCommands")
                .setSlashCMDPackage("fr.skitou.kanei.slashCommands")
                .setSubsystemPackage("fr.skitou.kanei.subsystems")
                .setEntitiesPackagePackage("fr.skitou.kanei.databaseentities")
                .setEnabledintents(Set.of(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS))
                .setEnabledcacheFlags(Set.of(CacheFlag.VOICE_STATE))
                .setDisabledcacheFlags(Collections.singleton(CacheFlag.MEMBER_OVERRIDES))
                .setDisabledintents(Set.of());
        botInstance = builder.build();
        BotInstance.getJda().getPresence().setActivity(Activity.listening("/help"));
    }
}
