package com.softdesign.devintensive.utils;

import android.content.IntentSender;

public interface ConstantManager {
    String TAG_PREFIX="DEV ";
    String COLOR_MODE_KEY="COLOR_MODE_KEY";
    String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    String USER_PHONE_KEY="USER_PHONE_KEY";
    String USER_MAIL_KEY="USER_MAIL_KEY";
    String USER_VK_KEY="USER_VK_KEY";
    String USER_REPOSITORY_KEY="USER_REPOSITORY_KEY";
    String USER_ABOUT_KEY="USER_ABOUT_KEY";
    String USER_PHOTO_KEY = "USER_PHOTO_KEY";
    String EMAIL_TYPE = "message/rfc822";
    String FILE_TYPE_IMAGE = "image/*";
    String FILE_MIME_TYPE_IMAGE = "image/jpeg";
    String FILE_NAME_TYPE = "yyyyMMdd_HHmmss";
    String FILE_NAME_EXT = ".jpg";

    String PATTERN_PHONE = "^[+]{1}[-0-9]{1}\\s[-0-9]{3}\\s[-0-9]{3}-[-0-9]{2}-[-0-9]{2}";
    String PATTERN_EMAIL = "^[a-zA-Z_0-9]{3,}@[a-zA-Z_0-9.]{2,}\\.[a-zA-Z0-9]{2,}$";
    String PATTERN_VK_URL = "^vk\\.com\\/[a-zA-Z_0-9.]+$";
    String PATTERN_GIT_URL = "^github\\.com\\/[a-zA-Z_0-9.]+$";

    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE = 99;
    int REQUEST_GALLERY_PICTURE = 88;

    int PERMISSION_REQUEST_SETTINGS_CODE = 101;
    int CAMERA_REQUEST_PERMISSION_CODE = 102;
    int PHONE_CALL_REQUEST_PERMISSION_CODE = 103;
}
