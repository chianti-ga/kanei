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

package fr.skitou.kanei.hibernate;

import fr.skitou.kanei.core.Config;
import fr.skitou.kanei.utils.ReflectionUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * Hibernate helper
 * <br/>
 *
 * @author uku3lig
 */
public class Database {

    protected static final Logger logger = LoggerFactory.getLogger(Database.class);
    private static final Random random = new Random();
    private static SessionFactory factory;

    private Database() {
        throw new IllegalStateException("Utility class");
    }

    public static SessionFactory getFactory() {
        if (factory == null) init();
        return factory;
    }

    private static void init() {
        try {
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");

            ReflectionUtils.findAnnotations(Entity.class, "fr.skitou.kanei.hibernate.entities").forEach(cfg::addAnnotatedClass);

            Config.CONFIG.getProperty("dbPass").ifPresent(pwd -> cfg.setProperty("hibernate.connection.password", pwd));

            final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
            factory = cfg.buildSessionFactory(registry);
        } catch (HibernateException | NullPointerException e) {
            e.printStackTrace();
            logger.error("Error {}: {}", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * Tests if an object is a hibernate entity.
     *
     * @param o The object.
     * @return <code>true</code> if the <code>o</code> is an entity
     */
    private static boolean isEntity(Object o) {
        return isEntity(o.getClass());
    }

    /**
     * @see #isEntity(Object)
     */
    private static boolean isEntity(Class<?> klass) {
        return klass.isAnnotationPresent(Entity.class);
    }

    /**
     * Saves (or updates) entities in the database.
     *
     * @param <T>      The type of the entity, to ensure they all are entities with only one check.
     * @param entities The entities to save or update.
     */
    @SafeVarargs
    public static <T> void saveOrUpdate(T... entities) {
        if (entities.length == 0 || !isEntity(entities[0])) return;
        try (Session s = getFactory().openSession()) {
            s.beginTransaction();
            Arrays.stream(entities).forEach(s::merge);
            s.getTransaction().commit();
        } catch (Exception e) {
            logger.error("An error happened while trying to saveOrUpdate a {}: {}",
                    entities[0].getClass().getSimpleName(), e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    @SafeVarargs
    public static <T> boolean delete(T... entities) {
        if (entities.length == 0 || !isEntity(entities[0])) return false;
        try (Session s = getFactory().openSession()) {
            s.beginTransaction();
            Stream.of(entities).forEach(s::remove);
            s.getTransaction().commit();
            return true;
        } catch (Exception e) {
            logger.error("An error happened while trying to delete a {}: {}",
                    entities[0].getClass().getSimpleName(), e.getClass().getSimpleName());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds an entity in the database with its type and id.
     *
     * @param klass The class of the entity.
     * @param id    The id of the entity.
     * @param <T>   The type of the entity.
     * @return An optional containing the object found in database.
     */
    public static <T> Optional<T> getById(Class<T> klass, Serializable id) {
        if (!isEntity(klass)) return Optional.empty();
        try (Session s = getFactory().openSession()) {
            s.beginTransaction();
            Optional<T> o = Optional.ofNullable(s.find(klass, id));
            s.getTransaction().commit();
            return o;
        } catch (Exception e) {
            logger.error("An error happened while trying to get a {}: {}", klass.getSimpleName(), e.getClass().getSimpleName());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Gets all the entities in a table. <br>
     * You should use {@link #getById(Class, Serializable)} to get a specific entity, instead of using streams.
     *
     * @param klass The class linked to the table in the database.
     * @param <T>   The type of the class.
     * @return A collection of the found entities, can be empty.
     */
    public static <T> Collection<T> getAll(Class<T> klass) {
        if (!isEntity(klass)) return Collections.emptySet();
        try (Session s = getFactory().openSession()) {
            return s.createQuery("SELECT a FROM " + klass.getSimpleName() + " a", klass).getResultList();
        }
    }

    /**
     * Gets the number of rows in the table linked to the given entity class.
     *
     * @param klass The entity class.
     * @param where The conditions.
     * @param <T>   The type of the entity.
     * @return The amount of rows.
     */
    @SafeVarargs
    public static <T> long count(Class<T> klass, BiFunction<CriteriaBuilder, Root<T>, Predicate>... where) {
        if (!isEntity(klass)) return 0;
        try (Session s = getFactory().openSession()) {
            CriteriaBuilder builder = s.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(long.class);
            Predicate[] wheres = Arrays.stream(where)
                    .map(f -> f.apply(builder, query.from(klass)))
                    .toArray(Predicate[]::new);
            query.select(builder.count(query.from(klass))).where(wheres);
            return s.createQuery(query).getSingleResult();
        }
    }

    public static boolean isEmpty(Class<?> klass) {
        return !isEntity(klass) || count(klass) == 0;
    }

    public static <T> Optional<T> getRand(Class<T> klass) {
        if (!isEntity(klass) || isEmpty(klass)) return Optional.empty();
        int index = random.nextInt((int) Database.count(klass));
        return getAll(klass).stream().skip(index).findFirst();
    }
}
