package app.example.android.bakingapp.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import app.example.android.bakingapp.R;
import app.example.android.bakingapp.data.Contract;
import app.example.android.bakingapp.sync.WidgetRemoteViewsFactory;
import app.example.android.bakingapp.sync.WidgetRemoteViewsService;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeAppWidget extends AppWidgetProvider{
    private static final String PREVIOUS_RECIPE = "app.example.android.bakingapp.PREVIOUS_RECIPE";
    private static final String NEXT_RECIPE = "app.example.android.bakingapp.NEXT_RECIPE";
    private static final String PAGE_ID = "PAGE_ID";
    public static final String UPDATE_TITLE = "app.example.android.bakingapp.UPDATE_TITLE";

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        int mainId = getPageId(context);
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            //AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            //ComponentName cn = new ComponentName(context, CollectionAppWidgetProvider.class);
            //mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widgetListView);
        }else if(action.equals(PREVIOUS_RECIPE)){
            if(mainId>1){
                mainId--;

                savePageId(context,mainId);

                AppWidgetManager mgr = AppWidgetManager.getInstance(context);
                ComponentName cn = new ComponentName(context, RecipeAppWidget.class);
                int[] appWidgetId = mgr.getAppWidgetIds(cn);
                onUpdate(context,mgr,appWidgetId);
            }
        }else if(action.equals(NEXT_RECIPE)){
            mainId++;

            savePageId(context,mainId);

            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, RecipeAppWidget.class);
            int[] appWidgetId = mgr.getAppWidgetIds(cn);
            onUpdate(context,mgr,appWidgetId);
        }
    }
    @Override
    public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds){
        int mainId = getPageId(context);
        for(int appWidgetId : appWidgetIds){
            updateAppWidget(context,appWidgetManager,appWidgetId,mainId);
        }
    }
    @Override
    public void onEnabled(Context context){
        // Enter relevant functionality for when the first widget is created
    }
    @Override
    public void onDisabled(Context context){
        // Enter relevant functionality for when the last widget is disabled
    }
    private void updateAppWidget(Context context,AppWidgetManager appWidgetManager,int appWidgetId,int mainId){
        Intent intent = new Intent(context,WidgetRemoteViewsService.class);
        intent.putExtra(WidgetRemoteViewsFactory.MAIN_ID,mainId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.recipe_app_widget);
        views.setRemoteAdapter(R.id.widgetListView,intent);

        Intent clickIntentTemplate = new Intent(context,RecipeStepListActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(clickIntentTemplate)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

        views.setTextViewText(R.id.widgetTitleLabel,getTitle(context,mainId));

        Intent i = new Intent(PREVIOUS_RECIPE);
        PendingIntent pI = PendingIntent.getBroadcast(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imageButtonPrev, pI);

        i = new Intent(NEXT_RECIPE);
        pI = PendingIntent.getBroadcast(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imageButtonNext, pI);

        appWidgetManager.updateAppWidget(appWidgetId,views);
        //appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widgetListView);
    }
    private String getTitle(Context context,int mainId){
        String str = context.getString(R.string.ingredients);
        Cursor c = null;
        try{
            Uri uri = Contract.Recipe.URI_RECIPE;
            c = context.getContentResolver().query(
                uri,
                Contract.Query.PROJECTION_RECIPE,
                Contract.Recipe._ID+" =? ",
                new String[]{mainId+""},
                null);
            c.moveToFirst();
            str = c.getString(c.getColumnIndex(Contract.Recipe.COLUMN_NAME));
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(c!=null){
                c.close();
            }
        }
        return str;
    }
    public static int getPageId(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(PAGE_ID,1);
    }
    public static void savePageId(Context context,int id){
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putInt(PAGE_ID,id);
        edit.commit();
    }
}

