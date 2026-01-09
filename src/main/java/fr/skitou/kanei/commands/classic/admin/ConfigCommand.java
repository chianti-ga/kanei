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

package fr.skitou.kanei.commands.classic.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import fr.skitou.kanei.commands.classic.AbstractCommand;
import fr.skitou.kanei.commands.classic.CommandReceivedEvent;
import fr.skitou.kanei.core.BotInstance;
import fr.skitou.kanei.core.Config;
import fr.skitou.kanei.utils.IsSenderAllowed;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * The configuration command, edit the json config file from bot command
 *
 * @author RyFax
 */
public class ConfigCommand extends AbstractCommand {

    private static final Config conf = Config.CONFIG;
    private final Logger logger = LoggerFactory.getLogger(ConfigCommand.class);

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        if (event.getArgs().isEmpty()) {
            event.getChannel().sendMessage(getHelp()).queue();
            return;
        }

        switch (event.getArgs().get(0)) {
            case "list" -> listConfig(event);
            case "edit" -> {
                if (event.getArgs().size() >= 3) editConfig(event);
                else event.getChannel().sendMessage(PREFIX + "config edit [key] [value]").queue();
            }
            case "add" -> {
                if (event.getArgs().size() >= 3) addConfig(event);
                else event.getChannel().sendMessage(PREFIX + "config add [key] [value]").queue();
            }
            case "remove" -> {
                if (event.getArgs().size() >= 3) removeConfig(event);
                else event.getChannel().sendMessage(PREFIX + "config remove [key] [value]").queue();
            }
            default -> event.getChannel().sendMessage(getHelp()).queue();
        }
    }

    @Override
    public @NotNull String getCommand() {
        return "config";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return Collections.singletonList("conf");
    }

    @Override
    public @NotNull String getName() {
        return "Configuration";
    }

    @Override
    public @NotNull String getHelp() {
        return PREFIX + "config (list|edit|add|remove) [*key*] [*value*]";
    }

    @Override
    public IsSenderAllowed isSenderAllowed() {
        return IsSenderAllowed.BotAdmin;
    }

    /**
     * Removing from a list
     */
    private void removeConfig(CommandReceivedEvent event) {
        Setting setting = new Setting(event);

        Optional<JsonElement> property = conf.getPropertyElement(setting.getKey());

        if (property.isEmpty()) {
            event.getChannel().sendMessage("Key not existing").queue();
            return;
        } else if (!property.get().isJsonArray()) {
            event.getChannel().sendMessage("To edit a single parameter please use " + PREFIX + "config edit [key] [value]").queue();
            return;
        }

        boolean found = false;
        JsonArray jsonArray = ((JsonArray) property.get());
        for (JsonElement el : jsonArray) {
            if (el.isJsonObject() || el.isJsonArray()) continue;

            if (el.getAsString().equals(setting.getValue())) {
                jsonArray.remove(el);
                found = true;
                break;
            }
        }

        if (found) {
            boolean saved = save(event);
            if (saved) {
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(0x1efa88)
                        .setTitle(getName())
                        .setDescription("`" + setting.getKey() + "` removed `" + setting.getValue() + "`")
                        .build();

                event.getChannel().sendMessageEmbeds(embed).queue();
            }
        } else {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(0xfc5444)
                    .setTitle(getName())
                    .setDescription("`" + setting.getKey() + "` can't removed `" + setting.getValue() + "`: not found.")
                    .build();

            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    /**
     * Adding to a list
     */
    private void addConfig(CommandReceivedEvent event) {
        Setting setting = new Setting(event);

        Optional<JsonElement> property = conf.getPropertyElement(setting.getKey());

        if (property.isEmpty()) {
            event.getChannel().sendMessage("Key not existing").queue();
            return;
        } else if (!property.get().isJsonArray()) {
            event.getChannel().sendMessage("To edit a single parameter please use " + PREFIX + "config edit [key] [value]").queue();
            return;
        }

        ((JsonArray) property.get()).add(setting.getValue());

        boolean saved = save(event);
        if (saved) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(0x1efa88)
                    .setTitle(getName())
                    .setDescription("`" + setting.getKey() + "` added `" + setting.getValue() + "`")
                    .build();

            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    /**
     * Edit a simple value
     */
    private void editConfig(CommandReceivedEvent event) {
        Setting setting = new Setting(event);

        Optional<JsonElement> property = conf.getPropertyElement(setting.getKey());

        if (property.isEmpty()) {
            event.getChannel().sendMessage("Key not existing").queue();
            return;
        } else if (property.get().isJsonArray()) {
            event.getChannel().sendMessage("To edit a list please use " + PREFIX + "config add/remove [key] [value]").queue();
            return;
        }

        conf.setProperty(setting.getKey(), setting.getValue());

        boolean saved = save(event);
        if (saved) {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(0x1efa88)
                    .setTitle(getName())
                    .setDescription("`" + setting.getKey() + "` set to `" + setting.getValue() + "`")
                    .build();

            event.getChannel().sendMessageEmbeds(embed).queue();
        }
    }

    /**
     * Send to the channel the current configuration
     */
    private void listConfig(CommandReceivedEvent event) {
        String json = applySecret(conf, conf.getGson().toJson(conf.getRootJson()));

        MessageEmbed embed = new EmbedBuilder()
                .setColor(0x3783ed)
                .setTitle(getName())
                .setDescription("List of properties in config.json:\n```JSON\n" + json + "```")
                .build();

        event.getChannel().sendMessageEmbeds(embed).queue();
    }

    /**
     * Try to save the config
     *
     * @return true if file saved successfully, and false if not
     */
    private boolean save(CommandReceivedEvent event) {
        try {
            conf.saveModifications();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("An internal failure occurred. The config was not saved.").queue();

            logger.error("Config threw while saving, {}: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            Sentry.captureException(e);
            return false;
        }
    }

    /**
     * Security : Replacing secret words like TOKEN to [SECRET]
     */
    private String applySecret(Config config, String source) {
        // Just to be sure there is no token in string
        List<String> secrets = List.of(
                BotInstance.getJda().getToken(),
                config.getPropertyOrDefault("bot.token"),
                config.getPropertyOrDefault("loggerUrl"),
                config.getPropertyOrDefault("sentryDns"),
                config.getPropertyOrDefault("spotify.id"),
                config.getPropertyOrDefault("spotify.secret")
        );

        for (String secret : secrets) source = source.replaceAll(secret, "[SECRET]");
        return source;
    }

    /**
     * Split arguments in command received to a key and value
     */
    @Getter
    private static class Setting {
        private final String key;
        private final String value;

        public Setting(CommandReceivedEvent event) {
            StringJoiner sj = new StringJoiner(" ");

            event.getArgs().stream().skip(2).forEach(sj::add);

            this.key = event.getArgs().get(1);
            this.value = sj.toString();
        }

    }

}
