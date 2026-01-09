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

package fr.skitou.kanei.subsystems;

import fr.skitou.kanei.utils.ReflectionUtils;
import io.sentry.Sentry;
import lombok.Getter;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.StringJoiner;

@Getter
public class SubsystemAdapter implements EventListener {
    @Getter
    private static final HashSet<fr.skitou.kanei.subsystems.ISubsystem> subsystems = new HashSet<>();
    private static final Logger logger = LoggerFactory.getLogger(SubsystemAdapter.class);
    private static SubsystemAdapter instance;

    public SubsystemAdapter() {
        subsystems.addAll(ReflectionUtils.getSubTypesInstance(fr.skitou.kanei.subsystems.ISubsystem.class));
        StringJoiner subsystemStringJoiner = new StringJoiner("\n");
        subsystems.forEach(subsystem -> subsystemStringJoiner.add(subsystem.getName()));
        logger.info("Detected subsystems: \n {}", subsystemStringJoiner);
        StringJoiner enabledSubsystemStringJoiner = new StringJoiner("\n");
        subsystems.stream().filter(fr.skitou.kanei.subsystems.ISubsystem::isEnabled).forEach(subsystem -> enabledSubsystemStringJoiner.add(subsystem.getName()));
        logger.info("Enabled subsystems: \n, {}", enabledSubsystemStringJoiner);
    }

    public static SubsystemAdapter getInstance() {
        if (instance == null) instance = new SubsystemAdapter();
        return instance;
    }

    /**
     * Dispatch {@link GenericEvent GenericEvents} to all {@link fr.skitou.kanei.subsystems.ISubsystem#isEnabled() enabled} {@link fr.skitou.kanei.subsystems.ISubsystem ISubsystems}.
     */
    @Override
    public void onEvent(@NotNull GenericEvent event) {
        subsystems.stream().filter(ISubsystem::isEnabled).forEach(subsystem -> {
            try {
                subsystem.onEvent(event);
            } catch (Exception exception) {
                logger.error("Subsystem {} threw a {}: {}", subsystem.getName(),
                        exception.getClass().getSimpleName(), exception.getMessage());

                exception.printStackTrace();

                Sentry.captureException(exception);
            }
        });
    }
}
