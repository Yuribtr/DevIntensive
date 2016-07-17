package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
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

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.List;

public class UserListActivity extends BaseActivity {
    private static final String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private RecyclerView mRecyclerView;
    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private List<User> mUsers;
    private MenuItem mSearchitem;

    private String mQuery;

    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mDataManager = DataManager.getInstance();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);

        mRecyclerView = (RecyclerView) findViewById(R.id.user_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


//// TODO: восстанавливать данные при повороте экрана

        mHandler = new Handler();

        setupToolbar();
        setupDrawer();
        loadUsersFromDb();
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

    private void loadUsersFromDb() {
        if (mDataManager.getUserListFromDb().size()==0) {
            showSnackbar("Список пользователей не может быть загружен");
        } else {
            //поиск по базе
            showUsers(mDataManager.getUserListFromDb());
        }
    }

    private void setupDrawer() {
        //// TODO: реализовать переход в другое активити при клике по элементу меню в NavigationDrawer
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
