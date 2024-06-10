package fr.skitou.kanei.databaseentities;

import fr.skitou.botcore.hibernate.Database;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Entity
@Table(name = "changelogEntity")
public class ChangelogEntity {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private String version;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String content;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime time;

    public ChangelogEntity(String version, String content) {
        this.version = version;
        this.content = content;
        this.time = LocalDateTime.now();
        Database.saveOrUpdate(this);
    }

    protected ChangelogEntity() {
    }
}
