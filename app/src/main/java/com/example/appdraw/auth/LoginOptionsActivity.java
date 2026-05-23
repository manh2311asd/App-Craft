package com.example.appdraw.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdraw.R;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Vũ Quang Vinh
 * @version 1.0
 */
public class LoginOptionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);

        findViewById(R.id.btn_goto_login).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        findViewById(R.id.btn_goto_register).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}
