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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.sentry.Sentry;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

@Getter
public enum Config {
    CONFIG;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String configFileName = "config.json";
    private final File configFile = new File("data", configFileName);

    @Getter
    private JsonObject rootJson;
    @Getter
    private JsonObject defaultJson;

    /**
     * Initiate configuration (parse JSON, copy from source if needed, etc...)
     */
    Config() {

        URL urlSourceJson = ClassLoader.getSystemResource(configFileName);

        try {
            if (!configFile.exists()) { // If root file not found, copy from source
                logger.warn("Config file not found, copying from sources...");
                Files.copy(urlSourceJson.openStream(), configFile.toPath());
            }

            try (InputStream stream = urlSourceJson.openStream()) {
                defaultJson = gson.fromJson(new String(stream.readAllBytes()), JsonObject.class);

                BufferedReader reader = new BufferedReader(new FileReader(configFile.toString()));
                rootJson = gson.fromJson(reader, JsonObject.class);
                reader.close();
            }
        } catch (IOException e) {
            logger.error("Config threw while loading a {}: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            Sentry.captureException(e);
            e.printStackTrace();
        }
    }

    /**
     * Provide shortcuts for frequently-used properties.
     */
    static String getGuildIdOrDefault(String guildConfigName, String defaultValue) {
        final Optional<String> guildIdOptional = Optional.of(CONFIG.getPropertyElement("guild").orElseThrow().getAsJsonObject().get(guildConfigName).getAsString());
        return guildIdOptional.orElse(defaultValue);
    }

    /**
     * Get the raw property
     *
     * @param key Property key to get
     * @return The JsonElement if found
     */
    @Nullable
    private JsonElement getRawProperty(String key) {
        return rootJson.get(key);
    }

    /**
     * Get the raw property from source
     *
     * @param key Property key to get
     * @return The JsonElement if found
     */
    @Nullable
    private JsonElement getRawDefaultProperty(String key) {
        return defaultJson.get(key);
    }

    public Optional<String> getProperty(@NotNull String key) {
        JsonElement element = getRawProperty(key);
        return Optional.ofNullable(element == null ? null : element.getAsString());
    }

    /**
     * Get property from config.js file or source file if not possible
     *
     * @param key Property key to get
     * @return The JsonElement if found or the default one
     */
    public String getPropertyOrDefault(@NotNull String key) {
        Optional<String> optional = getProperty(key);
        return optional.orElseGet(() -> Objects.requireNonNull(getRawDefaultProperty(key)).getAsString());
    }

    public Optional<JsonElement> getPropertyElement(@NotNull String key) {
        return Optional.ofNullable(getRawProperty(key));
    }

    public void setProperty(String key, String val) {
        rootJson.addProperty(key, val);
    }

    /**
     * Save the rootJson to the root config.json
     */
    public void saveModifications() throws IOException {
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(gson.toJson(rootJson));
            writer.flush();
        }
    }
}
