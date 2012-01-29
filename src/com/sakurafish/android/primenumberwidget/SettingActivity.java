/*
 * Copyright 2012 sakura_fish<neko3genki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sakurafish.android.primenumberwidget;

import com.sakurafish.android.primenumberwidget.MyException.IllegalNumberException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/***
 * 設定画面（単体で起動した場合この画面が表示される）
 * 
 * @author sakura_fish
 */
public class SettingActivity extends Activity {
    static final String TAG = "SettingActivity";
    {
        Log.d(TAG, "start");
    }
    /** 画面 */
    static TextView tv_setting_now_interval;
    static Spinner sp_setting_color;
    static EditText et_setting_range_from;
    static EditText et_setting_range_to;
    static EditText et_setting_showtext;
    static CheckBox cb_setting_favorite;

    Context mContext;
    /** 文字色 */
    int mColor;
    /** 更新間隔 */
    int mIntervalMinutes;
    /** お気に入りの数 */
    int mFavorite;
    /** 文字色の配列 */
    int[] colorsArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        mContext = this;

        // プリファレンスの値を読み込む
        Utils.loadSetting(mContext);
        // ビューの初期設定
        setView();
        // プリファレンスの値を画面にセット
        setViewByPreference();

    }

    private void setView() {
        tv_setting_now_interval = (TextView) findViewById(R.id.setting_now_interval);
        sp_setting_color = (Spinner) findViewById(R.id.spinner_color);
        // スピナーの設定
        setSpinner();
        et_setting_range_from = (EditText) findViewById(R.id.setting_range_from);
        et_setting_range_to = (EditText) findViewById(R.id.setting_range_to);
        et_setting_showtext = (EditText) findViewById(R.id.setting_showtext);
        cb_setting_favorite = (CheckBox) findViewById(R.id.setting_favorite_check);
        // お気に入りの数のチェックボックスのリスナー設定
        cb_setting_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb_setting_favorite.isChecked()) {
                    showFavoriteDialog();
                }
            }
        });

    }

    /**
     * スピナーのセット
     */
    private void setSpinner() {
        TypedArray colors = getResources().obtainTypedArray(R.array.array_colors);
        colorsArray = new int[colors.length()];
        for (int i = 0; i < colors.length(); i++) {
            colorsArray[i] = colors.getColor(i, 0);
        }

        String[] titles = getResources().getStringArray(R.array.array_color_titles);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, titles);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_setting_color.setAdapter(adapter);

        // スピナーのリスナー
        sp_setting_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mColor = colorsArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("IntervalMinutes", mIntervalMinutes);
        outState.putInt("Color", mColor);
        outState.putInt("rangeFrom", Integer.parseInt(et_setting_range_from.getText().toString()));
        outState.putInt("rangeTo", Integer.parseInt(et_setting_range_to.getText().toString()));
        outState.putString("showText", et_setting_showtext.getText().toString());
        outState.putInt("favorite", mFavorite);
        outState.putBoolean("favoriteCheck", cb_setting_favorite.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIntervalMinutes = savedInstanceState.getInt("IntervalMinutes");
        tv_setting_now_interval.setText(getResources().getString(R.string.setting_title_now)
                + Integer.toString(mIntervalMinutes)
                + getResources().getString(R.string.setting_title_minutes));

        mColor = savedInstanceState.getInt("Color");
        // スピナーの選択位置を設定
        int position = 0;
        for (int i = 0; i < colorsArray.length; i++) {
            if (colorsArray[i] == mColor) {
                position = i;
                break;
            }
        }
        sp_setting_color.setSelection(position);
        et_setting_range_from.setText(Integer.toString(savedInstanceState.getInt("rangeFrom")));
        et_setting_range_to.setText(Integer.toString(savedInstanceState.getInt("rangeTo")));
        et_setting_showtext.setText(savedInstanceState.getString("showText"));
        mFavorite = savedInstanceState.getInt("favorite");
        cb_setting_favorite.setChecked(savedInstanceState.getBoolean("favoriteCheck"));
    }

    /***
     * 設定更新ボタンが押された時の処理
     */
    public void update(View view) {

        // エラーチェック
        try {
            inputCheck();
        } catch (IllegalNumberException e) {
            // ダイアログを表示
            showSimpleDialog(e.getMessage().toString());
            e.printStackTrace();
            return;
        }
        // 画面の値をプリファレンス用変数にセット
        setPreferenseByView();
        // プリファレンスの値をセーブする
        Utils.saveSetting(mContext);

        // ダイアログを表示
        showSimpleDialog(getResources().getString(R.string.setting_massage_updated));
    }

    /***
     * プリファレンスの値を画面にセットする
     */
    private void setViewByPreference() {
        mIntervalMinutes = Utils.pref_setting_interval_minutes;
        tv_setting_now_interval.setText(getResources().getString(R.string.setting_title_now)
                + Integer.toString(mIntervalMinutes)
                + getResources().getString(R.string.setting_title_minutes));
        mColor = Utils.pref_setting_textcolor;
        // スピナーの選択位置を設定
        int position = 0;
        for (int i = 0; i < colorsArray.length; i++) {
            if (colorsArray[i] == Utils.pref_setting_textcolor) {
                position = i;
                break;
            }
        }
        sp_setting_color.setSelection(position);
        et_setting_range_from.setText(Integer.toString(Utils.pref_setting_range_from));
        et_setting_range_to.setText(Integer.toString(Utils.pref_setting_range_to));
        et_setting_showtext.setText(Utils.pref_setting_showtext);
        // チェックボックスのチェック状態を設定します
        cb_setting_favorite.setChecked(Utils.pref_setting_favorite_check);
        mFavorite = Utils.pref_setting_favorite;
    }

    /***
     * 画面の値をプリファレンス用変数にセットする
     */
    private void setPreferenseByView() {

        Utils.pref_setting_interval_minutes = mIntervalMinutes;

        Utils.pref_setting_textcolor = mColor;
        Utils.pref_setting_range_from = Integer
                .parseInt(et_setting_range_from.getText().toString());
        Utils.pref_setting_range_to = Integer.parseInt(et_setting_range_to.getText().toString());

        // from > toの時左右ひっくり返す
        if (Utils.pref_setting_range_from > Utils.pref_setting_range_to) {
            int x = Utils.pref_setting_range_from;
            Utils.pref_setting_range_from = Utils.pref_setting_range_to;
            Utils.pref_setting_range_to = x;
        }

        Utils.pref_setting_showtext = et_setting_showtext.getText().toString();
        Utils.pref_setting_favorite = mFavorite;
        Utils.pref_setting_favorite_check = cb_setting_favorite.isChecked();

    }

    /***
     * 入力チェック
     */
    private void inputCheck() throws IllegalNumberException {
        // 数値以外が入力されていないかCheck
        try {
            Integer.parseInt(et_setting_range_from.getText().toString());
        } catch (NumberFormatException e) {
            throw new IllegalNumberException(getResources().getString(
                    R.string.error_illegalNumber_range));
        }
        try {
            Integer.parseInt(et_setting_range_to.getText().toString());
        } catch (NumberFormatException e) {
            throw new IllegalNumberException(getResources().getString(
                    R.string.error_illegalNumber_range));
        }

        // 指定された範囲に素数が一つも存在しない時check
        int from = Integer.parseInt(et_setting_range_from.getText().toString());
        int to = Integer.parseInt(et_setting_range_to.getText().toString());

        if (Integer.parseInt(et_setting_range_from.getText().toString()) > Integer
                .parseInt(et_setting_range_to.getText().toString())) {
            // from > toの時左右ひっくり返す
            from = Integer.parseInt(et_setting_range_to.getText().toString());
            to = Integer.parseInt(et_setting_range_from.getText().toString());
        }

        boolean hasPrimeNumber = false;
        for (int i = from; i <= to; i++) {
            if (Utils.isPrimeNumber(i)) {
                hasPrimeNumber = true;
                break;
            }
        }
        if (!hasPrimeNumber) {
            throw new IllegalNumberException(getResources()
                    .getString(R.string.error_no_primenumber));
        }

    }

    /***
     * 更新間隔の設定ボタンが押された場合の処理
     * 
     * @param view
     */
    public void showTimePickerDialog(View view) {
        int m = mIntervalMinutes;
        int HH = m / 60;
        int MM = m % 60;

        final TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mIntervalMinutes = hourOfDay * 60 + minute;
                        if (mIntervalMinutes == 0) {
                            mIntervalMinutes = 1440; // 24hours
                        }
                        tv_setting_now_interval.setText(getResources().getString(
                                R.string.setting_title_now)
                                + Integer.toString(mIntervalMinutes)
                                + getResources().getString(R.string.setting_title_minutes));
                    }

                }, HH, MM, true);

        timePickerDialog.setTitle(R.string.setting_title_interval);
        timePickerDialog.setMessage(getResources().getText(R.string.setting_massage_interval));
        timePickerDialog.show();
    }

    /***
     * チェックボックスをONにした時ダイアログボックスにお気入りの数を入力してもらう
     */
    private void showFavoriteDialog() {
        // Create EditText
        final EditText edtInput = new EditText(this);
        edtInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtInput.setText(Integer.toString(mFavorite));
        // Show Dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.setting_title_favorite)
                .setView(edtInput)
                .setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                /* OKボタンをクリックした時の処理 */
                                try {
                                    mFavorite = Integer.parseInt(edtInput.getText().toString());
                                } catch (NumberFormatException e) {
                                    mFavorite = 0;
                                }
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                /* Cancel ボタンをクリックした時の処理 */
                            }
                        }).show();
    }

    /***
     * デフォルトの設定に戻すボタンが押された時の処理
     */
    public void showRestoreDefaultDialog(View view) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // メッセージを設定
        alertDialogBuilder.setMessage(getResources().getString(
                R.string.setting_massage_dialog_restore));
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                new OnClickListener() {
                    // OK
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // デフォルトの値をプリファレンスにセットする
                        Utils.setDefaultValue(mContext);
                        Utils.saveSetting(mContext);
                        // 画面を更新
                        setViewByPreference();
                        Toast.makeText(mContext,
                                getResources().getString(R.string.setting_massage_restore),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel),
                new OnClickListener() {
                    // Cancel
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialogBuilder.create().show();

    }

    /***
     * ダイアログにメッセージを表示してボタンを押すだけの処理
     */
    public void showSimpleDialog(String message) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.ok),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialogBuilder.create().show();

    }
}
