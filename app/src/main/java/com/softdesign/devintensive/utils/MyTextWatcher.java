package com.softdesign.devintensive.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class MyTextWatcher implements TextWatcher {
    private int maskType;
    public EditText editText;
    private CharSequence temp;
    private CharSequence mText;

    private static final Pattern sPattern
            = Pattern.compile("^[+]{1}[-0-9]{1}\\s[-0-9]{3}\\s[-0-9]{3}-[-0-9]{2}-[-0-9]{2}");

    public MyTextWatcher(EditText editText, int maskType){
        super();
        this.editText = editText;
        this.maskType = maskType;
    }

    private boolean isValid(CharSequence s) {
        return sPattern.matcher(s).matches();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after){
        temp = s.toString();
        //mText = isValid(s) ? new CharSequence(s)  : "";
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        if (!isValid(s))
        {
            editText.setText(temp);
        }
    }


}
