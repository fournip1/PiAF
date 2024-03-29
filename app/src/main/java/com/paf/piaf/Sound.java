package com.paf.piaf;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "sounds")
public class Sound {
    public final static String TYPE_FIELD_NAME = "type";
    public final static String LEVEL_FIELD_NAME = "level";
    public final static String PATH_FIELD_NAME = "path";
    public final static String BIRD_FIELD_NAME = "bird";
    public final static String CREDIT_FIELD_NAME = "credit";
    public final static String DEFAULT_CREDIT = "P.A. Fournié";


    // id is generated by the database and set on the object automagically
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, columnName = TYPE_FIELD_NAME)
    private String type;
    @DatabaseField(unique=true,  canBeNull = false, columnName = PATH_FIELD_NAME)
    private String path;
    @DatabaseField(canBeNull = true, columnName = CREDIT_FIELD_NAME)
    private String credit;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = LEVEL_FIELD_NAME)
    private Level level;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = BIRD_FIELD_NAME)
    private Bird bird;

    @ForeignCollectionField
    private ForeignCollection<Score> scores;

    Sound() {
        // needed by ormlite
    }

    public Sound(String type, String path, Level level, Bird bird, String credit) {
        this.level = level;
        this.type = type;
        this.path = path;
        this.bird = bird;
        this.credit = credit;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCredit() {
        return credit;
    }

    public String getBasePath() { return path.substring(0,path.lastIndexOf("."));}

    public Level getLevel() {
        return level;
    }

    public Bird getBird() {
        return bird;
    }

    public ForeignCollection<Score> getScores() {
        return scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sound)) return false;
        Sound sound = (Sound) o;
        return id == sound.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return bird.getFrench() + " (" + type + ")";
    }
}
