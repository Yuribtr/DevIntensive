package com.softdesign.devintensive.ui.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.softdesign.devintensive.data.network.FileUploadService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UploadPhotoRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.NetworkStatusChecker;
import com.softdesign.devintensive.utils.RoundedAvatarDrawable;
import com.softdesign.devintensive.utils.TransformRoundedImage;
import com.squareup.picasso.Picasso;

import android.graphics.BitmapFactory;
import android.widget.RelativeLayout;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;


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

    private TextView mUserValueRating, mUserValueCodeLines, mUserValueProjects, mUserFio, mUserEmail;
    private List<TextView> mUserValueViews;

    private AppBarLayout.LayoutParams mAppbarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private String mUserId;


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

        mUserValueRating = (TextView) findViewById(R.id.user_info_rating_tv);
        mUserValueCodeLines = (TextView) findViewById(R.id.user_info_code_lines_tv);
        mUserValueProjects = (TextView) findViewById(R.id.user_info_projects_tv);

        //mUserPhone.addTextChangedListener(new MyTextWatcher(mUserPhone, 1));//// TODO: доделать проверку на лету

        mUserInfoViews = new ArrayList<>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVK);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mUserValueViews = new ArrayList<>();
        mUserValueViews.add(mUserValueRating);
        mUserValueViews.add(mUserValueCodeLines);
        mUserValueViews.add(mUserValueProjects);

        mCallBtn.setOnClickListener(this);
        mEmailBtn.setOnClickListener(this);
        mVkBtn.setOnClickListener(this);
        mGitBtn.setOnClickListener(this);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        drawer_l = (DrawerLayout) findViewById(R.id.navigation_drawer);
        navigation_v = (NavigationView) findViewById(R.id.navigation_view);
        mUserFio = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_name_txt);
        mUserEmail = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_email_txt);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();

        mUserId = mDataManager.getPreferencesManager().getUserId();

        Picasso.with(this).load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto)//// TODO: сделать placeholder (1:38) трансформ и кроп
                .into(mProfileImage);

        avatar_iv = (ImageView) navigation_v.getHeaderView(0).findViewById(R.id.avatar);
        //avatar_iv.setImageBitmap(RoundedAvatarDrawable.getRoundedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)));
        if (avatar_iv != null)
            Picasso.with(this)
                    .load(mDataManager.getPreferencesManager().loadUserAvatar())
                    .transform(new TransformRoundedImage())
                    .into(avatar_iv);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
                    insertProfileImage(mSelectedImage, 1);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null){
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage, 2);
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
            setFocusEditText(R.id.phone_at);
            return mode;
        }
        else {
            if (!checkUserValues()) return 1;
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            saveUserFields();
            return mode;
        }
    }

    public void setFocusEditText(int editTextId){
        EditText editText = (EditText) findViewById(editTextId);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
    }

    private boolean checkUserValues() {
        for (EditText userValue : mUserInfoViews) {
            switch (userValue.getId()) {
                case R.id.phone_at:
                    if (!checkWithRegExp(userValue.getText().toString(), ConstantManager.PATTERN_PHONE)){
                        showToast(getString(R.string.error_phone_message));
                        setFocusEditText(R.id.phone_at);
                        return false;
                    }
                    break;
                case R.id.email_et:
                    if (!checkWithRegExp(userValue.getText().toString(), ConstantManager.PATTERN_EMAIL)){
                        showToast(getString(R.string.error_email_message));
                        setFocusEditText(R.id.email_et);
                        return false;
                    }
                    break;
                case R.id.vk_et:
                    if (!checkWithRegExp(userValue.getText().toString(), ConstantManager.PATTERN_VK_URL)){
                        showToast(getString(R.string.error_url_message));
                        setFocusEditText(R.id.vk_et);
                        return false;
                    }
                    break;
                case R.id.repository_et:
                    if (!checkWithRegExp(userValue.getText().toString(), ConstantManager.PATTERN_GIT_URL)){
                        showToast(getString(R.string.error_url_message));
                        setFocusEditText(R.id.repository_et);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    private boolean checkWithRegExp(String testString, String pattern){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(testString);
        return m.matches();
    }
//загружаем инфу пользователя в поля
    private void initUserFields() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i=0;i<userData.size();i++){
            mUserInfoViews.get(i).setText(userData.get(i));
        }

        //подгружаем ФИО и почту в боковое меню
        mUserFio.setText(mDataManager.getPreferencesManager().loadUserFio());
        setTitle(mDataManager.getPreferencesManager().loadUserFio()+" App");
        mUserEmail.setText(mDataManager.getPreferencesManager().loadUserEmail());
    }

    private void saveUserFields() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }
//загружаем счетчики в поля
    private void initUserInfoValue(){
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i=0; i<userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
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
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                dialog.cancel();
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

    private void insertProfileImage(Uri selectedImage, int mode) {
        Picasso.with(this)
                .load(mSelectedImage)
                .into(mProfileImage);
        ////TODO: сделать placeholder (1:38) трансформ и кроп
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        
        //// TODO: здесь посылаем фото на сервер
        updateSiteProfileImage(selectedImage, mode);
    }

    private void updateSiteProfileImage(Uri fileUri, int mode){
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            FileUploadService service = ServiceGenerator.createService(FileUploadService.class);
            File file=null;
            switch (mode) {
                case 1://если фото пришло с галлереи то преобразуем его путь
                    file = new File(getRealPathFromURI(this, fileUri));
                    break;
                case 2://если с камеры то напрямую подставляем
                    file = new File(fileUri.getPath());
                    break;
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);

            Call<UploadPhotoRes> call = service.uploadPhoto(mUserId, body);
            call.enqueue(new Callback<UploadPhotoRes>() {
                             @Override
                             public void onResponse(Call<UploadPhotoRes> call, Response<UploadPhotoRes> response) {
                                 if (response.code()==200) {
                                     showSnackbar(getString(R.string.success_photo_update_message));
                                 } else if (response.code()==404) {
                                     showSnackbar(getString(R.string.error_photo_update_message));
                                 } else {
                                     showSnackbar(getString(R.string.error_unknown));
                                 }
                             }
                             @Override
                             public void onFailure(Call<UploadPhotoRes> call, Throwable t) {
                                 //// TODO: обработать ошибки ретрофита
                             }
                         });

        } else {
            showSnackbar(getString(R.string.error_network_is_not_available));

        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    private void openSite (String url) {
        if (!url.isEmpty()) {
            //simple check if url have protocol name
            url = !url.startsWith("http://") ? "https://" + url : url;
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
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject+" "+R.string.app_name);
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
                showToast(getString(R.string.error_phone_message));
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
}
