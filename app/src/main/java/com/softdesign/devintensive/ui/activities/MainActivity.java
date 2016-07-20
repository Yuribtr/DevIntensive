package com.softdesign.devintensive.ui.activities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import android.os.PersistableBundle;
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
import com.redmadrobot.chronos.ChronosConnector;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.ChronosPhotoUpload;
import com.softdesign.devintensive.utils.ConstantManager;
import com.softdesign.devintensive.utils.DevintensiveApplication;
import com.softdesign.devintensive.utils.TransformRoundedImage;
import com.softdesign.devintensive.utils.UserFieldsWatcher;
import com.squareup.picasso.Picasso;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private MenuItem mUser_profile_menu;
    private ChronosConnector mConnector;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //инициализирвем рабочие классы
        mContext= DevintensiveApplication.getContext();
        mConnector = new ChronosConnector();
        mConnector.onCreate(this, savedInstanceState);
        mDataManager= DataManager.getInstance();
        mUserId = mDataManager.getPreferencesManager().getUserId();
        //находим наши вью элементы в раскладке
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
        drawer_l = (DrawerLayout) findViewById(R.id.navigation_drawer);
        navigation_v = (NavigationView) findViewById(R.id.navigation_view);
        mUserFio = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_name_txt);
        mUserEmail = (TextView) navigation_v.getHeaderView(0).findViewById(R.id.user_email_txt);
        avatar_iv = (ImageView) navigation_v.getHeaderView(0).findViewById(R.id.avatar);
        //подключаем класс проверки ввода "на лету"
        mUserPhone.addTextChangedListener(new UserFieldsWatcher(mUserPhone, 1));
        mUserMail.addTextChangedListener(new UserFieldsWatcher(mUserMail, 2));
        mUserVK.addTextChangedListener(new UserFieldsWatcher(mUserVK, 3));
        mUserGit.addTextChangedListener(new UserFieldsWatcher(mUserGit, 4));
        //биндим элементы управления в список для облегчения работы в цикле
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
        //подключаем слушатели кликов
        mCallBtn.setOnClickListener(this);
        mEmailBtn.setOnClickListener(this);
        mVkBtn.setOnClickListener(this);
        mGitBtn.setOnClickListener(this);
        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        initUserFields();
        initUserInfoValue();

        if (savedInstanceState == null) {
            //активити запускается впервые
        } else {
            //активити уже создавалось
            mCurrentEditMode= savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY,0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public void onBackPressed() {
        // временное решение, чтобы случайно не выскочить на заставку
        moveTaskToBack(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mConnector.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                //если клик на главной кнопке - то переключаем режим редактирования с проверкой данных и их сохранением
                mCurrentEditMode=mCurrentEditMode==0?changeEditMode(1):changeEditMode(0);
                break;
            case R.id.profile_placeholder:
                //показываем меню выбора способа загрузки фото профиля
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.call_btn:
                //открываем панель набора номера
                openDialPanel(mUserPhone.getText().toString());
                break;
            case R.id.email_btn:
                //запускаем почтовый клиент
                openEmail(mUserMail.getText().toString());
                break;
            case R.id.vk_btn:
                //запускаем броузер
                openSite(mUserVK.getText().toString());
                break;
            case R.id.git_btn:
                //запускаем броузер
                openSite(mUserGit.getText().toString());
                break;
        }
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void showSnackbar(String message) {Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();}

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        mAppbarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        //устанавливаем иконку сэндвича и поведение
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.user_profile_menu);
        //добавляем слушателей дял нажатия кнопок в боковом меню
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                                                             @Override
                                                             public boolean onNavigationItemSelected(MenuItem item) {
                                                                 if (item.getItemId()==R.id.team_menu) {
                                                                     //переходим в список пользователей
                                                                     Intent mainIntent = new Intent(MainActivity.this, UserListActivity.class);
                                                                     startActivity(mainIntent);
                                                                 }
                                                                 if (item.getItemId()==R.id.clear_credentials) {
                                                                     //очищаем сохраненный токен
                                                                     mDataManager.getPreferencesManager().clearAuthToken();
                                                                     showToast(getString(R.string.token_deleted_message));
                                                                 }
                                                                 item.setChecked(true);
                                                                 //сворачиваем панель
                                                                 mNavigationDrawer.closeDrawer(GravityCompat.START);
                                                                 return false;
                                                             }
                                                         }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //в диалоге выбрано получение изображения с галлереи
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null){
                    showProgress();
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage, ConstantManager.FROM_GALLERY);
                }
                break;
            //в диалоге выбрано получение изображения с камеры
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null){
                    showProgress();
                    mSelectedImage = Uri.fromFile(mPhotoFile);
                    insertProfileImage(mSelectedImage, ConstantManager.FROM_CAMERA);
                }
                break;
        }
    }

    private int changeEditMode(int mode)  {
        if (mode == 1) {
            //включаем режим редактирования
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
            //если в полях ошибки - не даем выйти из режима редактирования
            if (!checkUserValues()) return 1;
            //выключаем режим редактирования
            mFab.setImageResource(R.drawable.ic_create_black_24dp);
            //разрешаем ввод в поля ввода
            for (EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            //скрываем изображение профиля
            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            //сохраняем поля и счетчики
            saveUserFields();
            return mode;
        }
    }

    public void setFocusEditText(int editTextId){
        //устанавливаем фокус на поле ввода с показом клавиатуры
        EditText editText = (EditText) findViewById(editTextId);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
    }

    private boolean checkUserValues() {
        //проводим проверку полей по маске по очереди с установкой фокуса в ошибочное поле
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
        //проверка текста по маске
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(testString);
        return m.matches();
    }

    private void initUserFields() {
        //подгружаем инфо пользователя в поля
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i=0;i<userData.size();i++){
            mUserInfoViews.get(i).setText(userData.get(i));
        }
        //подгружаем ФИО и почту в боковое меню
        mUserFio.setText(mDataManager.getPreferencesManager().loadUserFio());
        mUserEmail.setText(mDataManager.getPreferencesManager().loadUserEmail());
        //меняем заголовок программы (на всякий случай)
        setTitle(mDataManager.getPreferencesManager().loadUserFio());
        //загружаем фото пользователя с ресайзом
        Picasso.with(this).load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto)
                .error(R.drawable.userphoto)
                .resize(768,512)
                .centerCrop()
                .into(mProfileImage);
        //загружаем аватарку пользователя с ресайзом
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserAvatar())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .fit()
                .centerCrop()
                .transform(new TransformRoundedImage())
                .into(avatar_iv);
    }

    private void saveUserFields() {
        //сохраняем отредактированные поля
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfoViews) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void initUserInfoValue(){
        //загружаем счетчики в поля
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileValues();
        for (int i=0; i<userData.size(); i++) {
            mUserValueViews.get(i).setText(userData.get(i));
        }
    }

    private void loadPhotoFromGallery () {
        //загружаем фото с галлереи с проверкой разрешений
        if (ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            takeGalleryIntent.setType(ConstantManager.FILE_TYPE_IMAGE);
            startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_choose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);
        } else {
            //если разрешений нет, то отменить действие и запросить все разрешения
            ActivityCompat.requestPermissions(this, new String[]{
                    permission.CAMERA,
                    permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
            //на всякий случай напоминаем пользователю что без разрешений рыбку не съесть
            Snackbar.make(mCoordinatorLayout, R.string.message_permission_needed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.message_allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    private void loadPhotoFromCamera () {
        //загружаем фото с галлереи с проверкой разрешений
        if (ContextCompat.checkSelfPermission(this, permission.CAMERA)== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mPhotoFile !=null) {
                //передаем фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            //если разрешений нет, то отменить действие и запросить все разрешения (чтобы потом по два раза не спрашивать)
            ActivityCompat.requestPermissions(this, new String[]{
                    permission.CAMERA,
                    permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);
            //на всякий случай напоминаем пользователю что без разрешений рыбку не съесть
            Snackbar.make(mCoordinatorLayout, R.string.message_permission_needed, Snackbar.LENGTH_LONG)
                    .setAction(R.string.message_allow, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //обработка полученных разрешений
        if (requestCode==ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length==2){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showToast(getString(R.string.camera_permission_granted));
            }
        }
        if (grantResults[1]==PackageManager.PERMISSION_GRANTED) {
            showToast(getString(R.string.gallery_permission_granted));
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
        //создаем диалог изменения фото профиля
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:

                String [] selectItems = {getString(R.string.user_profile_dialog_gallery),getString(R.string.user_profile_dialog_camera),getString(R.string.user_profile_dialog_cancel)};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //принудительно скрываем экранную клавиатуру
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mUserPhone.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mUserEmail.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mUserVK.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mUserGit.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mUserBio.getWindowToken(), 0);
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
        //создаем пустой временный файл для передачи в приложение Камера
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

    private void insertProfileImage(Uri selectedImage, byte mode) {
        //делаем ресайз изображения так как огромные многомегабайтные картинки не грузятся
        Picasso.with(this)
                .load(mSelectedImage)
                .resize(768,512)
                .centerCrop()
                .into(mProfileImage);
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
        //здесь сохраняем фото на сервере и выключаем режим редактирования
        mConnector.runOperation(new ChronosPhotoUpload(mUserId, mSelectedImage, mode),false);
    }

    public void onOperationFinished(final ChronosPhotoUpload.Result result) {
        if (result.isSuccessful()) {
            if (result.getOutput().equals(mContext.getString(R.string.success_photo_update_message))){
                //выключаем режим редактирования чтобы показать что фото обновилось
                mCurrentEditMode=mCurrentEditMode==0?changeEditMode(1):changeEditMode(0);
                showSnackbar(result.getOutput());
            } else {
                //если ошибочный пароль или токен или другая ошибка
                showSnackbar(result.getOutput());
            }
        } else {
            //ошибки кроноса выведутся здесь
            showToast(getString(R.string.error_internal_message)+" at ChronosPhotoUpload");
        }
        hideProgress();
    }

    public void openApplicationSettings() {
        //открываем настройки нашего приложения в системе при отсутствии разрешений и запрете на показ запроса
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    private void openSite (String url) {
        if (url!=null && !url.isEmpty()) {
            //простая проверка на наличие имени протокола впереди - так ка кбез этого броузер не запускается
            url = !url.startsWith("http://") ? "https://" + url : url;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            //если несколько броузеров то можно выбирать
            String title = getResources().getText(R.string.message_choose_browser).toString();
            Intent chooser = Intent.createChooser(intent, title);
            startActivity(chooser);
        } else {
            showToast(getString(R.string.error_url_message));
        }
    }

    private void openEmail (String email) {
        //простая проверка адреса
        if (email!=null && !email.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType(ConstantManager.EMAIL_TYPE);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.email_subject+" "+R.string.app_name);
            intent.putExtra(Intent.EXTRA_TEXT, "");
            //если несколько почтовых программ то можно выбирать
            String title = getResources().getText(R.string.message_choose_email).toString();
            Intent chooser = Intent.createChooser(intent, title);
            try {
                startActivity(chooser);
            } catch (android.content.ActivityNotFoundException ex) {
                showToast(getString(R.string.no_email_programms_installed));
            }
        } else {
            showToast(getString(R.string.error_email_message));
        }
    }
    private void openDialPanel (String phone){
        if (ContextCompat.checkSelfPermission(this, permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            if (phone!=null && !phone.isEmpty()) {
                Uri uri = Uri.parse("tel:" + phone);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(callIntent);
            } else {
                showToast(getString(R.string.error_phone_message));
            }
        } else {
            //если разрешений нет, то отменить действие и запросить разрешение
            ActivityCompat.requestPermissions(this, new String[]{
                    permission.CALL_PHONE,
            }, ConstantManager.PHONE_CALL_REQUEST_PERMISSION_CODE);
            //на всякий случай напоминаем пользователю что без разрешений рыбку не съесть
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
