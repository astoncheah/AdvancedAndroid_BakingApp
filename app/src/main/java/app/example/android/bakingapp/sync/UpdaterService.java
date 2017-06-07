package app.example.android.bakingapp.sync;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import app.example.android.bakingapp.data.Contract;

public class UpdaterService extends IntentService{
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE = "app.example.android.bakingapp.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING = "app.example.android.bakingapp.intent.extra.REFRESHING";
    public static final String TEST_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Time time = new Time();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }
        sendBroadcast(
            new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
        createRecipeData(TEST_URL);
        sendBroadcast(
            new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
        //new LoadURL().execute(TEST_URL);
    }
    public void createRecipeData(String url) {
        try {
            Uri dirRecipeUri = Contract.Recipe.URI_RECIPE;
            Uri dirIngreUri = Contract.Recipe.URI_RECIPE_INGREDIENTS;
            Uri dirStepsUri = Contract.Recipe.URI_RECIPE_STEPS;
            //JSONObject json = readJsonFromUrl(url);
            //json.optJSONArray("item");
            JSONArray jsonArray = readJsonFromUrl(url);
            if (jsonArray == null) {
                throw new JSONException("Invalid parsed item array" );
            }

            // Delete all items
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(dirRecipeUri,null,null);
            contentResolver.delete(dirIngreUri,null,null);
            contentResolver.delete(dirStepsUri,null,null);

            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues values = new ContentValues();
                JSONObject object = jsonArray.getJSONObject(i);
                String id  = object.getString("id" );
                values.put(Contract.Recipe._ID,id);
                values.put(Contract.Recipe.COLUMN_NAME, object.getString(Contract.Recipe.COLUMN_NAME));
                values.put(Contract.Recipe.COLUMN_SERVING, object.getString(Contract.Recipe.COLUMN_SERVING));
                values.put(Contract.Recipe.COLUMN_IMAGE, object.getString(Contract.Recipe.COLUMN_IMAGE));
                contentResolver.insert(dirRecipeUri,values);

                JSONArray ingredientsArray = object.optJSONArray(Contract.Recipe.Ingredients.TABLE_NAME);
                if (ingredientsArray != null) {
                    for (int z = 0; z < ingredientsArray.length(); z++) {
                        values = new ContentValues();
                        JSONObject objectIngredients = ingredientsArray.getJSONObject(z);

                        values.put(Contract.Recipe._ID,id);
                        values.put(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_INGREDIENT, objectIngredients.getString(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_INGREDIENT));
                        values.put(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_MEASURE, objectIngredients.getString(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_MEASURE));
                        values.put(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_QUANTITY, objectIngredients.getString(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_QUANTITY));
                        contentResolver.insert(dirIngreUri,values);
                    }
                }

                JSONArray stepsArray = object.optJSONArray(Contract.Recipe.Steps.TABLE_NAME);
                if (stepsArray != null) {
                    for (int y = 0; y < stepsArray.length(); y++) {
                        values = new ContentValues();
                        JSONObject objectSteps = stepsArray.getJSONObject(y);

                        values.put(Contract.Recipe._ID,id);
                        values.put(Contract.Recipe.Steps.COLUMN_STEPS_ID, objectSteps.getString("id"));
                        values.put(Contract.Recipe.Steps.COLUMN_STEPS_DESC, objectSteps.getString(Contract.Recipe.Steps.COLUMN_STEPS_DESC));
                        values.put(Contract.Recipe.Steps.COLUMN_STEPS_SHORT_DESC, objectSteps.getString(Contract.Recipe.Steps.COLUMN_STEPS_SHORT_DESC));
                        values.put(Contract.Recipe.Steps.COLUMN_STEPS_VIDEO_URL, objectSteps.getString(Contract.Recipe.Steps.COLUMN_STEPS_VIDEO_URL));
                        values.put(Contract.Recipe.Steps.COLUMN_STEPS_IMAGE, objectSteps.getString(Contract.Recipe.Steps.COLUMN_STEPS_IMAGE));
                        contentResolver.insert(dirStepsUri,values);
                    }
                }
            }
            getContentResolver().notifyChange(dirRecipeUri, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private JSONArray readJsonFromUrl(String url) throws IOException, JSONException{
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        Log.e("readJsonFromUrl",""+url);
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setReadTimeout(5000 /* milliseconds */);
            urlConnection.setConnectTimeout(10000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);

                //return new JSONObject("{item: "+jsonText+"}");
                return new JSONArray(jsonText);
            } else {
                Log.e("readJsonFromUrl", "Error response code: " + urlConnection.getResponseCode());
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }
    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
