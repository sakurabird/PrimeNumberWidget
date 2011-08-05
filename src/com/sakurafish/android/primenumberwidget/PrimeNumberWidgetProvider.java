package com.sakurafish.android.primenumberwidget;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

/***
 * 素数表示ウィジェット
 * 
 * @author sakura_fish
 * 
 */
public class PrimeNumberWidgetProvider extends AppWidgetProvider {

    static final Uri PRIMENUMBERWIDGET_URI = Uri.parse( "primenumber_widget://com.sakurafish.android.primenumberwidget.widget/id" );
    static final String FILTER_ACTION = "com.sakurafish.android.primenumberwidget.UPDATE";

    Context mContext;

    static String pref_setting_interval_minutes;
    static String pref_setting_range_from;
    static String pref_setting_range_to;
    static String pref_setting_showtext;
    static String pref_setting_favorite;

    @Override
    public void onEnabled( Context context ) {
        super.onEnabled( context );

        mContext = context;
        // プリファレンスの値を読み込む
        loadSetting();
        Utils.storeValue();
    }

    @Override
    public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
        // AppWidget単位に更新処理を行う
        for ( int i = 0; i < appWidgetIds.length; i++ ) {

            int id = appWidgetIds[ i ];
            Intent intent = Utils.createIntent( context, id );
            PendingIntent operation = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
            long firstTime = System.currentTimeMillis();
            AlarmManager am = ( AlarmManager ) context.getSystemService( Activity.ALARM_SERVICE );

            // TODO このコメント後で消す
            // // 設定情報から更新時間を取得する
            // long interval = Integer.parseInt( PreferenceManager.getDefaultSharedPreferences( context ).getString( "interval_key", "20" ) ) * 1000;
            am.setRepeating( AlarmManager.RTC, firstTime, Utils.interval_millisecond, operation );
            // 設定用のServiceを開始する
            Intent service = new Intent( context, PrimeNumberWidgetService.class );
            service.putExtra( PrimeNumberWidgetService.KEY_WIDGETID, id );
            context.startService( service );
        }
    }

    @Override
    public void onDeleted( Context context, int[] appWidgetIds ) {
        super.onDeleted( context, appWidgetIds );

        // AppWidgetが削除されたら、Alarmをキャンセルして設定も削除する
        for ( int i = 0; i < appWidgetIds.length; i++ ) {
            // 削除するAlarmを特定するためのPendingIntentを作成する
            int id = appWidgetIds[ i ];
            Intent intent = Utils.createIntent( context, id );
            PendingIntent operation = PendingIntent.getBroadcast( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
            AlarmManager am = ( AlarmManager ) context.getSystemService( Activity.ALARM_SERVICE );

            // Alarm削除を実行する
            am.cancel( operation );

            // TODO このコメント後で消す
            // // 設定を削除する
            // SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( context );
            // Editor editor = pref.edit();
            // editor.remove( Utils.getWidgetKey( id ) );
            // editor.commit();
        }
    }

    @Override
    public void onReceive( Context context, Intent intent ) {
        super.onReceive( context, intent );

        // Alarmから呼ばれた場合の処理を行う
        String act = intent.getAction();
        if ( act != null && act.equals( FILTER_ACTION ) ) {
            Uri uri = intent.getData();
            int id = Integer.parseInt( uri.getPathSegments().get( 1 ) );
            // 設定用のServiceを開始する
            Intent service = new Intent( context, PrimeNumberWidgetService.class );
            service.putExtra( PrimeNumberWidgetService.KEY_WIDGETID, id );
            context.startService( service );
        }
    }

    /***
     * プリファレンスの値を読み込む
     * 
     */
    void loadSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );

        pref_setting_interval_minutes = pref.getString( ( String ) mContext.getResources().getText( R.string.pref_key_interval_minutes ), "30" );
        pref_setting_range_from = pref.getString( ( String ) mContext.getResources().getText( R.string.pref_key_range_from ), "2" );
        pref_setting_range_to = pref.getString( ( String ) mContext.getResources().getText( R.string.pref_key_range_to ), "1000" );
        pref_setting_showtext = pref.getString( ( String ) mContext.getResources().getText( R.string.pref_key_showtext ), "癒しの素数" );
        pref_setting_favorite = pref.getString( ( String ) mContext.getResources().getText( R.string.pref_key_favorite ), "" );
    }
}
