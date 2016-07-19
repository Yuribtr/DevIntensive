package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.managers.UserModelManager;
import com.softdesign.devintensive.data.network.GetUserService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.GetUserRes;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.data.storage.models.Repository;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.User;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.AppConfig;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {
    private Button mSignIn;
    private TextView mRememberPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;
    private DataManager mDataManager;
    private boolean autoLoginSuccess=false;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();


        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mSignIn = (Button) findViewById(R.id.login_btn);
        mRememberPassword = (TextView) findViewById(R.id.password_lost_tv);
        mLogin = (EditText) findViewById(R.id.login_email_et);
        mPassword = (EditText) findViewById(R.id.login_password_et);

        mLogin.setEnabled(false);
        mPassword.setEnabled(false);
        mSignIn.setEnabled(false);

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);

        showProgress();
        showToastShort("Проверяем автовход");
        //проверяем на возможность автовхода
        checkAutoLogin();

        if (!autoLoginSuccess) {
            hideProgress();
            mLogin.setEnabled(true);
            mPassword.setEnabled(true);
            mSignIn.setEnabled(true);
            EditText editText = (EditText) findViewById(R.id.login_email_et);
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private boolean checkAutoLogin (){
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            GetUserService service = ServiceGenerator.createService(GetUserService.class);
            String userId = mDataManager.getPreferencesManager().getUserId();
            String authToken = mDataManager.getPreferencesManager().getAuthToken();
            if (userId.isEmpty() || authToken.isEmpty()) return false;
            Call<GetUserRes> call = service.getUser(userId);
            call.enqueue(new Callback<GetUserRes>() {
                             @Override
                             public void onResponse(Call<GetUserRes> call, Response<GetUserRes> response) {
                                 if (response.code()==200) {
                                     showSnackbar(getString(R.string.success_token_message));
                                     loginSuccess(response.body());
                                     autoLoginSuccess = true;
                                 } else if (response.code()==404) {
                                     showSnackbar(getString(R.string.error_login_or_password));
                                 } else if (response.code()==401) {
                                     showSnackbar(getString(R.string.error_token_message));
                                 } else {
                                     showSnackbar(getString(R.string.error_unknown));
                                 }
                             }
                             @Override
                             public void onFailure(Call<GetUserRes> call, Throwable t) {

                             }
                         }
            );
        } else {
            showSnackbar(getString(R.string.error_network_is_not_available));

        }
        return autoLoginSuccess;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                mLogin.clearFocus();
                mPassword.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLogin.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                signIn();
                break;
            case R.id.password_lost_tv:
                rememberPassword();
                break;
        }
    }

    private void showSnackbar (String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword (){
        Intent rememberIntent = new Intent (Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess (UserModelRes userModel){
        UserModelManager.saveUserModelToPreferenses(mDataManager, userModel);
        saveUserValues(userModel);
        //сохранение базы и вложенный запрос пользователей нужно вынести в отдельный поток
        saveUserInDb();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
                Intent loginIntent = new Intent(AuthActivity.this, UserListActivity.class);
                startActivity(loginIntent);
            }
        }, AppConfig.START_DELAY);
    }

    private void loginSuccess (GetUserRes userModel){
        UserModelManager.saveUserModelToPreferenses(mDataManager, userModel);
        saveUserValues(userModel);
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void signIn (){
        showProgress();
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(),mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code()==200) {
                        loginSuccess(response.body());
                    } else if (response.code()==404) {
                        showSnackbar(getString(R.string.error_login_or_password));
                    } else {
                        showSnackbar(getString(R.string.error_unknown));
                    }
                }
                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    //// TODO: обработать ошибки ретрофита
                }
            });
        } else {
            showSnackbar(getString(R.string.error_network_is_not_available));
        }
        hideProgress();
    }

    private void saveUserValues (UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserValues (GetUserRes userModel) {
        int[] userValues = {
                userModel.getData().getProfileValues().getRait(),
                userModel.getData().getProfileValues().getLinesCode(),
                userModel.getData().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserInDb() {
        Call<UserListRes> call = mDataManager.getUserListFromNetork();
        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code()==200) {
                        List<Repository> allRepositories = new ArrayList<Repository>();
                        List<User> allUsers = new ArrayList<User>();

                        for (UserListRes.Datum  userRes : response.body().getData()) {
                            //сохраняем список репозиториев каждого пользователя в общий список
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            //сохраняем каждого пользователя в общий список
                            allUsers.add(new User(userRes));
                        }
                        mRepositoryDao.insertOrReplaceInTx(allRepositories);
                        mUserDao.insertOrReplaceInTx(allUsers);
                    } else {
                        showSnackbar(getString(R.string.error_user_list_receive));
                        Log.e(TAG, "onResponse: " + String.valueOf(response.errorBody().source()));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } finally {
                    //hideProgress();
                }
            }
            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                //// TODO: обработать ошибки ретрофита
            }
        });
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.Datum userData){
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();

        for (UserListRes.Datum.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }
        return repositories;
    }


}
