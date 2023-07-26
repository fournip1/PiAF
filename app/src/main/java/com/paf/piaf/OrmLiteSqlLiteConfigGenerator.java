package com.paf.piaf;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/*
This autorunning class is used to generate the ormlite config file referenced in the DatabaseHelper class.
Careful to set the working project directory to main (got to run -> edit configuration) and to run this class with coverage
*/

public class OrmLiteSqlLiteConfigGenerator extends OrmLiteConfigUtil {
    private final static Class<?>[] entityClasses=new Class[]{Bird.class, Sound.class, Score.class, User.class, Level.class};
    public static void main(String... args) throws Exception{
        writeConfigFile("ormlite_config.txt",entityClasses);
    }
}
