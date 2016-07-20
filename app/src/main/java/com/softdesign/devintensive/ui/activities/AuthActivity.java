package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.database.ChronosSaveUsersToDb;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.ChronosAutoLogin;
import com.softdesign.devintensive.data.network.ChronosLogin;
import com.softdesign.devintensive.data.storage.models.RepositoryDao;
import com.softdesign.devintensive.data.storage.models.UserDao;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AuthActivity extends BaseActivity implements View.OnClickListener {
    private Button mSignIn;
    private TextView mRememberPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;
    private DataManager mDataManager;
    private boolean autoLoginSuccess=false;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;
    private ChronosConnector mConnector;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);
        mContext = DevintensiveApplication.getContext();
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
        mLogin.setEnabled(true);
        mPassword.setEnabled(true);
        mSignIn.setEnabled(true);
        EditText editText = (EditText) findViewById(R.id.login_email_et);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onBackPressed() {
        // временное решение, чтобы случайно не выскочить на заставку
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                showProgress();
                mLogin.clearFocus();
                mPassword.clearFocus();
                //принудительно скрываем экранную клавиатуру
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLogin.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);
                //логинимся в отдельном потоке
                mConnector.runOperation(new ChronosLogin(mLogin.getText().toString(),mPassword.getText().toString()), false);
                break;
            case R.id.password_lost_tv:
                rememberPassword();
                break;
        }
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

    public void onOperationFinished(final ChronosLogin.Result result) {
        if (result.isSuccessful()) {
            //если сохраненный токен подошел
            if (result.getOutput().equals(mContext.getString(R.string.success_login_message))){
                //запускаем поток загрузки списка в базу и перехода в главное активити
                mConnector.runOperation(new ChronosSaveUsersToDb(), false);
            } else {
                //если ошибочный пароль или токен или другая ошибка выводим сообщение
                showSnackbar(result.getOutput());
                hideProgress();
            }
        } else {
            //ошибки кроноса выведутся здесь
            showToast(getString(R.string.error_internal_message)+" at ChronosAutoLogin");
            hideProgress();
        }
    }

    public void onOperationFinished(final ChronosSaveUsersToDb.Result result) {
        hideProgress();
        if (result.isSuccessful()) {
            Intent loginIntent = new Intent(AuthActivity.this, MainActivity.class);
            startActivity(loginIntent);
        } else {
            //ошибки кроноса выведутся здесь
            showToast(getString(R.string.error_internal_message)+" at ChronosSaveUsersToDb");
        }
    }

    private void showSnackbar (String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword (){
        Intent rememberIntent = new Intent (Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }
}
