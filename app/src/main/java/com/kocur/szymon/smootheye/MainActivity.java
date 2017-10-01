package com.kocur.szymon.smootheye;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, QRCaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // settings
        intent.putExtra(QRCaptureActivity.AutoFocus, true);
        intent.putExtra(QRCaptureActivity.UseFlash, false);
        startActivity(intent);
        finish();
    }
}
