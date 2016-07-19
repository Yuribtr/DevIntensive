package com.softdesign.devintensive.utils;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.softdesign.devintensive.R;

import java.util.regex.Pattern;

public class UserFieldsWatcher implements android.text.TextWatcher {
    private int maskType;
    public EditText editText;
    private Pattern sPattern1 = Pattern.compile(ConstantManager.PATTERN_PHONE);
    private Pattern sPattern2 = Pattern.compile(ConstantManager.PATTERN_EMAIL);
    private Pattern sPattern3 = Pattern.compile(ConstantManager.PATTERN_VK_URL);
    private Pattern sPattern4 = Pattern.compile(ConstantManager.PATTERN_GIT_URL);

    public UserFieldsWatcher(EditText editText, int maskType){
        super();
        this.editText = editText;
        this.maskType = maskType;
    }

    private boolean isValid(CharSequence s) {
        boolean res=true;
        switch (maskType) {
            case 1:
                res=sPattern1.matcher(s).matches();
             break;
            case 2:
                res=sPattern2.matcher(s).matches();
                break;
            case 3:
                res=sPattern3.matcher(s).matches();
                break;
            case 4:
                res=sPattern4.matcher(s).matches();
                break;
        }
        return res;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after){
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        if (!isValid(s)) {
            editText.setTextColor(Color.RED);
        }
        else {
            editText.setTextColor(Color.BLACK);
        }
    }


}
