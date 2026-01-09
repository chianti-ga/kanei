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

package fr.skitou.kanei.utils;

import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * A simple file cache implementation to store and manage cached files.
 * This cache periodically scans the cache folder for changes and deletes files that are no longer required.
 *
 * @author Skitou
 */
public class FilesCache {

    /**
     * The logger instance used for logging messages related to the file cache.
     */
    protected static final Logger logger = LoggerFactory.getLogger(FilesCache.class);

    /**
     * The executor service responsible for scheduling periodic cache scans.
     */
    private final ScheduledExecutorService scanExecutor = Executors.newScheduledThreadPool(1);

    /**
     * The executor service responsible for scheduling periodic deletion of disposable files.
     */
    private final ScheduledExecutorService deleteExecutor = Executors.newScheduledThreadPool(1);

    /**
     * The folder where cached files are stored.
     */
    @Getter
    private final File cacheFolder = new File("data/cache/");

    /**
     * The list of cached files currently managed by the cache.
     */
    private List<File> cachedFiles = new ArrayList<>();

    /**
     * The list of files to ignore during cache cleanup.
     */
    @Getter
    @Setter
    private List<String> ignoreList = new ArrayList<>();

    /**
     * Constructs a new instance of the FilesCache class.
     * It initializes the cache folder, performs an initial scan, and schedules periodic cache scans and file cleanup.
     */
    public FilesCache() {
        if (!cacheFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheFolder.mkdirs();
        }
        scan();
        scanExecutor.scheduleWithFixedDelay(this::scan, 0, 5, TimeUnit.MINUTES);
        deleteExecutor.scheduleWithFixedDelay(this::deleteDisposableFiles, 0, 15, TimeUnit.MINUTES);
    }

    /**
     * Scans the cache folder to update the list of cached files and updates the ignore list.
     * This method is called during the initialization and periodic scans.
     */
    private void scan() {
        try (Stream<Path> walk = Files.walk(cacheFolder.toPath())) {
            cachedFiles = walk.filter(Files::isRegularFile).map(Path::toFile).toList();
            cachedFiles.forEach(File::deleteOnExit); // Delete them even if the bot crashes or suddenly stops
            //noinspection ResultOfMethodCallIgnored
            ignoreList.forEach(String::toLowerCase);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes files in the cache that are eligible for deletion.
     * Files that exist in the cache and are not in the ignore list will be deleted.
     * This method is called periodically for cache cleanup.
     */
    private void deleteDisposableFiles() {
        AtomicInteger fileCount = new AtomicInteger();
        logger.info("Performing cache cleanup...");
        cachedFiles.forEach(file -> {
            if (file.exists() && !ignoreList.contains(file.getName().toLowerCase())) {
                fileCount.getAndIncrement();
                try {
                    Files.delete(file.toPath());
                } catch (IOException e) {
                    Sentry.captureException(e);
                    throw new RuntimeException(e);
                }
            }
        });

        logger.info("{} cached files deleted.", fileCount);
        fileCount.set(0);
    }
}