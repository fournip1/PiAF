package com.paf.piaf;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "user")
public class User {
    public final static String QCM_FIELD_NAME = "qcm";
    public final static String LEVEL_FIELD_NAME = "level";
    public final static String NB_QUESTIONS_FIELD_NAME = "nb_questions";

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


    User() {
        // needed by ormlite
    }

    public User(boolean QCM, Level level, int nbQuestions) {
        this.level = level;
        this.QCM = QCM;
        this.nbQuestions = nbQuestions;
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

    public void setNbQuestions(int nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}