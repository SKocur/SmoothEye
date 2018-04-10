package com.kocur.szymon.smootheye;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Szymon Kocur on 2017-10-13.
 */

public class TextToVoice {

    private TextToSpeech tts;
    private float speechRate;

    TextToVoice(Context context){
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
            }
        });
    }

    TextToVoice(Context context, final String initText) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                speak(initText);
            }
        });
    }

    public void setSpeechRate(float rate) {
        this.speechRate = rate;
        tts.setSpeechRate(speechRate);
    }

    public void speak(String text) {
        if (!tts.isSpeaking()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                tts.playSilence(2000, TextToSpeech.QUEUE_ADD, null);
            } else {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                tts.playSilence(2000, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    @Override
    public String toString(){
        return "speechRate: " + this.speechRate;
    }
}
