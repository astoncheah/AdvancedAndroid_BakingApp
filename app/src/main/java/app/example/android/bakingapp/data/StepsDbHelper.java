package app.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class StepsDbHelper extends SQLiteOpenHelper {
    private static final String NAME = "RecipeSteps.db";
    private static final int VERSION = 1;


    StepsDbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String builder = "CREATE TABLE " + Contract.Recipe.Steps.TABLE_NAME + " ("
            + Contract.Recipe._ID                     + " INTEGER NOT NULL, "
            + Contract.Recipe.Steps.COLUMN_STEPS_ID         + " TEXT, "
            + Contract.Recipe.Steps.COLUMN_STEPS_SHORT_DESC + " TEXT, "
            + Contract.Recipe.Steps.COLUMN_STEPS_DESC       + " TEXT, "
            + Contract.Recipe.Steps.COLUMN_STEPS_VIDEO_URL  + " TEXT, "
            + Contract.Recipe.Steps.COLUMN_STEPS_IMAGE      + " TEXT"
            + ")";
        db.execSQL(builder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + Contract.Recipe.Steps.TABLE_NAME);
        onCreate(db);
    }
}
