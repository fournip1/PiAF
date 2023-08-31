package com.paf.piaf;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "user")
public class User {
    public final static String QCM_FIELD_NAME = "qcm";
    public final static String LEVEL_FIELD_NAME = "level";
    public final static String NB_QUESTIONS_FIELD_NAME = "nb_questions";
    public final static String NB_CHOICES_FIELD_NAME = "nb_choices";
    public final static String LAST_VALIDATION_FIELD_NAME = "last_validation_timestamp";


    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, columnName = QCM_FIELD_NAME)
    private boolean QCM;
    // this is the difficulty level
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = LEVEL_FIELD_NAME)
    private Level level;
    // this is the number of questions
    @DatabaseField(canBeNull = false, columnName = NB_QUESTIONS_FIELD_NAME)
    private int nbQuestions;
    // this is the number of choices
    @DatabaseField(canBeNull = false, columnName = NB_CHOICES_FIELD_NAME)
    private int nbChoices;

    // this is a timestamp used to avoid a too quick validation
    @DatabaseField(canBeNull = false, columnName = LAST_VALIDATION_FIELD_NAME)
    private long lastValidationTimestamp;


    User() {
        // needed by ormlite
    }

    public User(boolean QCM, Level level, int nbQuestions, int nbChoices) {
        this.level = level;
        this.QCM = QCM;
        this.nbQuestions = nbQuestions;
        this.nbChoices = nbChoices;
        this.lastValidationTimestamp = System.currentTimeMillis();
    }

    public boolean isQCM() {
        return QCM;
    }

    public void setQCM(boolean QCM) {
        this.QCM = QCM;
    }

    public Level getLevel() {
        return level;
    }

    public int getNbQuestions() {
        return nbQuestions;
    }

    public int getNbChoices() {
        return nbChoices;
    }

    public void setNbChoices(int nbChoices) {
        this.nbChoices = nbChoices;
    }

    public long getLastValidationTimestamp() {
        return lastValidationTimestamp;
    }

    public void setLastValidationTimestamp() {
        this.lastValidationTimestamp = System.currentTimeMillis();
    }

    public void setNbQuestions(int nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}