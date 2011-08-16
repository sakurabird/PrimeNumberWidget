package com.sakurafish.android.primenumberwidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
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
    static CheckBox cb_setting_favorite;

    // static EditText edtInput;

    Context mContext;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        Log.d( TAG, "onCreate1" );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.setting );

        mContext = this;
        et_setting_interval_minutes = ( EditText ) findViewById( R.id.setting_interval_minutes );
        et_setting_range_from = ( EditText ) findViewById( R.id.setting_range_from );
        et_setting_range_to = ( EditText ) findViewById( R.id.setting_range_to );
        et_setting_showtext = ( EditText ) findViewById( R.id.setting_showtext );
        et_setting_favorite = ( EditText ) findViewById( R.id.setting_favorite );
        cb_setting_favorite = ( CheckBox ) findViewById( R.id.setting_favorite_check );

        // プリファレンスの値を読み込む
        Utils.loadSetting( mContext );
        // プリファレンスの値を画面にセット
        loadDisplay();

        // チェックボックスがクリックされた時に呼び出されるコールバックリスナーを登録します
        // cb_setting_favorite.setOnClickListener( new View.OnClickListener() {
        // @Override
        // public void onClick( View view ) {
        // pref_setting_favorite_check = cb_setting_favorite.isChecked();
        // if ( pref_setting_favorite_check == true ) {
        // getFavoriteDialog();
        // }
        // }
        // } );

    }

    // /***
    // * ダイアログボックスにお気入りの数を入力してもらう
    // *
    // */
    // private void getFavoriteDialog() {
    // // Create EditText
    // edtInput = new EditText( this );
    // // Show Dialog
    // new AlertDialog.Builder( this ).setTitle( R.string.setting_title_favorite ).setView( edtInput ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {
    // public void onClick( DialogInterface dialog, int whichButton ) {
    // /* OKボタンをクリックした時の処理 */
    // }
    // } ).setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
    // public void onClick( DialogInterface dialog, int whichButton ) {
    // /* Cancel ボタンをクリックした時の処理 */
    // }
    // } ).show();
    // }

    @Override
    protected void onResume() {
        super.onResume();
        // プリファレンスの値を読み込む
        Utils.loadSetting( mContext );
        // プリファレンスの値を画面にセット
        loadDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 画面の値を変数にセット
        saveDisplay();
        // プリファレンスの値をセーブする
        Utils.saveSetting( mContext );
    }

    /***
     * プリファレンスの値を画面にセットする
     * 
     */
    private void loadDisplay() {
        et_setting_interval_minutes.setText( Integer.toString( Utils.pref_setting_interval_minutes ) );
        et_setting_range_from.setText( Integer.toString( Utils.pref_setting_range_from ) );
        et_setting_range_to.setText( Integer.toString( Utils.pref_setting_range_to ) );
        et_setting_showtext.setText( Utils.pref_setting_showtext );
        et_setting_favorite.setText( Integer.toString( Utils.pref_setting_favorite ) );
        // チェックボックスのチェック状態を設定します
        cb_setting_favorite.setChecked( Utils.pref_setting_favorite_check );
    }

    /***
     * 画面の値を変数にセットする
     * 
     */
    private void saveDisplay() {
        String s;

        Log.d( TAG, "saveDisplay1" );
        s = et_setting_interval_minutes.getText().toString();
        Log.d( TAG, "saveDisplay2" );
        if ( isInteger( s ) ) {
            Log.d( TAG, "saveDisplay3" );
            Utils.pref_setting_interval_minutes = Integer.parseInt( s );
        } else {
            Log.d( TAG, "saveDisplay4" );
            // TODO 何も入力されなかったら1分
            Utils.pref_setting_interval_minutes = 1;
        }
        Log.d( TAG, "saveDisplay5" );

        s = et_setting_range_from.getText().toString();
        if ( isInteger( s ) ) {
            Utils.pref_setting_range_from = Integer.parseInt( s );
        } else {
            // TODO 何も入力されなかったら2
            Utils.pref_setting_range_from = 2;
        }

        s = et_setting_range_to.getText().toString();
        if ( isInteger( s ) ) {
            Utils.pref_setting_range_to = Integer.parseInt( s );
        } else {
            // TODO 何も入力されなかったら+1000
            Utils.pref_setting_range_to = Utils.pref_setting_range_from + 1000;
        }

        // TODO これはインプットフィルターでやりたい
        // from > to やto - from == 0 の時
        if ( Utils.pref_setting_range_from > Utils.pref_setting_range_to ) {
            int x = Utils.pref_setting_range_from;
            Utils.pref_setting_range_from = Utils.pref_setting_range_to;
            Utils.pref_setting_range_to = x;
        }

        Utils.pref_setting_showtext = et_setting_showtext.getText().toString();

        s = et_setting_favorite.getText().toString();
        if ( isInteger( s ) ) {
            Utils.pref_setting_favorite = Integer.parseInt( s );
        } else {
            // TODO 何も入力されなかったら0
            Utils.pref_setting_favorite = 0;
        }

        Utils.pref_setting_favorite_check = cb_setting_favorite.isChecked();

    }

    /***
     * Stringが数値かどうか判定
     * 
     * @param s
     * @return
     */
    static boolean isInteger( String s ) {
        try {
            Integer.parseInt( s );
            return true;
        } catch ( NumberFormatException e ) {
            return false;
        }
    }
}
