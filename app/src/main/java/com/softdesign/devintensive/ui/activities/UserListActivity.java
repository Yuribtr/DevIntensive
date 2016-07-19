package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.TransformRoundedImage;
import com.squareup.picasso.Picasso;
import com.softdesign.devintensive.data.database.ChronosLoadUsersFromDb;
import java.util.List;

public class UserListActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;
    private MenuItem mSearchitem;
    private NavigationView navigation_v;
    private DrawerLayout drawer_l;
    private DrawerLayout mNavigationDrawer;
    private TextView  mUserFio, mUserEmail;
    private ImageView avatar_iv;
    private String mQuery;
    private ChronosConnector mConnector;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);

        setContentView(R.layout.activity_user_list);

        mDataManager = DataManager.getInstance();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);

        mRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mHandler = new Handler();

        drawer_l = (DrawerLayout) findViewById(R.id.navigation_drawer);
        navigation_v = (NavigationView) findViewById(R.id.navigation_view);
        mUserFio = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_name_txt);
        mUserEmail = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_email_txt);
        avatar_iv = (ImageView) navigation_v.getHeaderView(0).findViewById(R.id.avatar);
        if (avatar_iv != null)
            Picasso.with(this)
                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                    .transform(new TransformRoundedImage())
                    .into(avatar_iv);
        //подгружаем ФИО и почту в боковое меню
        mUserFio.setText(mDataManager.getPreferencesManager().loadUserFio());
        mUserEmail.setText(mDataManager.getPreferencesManager().loadUserEmail());

        setupToolbar();
        setupDrawer();
        loadUsersFromDb();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbar(String message, int duration) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void loadUsersFromDb() {
        //поиск по базе в отдельном потоке Chronos
        showProgress();
        mConnector.runOperation (new ChronosLoadUsersFromDb(), false);
        showSnackbar(getString(R.string.users_list_loading_message), 3000);
    }

    public void onOperationFinished(final ChronosLoadUsersFromDb.Result result) {
        hideProgress();
        if (result.isSuccessful()) {
            mUsers = result.getOutput();
            if (mUsers.size()==0) {
                showSnackbar(getString(R.string.error_users_list_empty));
            } else {
                showUsers(mUsers);
            }
        } else {
            showSnackbar(getString(R.string.error_user_list_receive));
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.team_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                             @Override
                                                             public boolean onNavigationItemSelected(MenuItem item) {
                                                                 if (item.getItemId()==R.id.user_profile_menu) {
                                                                     Intent mainIntent = new Intent(UserListActivity.this, MainActivity.class);
                                                                     startActivity(mainIntent);
                                                                 }
                                                                 item.setChecked(true);
                                                                 mNavigationDrawer.closeDrawer(GravityCompat.START);
                                                                 return false;
                                                             }
                                                         }
        );
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        mSearchitem = menu.findItem(R.id.search_action);
        //подключаем SearchView не из библиотеки поддержки!
        android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(mSearchitem);
        searchView.setQueryHint(getString(R.string.search_enter_user_name));
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                showUserByQuery(newText);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void showUsers(List<User> users) {
        mUsers = users;
        mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
            @Override
            public void onUserItemClickListener(int position) {
                //передаем данные профиля конкретного пользователя в новую активити через Data Transfer Objects
                showProgress();
                UserDTO userDTO = new UserDTO(mUsers.get(position));
                Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                startActivity(profileIntent);
                hideProgress();
            }
        });//чтобы view не перерисовывались findviewbyid не вызываются
        mRecyclerView.swapAdapter(mUsersAdapter, false);
    }

    private void showUserByQuery (String query) {
        mQuery = query;
        //убираем задержку, если очищена строка поиска
        if (query.isEmpty()) {
            showUsers(mDataManager.getUserListByName(mQuery));
        } else {

            Runnable searchUsers = new Runnable() {
                @Override
                public void run() {
                    showUsers(mDataManager.getUserListByName(mQuery));
                }
            };
            //потушить все предыдущие вызовы getUserListByName через searchUsers
            mHandler.removeCallbacks(searchUsers);
            mHandler.postDelayed(searchUsers, ConstantManager.SEARCH_DELAY);
        }
    }

}
