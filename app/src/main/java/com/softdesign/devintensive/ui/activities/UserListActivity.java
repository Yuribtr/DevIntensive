package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;

import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends BaseActivity implements SearchView.OnQueryTextListener{
    private static final String TAG = ConstantManager.TAG_PREFIX + " UserListActivity";

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private RecyclerView mRecyclerView;

    private DataManager mDataManager;
    private UsersAdapter mUsersAdapter;
    private ArrayList<UserListRes.Datum> mUsers;


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



        if (savedInstanceState!=null && mUsers==null){
            mUsers = savedInstanceState.getParcelableArrayList("mUsers");
        }
        setupToolbar();
        setupDrawer();
        loadUsers();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mUsers", mUsers);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState!=null && mUsers==null){
//            mUsers = savedInstanceState.getParcelableArrayList("mUsers");
//        }
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

    private void loadUsers() {
        showProgress();

        if (mUsers==null) {
            Call<UserListRes> call = mDataManager.getUserList();

            call.enqueue(new Callback<UserListRes>() {
                @Override
                public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                    if (response.code()==200) {
                        try {
                            mUsers = response.body().getData();
                            mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
                                @Override
                                public void onUserItemClickListener(int position) {
                                    //передаем данные в новую активити через Data Transfer Objects
                                    showProgress();
                                    UserDTO userDTO = new UserDTO(mUsers.get(position));
                                    Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                                    profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                                    //profileIntent.putParcelableArrayListExtra("mUsers", mUsers);
                                    startActivity(profileIntent);
                                    hideProgress();
                                }
                            });
                            mRecyclerView.setAdapter(mUsersAdapter);
                        } catch (NullPointerException e) {
                            Log.e(TAG, e.toString());
                            showSnackbar(getString(R.string.error_null_pointer));
                        } finally {
                            hideProgress();
                        }
                    } else if (response.code()==404) {
                        showSnackbar(getString(R.string.error_not_found));
                    } else {
                        showSnackbar(getString(R.string.error_unknown));
                    }
                }

                @Override
                public void onFailure(Call<UserListRes> call, Throwable t) {
                    //// TODO: обработать ошибки ретрофита
                }
            });

        } else {
            mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
                @Override
                public void onUserItemClickListener(int position) {
                    //передаем данные в новую активити через Data Transfer Objects
                    showProgress();
                    UserDTO userDTO = new UserDTO(mUsers.get(position));
                    Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                    profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                    //profileIntent.putParcelableArrayListExtra("mUsers", mUsers);
                    startActivity(profileIntent);
                    hideProgress();
                }
            });
            mRecyclerView.setAdapter(mUsersAdapter);
            hideProgress();
        }
    }

    private void setupDrawer() {
        //// TODO: 14.07.2016 реализовать переход в другое активити при клике по элементу меню в NavigationDrawer
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

}