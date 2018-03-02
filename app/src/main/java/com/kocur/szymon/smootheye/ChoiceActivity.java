package com.kocur.szymon.smootheye;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Locale;

public class ChoiceActivity extends AppCompatActivity {

    TextToVoice textToVoice;

    Handler handler;
    Button buttonChoice;

    private volatile boolean isBackgroundThreadRunning;

    HashMap<String, Integer> availableOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        textToVoice = new TextToVoice(getApplicationContext(), "Choose your destination");
        textToVoice.setSpeechRate(0.9f);

        buttonChoice = (Button) findViewById(R.id.buttonChoice);

        availableOptions = new HashMap<>();

        handler = new Handler();

        SpeakContent speakContent = new SpeakContent();
        isBackgroundThreadRunning = true;
        speakContent.start();
    }

    private class SpeakContent extends Thread {
        Intent intent = getIntent();
        final String intentData = intent.getStringExtra("data");
        final String data[] = intentData.split("[-]");

        @Override
        public void run(){
            while (isBackgroundThreadRunning) {
                for (int i = 1; i < data.length; i++) {
                    if (!isBackgroundThreadRunning) {
                        break;
                    }

                    if (i % QRCaptureActivity.CHOICE_EVEN_SEPARATOR == 0) {
                        availableOptions.put(data[i], Integer.parseInt(data[i-1]));
                        textToVoice.speak(data[i]);
                        enableOptionOnButton(data[i]);

                        Log.e(">>>>>>>>>>>>>>>>>>", data[i-1]);

                        try {
                            Thread.sleep(data[i].length() * 450);
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        }
    }

    public void enableOptionOnButton(final String option) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonChoice.setText(option);
                buttonChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isBackgroundThreadRunning = false;
                        handler.removeCallbacksAndMessages(null);
                        Intent intent = new Intent();
                        intent.putExtra("data", availableOptions.get(option).toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
    }
}
