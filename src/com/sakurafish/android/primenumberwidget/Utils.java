package com.sakurafish.android.primenumberwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class Utils {

    static final String TAG = "Utils";
    {
        Log.d( TAG, "@@@---start---@@@" );
    }

    static long interval_millisecond;
    static int range_from;
    static int range_to;
    // static String showtext;
    static int favorite;

    /***
     * 設定ファイルでAppWidgetを特定するためのKEY
     * 
     * @param id
     * @return
     */
    public static String getWidgetKey( int id ) {
        return "WidgetId" + String.valueOf( id );
    }

    /***
     * 素数を判定してセットする
     * 
     * @param context
     * @param id
     */

    // 素数　String
    public static String primeNumberText;

    public static void setWidgetDisplay( Context context, int id ) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( context );
        if ( appWidgetManager != null ) {

            // 素数処理

            long number;
            primeNumberText = "2"; // initialize
            // 素数が見つかるまでループ
            do {
                // number = 2 + (long) java.lang.Math.floor(java.lang.Math.random()
                // * Long.MAX_VALUE); これだと時間がかかり過ぎて無限ループしているように見えてしまう。
                number = range_from + ( long ) java.lang.Math.floor( java.lang.Math.random() * range_to );
                Log.d( TAG, "ransuu= " + number );

                if ( isPrimeNumber( number ) ) {
                    Log.d( TAG, "sosuu= " + number );
                    primeNumberText = Long.toString( number );
                }

            } while ( isPrimeNumber( number ) == false );
            // 画面の素数を更新する
            RemoteViews remoteviews = new RemoteViews( context.getPackageName(), R.layout.main );
            remoteviews.setTextViewText( R.id.Tv_PrimeNumber, primeNumberText );

            // 画面の表示文字（デフォルト：癒しの素数）を更新する
            remoteviews.setTextViewText( R.id.Tv_showtext, PrimeNumberWidgetProvider.pref_setting_showtext );

            // 画像クリックでイメージを更新する
            Intent intent = Utils.createIntent( context, id );
            PendingIntent operation = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
            remoteviews.setOnClickPendingIntent( R.id.Tv_PrimeNumber, operation );

            // AppWidgetの更新を確定する
            appWidgetManager.updateAppWidget( id, remoteviews );
        }

    }

    // 素数判定
    static boolean isPrimeNumber( long number ) {
        // TODO Auto-generated method stub
        for ( int i = 2; i <= number - 1; i++ ) {
            if ( number % i == 0 ) {
                return false;
            }
        }
        return true;
    }

    /***
     * Alarmに設定するIntentを生成する
     * 
     * @param context
     * @param id
     * @return
     */
    public static Intent createIntent( Context context, int id ) {
        // Alarmにより実行されるIntentを作成する
        Intent intent = new Intent( context, PrimeNumberWidgetProvider.class );
        intent.setAction( PrimeNumberWidgetProvider.FILTER_ACTION );
        Uri uri = Uri.withAppendedPath( PrimeNumberWidgetProvider.PRIMENUMBERWIDGET_URI, String.valueOf( id ) );
        intent.setData( uri );
        return intent;
    }

    /***
     * プリファレンスの値を変数に格納する
     * 
     */
    public static void storeValue() {
        interval_millisecond = ( Long.valueOf( PrimeNumberWidgetProvider.pref_setting_interval_minutes ) ) * 1000 * 60;
        range_from = Integer.valueOf( PrimeNumberWidgetProvider.pref_setting_range_from );
        range_to = Integer.valueOf( PrimeNumberWidgetProvider.pref_setting_range_to );
        favorite = Integer.valueOf( PrimeNumberWidgetProvider.pref_setting_favorite );
    }
}
