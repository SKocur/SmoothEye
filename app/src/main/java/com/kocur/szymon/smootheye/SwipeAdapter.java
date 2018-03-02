package com.kocur.szymon.smootheye;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Szymon Kocur on 2017-10-20.
 */

public class SwipeAdapter extends FragmentStatePagerAdapter {
    public SwipeAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i){
       switch(i){
           case 0:
               return new MoreActivity();
           case 1:
               return new QRCaptureActivity();
       }

       return null;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return "SE";
    }

    @Override
    public int getCount(){
        return 2;
    }
}
