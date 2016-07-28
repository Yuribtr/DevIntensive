package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;
import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.database.ChronosSaveUsersToDb;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.ChronosAutoLogin;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.UiHelper;

public class SplashActivity extends BaseActivity{
    private ChronosConnector mConnector;
    private DataManager mDataManager;
    private Context mContext;
    private TextView mSplashMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        showProgress();
        mContext= DevintensiveApplication.getContext();
        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);
        mDataManager = DataManager.getInstance();
        mSplashMessage = (TextView) findViewById(R.id.splash_message);
        mSplashMessage.setText(R.string.connecting_to_server_message);
        UiHelper.writeLog(mContext.getString(R.string.connecting_to_server_message));
        //запускаем поток автовхода
        mConnector.runOperation(new ChronosAutoLogin(), false);
    }

    public void onOperationFinished(final ChronosAutoLogin.Result result) {
        if (result.isSuccessful()) {
            mSplashMessage.setText(result.getOutput());
            //если сохраненный токен подошел
            if (result.getOutput().equals(mContext.getString(R.string.success_token_message))){
                UiHelper.writeLog(mContext.getString(R.string.success_token_message));
                //запускаем поток загрузки списка в базу и перехода в главное активити
                mConnector.runOperation(new ChronosSaveUsersToDb(), false);
            } else {
                //если ошибочный пароль или токен или другая ошибка - кидаем на активити входа
                UiHelper.writeLog(result.getOutput());
                Intent loginIntent = new Intent(SplashActivity.this, AuthActivity.class);
                startActivity(loginIntent);
            }
        } else {
            //ошибки кроноса выведутся здесь
            UiHelper.writeLog(mContext.getString(R.string.error_internal_message));
            showToast(getString(R.string.error_internal_message)+" at ChronosAutoLogin");
            hideProgress();
        }
    }

    public void onOperationFinished(final ChronosSaveUsersToDb.Result result) {
        hideProgress();
        if (result.isSuccessful()) {
            UiHelper.writeLog(result.getOutput());
            mSplashMessage.setText(result.getOutput());
            //если все в порядке - переходим в главное активити
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            //ошибки кроноса выведутся здесь
            UiHelper.writeLog(mContext.getString(R.string.error_internal_message));
            showToast(getString(R.string.error_internal_message)+" at ChronosSaveUsersToDb");
        }
    }

    @Override
    protected void onPause() {
        mConnector.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnector.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mConnector.onSaveInstanceState(outState);
    }

}
