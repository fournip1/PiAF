package com.paf.piaf;


import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;

import java.util.Objects;

@DatabaseTable(tableName = "birds")
public class Bird {
    public final static String LATIN_FIELD_NAME = "latin";
    public final static String FRENCH_FIELD_NAME = "french";
    public final static String IMAGE_PATH_FIELD_NAME = "image_path";
    public final static String AUDIO_DESCRIPTION_PATH_FIELD_NAME = "audio_description_path";
    public final static String URL_FIELD_NAME = "url";


    // id is generated by the database and set on the object automagically
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(unique=true, canBeNull = false, columnName = LATIN_FIELD_NAME)
    private String latin;
    @DatabaseField(canBeNull = false, columnName = FRENCH_FIELD_NAME)
    private String french;
    @DatabaseField(canBeNull = true, columnName = IMAGE_PATH_FIELD_NAME)
    private String imagePath;
    @DatabaseField(canBeNull = true, columnName = AUDIO_DESCRIPTION_PATH_FIELD_NAME)
    private String audioDescriptionPath;
    @DatabaseField(canBeNull = true, columnName = URL_FIELD_NAME)
    private String url;

    @ForeignCollectionField
    private ForeignCollection<Sound> sounds;

    Bird() {
        // needed by ormlite
    }

    public Bird(String latin, String french, String imagePath, String audioDescriptionPath, String url) {
        this.latin = latin;
        this.french = french;
        this.imagePath = imagePath;
        this.audioDescriptionPath = audioDescriptionPath;
        this.url = url;
    }

    public String getLatin() {
        return latin;
    }

    public String getFrench() {
        return french;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getAudioDescriptionPath() {
        return audioDescriptionPath;
    }

    public String getUrl() {
        return url;
    }

    public ForeignCollection<Sound> getSounds() {
        return sounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bird)) return false;
        Bird bird = (Bird) o;
        return id == bird.id;
    }

    public String getImageBasePath() {
        if (imagePath==null) {
            return "";
        } else if (imagePath.equals("")) {
            return "";
        } else {
            // Log.i(Bird.class.getName(),"Image path: " + imagePath);
            return imagePath.substring(0, imagePath.lastIndexOf("."));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return french + " (" + latin + ")";
    }


}
