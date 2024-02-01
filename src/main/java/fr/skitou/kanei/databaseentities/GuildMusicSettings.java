/*
 * Copyright (c) Skitou 2024.
 */

package fr.skitou.kanei.databaseentities;

import fr.skitou.botcore.hibernate.Database;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "guildMusicSettings")
public class GuildMusicSettings {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private long guild;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int volume;

    public GuildMusicSettings(long guild, int volume) {
        this.guild = guild;
        this.volume = volume;

        Database.saveOrUpdate(this);
    }

    protected GuildMusicSettings() {
    }
}
