package com.kocur.szymon.smootheye;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.vision.barcode.Barcode;
import com.kocur.szymon.smootheye.Barcode.BarcodeGraphic;
import com.kocur.szymon.smootheye.Barcode.CameraInit;
import com.kocur.szymon.smootheye.ui.camera.CameraSource;
import com.kocur.szymon.smootheye.ui.camera.CameraSourcePreview;
import com.kocur.szymon.smootheye.ui.camera.GraphicOverlay;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * <h1>QRCaptureActivity</h1>
 *
 * This class is core of everything in this universe.
 * It contains method responsible for scanning view (in separated thread) from Camera.
 * Opens second activity {@link ChoiceActivity} when scanner method finds QR-Code on the screen
 * with data saved in it with a given pattern.
 *
 * Pattern:
 * SEMENU-1-Place1-2-Place2-...-n-somePlace
 *
 * @author Szymon Kocur
 */
public final class QRCaptureActivity extends Fragment {

    private int WIDTH = 0;
    private int HEIGHT = 0;

    private boolean isOnSEMENU = false;
    private boolean useFlash = false;
    private int choice = 0;

    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    private HashMap<Integer,String> availableChoices;

    private TextToVoice textToVoice;
    private CameraInit cameraInit;

    private Handler handler;

    /**
     *  Data is formatted into QR Code in format:
     *
     *  SEMENU-1-Place1-2-Place2-3-ThirdElement
     *  and so on.
     *
     *  SEMENU has 0 index in the list
     *  1 has 1st index
     *  Place1 has 2nd index
     *  ...
     *  Place2 has 4th index
     *  ...
     *  ThirdElement has 6th index
     *
     *  All of these places can be divided by two and we can use this property to extract them.
     * */
    public static final int CHOICE_EVEN_SEPARATOR = 2;
    private final boolean USE_AUTO_FOCUS = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.barcode_capture, container, false);

        useFlash = getActivity().getIntent().getBooleanExtra("useFlash", false);

        textToVoice = new TextToVoice(getActivity().getApplicationContext());
        textToVoice.setSpeechRate(0.6f);

        mPreview = (CameraSourcePreview) rootView.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) rootView.findViewById(R.id.graphicOverlay);

        cameraInit = new CameraInit(getActivity().getApplicationContext(), mCameraSource, mPreview, mGraphicOverlay);

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        WIDTH = display.getWidth();
        HEIGHT = display.getHeight();

        final SharedPreferences prefs = getActivity().getSharedPreferences("general_settings", Context.MODE_PRIVATE);
        int isFirstStart = prefs.getInt("is_first_start", 1);

        if(isFirstStart == 1){
            TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                    TapTarget.forView(rootView.findViewById(R.id.image_icon_help), "User Guide", "How does it work?")
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .titleTextSize(30)                  // Specify the size (in sp) of the title text
                            .descriptionTextSize(25)            // Specify the size (in sp) of the description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            .targetRadius(60),                  // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("is_first_start",0);
                            editor.commit();

                            rootView.findViewById(R.id.image_icon_help).performClick();
                        }
                    });
        }

        rootView.findViewById(R.id.image_icon_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.StandUp).duration(400).playOn(rootView.findViewById(R.id.image_icon_help));

                SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText(getString(R.string.user_guide));
                pDialog.setContentText(getString(R.string.first_step) + "\n\n" +
                        getString(R.string.second_step) + "\n\n" +
                        getString(R.string.third_step) + "\n\n" +
                        getString(R.string.fourth_step) + "\n\n" +
                        getString(R.string.fifth_step) + "\n");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        });

        rootView.findViewById(R.id.image_icon_settings).setOnClickListener(view -> {
            YoYo.with(Techniques.StandUp).duration(400).playOn(rootView.findViewById(R.id.image_icon_settings));
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.new_feature_in_future), Toast.LENGTH_SHORT).show();
        });

        rootView.findViewById(R.id.image_icon_smootheye).setOnClickListener(view -> {
            YoYo.with(Techniques.RotateIn).duration(400).playOn(rootView.findViewById(R.id.image_icon_smootheye));
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.new_feature_in_future), Toast.LENGTH_SHORT).show();
        });

        rootView.findViewById(R.id.image_icon_flashlight).setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SwipeCore.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (!useFlash) {
                useFlash = true;
                intent.putExtra("useFlash", useFlash);
            } else {
                useFlash = false;
                intent.putExtra("useFlash", useFlash);
            }

            intent.putExtra("data", choice);
            intent.putExtra("availableChoices", availableChoices);
            intent.putExtra("isOnSemenu", isOnSEMENU);
            intent.putExtra("fragmentID", 1);
            startActivity(intent);

            YoYo.with(Techniques.StandUp).duration(400).playOn(rootView.findViewById(R.id.image_icon_flashlight));
        });

        choice = getActivity().getIntent().getIntExtra("data", 0);
        isOnSEMENU = getActivity().getIntent().getBooleanExtra("isOnSemenu", false);

        if (getActivity().getIntent().getSerializableExtra("availableChoices") != null) {
            availableChoices = (HashMap<Integer, String>) getActivity().getIntent().getSerializableExtra("availableChoices");
        }

        availableChoices = new HashMap<>();

        cameraInit.createCameraSource(USE_AUTO_FOCUS, useFlash, HEIGHT, WIDTH);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanLoop();
                handler.postDelayed(this, 100);
            }
        }, 100);

        return rootView;
    }

    /**
     * It waits until user choose desired place in {@link ChoiceActivity}.
     *
     * @param requestCode ID of result.
     * @param resultCode Integer to check if result is good.
     * @param data Data fetched from {@link ChoiceActivity}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1309 && resultCode == RESULT_OK) {
            scan(data);
        }
    }

    /**
     * This method is executed after it receives data about chosen place/target from {@link ChoiceActivity}.
     * In {@link ChoiceActivity} user choose place by clicking on a button.
     *
     * @param data This is data fetched from {@link ChoiceActivity}.
     */
    private void scan(Intent data) {
        textToVoice.speak("You have chosen " + availableChoices.get(Integer.parseInt(data.getStringExtra("data"))));
        choice = Integer.parseInt(data.getStringExtra("data"));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scanLoop();
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    /**
     * Simple method responsible for executing checkScreenSector in nested loops.
     * <br><br>
     *  Inner loop <br>
     *  <-------> <br>
     *    ______ <br>
     *  |-------|<br>
     *  |-------|      <--- This is the screen. <br>
     *  |-------|<br>
     *  |-------|<br>
     *  |-------|<br>
     *  |-------|<br>
     */
    private void scanLoop() {
        for (int i = 0; i < HEIGHT /100; i++) {
            for (int j = 0; j < WIDTH /100; j++) {
                checkScreenSector(i*150, j*150);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraInit.startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mPreview != null) {
            mPreview.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mPreview != null) {
            mPreview.release();
        }
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * checkScreenSector returns the tapped barcode result to the calling Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    public boolean checkScreenSector(float rawX, float rawY) {
        // Find tap point in preview frame coordinates.
        int[] location = new int[2];
        mGraphicOverlay.getLocationOnScreen(location);
        float x = (rawX - location[0]) / mGraphicOverlay.getWidthScaleFactor();
        float y = (rawY - location[1]) / mGraphicOverlay.getHeightScaleFactor();

        Barcode best;
        for (BarcodeGraphic graphic : mGraphicOverlay.getGraphics()) {
            Barcode barcode = graphic.getBarcode();
            if (barcode.getBoundingBox() != null) {
                if (barcode.getBoundingBox().contains((int) x, (int) y)) {
                    // Exact hit, no need to keep looking.
                    best = barcode;

                    if (isOnSEMENU && best.rawValue.contains("#SE-")) {
                        String[] text = barcode.rawValue.split("#|\\-");
                        if (Integer.parseInt(text[0]) == choice) {
                            textToVoice.speak(text[2]);
                        }
                    } else if (best.rawValue.contains("SEMENU") && !isOnSEMENU) {
                        isOnSEMENU = true;

                        final String data[] = best.rawValue.split("[-]");
                        for (int i = 1; i < data.length; i++) {

                            if (i % CHOICE_EVEN_SEPARATOR != 0) {
                                availableChoices.put(Integer.parseInt(data[i]), data[i + 1]);
                            }
                        }

                        Intent intent = new Intent(getActivity(), ChoiceActivity.class);
                        intent.putExtra("data", best.rawValue);
                        startActivityForResult(intent, 1309);
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
}
