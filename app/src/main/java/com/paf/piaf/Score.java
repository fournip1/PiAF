package com.paf.piaf;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "scores")
public class Score {
    public final static Long SCORES_DEPTH = 20L;
    public final static float VALIDATION_PERCENTAGE = 90;
    public final static String DATE_MILLIS_FIELD_NAME = "date_millis";
    public final static String SOUND_FIELD_NAME = "sound";
    public final static String SCORE_FIELD_NAME = "score";

//    0 means unanswered, 1 means correct answer and -1 means bad answer
    final static int[] SCORES = {0,-1,1};

    @DatabaseField(id = true, canBeNull = false, columnName = DATE_MILLIS_FIELD_NAME)
    long dateMillis;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = SOUND_FIELD_NAME)
    private Sound sound;
    @DatabaseField(canBeNull = false, columnName = SCORE_FIELD_NAME)
    private int score;


    Score() {
        // needed by ormlite
    }

    public Score(Sound sound, int score) {
        this.score = score;
        this.sound = sound;
        this.dateMillis =  System.currentTimeMillis();
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public Sound getSound() {
        return sound;
    }

    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;
        Score score = (Score) o;
        return dateMillis == score.dateMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateMillis);
    }
}
