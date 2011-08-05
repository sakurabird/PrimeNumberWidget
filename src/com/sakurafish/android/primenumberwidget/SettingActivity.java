package com.sakurafish.android.primenumberwidget;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

/***
 * 設定画面
 * 
 * @author sakura_fish
 * 
 */
public class SettingActivity extends Activity {
    static final String TAG = "SettingActivity";
    {
        Log.d( TAG, "start" );
    }
    static EditText et_setting_interval_minutes;
    static EditText et_setting_range_from;
    static EditText et_setting_range_to;
    static EditText et_setting_showtext;
    static EditText et_setting_favorite;

    static String pref_setting_interval_minutes;
    static String pref_setting_range_from;
    static String pref_setting_range_to;
    static String pref_setting_showtext;
    static String pref_setting_favorite;

    Context mContext;

    @Override
    public void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.setting );

        mContext = this;
        et_setting_interval_minutes = ( EditText ) findViewById( R.id.setting_interval_minutes );
        et_setting_range_from = ( EditText ) findViewById( R.id.setting_range_from );
        et_setting_range_to = ( EditText ) findViewById( R.id.setting_range_to );
        et_setting_showtext = ( EditText ) findViewById( R.id.setting_showtext );
        et_setting_favorite = ( EditText ) findViewById( R.id.setting_favorite );

        // プリファレンスの値を読み込む
        loadSetting();
        // プリファレンスの値を画面にセット
        loadDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // プリファレンスの値を読み込む
        loadSetting();
        // プリファレンスの値を画面にセット
        loadDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 画面の値を変数にセット
        saveDisplay();
        // プリファレンスの値をセーブする
        saveSetting();
    }

    /***
     * プリファレンスの値を読み込む
     * 
     */
    private void loadSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );

        pref_setting_interval_minutes = pref.getString( ( String ) getResources().getText( R.string.pref_key_interval_minutes ), "30" );
        pref_setting_range_from = pref.getString( ( String ) getResources().getText( R.string.pref_key_range_from ), "2" );
        pref_setting_range_to = pref.getString( ( String ) getResources().getText( R.string.pref_key_range_to ), "1000" );
        pref_setting_showtext = pref.getString( ( String ) getResources().getText( R.string.pref_key_showtext ), "癒しの素数" );
        pref_setting_favorite = pref.getString( ( String ) getResources().getText( R.string.pref_key_favorite ), "" );
    }

    /***
     * プリファレンスの値を保存する
     * 
     */
    private void saveSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( mContext );
        SharedPreferences.Editor edt = pref.edit();

        edt.putString( ( String ) getResources().getText( R.string.pref_key_interval_minutes ), pref_setting_interval_minutes );
        edt.putString( ( String ) getResources().getText( R.string.pref_key_range_from ), pref_setting_range_from );
        edt.putString( ( String ) getResources().getText( R.string.pref_key_range_to ), pref_setting_range_to );
        edt.putString( ( String ) getResources().getText( R.string.pref_key_showtext ), pref_setting_showtext );
        edt.putString( ( String ) getResources().getText( R.string.pref_key_favorite ), pref_setting_favorite );

        edt.commit();
    }

    /***
     * プリファレンスの値を画面にセットする
     * 
     */
    private void loadDisplay() {
        et_setting_interval_minutes.setText( pref_setting_interval_minutes );
        et_setting_range_from.setText( pref_setting_range_from );
        et_setting_range_to.setText( pref_setting_range_to );
        et_setting_showtext.setText( pref_setting_showtext );
        et_setting_favorite.setText( pref_setting_favorite );
    }

    /***
     * 画面の値を変数にセットする
     * 
     */
    private void saveDisplay() {
        pref_setting_interval_minutes = et_setting_interval_minutes.getText().toString();
        pref_setting_range_from = et_setting_range_from.getText().toString();
        pref_setting_range_to = et_setting_range_to.getText().toString();
        pref_setting_showtext = et_setting_showtext.getText().toString();
        pref_setting_favorite = et_setting_favorite.getText().toString();
    }

}
