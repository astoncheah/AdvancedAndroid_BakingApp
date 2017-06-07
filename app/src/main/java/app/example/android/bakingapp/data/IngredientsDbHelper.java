package app.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class IngredientsDbHelper extends SQLiteOpenHelper {
    private static final String NAME = "RecipeIngredients.db";
    private static final int VERSION = 1;


    IngredientsDbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String builder = "CREATE TABLE " + Contract.Recipe.Ingredients.TABLE_NAME + " ("
                + Contract.Recipe._ID                                       + " INTEGER NOT NULL, "
                + Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_INGREDIENT + " TEXT, "
                + Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_MEASURE    + " TEXT, "
                + Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_QUANTITY   + " TEXT"
                + ")";
        db.execSQL(builder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + Contract.Recipe.Ingredients.TABLE_NAME);
        onCreate(db);
    }
}
