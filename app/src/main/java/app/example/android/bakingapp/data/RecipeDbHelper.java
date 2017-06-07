package app.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class RecipeDbHelper extends SQLiteOpenHelper {
    private static final String NAME = "Recipe.db";
    private static final int VERSION = 1;


    RecipeDbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String builder = "CREATE TABLE " + Contract.Recipe.TABLE_NAME + " ("
                + Contract.Recipe._ID               + " INTEGER PRIMARY KEY, "
                + Contract.Recipe.COLUMN_NAME       + " TEXT NOT NULL, "
                + Contract.Recipe.COLUMN_SERVING    + " TEXT, "
                + Contract.Recipe.COLUMN_IMAGE      + " TEXT"
                + ")";
        db.execSQL(builder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + Contract.Recipe.TABLE_NAME);
        onCreate(db);
    }
}
