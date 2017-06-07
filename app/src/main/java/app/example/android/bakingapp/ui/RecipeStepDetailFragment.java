package app.example.android.bakingapp.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import app.example.android.bakingapp.R;
import app.example.android.bakingapp.data.Contract;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment implements ExoPlayer.EventListener{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String IS_INGREDIENT_PAGE = "IS_INGREDIENT_PAGE";
    public static final String ARG_RECIPE_ID = "recipe_id";
    public static final String ARG_STEP_ID = "item_id";
    public static final String MAX_COUNT = "MAX_COUNT";

    private Activity context;
    private Cursor cursor;
    private boolean isIngredientPage;
    private boolean isSmallScreenLand = false;
    private String mainId;
    private String stepsId;

    private ImageView imageViewUrl;
    private TextView recipestepDetail;
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;

    public RecipeStepDetailFragment(){
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = this.getActivity();

        if(getArguments().containsKey(ARG_RECIPE_ID)){
            isIngredientPage = getArguments().getBoolean(IS_INGREDIENT_PAGE);
            mainId = getArguments().getString(ARG_RECIPE_ID);
            stepsId = getArguments().getString(ARG_STEP_ID);

            cursor = getStepsInfo(mainId,stepsId);
            cursor.moveToFirst();

            Toolbar appBarLayout = (Toolbar)context.findViewById(R.id.detail_toolbar);
            if(appBarLayout!=null){
                if(isIngredientPage){
                    appBarLayout.setTitle(R.string.ingredients);
                }else{
                    appBarLayout.setTitle(cursor.getString(cursor.getColumnIndex(Contract.Recipe.Steps.COLUMN_STEPS_SHORT_DESC)));
                }
            }//*/
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.recipestep_detail,container,false);

        imageViewUrl = (ImageView)rootView.findViewById(R.id.imageViewUrl);
        mPlayerView = (SimpleExoPlayerView)rootView.findViewById(R.id.player_view);
        recipestepDetail = (TextView)rootView.findViewById(R.id.recipestep_detail);
        if(isIngredientPage){
            mPlayerView.setVisibility(View.GONE);
            recipestepDetail.setVisibility(View.GONE);

            ListView listIngredients = (ListView) rootView.findViewById(R.id.listIngredients);
            CursorAdapter adapter = new CursorAdapter(context,cursor,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
                @Override
                public View newView(Context context,Cursor cursor,ViewGroup parent){
                    return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
                }
                @Override
                public void bindView(View view,Context context,Cursor cursor){
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                    text1.setText(cursor.getString(cursor.getColumnIndex(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_INGREDIENT)));
                    text2.setText(
                            cursor.getString(cursor.getColumnIndex(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_QUANTITY))+" ("+
                            cursor.getString(cursor.getColumnIndex(Contract.Recipe.Ingredients.COLUMN_INGREDIENTS_MEASURE))+")"
                    );
                }
            };
            listIngredients.setAdapter(adapter);
            listIngredients.setVisibility(View.VISIBLE);
        }else{
            String link = cursor.getString(cursor.getColumnIndex(Contract.Recipe.Steps.COLUMN_STEPS_VIDEO_URL));
            String imageThumbnail = cursor.getString(cursor.getColumnIndex(Contract.Recipe.Steps.COLUMN_STEPS_IMAGE));
            if(link==null || link.length()==0){
                mPlayerView.setVisibility(View.GONE);
                if(!recipestepDetail.isShown()){
                    recipestepDetail.setVisibility(View.VISIBLE);
                    isSmallScreenLand = true;
                }
            }else{
                if(isSmallScreenLand){
                    mPlayerView.setVisibility(View.VISIBLE);
                    recipestepDetail.setVisibility(View.GONE);
                }
                mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.question_mark));
                Uri uri = Uri.parse(link);//"https://streamable.com/rhd0f"
                Log.e("link","link: "+link);
                //Uri uri = Uri.parse("https://www.youtube.com/watch?v=isgIxk6sgRk");//"https://streamable.com/rhd0f"
                initializePlayer(uri);
            }
            if(imageThumbnail==null || imageThumbnail.length()==0){
                imageViewUrl.setVisibility(View.GONE);
            }else{
                Picasso.with(context)
                    .load(imageThumbnail)
                    //.resizeDimen(R.dimen.url_image_large,R.dimen.url_image_large)
                    //.centerCrop()
                    .placeholder(R.drawable.question_mark)
                    .error(R.drawable.question_mark)
                    .into(imageViewUrl);
            }

            if(cursor!=null){
                recipestepDetail.setText(cursor.getString(cursor.getColumnIndex(Contract.Recipe.Steps.COLUMN_STEPS_DESC)));
            }
        }
        return rootView;
    }
    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        if(mExoPlayer!=null){
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
    @Override
    public void onDestroy(){
        if(cursor!=null){
            cursor.close();
        }
        super.onDestroy();
    }
    private Cursor getStepsInfo(String mainId,String stepsId){
        if(isIngredientPage){
            return context.getContentResolver().query(
                Contract.Recipe.URI_RECIPE_INGREDIENTS,
                Contract.Query.PROJECTION_INGREDIENTS,
                Contract.Recipe._ID+" =?",
                new String[]{mainId},
                null);
        }else{
            return context.getContentResolver().query(
                Contract.Recipe.URI_RECIPE_STEPS,
                Contract.Query.PROJECTION_STEPS,
                Contract.Recipe._ID+" =? AND "+Contract.Recipe.Steps.COLUMN_STEPS_ID+" =?",
                new String[]{mainId,stepsId},
                null);
        }
    }
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getActivity(), "RecipeStepDetailFragment");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            mExoPlayer.addListener(this);
        }
    }
    @Override
    public void onTimelineChanged(Timeline timeline,Object manifest){
        //Log.e("onTimelineChanged",timeline+"/"+manifest);
    }
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups,TrackSelectionArray trackSelections){
        //Log.e("onTracksChanged",trackGroups+"/"+trackSelections);
    }
    @Override
    public void onLoadingChanged(boolean isLoading){
        //Log.e("onLoadingChanged","/"+isLoading);
    }
    @Override
    public void onPlayerStateChanged(boolean playWhenReady,int playbackState){
        Log.e("onPlayerStateChanged1",playWhenReady+"/"+playbackState);
        //STATE_IDLE = 1
        //STATE_BUFFERING = 2
        //STATE_READY = 3
        //STATE_ENDED = 4
    }
    @Override
    public void onPlayerError(ExoPlaybackException error){
        //Log.e("onPlayerError","/"+error);
    }
    @Override
    public void onPositionDiscontinuity(){
        //Log.e("onPositionDiscontinuity","/onPositionDiscontinuity");
    }
}
