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

import java.util.HashMap;
import java.util.Locale;

public class ChoiceActivity extends AppCompatActivity {

    private TextToSpeech tts;

    Handler handler;
    Button buttonChoice;

    private volatile boolean isBackgroundThreadRunning;

    HashMap<String, Integer> availableOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        buttonChoice = (Button) findViewById(R.id.buttonChoice);

        availableOptions = new HashMap<String, Integer>();

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    tts.setSpeechRate(0.7f);
                    speak("Please, choose your destination");
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

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
            while(isBackgroundThreadRunning) {
                for(int i = 1; i < data.length; i++){
                    if(!isBackgroundThreadRunning)
                        break;

                    if(i % QRCaptureActivity.CHOICE_EVEN_SEPARATOR == 0) {
                        availableOptions.put(data[i], Integer.parseInt(data[i-1]));
                        speak(data[i]);
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

    public void enableOptionOnButton(final String option){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonChoice.setText(option);
                buttonChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isBackgroundThreadRunning = false;
                        tts.shutdown();
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

    public void speak(String text){
        if(!tts.isSpeaking()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            else
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
