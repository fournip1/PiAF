package com.paf.piaf;

import static java.util.Comparator.comparing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes. It implements seriazable because it is needed to pass this between actvities
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper implements Serializable {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "piaf.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 2;

    // the DAO object we use to access the SimpleData table

    private RuntimeExceptionDao<Level, Integer> levelRuntimeDao = null;
    private RuntimeExceptionDao<Sound, Integer> soundRuntimeDao = null;
    private RuntimeExceptionDao<Score, Long> scoreRuntimeDao = null;
    private RuntimeExceptionDao<User, Integer> userRuntimeDao = null;
    private RuntimeExceptionDao<Bird, Integer> birdRuntimeDao = null;

    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        mContext = context;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            // Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, Bird.class);
            TableUtils.createTableIfNotExists(connectionSource, Sound.class);
            TableUtils.createTableIfNotExists(connectionSource, Score.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, Level.class);
            // Log.i(DatabaseHelper.class.getName(),"Database created.");
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        populateBirdsSoundsLevels();

        // here we create the default app user
        Level level = getLevelRuntimeDao().queryForFirst();
        User user = new User(true,false,true,level, 10, 4);
        getUserRuntimeDao().create(user);
    }

    public void populateBirdsSoundsLevels() {
        // we set the user DAO
        RuntimeExceptionDao<User, Integer> userDao = getUserRuntimeDao();

        // now we populate the level table
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.populate_levels);
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                userDao.updateRaw(strLine);
            }
            dataInputStream.close();
            // Log.i(this.getClass().getName(), "Levels insertion went right");
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Levels insertion went wrong");
            e.printStackTrace();
        }

        // then we populate the birds table
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.populate_birds);
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                userDao.updateRaw(strLine);
            }
            dataInputStream.close();
            // Log.i(this.getClass().getName(), "Birds insertion went right");
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Birds insertion went wrong");
            e.printStackTrace();
        }

        // last we populate the sound table
        try {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.populate_sounds);
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                userDao.updateRaw(strLine);
            }
            dataInputStream.close();
            // Log.i(this.getClass().getName(), "Sounds insertion went right");
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Sounds insertion went wrong");
            e.printStackTrace();
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            // we want the user and the scores to be kept
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Bird.class, true);
            TableUtils.dropTable(connectionSource, Sound.class, true);
            TableUtils.dropTable(connectionSource, Level.class, true);

            if (oldVersion<2) {
                getScoreRuntimeDao().executeRaw("ALTER TABLE `user` ADD COLUMN finished SMALLINT NOT NULL DEFAULT 0;");
            }
            populateBirdsSoundsLevels();

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }



    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our classes. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */

    public RuntimeExceptionDao<Level, Integer> getLevelRuntimeDao() {
        if (levelRuntimeDao == null) {
            levelRuntimeDao = getRuntimeExceptionDao(Level.class);
        }
        return levelRuntimeDao;
    }


    public RuntimeExceptionDao<Sound, Integer> getSoundRuntimeDao() {
        if (soundRuntimeDao == null) {
            soundRuntimeDao = getRuntimeExceptionDao(Sound.class);
        }
        return soundRuntimeDao;
    }

    public RuntimeExceptionDao<Score, Long> getScoreRuntimeDao() {
        if (scoreRuntimeDao == null) {
            scoreRuntimeDao = getRuntimeExceptionDao(Score.class);
        }
        return scoreRuntimeDao;
    }

    public RuntimeExceptionDao<User, Integer> getUserRuntimeDao() {
        if (userRuntimeDao == null) {
            userRuntimeDao = getRuntimeExceptionDao(User.class);
        }
        return userRuntimeDao;
    }

    public RuntimeExceptionDao<Bird, Integer> getBirdRuntimeDao() {
        if (birdRuntimeDao == null) {
            birdRuntimeDao = getRuntimeExceptionDao(Bird.class);
        }
        return birdRuntimeDao;
    }

    public float getTargetVicinity() {
        Float fMaxErrors = (Score.SCORES_DEPTH*(100-Score.VALIDATION_PERCENTAGE)/100);
        Long maxErrors = fMaxErrors.longValue();
        Long lastValidationTimestamp = getUserRuntimeDao().queryForFirst().getLastValidationTimestamp();
        // Log.i(DatabaseHelper.class.getName(),"Maximum erreurs: " + maxErrors);
        int i=0;
        int e=0;
        List<Score> lastScores = getLastScores(Score.SCORES_DEPTH).stream()
                .filter((s) -> s.dateMillis > lastValidationTimestamp)
                .collect(Collectors.toList());
        while (e <= maxErrors && i<lastScores.size()) {
            if (lastScores.get(i).getScore()!=1) {
                e++;
            }
            // Log.i(DatabaseHelper.class.getName(),"valeur de i:" + i);
            // Log.i(DatabaseHelper.class.getName(),"valeur de e:" + e);
            i++;
        }

        return (float) (Score.SCORES_DEPTH+1-i)/Score.SCORES_DEPTH;
    }

    public List<Bird> getBirds() {
        Level level = getUserRuntimeDao().queryForFirst().getLevel();
        Set<Bird> hBirds = getSoundRuntimeDao().queryForAll().stream()
                .filter((s)->(s.getLevel().getId() <= level.getId()))
                .map(Sound::getBird)
                .collect(Collectors.toSet());
        return hBirds.stream()
                .sorted(comparing(Bird::getFrenchNonAccentuated))
                .collect(Collectors.toList());
    }

    public boolean validateLevel() {
        // try {
        List<Score> lastScores = getLastScores(Score.SCORES_DEPTH);
        Long totalScore = lastScores.stream()
                .filter((s) -> (s.getScore() == 1))
                .filter((s) -> (s.dateMillis > getUserRuntimeDao().queryForFirst().getLastValidationTimestamp()))
                .count();
        float percentageValidated = (float) 100 * totalScore / Score.SCORES_DEPTH;
        // Log.i(DatabaseHelper.class.getName(), "Percentage validated: " + percentageValidated);
        if (percentageValidated >= Score.VALIDATION_PERCENTAGE) {
            return true;
        } else {
            return false;
        }
        // } catch(SQLException e) {
        // Log.e(DatabaseHelper.class.getName(), "Error in the SQL Query to get the sounds for a given level.");
        // return false;
        // }
    }

    public List<Score> getLastScores(Long depth) {
        List<Score> lastScores = new ArrayList<>();
        try {
            lastScores = getScoreRuntimeDao().queryBuilder()
                    .orderBy(Score.DATE_MILLIS_FIELD_NAME, false)
                    .limit(depth)
                    .query();
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Error in the SQL Query to get the last scores.");
        }
        return lastScores;
    }
    
    public List<Sound> getSoundsByBirdAndLevel(int birdId, int levelId) {
        Bird bird = getBirdRuntimeDao().queryForId(birdId);
        Level level = getLevelRuntimeDao().queryForId(levelId);

        List<Sound> sounds = bird.getSounds().stream()
                .filter((s) -> (s.getLevel().getId() <= levelId))
                .sorted(comparing(Sound::getType))
                .collect(Collectors.toList());

        if (!bird.getAudioDescriptionPath().equals("")) {
            Sound descSound = new Sound(Bird.DESCRIPTION_NAME,bird.getAudioDescriptionPath(),level,bird);
            sounds.add(0,descSound);
        }
        return sounds;
    }
    
    public List<Sound> getSoundsByLevel(Level level) {
        int levelId = level.getId();
        List<Sound> sounds = new ArrayList<>();
        try {
            sounds = getSoundRuntimeDao().queryBuilder()
                    .where()
                    .le(Sound.LEVEL_FIELD_NAME, levelId)
                    .query();
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Error in the SQL Query to get the sounds for a given level.");
        }
        return sounds;
    }

    public void resetGame() {
        // we reset the user
        Level level = getLevelRuntimeDao().queryForFirst();
        RuntimeExceptionDao<User, Integer> userDao = getUserRuntimeDao();
        User user = userDao.queryForFirst();
        user.setLevel(level);
        user.setFinished(false);
        userDao.update(user);

        // we reset the scores
        userDao.executeRaw("DELETE FROM scores");
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        levelRuntimeDao = null;
        soundRuntimeDao = null;
        scoreRuntimeDao = null;
        userRuntimeDao = null;
        birdRuntimeDao = null;
        // Log.i(DatabaseHelper.class.getName(),"Closing database.");
        super.close();
    }
}
