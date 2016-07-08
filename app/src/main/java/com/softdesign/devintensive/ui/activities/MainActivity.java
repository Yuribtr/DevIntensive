package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.Manifest.*;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.squareup.picasso.Picasso;

import android.graphics.BitmapFactory;
import android.widget.RelativeLayout;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    private ImageView mCallBtn, mEmailBtn, mVkBtn, mGitBtn;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout drawer_l;
    private DrawerLayout mNavigationDrawer;
    private FloatingActionButton mFab;


    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout mAppBarLayout;
    private ImageView mProfileImage;

    private ImageView avatar_iv;
    private NavigationView navigation_v;
    private EditText mUserPhone, mUserMail, mUserVK, mUserGit, mUserBio;

    private List<EditText> mUserInfoViews;
    private AppBarLayout.LayoutParams mAppbarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.d(TAG, "onCreate");

        mDataManager= DataManager.getInstance();

        mCallBtn = (ImageView) findViewById(R.id.call_btn);
        mEmailBtn = (ImageView) findViewById(R.id.email_btn);
        mVkBtn = (ImageView) findViewById(R.id.vk_btn);
        mGitBtn = (ImageView) findViewById(R.id.git_btn);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);


        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);

        mUserPhone = (EditText) findViewById(R.id.phone_at);
        mUserMail = (EditText) findViewById(R.id.email_et);
        mUserVK = (EditText) findViewById(R.id.vk_et);
        mUserGit = (EditText) findViewById(R.id.repository_et);
        mUserBio = (EditText) findViewById(R.id.about_et);

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mCallBtn.setOnClickListener(this);
        mEmailBtn.setOnClickListener(this);
        mVkBtn.setOnClickListener(this);
        mGitBtn.setOnClickListener(this);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);
        setupToolbar();
        setupDrawer();
        loadUserInfoValue();
        Picasso.with(this).load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto)//// TODO: сделать placeholder (1:38) трансформ и кроп
                .into(mProfileImage);
        drawer_l = (DrawerLayout) findViewById(R.id.navigation_drawer);
        navigation_v = (NavigationView) findViewById(R.id.navigation_view);
        avatar_iv = (ImageView) navigation_v.getHeaderView(0).findViewById(R.id.avatar);
        avatar_iv.setImageBitmap(RoundedAvatarDrawable.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));

        if (savedInstanceState == null) {
            //активити запускается впервые
        } else {
            //активити уже создавалось
            mCurrentEditMode= savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY,0);
            changeEditMode(mCurrentEditMode);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.d(TAG, "onRestart");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                mCurrentEditMode=mCurrentEditMode==0?changeEditMode(1):changeEditMode(0);
                break;
            case R.id.profile_placeholder:
                //// TODO: сделать загрузку фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_btn:
                openDialPanel(mUserPhone.getText().toString());
                break;
            case R.id.email_btn:
                openEmail(mUserMail.getText().toString());
                break;
            case R.id.vk_btn:
                openSite(mUserVK.getText().toString());
                break;
            case R.id.git_btn:
                openSite(mUserGit.getText().toString());
                break;
//            case R.id.call_img:
//                showProgress();
//                runWithDelay();
//                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            if(drawer_l.isDrawerOpen(GravityCompat.START)) {
                drawer_l.closeDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.d(TAG, "onSaveInstanceState");
//        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppbarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                             @Override
                                                             public boolean onNavigationItemSelected(MenuItem item) {
                                                                 showSnackbar(item.getTitle().toString());
                                                                 item.setChecked(true);
                                                                 mNavigationDrawer.closeDrawer(GravityCompat.START);
                                                                 return false;
                                                             }
                                                         }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null){
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null){
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }

    private int changeEditMode(int mode)  {
        if (mode == 1) {
            mFab.setImageResource(R.drawable.ic_done_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

            EditText editText = (EditText) findViewById(R.id.phone_at);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            editText.requestFocus();
            return mode;
        }
        else {

            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            saveUserInfoValue();
            return mode;
        }
    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i=0;i<userData.size();i++){
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void loadPhotoFromGallery () {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        takeGalleryIntent.setType(ConstantManager.FILE_TYPE_IMAGE);
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    private void loadPhotoFromCamera () {
        if (ContextCompat.checkSelfPermission(this, permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                //// TODO: обработать ошибку
            }

            if (mPhotoFile !=null) {
                //// TODO: передать фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    permission.CAMERA,
                    permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
            Snackbar.make(mCoordinatorLayout, R.string.message_permission_needed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.message_allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();//// TODO: 01:37
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length==2){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //// TODO: тут обрабатываем разрешение 1 (01:33)
            }
        }
        if (grantResults[1]==PackageManager.PERMISSION_GRANTED) {
            //// TODO: тут обрабатываем разрешение 2
        }
    }

    private void hideProfilePlaceholder () {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder () {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    private void lockToolbar (){
        mAppBarLayout.setExpanded(true, true);
        mAppbarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppbarParams);
    }

    private void unlockToolbar (){
        mAppbarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL|AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppbarParams);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String [] selectItems = {getString(R.string.user_profile_dialog_gallery),getString(R.string.user_profile_dialog_camera),getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem){
                            case 0:
                                //// TODO: загрузить из галлереи
                                loadPhotoFromGallery();
                                //showSnackbar("загрузить из галлереи");
                                break;
                            case 1:
                                //// TODO: загрузить из камеры
                                loadPhotoFromCamera();
                                //showSnackbar("загрузить из камеры");
                                break;
                            case 2:
                                //// TODO: cancel
                                dialog.cancel();
                                showSnackbar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(ConstantManager.FILE_NAME_TYPE).format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ConstantManager.FILE_NAME_EXT, storageDir);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, ConstantManager.FILE_MIME_TYPE_IMAGE);
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());
        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return image;
    }

    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(mSelectedImage)
                .into(mProfileImage);
        ////TODO: сделать placeholder (1:38) трансформ и кроп
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    private void openSite (String url) {
        if (!url.isEmpty()) {
            //simple check if url have protocol name
            url = !url.startsWith("http://") ? "http://" + url : url;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            String title = getResources().getText(R.string.message_choose_browser).toString();
            Intent chooser = Intent.createChooser(intent, title);
            startActivity(chooser);
        } else {
            showToast(getString(R.string.error_url_message));
        }
    }
    private void openEmail (String email) {
        //simple check if address exist
        if (!email.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject+R.string.app_name);
            intent.setType(ConstantManager.EMAIL_TYPE);
            try {
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                showToast(getString(R.string.no_email_programms_installed));
            }
        } else {
            showToast(getString(R.string.error_email_message));
        }
    }
    private void openDialPanel (String phone){
        if (ContextCompat.checkSelfPermission(this, permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            if (!phone.isEmpty()) {
                Uri uri = Uri.parse("tel:" + phone);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(callIntent);
            } else {
                showToast(getString(R.string.error_tel_message));
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    permission.CALL_PHONE,
            }, ConstantManager.PHONE_CALL_REQUEST_PERMISSION_CODE);
            Snackbar.make(mCoordinatorLayout, R.string.message_permission_needed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.message_allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

//       protected void runWithDelay (){
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //TODO:выполнить с задержкой
//                hideProgress();
//            }
//        },5000);
//    }
}
//// TODO: 01:42