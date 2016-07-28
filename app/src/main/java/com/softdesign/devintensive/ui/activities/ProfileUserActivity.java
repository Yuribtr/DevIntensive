package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.PreferencesManager;
import com.softdesign.devintensive.data.network.ChronosLikeUnlike;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.ui.views.AspectRatioImageView;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.UiHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;

import java.util.List;

public class ProfileUserActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ImageView mProfileImage;
    private EditText mUserBio;
    private TextView mUserRating, mUserCodeLines, mUserProjects, mLikesCountBadge;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private ListView mRepoListView;
    private Context mContext;
    private FloatingActionButton mFavFab;
    private DataManager mDataManager;
    private String mUserId, mMyUserId;
    private boolean mUserLiked;
    private ChronosConnector mConnector;
    private UserDTO mUser;
    private List<String> mRepositories;
    private RepositoriesAdapter mRepositoriesAdapter;
    private int mLikesCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);
        mUserBio = (EditText) findViewById(R.id.about_et);
        mUserRating = (TextView) findViewById(R.id.user_info_rating_tv);//должно подтягиваться с activity_profile_user 01:30
        mUserCodeLines = (TextView) findViewById(R.id.user_info_code_lines_tv);
        mUserProjects = (TextView) findViewById(R.id.user_info_projects_tv);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mRepoListView = (ListView) findViewById(R.id.repositories_list);//static_profile_content
        mContext= DevintensiveApplication.getContext();
        mFavFab = (FloatingActionButton) findViewById(R.id.fav_fab);
        mLikesCountBadge = (TextView) findViewById(R.id.likes_count_badge);
        mDataManager = DataManager.getInstance();
        PreferencesManager pm = mDataManager.getPreferencesManager();
        mMyUserId = pm.getUserId();

        setupToolbar();
        initProfileData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnector.onResume();
    }

    @Override
    protected void onPause() {
        mConnector.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData(){
        //получаем из ЭКСТРЫ переданные данные из другого активити
        mUser = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);
        mRepositories = mUser.getRepositories();
        mRepositoriesAdapter = new RepositoriesAdapter(this, mRepositories);
        final String userPhotoUri;
        //подключаем адаптер обработки списка репозиториев
        mRepoListView.setAdapter(mRepositoriesAdapter);
        //небольшой хак для установки высоты списка репозиториев
        setListViewHeightBasedOnItems(mRepoListView);
        //обработчик клика по пункту репозитория с переходом на интернет страницу
        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://" + mRepositories.get(position)));
                startActivity(browseIntent);
            }
        });
        //инициализация полей профиля
        mUserBio.setText(mUser.getBio());
        mUserRating.setText(mUser.getRating());
        mUserCodeLines.setText(mUser.getCodeLines());
        mUserProjects.setText(mUser.getProjects());
        mCollapsingToolbarLayout.setTitle(mUser.getFullName());

        mUserId = mUser.getRemoteId();
        mUserLiked = Boolean.valueOf(mUser.getIsMyLike());
        if (mUser.getLikesBy()!=null) mLikesCount = mUser.getLikesBy().size();
        else mLikesCount = 0;
        mLikesCountBadge.setText(String.valueOf(mLikesCount));
        if (mLikesCount>0) {
            mLikesCountBadge.setVisibility(View.VISIBLE);
        }
        if (mUserLiked) {
            mFavFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
        }
        mFavFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnector.runOperation(new ChronosLikeUnlike(mUserId, !mUserLiked),false);
            }
        });

        if (mUser.getPhoto().isEmpty()) {
            userPhotoUri="null";
            UiHelper.writeLog(mContext.getString(R.string.no_photo_assigned)+mUser.getFullName());
        } else {
            userPhotoUri = mUser.getPhoto();
        }
        //сначала пробуем взять фото из кеша, при неудаче грузим из сети, делаем ресайз для соблюдения пропорций
        DataManager.getInstance().getPicasso()
                .load(userPhotoUri)
                .error(R.drawable.user_bg)
                .placeholder(R.drawable.user_bg)
                .resize((int) DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size), (int) (DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size)/ AspectRatioImageView.getDefaultAspectRatio()))
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        UiHelper.writeLog(mContext.getString(R.string.success_photo_loaded_from_cache)+mUser.getFullName());
                    }
                    @Override
                    public void onError() {
                        UiHelper.writeLog(mContext.getString(R.string.error_load_photo_from_cache) + mUser.getFullName());
                        DataManager.getInstance().getPicasso()
                                .load(userPhotoUri)
                                .error(R.drawable.user_bg)
                                .placeholder(R.drawable.user_bg)
                                .resize((int) DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size), (int) (DevintensiveApplication.getContext().getResources().getDimension(R.dimen.profile_image_size)/AspectRatioImageView.getDefaultAspectRatio()))
                                .centerCrop()
                                .into(mProfileImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        UiHelper.writeLog(mContext.getString(R.string.success_photo_loaded_from_network) + mUser.getFullName());
                                    }
                                    @Override
                                    public void onError() {
                                        UiHelper.writeLog(mContext.getString(R.string.error_load_photo_from_network) + userPhotoUri);
                                    }
                                });
                    }
                });
    }

    public void onOperationFinished(final ChronosLikeUnlike.Result result) {
        if (result.isSuccessful()) {

            if (result.getOutput().getData().getResponseMessage().equals(mContext.getString(R.string.success_like_message))
                    || result.getOutput().getData().getResponseMessage().equals(mContext.getString(R.string.success_unlike_message))){
                    int likesCount=result.getOutput().getData().getLikesBy().size();
                    boolean isMyLike=false;
                    if ((likesCount==0)) {
                        //если вообще нет лайков скрываем счетчик и устанавливаем пустое сердечко
                        mFavFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                        mLikesCountBadge.setVisibility(View.GONE);
                    } else {
                        //ищем свой лайк
                        for (int i=0; i<likesCount;i++) {
                            if (result.getOutput().getData().getLikesBy().get(i).compareTo(String.valueOf(mMyUserId))==0)
                                isMyLike=true;
                        }
                        if (isMyLike) {
                            //устанавливаем полное сердечко, если вдруг лайк только что поставили
                            mFavFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                            //mUserLiked=isMyLike;
                        } else {
                            //сбрасываем картинку, если вдруг лайк только что снялся
                            mFavFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                            //mUserLiked=isMyLike;
                        }
                        //если есть лайки обновляем счетчик лайков и показываем его
                        mLikesCountBadge.setText(String.valueOf(result.getOutput().getData().getLikesBy().size()));
                        mLikesCountBadge.setVisibility(View.VISIBLE);
                    }
                //mUser.setLikesCount(String.valueOf(likesCount));
                mUser.setIsMyLike(String.valueOf(isMyLike));
                mUserLiked=isMyLike;
                UiHelper.writeLog(result.getOutput().getData().getResponseMessage());
                showToast(result.getOutput().getData().getResponseMessage());
            } else {
                //если ошибочный пароль или токен или другая ошибка
                UiHelper.writeLog(result.getOutput().getData().getResponseMessage());
                showToast(result.getOutput().getData().getResponseMessage());
            }
        } else {
            //ошибки кроноса выведутся здесь
            UiHelper.writeLog(mContext.getString(R.string.error_internal_message));
            showToast(getString(R.string.error_internal_message)+" at ChronosLikeUnlike");
        }
        hideProgress();
    }

    public static void setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        View view = listAdapter.getView(0, null, listView);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int totalHeight = view.getMeasuredHeight() * listAdapter.getCount();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount()-1);
        listView.setLayoutParams(params);
        listView.requestLayout();
        }
}
