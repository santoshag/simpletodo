package com.santoshag.hoopla;


import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by santoshag on 6/2/16.
 */
public class HooplaApplication extends com.activeandroid.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
}