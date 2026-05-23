package com.example.appdraw.challenge;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appdraw.R;
import androidx.appcompat.widget.Toolbar;

/**
 * Mảng chức năng được phân công và phát triển.
 * @author Đặng Thị Hồng Vân
 * @version 1.0
 */
public class ChallengeEntryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_entry_list);

        Toolbar toolbar = findViewById(R.id.toolbar_entry_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        String title = getIntent().getStringExtra("CHALLENGE_TITLE");
        if (title != null) {
            TextView tvTitle = findViewById(R.id.tv_toolbar_challenge_title);
            if (tvTitle != null) {
                tvTitle.setText(title);
            }
        }
    }
}
