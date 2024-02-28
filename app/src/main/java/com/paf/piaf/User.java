package com.paf.piaf;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class User {
    public final static String QCM_FIELD_NAME = "qcm";
    public final static String FIRST_FIELD_NAME = "first";
    public final static String FINISHED_FIELD_NAME = "finished";
    public final static String WARNING_FIELD_NAME = "warning";
    public final static String HINT_FIELD_NAME = "hint";
    public final static String LEVEL_FIELD_NAME = "level";
    public final static String NB_QUESTIONS_FIELD_NAME = "nb_questions";
    public final static String NB_CHOICES_FIELD_NAME = "nb_choices";
    public final static String LAST_VALIDATION_FIELD_NAME = "last_validation_timestamp";




    @DatabaseField(generatedId = true)
    private int id;
    // check if it is a first run
    @DatabaseField(canBeNull = false, columnName = FIRST_FIELD_NAME)
    private boolean first;
    // check if user wants QCM mdoe
    @DatabaseField(canBeNull = false, columnName = QCM_FIELD_NAME)
    private boolean QCM;
    // check if user has finished the game
    @DatabaseField(canBeNull = false, columnName = FINISHED_FIELD_NAME)
    private boolean finished;
    // check if user wants to display warnings in the QCM
    @DatabaseField(canBeNull = false, columnName = WARNING_FIELD_NAME)
    private boolean warning;
    // check if user wants to display hints
    @DatabaseField(canBeNull = false, columnName = HINT_FIELD_NAME)
    private boolean hint;

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

    public User(boolean first, boolean finished, boolean warning, boolean QCM, boolean hint, Level level, int nbQuestions, int nbChoices) {
        this.level = level;
        this.QCM = QCM;
        this.first = first;
        this.finished = finished;
        this.warning = warning;
        this.hint = hint;
        this.nbQuestions = nbQuestions;
        this.nbChoices = nbChoices;
        this.lastValidationTimestamp = System.currentTimeMillis();
    }

    public boolean isQCM() {
        return QCM;
    }

    public boolean isFirst() {
        return first;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWarning() {
        return warning;
    }

    public boolean isHint() {
        return hint;
    }

    public void setQCM(boolean QCM) {
        this.QCM = QCM;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setWarning(boolean warning) {
        this.warning = warning;
    }

    public void setHint(boolean hint) {
        this.hint = hint;
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