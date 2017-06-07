package app.example.android.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Provider extends ContentProvider {

    private static final int ITEM                   = 100;
    private static final int ITEM_FOR_ID            = 101;
    private static final int ITEM_FOR_INGREDIENTS   = 102;
    private static final int ITEM_FOR_STEPS         = 103;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private RecipeDbHelper recipeDbHelper;
    private IngredientsDbHelper ingredientsDbHelper;
    private StepsDbHelper stepsDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_ITEM, ITEM);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_ITEM_ID, ITEM_FOR_ID);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_INGREDIENTS, ITEM_FOR_INGREDIENTS);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_STEPS, ITEM_FOR_STEPS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        recipeDbHelper = new RecipeDbHelper(getContext());
        ingredientsDbHelper = new IngredientsDbHelper(getContext());
        stepsDbHelper = new StepsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        switch (uriMatcher.match(uri)) {
            case ITEM:
                returnCursor = recipeDbHelper.getReadableDatabase().query(
                    Contract.Recipe.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            case ITEM_FOR_ID:
                returnCursor = recipeDbHelper.getReadableDatabase().query(
                    Contract.Recipe.TABLE_NAME,
                    projection,
                    Contract.Recipe._ID + " = ?",
                    new String[]{Contract.Recipe.getRecipeFromUri(uri)},
                    null,
                    null,
                    sortOrder
                );
                break;
            case ITEM_FOR_INGREDIENTS:
                returnCursor = ingredientsDbHelper.getReadableDatabase().query(
                    Contract.Recipe.Ingredients.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            case ITEM_FOR_STEPS:
                returnCursor = stepsDbHelper.getReadableDatabase().query(
                    Contract.Recipe.Steps.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        Context context = getContext();
        if (context != null){
            returnCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case ITEM:
                recipeDbHelper.getWritableDatabase().insert(
                        Contract.Recipe.TABLE_NAME,
                        null,
                        values
                );
                returnUri = Contract.Recipe.URI_RECIPE;
                break;
            case ITEM_FOR_INGREDIENTS:
                ingredientsDbHelper.getWritableDatabase().insert(
                    Contract.Recipe.Ingredients.TABLE_NAME,
                    null,
                    values
                );
                returnUri = Contract.Recipe.URI_RECIPE_INGREDIENTS;
                break;
            case ITEM_FOR_STEPS:
                stepsDbHelper.getWritableDatabase().insert(
                    Contract.Recipe.Steps.TABLE_NAME,
                    null,
                    values
                );
                returnUri = Contract.Recipe.URI_RECIPE_STEPS;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (uriMatcher.match(uri)) {
            case ITEM:
                rowsDeleted = recipeDbHelper.getWritableDatabase().delete(
                        Contract.Recipe.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;
            case ITEM_FOR_ID:
                String idRecipe = Contract.Recipe.getRecipeFromUri(uri);
                rowsDeleted = recipeDbHelper.getWritableDatabase().delete(
                        Contract.Recipe.TABLE_NAME,
                        '"' + idRecipe + '"' + " =" + Contract.Recipe._ID,
                        selectionArgs
                );
                break;
            case ITEM_FOR_INGREDIENTS:
                String idIng = Contract.Recipe.getRecipeFromUri(uri);
                rowsDeleted = ingredientsDbHelper.getWritableDatabase().delete(
                    Contract.Recipe.Ingredients.TABLE_NAME,
                    //'"' + idIng + '"' + " =" + Contract.Recipe._ID,
                    selection,
                    selectionArgs
                );
                break;
            case ITEM_FOR_STEPS:
                String idSteps = Contract.Recipe.getRecipeFromUri(uri);
                rowsDeleted = stepsDbHelper.getWritableDatabase().delete(
                    Contract.Recipe.Steps.TABLE_NAME,
                    //'"' + idSteps + '"' + " =" + Contract.Recipe._ID,
                    selection,
                    selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsDeleted != 0) {
            Context context = getContext();
            if (context != null){
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (uriMatcher.match(uri)) {
            case ITEM:
                final SQLiteDatabase db = recipeDbHelper.getWritableDatabase();
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                Contract.Recipe.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }


    }
}
