package com.paf.piaf;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "levels")
public class Level {
    // id is generated by the database and set on the object automagically
    public final static String IMAGE_PATH_FIELD_NAME = "image_path";
    public final static String FRENCH_FIELD_NAME = "french";

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, columnName = FRENCH_FIELD_NAME)
    private String french;
    @DatabaseField(canBeNull = false, columnName = IMAGE_PATH_FIELD_NAME)
    private String imagePath;

    @ForeignCollectionField
    private ForeignCollection<Sound> sounds;

    Level() {
        // needed by ormlite
    }

    public Level(String french, String imagePath) {
        this.french = french;
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public String getFrench() {
        return french;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ForeignCollection<Sound> getSounds() {
        return sounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Level)) return false;
        Level level = (Level) o;
        return id == level.id;
    }

    public String getImageBasePath() {
            return imagePath.substring(0, imagePath.lastIndexOf("."));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return french;
    }
}
