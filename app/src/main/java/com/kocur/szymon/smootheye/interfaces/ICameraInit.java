package com.kocur.szymon.smootheye.interfaces;

/**
 * Created by Szymon Kocur on 2017-10-14.
 */

public interface ICameraInit {
    void createCameraSource(boolean autoFocus, boolean useFlash, int width, int height);
    void startCameraSource();
}
