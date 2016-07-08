package com.softdesign.devintensive.ui.activities;

        import android.content.Context;
        import android.content.Intent;
        import com.softdesign.devintensive.R;

        import android.hardware.input.InputManager;
        import android.os.Bundle;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.widget.EditText;

        import java.util.concurrent.TimeUnit;

        import butterknife.BindView;
        import butterknife.ButterKnife;
        import butterknife.OnClick;
        import butterknife.Optional;

public class AuthActivity extends AppCompatActivity {
    @Nullable
    @BindView(R.id.login_btn) Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_window);
        ButterKnife.bind(this);

        EditText editText = (EditText) findViewById(R.id.login_email);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editText.requestFocus();
    }

    @Optional
    @OnClick(R.id.login_btn)
    void submit(){
        Intent intent = new Intent(AuthActivity.this,MainActivity.class);
        startActivity(intent);
    }
}
