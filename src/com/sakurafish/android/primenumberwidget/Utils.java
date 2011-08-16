package com.sakurafish.android.primenumberwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class Utils {

    static final String TAG = "Utils";
    {
        Log.d( TAG, "@@@---start---@@@" );
    }
    // プリファレンスの情報
    protected static int pref_setting_interval_minutes;
    protected static int pref_setting_range_from;
    protected static int pref_setting_range_to;
    protected static String pref_setting_showtext;
    protected static int pref_setting_favorite;
    protected static boolean pref_setting_favorite_check;

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

        // プリファレンスの値を読み込む
        Utils.loadSetting( context );

        if ( appWidgetManager != null ) {

            // 素数処理

            long number;
            primeNumberText = "2"; // initialize
            // 素数が見つかるまでループ
            do {
                // Long.MAX_VALUE だと時間がかかり過ぎて無限ループしているように見えてしまう。
                // number = range_from + ( long ) java.lang.Math.floor( java.lang.Math.random() * range_to );
                number = ( long ) java.lang.Math.floor( java.lang.Math.random() * ( pref_setting_range_to - pref_setting_range_from + 1 ) + pref_setting_range_from );
                Log.d( TAG, "ransuu= " + number );

                // お気に入りの数が設定されている場合、乱数の下一桁が0ならお気に入りの数を表示する
                if ( pref_setting_favorite_check && ( number % 10 == 0 ) ) {
                    Log.d( TAG, "fav number block" + number );
                    primeNumberText = Integer.toString( pref_setting_favorite );
                    break;
                }

                if ( isPrimeNumber( number ) ) {
                    primeNumberText = Long.toString( number );
                }

            } while ( isPrimeNumber( number ) == false );
            // 画面の素数を更新する
            RemoteViews remoteviews = new RemoteViews( context.getPackageName(), R.layout.main );
            remoteviews.setTextViewText( R.id.Tv_PrimeNumber, primeNumberText );

            // 画面の表示文字（デフォルト：癒しの素数）を更新する
            remoteviews.setTextViewText( R.id.Tv_showtext, pref_setting_showtext );

            // 画像クリックでイメージを更新する
            Intent intent = Utils.createIntent( context, id );
            PendingIntent operation = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
            remoteviews.setOnClickPendingIntent( R.id.Tv_PrimeNumber, operation );

            // AppWidgetの更新を確定する
            appWidgetManager.updateAppWidget( id, remoteviews );
        }

    }

    /***
     * 素数判定
     * 
     * @param number
     * @return
     */
    static boolean isPrimeNumber( long number ) {
        if ( number < 2 ) {
            return false;
        }
        for ( int i = 2; i <= number - 1; i++ ) {
            if ( number % i == 0 ) {
                return false;
            }
        }
        return true;
    }

    /***
     * プリファレンスの値を読み込む
     * 
     * @param mContext
     */
    protected static void loadSetting( Context context ) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );

        pref_setting_interval_minutes = Integer.valueOf( pref.getString( ( String ) context.getResources().getText( R.string.pref_key_interval_minutes ), "30" ) );
        pref_setting_range_from = Integer.valueOf( pref.getString( ( String ) context.getResources().getText( R.string.pref_key_range_from ), "2" ) );
        pref_setting_range_to = Integer.valueOf( pref.getString( ( String ) context.getResources().getText( R.string.pref_key_range_to ), "1000" ) );
        pref_setting_showtext = pref.getString( ( String ) context.getResources().getText( R.string.pref_key_showtext ), "癒しの素数" );
        pref_setting_favorite = Integer.valueOf( pref.getString( ( String ) context.getResources().getText( R.string.pref_key_favorite ), "2" ) );
        pref_setting_favorite_check = pref.getBoolean( ( String ) context.getResources().getText( R.string.pref_key_favorite_check ), false );
    }

    /***
     * プリファレンスの値を保存する
     * 
     * @param mContext
     */
    protected static void saveSetting( Context context ) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
        SharedPreferences.Editor edt = pref.edit();

        edt.putString( ( String ) context.getResources().getText( R.string.pref_key_interval_minutes ), Integer.toString( pref_setting_interval_minutes ) );
        edt.putString( ( String ) context.getResources().getText( R.string.pref_key_range_from ), Integer.toString( pref_setting_range_from ) );
        edt.putString( ( String ) context.getResources().getText( R.string.pref_key_range_to ), Integer.toString( pref_setting_range_to ) );
        edt.putString( ( String ) context.getResources().getText( R.string.pref_key_showtext ), pref_setting_showtext );
        edt.putString( ( String ) context.getResources().getText( R.string.pref_key_favorite ), Integer.toString( pref_setting_favorite ) );
        edt.putBoolean( ( String ) context.getResources().getText( R.string.pref_key_favorite_check ), pref_setting_favorite_check );

        edt.commit();
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

}
