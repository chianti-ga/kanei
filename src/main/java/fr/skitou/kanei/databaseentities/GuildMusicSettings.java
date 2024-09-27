/*
 * Copyright (c) Skitou 2024.
 */

package fr.skitou.kanei.databaseentities;

import fr.skitou.botcore.hibernate.Database;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@Table(name = "guildMusicSettings")
public class GuildMusicSettings {
    @Id
    @Setter()
    private long guild;

    @Getter
    @Setter
    private int volume;

    @Getter
    @Setter
    private String lang;

    public GuildMusicSettings(long guild, int volume, String lang) {
        this.guild = guild;
        this.volume = volume;
        this.lang = lang;

        Database.saveOrUpdate(this);
    }

    protected GuildMusicSettings() {
    }
}
