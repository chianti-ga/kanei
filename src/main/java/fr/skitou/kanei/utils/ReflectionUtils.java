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
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtils {
    public static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    private ReflectionUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Detect all subtypes of {@code superClass} in the {@link fr.skitou.kanei.utils.Children#targetPackages()} package (and sub-packages), and get an instance for each of them.
     * This method internally calls {@link #getSubTypesInstance(Class, String[]) getSubTypesInstance(superClass, Children#targetPackages()}
     *
     * @param <T>        A subtype of {@code superClass}, using inheritance.
     * @param superClass A class <b>ANNOTATED</b> by {@link fr.skitou.kanei.utils.Children} which will be searched for subtypes.
     * @return An {@code HashSet<T>} containing an instance for each detected subtypes of {@code superClass}.
     * @throws InvalidParameterException If the {@code superClass} is not annotated by {@link fr.skitou.kanei.utils.Children}.
     */
    public static <T> HashSet<T> getSubTypesInstance(Class<T> superClass) {
        if (!(superClass.isAnnotationPresent(fr.skitou.kanei.utils.Children.class))) {
            throw new InvalidParameterException("superClass MUST be annotated with @Children. See the javadoc.");
        }
        return getSubTypesInstance(superClass, superClass.getAnnotation(Children.class).targetPackages());
    }

    private static <T> HashSet<T> getSubTypesInstance(Class<T> superClass, String[] targetPackages) {
        HashSet<T> subTypes = new HashSet<>();
        // Search all classes extending from T, but exclude Interfaces and Abstract classes.
        Set<Class<? extends T>> subTypesClasses = new HashSet<>(new Reflections(new ConfigurationBuilder()
                .setUrls(Stream.of(targetPackages).flatMap(targetPackage -> ClasspathHelper.forPackage(targetPackage).stream()).collect(Collectors.toSet()))
                .setScanners(Scanners.SubTypes))
                .getSubTypesOf(superClass)).parallelStream()
                .filter(aClass -> !(Modifier.isAbstract(aClass.getModifiers()) || Modifier.isInterface(aClass.getModifiers())))
                .collect(Collectors.toSet());

        // We get an instance from each class.
        subTypesClasses.forEach(subTypeClass -> {
            try {
                subTypeClass.getConstructor().setAccessible(true);
                T subType = subTypeClass.getConstructor().newInstance();
                // Check if the instance is already present before adding.
                if (!subTypes.contains(subType)) {
                    subTypes.add(subType);
                    // Debug
                    logger.debug(subType.toString());
                }
            } catch (ExceptionInInitializerError exceptionInInitializerError) {
                logger.error("An exception occurred during the initialization of class " + subTypeClass.getName() + ".");
                exceptionInInitializerError.getCause().printStackTrace();
            } catch (InstantiationException | IllegalAccessException | NullPointerException | NoSuchMethodException |
                     InvocationTargetException e) {
                logger.error("An exception occurred during the initialization of class " + subTypeClass.getName() + ".");
                e.printStackTrace();
            }
        });
        return subTypes;
    }

    public static <T> HashSet<T> getSubTypesInstance(Class<T> superClass, String targetPackages) {
        HashSet<T> subTypes = new HashSet<>();
        //Search all classes extending from T, but exclude Interfaces and Abstract classes.
        Set<Class<? extends T>> subTypesClasses = new HashSet<>(new Reflections(new ConfigurationBuilder()
                .setUrls(Stream.of(targetPackages).flatMap(targetPackage -> ClasspathHelper.forPackage(targetPackage).stream()).collect(Collectors.toSet()))
                .setScanners(Scanners.SubTypes))
                .getSubTypesOf(superClass)).parallelStream()
                .filter(aClass -> !(Modifier.isAbstract(aClass.getModifiers()) || Modifier.isInterface(aClass.getModifiers())))
                .collect(Collectors.toSet());

        //We get an instance from each class.
        subTypesClasses.forEach(subTypeClass -> {
            try {
                subTypeClass.getConstructor().setAccessible(true);
                T subType = subTypeClass.getConstructor().newInstance();
                subTypes.add(subType);
                //Debug
                logger.debug(subType.toString());
            } catch (ExceptionInInitializerError | InstantiationException | IllegalAccessException |
                     NullPointerException | NoSuchMethodException |
                     InvocationTargetException exceptionInInitializerError) {
                logger.error("An exception occurred during the initialisation of class " + subTypeClass.getName() + ".");
                Sentry.captureException(exceptionInInitializerError);
                exceptionInInitializerError.printStackTrace();
            }
        });
        return subTypes;
    }

    public static <T> Set<Class<? extends T>> getSubTypes(Class<T> superClass, String targetPackage) {
        //Search all classes extending from T, but exclude Interfaces and Abstract classes.
        return new HashSet<>(new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(targetPackage))
                .setScanners(Scanners.SubTypes))
                .getSubTypesOf(superClass)).parallelStream()
                .filter(aClass -> !(Modifier.isAbstract(aClass.getModifiers()) || Modifier.isInterface(aClass.getModifiers())))
                .collect(Collectors.toSet());
    }

    /**
     * Find all classes annotated with a given annotation, in the given package.
     *
     * @param annotation The annotation.
     * @param pkg        The package to scan.
     * @return A set of classes.
     */
    public static Set<Class<?>> findAnnotations(Class<? extends Annotation> annotation, String pkg) {
        return new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(pkg))
                .filterInputsBy(new FilterBuilder().includePackage(pkg)))
                //.setScanners(new TypeAnnotationsScanner()))
                .getTypesAnnotatedWith(annotation);
    }

    public static <T, E> Set<Class<? extends T>> getSubClasses(Class<T> superClass, Class<E> parentClass) {
        //Search all classes extending from T, but exclude Interfaces and Abstract classes.

        logger.debug(ClasspathHelper.forClass(parentClass, parentClass.getClassLoader()).toString());
        return new HashSet<>(new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClass(parentClass))
                .setScanners(Scanners.SubTypes))
                .getSubTypesOf(superClass)).parallelStream()
                .filter(aClass -> !(Modifier.isAbstract(aClass.getModifiers()) || Modifier.isInterface(aClass.getModifiers())))
                .collect(Collectors.toSet());
    }


    public static <T> T getInstanceFromClass(@NotNull Class<? extends T> classToInstanciate) {
        //We get an instance from each class.
        T instance;
        try {
            Objects.requireNonNull(classToInstanciate).getConstructor().setAccessible(true);
            instance = classToInstanciate.getConstructor().newInstance();
            logger.debug(instance.toString());
        } catch (ExceptionInInitializerError exceptionInInitializerError) {
            logger.error("An exception occurred during the initialisation of class " + classToInstanciate.getName() + ".");
            exceptionInInitializerError.getCause().printStackTrace();
            return null;
        } catch (InstantiationException | IllegalAccessException | NullPointerException | NoSuchMethodException |
                 InvocationTargetException e) {
            logger.error("An exception occurred during the initialisation of class " + classToInstanciate.getName() + ".");
            e.printStackTrace();
            return null;
        }

        return instance;
    }

}
