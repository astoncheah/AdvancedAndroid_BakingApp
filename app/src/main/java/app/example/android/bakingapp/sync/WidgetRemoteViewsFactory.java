package app.example.android.bakingapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import app.example.android.bakingapp.R;
import app.example.android.bakingapp.data.Contract;
import app.example.android.bakingapp.ui.RecipeAppWidget;

/**
 * Created by cheah on 1/6/17.
 */
public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
    public static final String MAIN_ID = "MAIN_ID";
    private Context mContext;
    private Cursor mCursor;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }
    @Override
    public void onCreate(){}
    @Override
    public void onDataSetChanged(){
        if (mCursor != null) {
            mCursor.close();
        }
        int mainId = RecipeAppWidget.getPageId(mContext);
        try{
            final long identityToken = Binder.clearCallingIdentity();
            mCursor = getCursor(mainId);
            //return to page 1 after last page
            if(mCursor.getCount()==0){
                mainId = 1;
                RecipeAppWidget.savePageId(mContext,mainId);

                mCursor.close();
                mCursor = getCursor(mainId);
            }
            Binder.restoreCallingIdentity(identityToken);
        }catch(Exception e){
            e.printStackTrace();
        }
        Log.e("WidgetRemoteViewsFact","/onDataSetChanged2/"+mainId);
    }
    private Cursor getCursor(int mainId){
        Uri uri = Contract.Recipe.URI_RECIPE_INGREDIENTS;
        Cursor c = mContext.getContentResolver().query(
            uri,
            Contract.Query.PROJECTION_INGREDIENTS,
            Contract.Recipe._ID+" =? ",
            new String[]{mainId+""},
            null);
        return c;
    }
    @Override
    public void onDestroy(){
        if (mCursor != null) {
            mCursor.close();
        }
    }
    @Override
    public int getCount(){
        return mCursor == null ? 0 : mCursor.getCount();
    }
    @Override
    public RemoteViews getViewAt(int position){
        if (position == AdapterView.INVALID_POSITION ||
            mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        mCursor.moveToPosition(position);
        String desc =
            mCursor.getString(mCursor.getColumnIndex(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_INGREDIENT))+" ("+
            mCursor.getString(mCursor.getColumnIndex(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_QUANTITY))+" "+
            mCursor.getString(mCursor.getColumnIndex(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_MEASURE))+")";

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.collection_widget_list_item);
        rv.setTextViewText(R.id.widgetTextItemList,desc);

        Intent i = new Intent();
        i.putExtra("_ID",RecipeAppWidget.getPageId(mContext)+"");
        rv.setOnClickFillInIntent(R.id.widgetItemContainer, i);
        return rv;
    }
    @Override
    public RemoteViews getLoadingView(){
        return null;
    }
    @Override
    public int getViewTypeCount(){
        return 1;
    }
    @Override
    public long getItemId(int position){
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }
    @Override
    public boolean hasStableIds(){
        return false;
    }
}
