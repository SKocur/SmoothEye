package com.kocur.szymon.smootheye;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import com.kocur.szymon.smootheye.interfaces.ITextToVoice;

import java.util.Locale;

/**
 * Created by Szymon Kocur on 2017-10-13.
 */

public class TextToVoice implements ITextToVoice {

    private TextToSpeech tts;
    private float speechRate;

    TextToVoice(Context context){
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                }
            }
        });
    }

    TextToVoice(Context context, final String initText){
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US);
                    speak(initText);
                }
            }
        });
    }

    @Override
    public void setSpeechRate(float rate){
        this.speechRate = rate;
        tts.setSpeechRate(speechRate);
    }

    @Override
    public void speak(String text){
        if(!tts.isSpeaking()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            else
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public String toString(){
        return "speechRate: " + this.speechRate;
    }
}
