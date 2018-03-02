package com.kocur.szymon.smootheye.interfaces;

/**
 * Created by Szymon Kocur on 2017-10-13.
 */

public interface ITextToVoice {
    void setSpeechRate(float rate);
    void speak(String text);
    String toString();
}
