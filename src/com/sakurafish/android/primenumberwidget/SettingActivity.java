package com.sakurafish.android.primenumberwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
	static int setting_interval_minutes;
	static int setting_favorite;
	static TextView tv_setting_now_interval;
	static EditText et_setting_range_from;
	static EditText et_setting_range_to;
	static EditText et_setting_showtext;
	static CheckBox cb_setting_favorite;

	// static EditText edtInput;

	Context mContext;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate1" );
		super.onCreate( savedInstanceState );
		setContentView( R.layout.setting );

		mContext = this;
		tv_setting_now_interval = ( TextView ) findViewById( R.id.setting_now_interval );
		et_setting_range_from = ( EditText ) findViewById( R.id.setting_range_from );
		et_setting_range_to = ( EditText ) findViewById( R.id.setting_range_to );
		et_setting_showtext = ( EditText ) findViewById( R.id.setting_showtext );
		cb_setting_favorite = ( CheckBox ) findViewById( R.id.setting_favorite_check );

		// プリファレンスの値を読み込む
		Utils.loadSetting( mContext );
		// プリファレンスの値を画面にセット
		loadDisplay();

		// 　TODO edittest test
		// et_setting_range_from.addTextChangedListener( watchHandler );
		// チェックボックスがクリックされた時に呼び出されるコールバックリスナーを登録します
		cb_setting_favorite.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick( View view ) {
				if ( cb_setting_favorite.isChecked() ) {
					showFavoriteDialog();
				}
			}
		} );

	}

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
		setting_interval_minutes = Utils.pref_setting_interval_minutes;
		tv_setting_now_interval.setText( "現在" + Integer.toString( setting_interval_minutes ) + "分" );

		et_setting_range_from.setText( Integer.toString( Utils.pref_setting_range_from ) );
		et_setting_range_to.setText( Integer.toString( Utils.pref_setting_range_to ) );
		et_setting_showtext.setText( Utils.pref_setting_showtext );
		// et_setting_favorite.setText( Integer.toString( Utils.pref_setting_favorite ) );
		// チェックボックスのチェック状態を設定します
		cb_setting_favorite.setChecked( Utils.pref_setting_favorite_check );
		setting_favorite = Utils.pref_setting_favorite;
	}

	/***
	 * 画面の値を変数にセットする
	 * 
	 */
	private void saveDisplay() {

		Utils.pref_setting_interval_minutes = setting_interval_minutes;

		int wk_setting_range_from = Utils.pref_setting_range_from;
		int wk_setting_range_to = Utils.pref_setting_range_to;
		// 数値以外
		try {
			Utils.pref_setting_range_from = Integer.parseInt( et_setting_range_from.getText().toString() );
		} catch ( NumberFormatException e ) {
			// TODO
			Toast.makeText( mContext, "不正な数値が入力されたので、キャンセルしました", Toast.LENGTH_SHORT ).show();
			Utils.pref_setting_range_from = wk_setting_range_from;
		}

		// 数値以外
		try {
			Utils.pref_setting_range_to = Integer.parseInt( et_setting_range_to.getText().toString() );
		} catch ( NumberFormatException e ) {
			// TODO
			Toast.makeText( mContext, "不正な数値が入力されたので、キャンセルしました", Toast.LENGTH_SHORT ).show();
			Utils.pref_setting_range_to = wk_setting_range_to;
		}

		// from > toの時左右ひっくり返す
		if ( Utils.pref_setting_range_from > Utils.pref_setting_range_to ) {
			int x = Utils.pref_setting_range_from;
			Utils.pref_setting_range_from = Utils.pref_setting_range_to;
			Utils.pref_setting_range_to = x;
		}

		// to - from == 0 の時
		if ( Utils.pref_setting_range_from - Utils.pref_setting_range_to == 0 ) {
			Toast.makeText( mContext, "不正な数値が入力されたので、キャンセルしました", Toast.LENGTH_SHORT ).show();
			Utils.pref_setting_range_from = wk_setting_range_from;
			Utils.pref_setting_range_to = wk_setting_range_to;
		}

		// TODO指定された範囲に素数が一つも存在しない時check
		boolean hasprimeNumberflag = false;
		for ( int i = Utils.pref_setting_range_from; i < Utils.pref_setting_range_to; i++ ) {
			if ( Utils.isPrimeNumber( i ) ) {
				hasprimeNumberflag = true;
				break;
			}
		}
		if ( !hasprimeNumberflag ) {
			Toast.makeText( mContext, "入力された範囲に素数は存在ないので、キャンセルしました", Toast.LENGTH_SHORT ).show();
			Utils.pref_setting_range_from = wk_setting_range_from;
			Utils.pref_setting_range_to = wk_setting_range_to;
		}

		Utils.pref_setting_showtext = et_setting_showtext.getText().toString();
		Utils.pref_setting_favorite = setting_favorite;
		Utils.pref_setting_favorite_check = cb_setting_favorite.isChecked();

	}

	/***
	 * 更新間隔の設定ボタンが押された場合の処理
	 * 
	 * @param view
	 */
	public void showTimePickerDialog( View view ) {
		int m = setting_interval_minutes;
		int HH = m / 60;
		int MM = m % 60;

		final TimePickerDialog timePickerDialog = new TimePickerDialog( this, new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet( TimePicker view, int hourOfDay, int minute ) {
				setting_interval_minutes = hourOfDay * 60 + minute;
				if ( setting_interval_minutes == 0 ) {
					setting_interval_minutes = 1440; // 24hours
				}
				tv_setting_now_interval.setText( "現在" + Integer.toString( setting_interval_minutes ) + "分" );
			}

		}, HH, MM, true );

		timePickerDialog.setTitle( R.string.setting_title_interval );
		timePickerDialog.setMessage( getResources().getText( R.string.setting_massage_interval ) );
		timePickerDialog.show();
	}

	/***
	 * 背景色の設定ボタンが押された場合の処理
	 * 
	 * @param view
	 */
	public void showColorPickerDialog( View view ) {

		Log.d( TAG, "showColorPickerDialog1" );
		ColorPickerDialog cpd = new ColorPickerDialog( mContext, new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged( int color ) {
				Log.d( TAG, "color=" + color );
				// 色が選択されるとcolor（選択された色）に値が入る
				int R = Color.red( color );
				int G = Color.green( color );
				int B = Color.blue( color );

				android.util.Log.d( "ColorPickerDialog", "(R,G,B)=(" + R + "," + G + "," + B + ")" );
				Utils.pref_setting_textcolor = color;
			}
		}, Color.BLACK );

		cpd.show();
	}

	/***
	 * チェックボックスをONにした時ダイアログボックスにお気入りの数を入力してもらう
	 * 
	 */
	private void showFavoriteDialog() {
		// Create EditText
		final EditText edtInput = new EditText( this );
		edtInput.setInputType( InputType.TYPE_CLASS_NUMBER );
		edtInput.setText( Integer.toString( setting_favorite ) );
		// Show Dialog
		new AlertDialog.Builder( this ).setTitle( R.string.setting_title_favorite ).setView( edtInput ).setPositiveButton( "OK", new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int whichButton ) {
				/* OKボタンをクリックした時の処理 */
				try {
					setting_favorite = Integer.parseInt( edtInput.getText().toString() );
				} catch ( NumberFormatException e ) {
					setting_favorite = 0;
				}
				Log.d( TAG, "getFavoriteDialog() OK BUTTON Clicked setting_favorite=" + setting_favorite );

			}
		} ).setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int whichButton ) {
				/* Cancel ボタンをクリックした時の処理 */
			}
		} ).show();
	}

}
