package app.example.android.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.example.android.bakingapp.R;
import app.example.android.bakingapp.data.Contract;
import app.example.android.bakingapp.sync.TestIdlingResource;
import app.example.android.bakingapp.sync.UpdaterService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityRecipeList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private RecipeAdapter adapter;
    @Nullable
    private TestIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new TestIdlingResource();
        }
        return mIdlingResource;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }
    private void refresh() {
        Intent i = new Intent(this, UpdaterService.class);
        startService(i);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args){
        return new CursorLoader(this,Contract.Recipe.URI_RECIPE,Contract.Query.PROJECTION_RECIPE,null,null,null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor data){
        if(data.getCount()==0){
            refresh();
        }else{
            adapter = new RecipeAdapter(this);
            adapter.setCursor(data);

            mRecyclerView.setAdapter(adapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                Slide slide = new Slide();
                slide.setSlideEdge(Gravity.BOTTOM);

                TransitionManager.beginDelayedTransition(mRecyclerView, slide);
            }
        }
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mRecyclerView.setAdapter(null);
    }
    public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
        private final Context context;
        private Cursor cursor;

        RecipeAdapter(Context context) {
            this.context = context;
        }

        void setCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }

        @Override
        public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View item = LayoutInflater.from(context).inflate(R.layout.list_main_recipe_card, parent, false);

            return new RecipeViewHolder(item);
        }

        @Override
        public void onBindViewHolder(RecipeViewHolder holder, int position) {
            cursor.moveToPosition(position);

            final String id = cursor.getString(cursor.getColumnIndex(Contract.Recipe._ID));
            final String name = cursor.getString(cursor.getColumnIndex(Contract.Recipe.COLUMN_NAME));

            holder.txtTitle.setText(name);
            holder.txtTitle.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    //testing

                    Intent i = new Intent(context,RecipeStepListActivity.class);
                    i.putExtra("name",name);
                    i.putExtra("_ID",id);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            int count = 0;
            if (cursor != null) {
                count = cursor.getCount();
            }
            return count;
        }

        public class RecipeViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.txtTitle)
            TextView txtTitle;

            RecipeViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
