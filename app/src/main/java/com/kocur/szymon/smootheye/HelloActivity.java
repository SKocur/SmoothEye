package com.kocur.szymon.smootheye;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * <h1>HelloActivity</h1>
 *
 * This activity is displayed once, on first start up of application.
 */
public class HelloActivity extends AppCompatActivity {

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkCameraPermission()) {
            setContentView(R.layout.activity_hello);

            findViewById(R.id.hello_button_letsgo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestCameraPermission();
                }
            });
        } else {
            Intent intent = new Intent(this, SwipeCore.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("useFlash", false);
            intent.putExtra("fragmentID", 1);
            startActivity(intent);
        }
    }

    /**
     * If permission was granted this method executes {@link SwipeCore}.
     * In other case it do nothing.
     *
     * @param requestCode ID of permission code.
     * @param permissions Array of permissions.
     * @param grantResults Integer to check if permission was granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(HelloActivity.this, SwipeCore.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            intent.putExtra("fragmentID", 1);
            startActivity(intent);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);

            return;
        }
    }

    private boolean checkCameraPermission() {
        String permission = "android.permission.CAMERA";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
