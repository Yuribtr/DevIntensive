package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.RepositoriesAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileUserActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ImageView mProfileImage;
    private EditText mUserBio;
    private TextView mUserRating, mUserCodeLines, mUserProjects;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private ListView mRepoListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);
        mUserBio = (EditText) findViewById(R.id.about_et);//// TODO: проверить правльность присвоения идентификатора 01:29 №6
        mUserRating = (TextView) findViewById(R.id.user_info_rating_tv);//должно подтягиваться с activity_profile_user 01:30
        mUserCodeLines = (TextView) findViewById(R.id.user_info_code_lines_tv);
        mUserProjects = (TextView) findViewById(R.id.user_info_projects_tv);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);

        mRepoListView = (ListView) findViewById(R.id.repositories_list);//static_profile_content

        setupToolbar();
        initProfileData();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initProfileData(){
        UserDTO userDTO = getIntent().getParcelableExtra(ConstantManager.PARCELABLE_KEY);

        final List<String> repositories = userDTO.getRepositories();
        final RepositoriesAdapter repositoriesAdapter = new RepositoriesAdapter(this, repositories);
        mRepoListView.setAdapter(repositoriesAdapter);
        setListViewHeightBasedOnItems(mRepoListView);

        mRepoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://" + repositories.get(position)));
                startActivity(browseIntent);
            }
        });

        mUserBio.setText(userDTO.getBio());
        mUserRating.setText(userDTO.getRating());
        mUserCodeLines.setText(userDTO.getCodeLines());
        mUserProjects.setText(userDTO.getProjects());

        mCollapsingToolbarLayout.setTitle(userDTO.getFullName());

        if (userDTO.getPhoto()==null || userDTO.getPhoto().isEmpty()) {
        } else {
        Picasso.with(this)
                .load(userDTO.getPhoto())
                .placeholder(R.drawable.user_bg)
                .error(R.drawable.user_bg)
                .into(mProfileImage);
        }
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
