package app.example.android.bakingapp.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.example.android.bakingapp.R;
import app.example.android.bakingapp.data.Contract;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of RecipeSteps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeStepListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane,isDefaultPage = true;
    private String main_id;
    @BindView(R.id.recipestep_list)
    RecyclerView mRecyclerView;
    private SimpleItemRecyclerViewAdapter adapter;
    private View PressedView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_list);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Snackbar.make(view,"Replace with your own action",Snackbar.LENGTH_LONG).setAction("Action",null).show();
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra("name"));
        }

        if(findViewById(R.id.recipestep_detail_container)!=null){
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        main_id = getIntent().getStringExtra("_ID");
        Log.e("RecipeStepListActivity","onCreate: "+main_id);
        getSupportLoaderManager().initLoader(0, null, this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id,Bundle args){
        return new CursorLoader(this,
            Contract.Recipe.URI_RECIPE_STEPS,
            Contract.Query.PROJECTION_STEPS,
            Contract.Recipe._ID+" =? ",
            new String[]{main_id},
            null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader,Cursor cursor){
        adapter = new SimpleItemRecyclerViewAdapter();
        adapter.setCursor(cursor);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);

            TransitionManager.beginDelayedTransition(mRecyclerView, slide);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mRecyclerView.setAdapter(null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void launchStepDetailFragment(boolean isIngredientPage,String stepId,int maxCount,final View v){
        if(mTwoPane){
            if(PressedView!=null){
                PressedView.setPressed(false);
            }
            v.postDelayed(new Runnable(){
                @Override
                public void run(){
                    PressedView = v;
                    PressedView.setPressed(true);
                }
            },300);
        }

        Bundle arguments = new Bundle();
        arguments.putBoolean(RecipeStepDetailFragment.IS_INGREDIENT_PAGE,isIngredientPage);
        arguments.putString(RecipeStepDetailFragment.ARG_RECIPE_ID,main_id);
        arguments.putString(RecipeStepDetailFragment.ARG_STEP_ID,stepId);
        arguments.putInt(RecipeStepDetailFragment.MAX_COUNT,maxCount);
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.recipestep_detail_container,fragment)
            .commit();
    }
    private void launchStepDetailActivity(boolean isIngredientPage,String stepId,int maxCount){
        Intent intent = new Intent(this,RecipeStepDetailActivity.class);
        intent.putExtra(RecipeStepDetailFragment.IS_INGREDIENT_PAGE,isIngredientPage);
        intent.putExtra(RecipeStepDetailFragment.ARG_RECIPE_ID,main_id);
        intent.putExtra(RecipeStepDetailFragment.ARG_STEP_ID,stepId);
        intent.putExtra(RecipeStepDetailFragment.MAX_COUNT,maxCount);
        startActivity(intent);
    }
    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>{
        private Cursor cursor;

        void setCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipestep_list_content,parent,false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ViewHolder holder,int position){
            final boolean isIngredientPage;
            final String stepId;
            if(position==0){
                isIngredientPage = true;
                stepId = "-1";
                holder.recipeStepsDesc.setText(R.string.ingredients);
            }else{
                cursor.moveToPosition(position-1);
                isIngredientPage = false;
                stepId = cursor.getString(cursor.getColumnIndex(Contract.Recipe.Steps.COLUMN_STEPS_ID));
                final String desc = cursor.getString(cursor.getColumnIndex(Contract.Recipe.Steps.COLUMN_STEPS_SHORT_DESC));
                holder.recipeStepsDesc.setText(desc);
            }
            if(mTwoPane && isDefaultPage){
                isDefaultPage = false;
                launchStepDetailFragment(true,"-1",cursor.getCount()-1,holder.recipeStepsDesc);
            }
            holder.recipeStepsDesc.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mTwoPane){
                        launchStepDetailFragment(isIngredientPage,stepId,cursor.getCount()-1,v);
                    }else{
                        launchStepDetailActivity(isIngredientPage,stepId,cursor.getCount()-1);
                    }
                }
            });
        }
        @Override
        public int getItemCount(){
            int count = 0;
            if (cursor != null) {
                count = cursor.getCount()+1;
            }
            return count;
        }
        public class ViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.recipeStepsDesc)
            TextView recipeStepsDesc;

            public ViewHolder(View view){
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
