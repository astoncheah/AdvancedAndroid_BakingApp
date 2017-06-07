package app.example.android.bakingapp.data;


import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract{

    public static final String AUTHORITY = "app.example.android.bakingapp";
    public static final String PATH_ITEM = "item";
    public static final String PATH_ITEM_ID = "item/*";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_INGREDIENTS_ID = "ingredients/*";
    public static final String PATH_STEPS = "steps";
    public static final String PATH_STEPS_ID = "steps/*";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    private Contract() {
    }

    @SuppressWarnings("unused")
    public static final class Recipe implements BaseColumns {
        public static final String ALL = "GET_ALL";
        public static final Uri URI_RECIPE = BASE_URI.buildUpon().appendPath(PATH_ITEM).build();
        public static final Uri URI_RECIPE_INGREDIENTS = BASE_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();
        public static final Uri URI_RECIPE_STEPS = BASE_URI.buildUpon().appendPath(PATH_STEPS).build();

        public static final String TABLE_NAME = "recipe";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVING = "servings";
        public static final String COLUMN_IMAGE = "image";

        public static Uri makeUriForRecipe(String id) {
            return URI_RECIPE.buildUpon().appendPath(id).build();
        }

        public static String getRecipeFromUri(Uri queryUri) {
            return queryUri.getLastPathSegment();
        }

        public static final class Ingredients implements BaseColumns {
            public static final String TABLE_NAME = "ingredients";
            public static final String COLUMN_INGREDIENTS_QUANTITY = "quantity";
            public static final String COLUMN_INGREDIENTS_MEASURE = "measure";
            public static final String COLUMN_INGREDIENTS_INGREDIENT = "ingredient";
        }
        public static final class Steps implements BaseColumns {
            public static final String TABLE_NAME = "steps";
            public static final String COLUMN_STEPS_ID = "steps_id";
            public static final String COLUMN_STEPS_SHORT_DESC = "shortDescription";
            public static final String COLUMN_STEPS_DESC = "description";
            public static final String COLUMN_STEPS_VIDEO_URL = "videoURL";
            public static final String COLUMN_STEPS_IMAGE = "thumbnailURL";
        }
    }
    public interface Query {
        String[] PROJECTION_RECIPE = {
            Recipe._ID,
            Recipe.COLUMN_NAME,
            Recipe.COLUMN_SERVING,
            Recipe.COLUMN_IMAGE
        };
        String[] PROJECTION_INGREDIENTS = {
            Recipe._ID,
            Recipe.Ingredients.COLUMN_INGREDIENTS_QUANTITY,
            Recipe.Ingredients.COLUMN_INGREDIENTS_MEASURE,
            Recipe.Ingredients.COLUMN_INGREDIENTS_INGREDIENT
        };
        String[] PROJECTION_STEPS = {
            Recipe._ID,
            Recipe.Steps.COLUMN_STEPS_ID,
            Recipe.Steps.COLUMN_STEPS_SHORT_DESC,
            Recipe.Steps.COLUMN_STEPS_DESC,
            Recipe.Steps.COLUMN_STEPS_VIDEO_URL,
            Recipe.Steps.COLUMN_STEPS_IMAGE
        };
    }
}
