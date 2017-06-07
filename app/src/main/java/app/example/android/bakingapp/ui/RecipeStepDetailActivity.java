package app.example.android.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import app.example.android.bakingapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single RecipeStep detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeStepListActivity}.
 */
public class RecipeStepDetailActivity extends AppCompatActivity{
    private boolean isIngredientPage;
    private String mainId;
    private String stepsId;
    private int maxCount;

    @BindView(R.id.textPrev)
    TextView textPrev;
    @BindView(R.id.textNext)
    TextView textNext;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipestep_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra("name"));
        }
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if(savedInstanceState==null){
            isIngredientPage = getIntent().getBooleanExtra(RecipeStepDetailFragment.IS_INGREDIENT_PAGE,false);
            mainId = getIntent().getStringExtra(RecipeStepDetailFragment.ARG_RECIPE_ID);
            stepsId = getIntent().getStringExtra(RecipeStepDetailFragment.ARG_STEP_ID);
            maxCount = getIntent().getIntExtra(RecipeStepDetailFragment.MAX_COUNT,0);
        }else{
            isIngredientPage = savedInstanceState.getBoolean(RecipeStepDetailFragment.IS_INGREDIENT_PAGE,false);
            mainId = savedInstanceState.getString(RecipeStepDetailFragment.ARG_RECIPE_ID);
            stepsId = savedInstanceState.getString(RecipeStepDetailFragment.ARG_STEP_ID);
            maxCount = savedInstanceState.getInt(RecipeStepDetailFragment.MAX_COUNT,0);
        }

        Bundle arguments = new Bundle();
        arguments.putBoolean(RecipeStepDetailFragment.IS_INGREDIENT_PAGE,isIngredientPage);
        arguments.putString(RecipeStepDetailFragment.ARG_RECIPE_ID,mainId);
        arguments.putString(RecipeStepDetailFragment.ARG_STEP_ID,stepsId);
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .add(R.id.recipestep_detail_container,fragment)
            .commit();

        int id = Integer.parseInt(stepsId);
        if(id<0){
            textPrev.setVisibility(View.GONE);
        }else if(id==maxCount){
            textNext.setVisibility(View.GONE);
        }
        textPrev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int id = Integer.parseInt(stepsId);
                if(id>0){
                    textNext.setVisibility(View.VISIBLE);
                }else{
                    isIngredientPage = true;
                    textPrev.setVisibility(View.GONE);
                }
                id--;
                setStepsPage(id+"");
            }
        });
        textNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int id = Integer.parseInt(stepsId);
                id++;
                if(id<maxCount){
                    isIngredientPage = false;
                    textPrev.setVisibility(View.VISIBLE);
                }else{
                    textNext.setVisibility(View.GONE);
                }
                setStepsPage(id+"");
            }
        });
    }
    private void setStepsPage(String stepsId){
        this.stepsId = stepsId;
        Bundle arguments = new Bundle();
        arguments.putBoolean(RecipeStepDetailFragment.IS_INGREDIENT_PAGE,isIngredientPage);
        arguments.putString(RecipeStepDetailFragment.ARG_RECIPE_ID,mainId);
        arguments.putString(RecipeStepDetailFragment.ARG_STEP_ID,stepsId);
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.setArguments(arguments);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.recipestep_detail_container,fragment);
        fragmentTransaction.commit();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putBoolean(RecipeStepDetailFragment.IS_INGREDIENT_PAGE,isIngredientPage);
        outState.putString(RecipeStepDetailFragment.ARG_RECIPE_ID,mainId);
        outState.putString(RecipeStepDetailFragment.ARG_STEP_ID,stepsId);
        outState.putInt(RecipeStepDetailFragment.MAX_COUNT,maxCount);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==android.R.id.home){
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            //NavUtils.navigateUpTo(this,new Intent(this,RecipeStepListActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
