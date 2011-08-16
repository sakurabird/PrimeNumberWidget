package com.sakurafish.android.primenumberwidget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/***
 * 素数を取得しウィジェットに表示
 * 
 * @author sakura_fish
 * 
 */
public class PrimeNumberWidgetService extends Service {

    static final String TAG = "PrimeNumberWidgetService";
    {
        Log.d( TAG, "@@@---start---@@@" );
    }
    static final String KEY_WIDGETID = "key.widgetid";
    Context mContext;

    @Override
    public IBinder onBind( Intent intent ) {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public void onStart( Intent intent, int startId ) {
        mContext = this;
        int wid = intent.getIntExtra( KEY_WIDGETID, -1 );
        if ( wid != -1 ) {
            Utils.setWidgetDisplay( mContext, wid );
        }
        stopSelf();
    }
}
