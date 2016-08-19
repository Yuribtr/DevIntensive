package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.softdesign.devintensive.data.network.ChronosLoadProfileImages;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.storage.models.DaoSession;
import com.softdesign.devintensive.data.storage.models.LikesByDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDTO;
import com.softdesign.devintensive.ui.adapters.ItemTouchHelperCallback;
import com.softdesign.devintensive.ui.adapters.UsersAdapter;
import com.softdesign.devintensive.ui.adapters.UsersItemAnimator;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.CustomClickListener;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.RecyclerItemClickListener;
import com.softdesign.devintensive.utils.TransformRoundedImage;
import com.softdesign.devintensive.utils.UiHelper;
import com.squareup.picasso.Picasso;
import com.softdesign.devintensive.data.database.ChronosLoadUsersFromDb;

import java.util.ArrayList;
import java.util.HashMap;
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
    private TextView  mUserFio, mUserEmail, likesCount;
    private ImageView avatar_iv, likesImage;
    private String mQuery;
    private ChronosConnector mConnector;
    private Handler mHandler;
    private Runnable mSearchUsers;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);
        setContentView(R.layout.activity_user_list);
        mContext= DevintensiveApplication.getContext();

        mDataManager = DataManager.getInstance();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mRecyclerView = (RecyclerView) findViewById(R.id.user_list);

//        mRecyclerView.addOnItemTouchListener(    new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override public void onItemClick(View view, int position) {
//                //UiHelper.writeLog("onItemClick "+view.getId());
//            }
//        }));

        mHandler = new Handler();

        drawer_l = (DrawerLayout) findViewById(R.id.navigation_drawer);
        navigation_v = (NavigationView) findViewById(R.id.navigation_view);
        mUserFio = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_name_txt);
        mUserEmail = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_email_txt);
        avatar_iv = (ImageView) navigation_v.getHeaderView(0).findViewById(R.id.avatar);

        if (avatar_iv != null)
            Picasso.with(this)
                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .fit()
                    .centerCrop()
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //слушаем нажатие кнопки Назад
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            //если боковое меню развернуто, сворачиваем её
            if(drawer_l.isDrawerOpen(GravityCompat.START)) {
                drawer_l.closeDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
                                                                 if (item.getItemId()==R.id.clear_credentials) {
                                                                     mDataManager.getPreferencesManager().clearAuthToken();
                                                                     UiHelper.writeLog(mContext.getString(R.string.token_deleted_message));
                                                                     //переходим в окно авторизации
                                                                     Intent authIntent = new Intent(UserListActivity.this, AuthActivity.class);
                                                                     startActivity(authIntent);
//                                                                     showToast(getString(R.string.token_deleted_message));
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

    private void setupAdapters () {
        UiHelper.writeLog("setupAdapters calling");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //// TODO: здесь подключаем анимацию
        mRecyclerView.setItemAnimator(new UsersItemAnimator());
        
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mUsersAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
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
                loadUsersFromDb(newText);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void loadUsersFromDb () {
        loadUsersFromDb ("");
    }

    private void loadUsersFromDb (String query) {
        UiHelper.writeLog("loadUsersFromDb called");
        mQuery = query;
        //убираем задержку, если очищена строка поиска
        if (query.isEmpty()) {
            mConnector.runOperation (new ChronosLoadUsersFromDb(), false);
        } else {
            if (mSearchUsers!=null) {
                //потушить все предыдущие вызовы ChronosLoadUsersFromDb правильным образом!!!
                mHandler.removeCallbacks(mSearchUsers);
                mSearchUsers=null;
            } else {
                mSearchUsers = new Runnable(){
                    @Override
                    public void run() {
                        mSearchUsers=null;
                        mConnector.runOperation (new ChronosLoadUsersFromDb(mQuery), false);
                    }
                };
                mHandler.postDelayed(mSearchUsers, ConstantManager.SEARCH_DELAY);
            }
        }
    }

    public void onOperationFinished(final ChronosLoadUsersFromDb.Result result) {
        if (result.isSuccessful()) {
            mUsers = result.getOutput();
            if (mUsers==null) {
                showSnackbar(getString(R.string.error_internal_message));
            } else {
                UiHelper.writeLog("showUsers calling");
                showUsers(mUsers);
            }
        } else {
            showSnackbar(getString(R.string.error_user_list_receive));
        }
        hideProgress();
        setupAdapters ();
    }

    private void showUsers(List<User> users) {
        if (users!=null) {
            mUsers = users;
            mUsersAdapter = new UsersAdapter(mUsers, new CustomClickListener() {
                @Override
                public void onUserItemClickListener(int position) {
                    //передаем данные профиля конкретного пользователя в новую активити через Data Transfer Objects
                    UserDTO userDTO = new UserDTO(mUsers.get(position));
                    Intent profileIntent = new Intent(UserListActivity.this, ProfileUserActivity.class);
                    profileIntent.putExtra(ConstantManager.PARCELABLE_KEY, userDTO);
                    startActivity(profileIntent);
                    hideProgress();
                }
            });//чтобы view не перерисовывались findviewbyid не вызываются
            mRecyclerView.swapAdapter(mUsersAdapter, false);
        }
    }

    public void onOperationFinished(final ChronosLoadProfileImages.Result result) {
        if (result.isSuccessful()) {
            mUsersAdapter = result.getOutput();
            if (mUsersAdapter==null) {
                showSnackbar(getString(R.string.error_internal_message));
            } else {
                mRecyclerView.swapAdapter(mUsersAdapter, false);
            }
        } else {
            showSnackbar(getString(R.string.error_user_list_receive));
        }
    }
}
