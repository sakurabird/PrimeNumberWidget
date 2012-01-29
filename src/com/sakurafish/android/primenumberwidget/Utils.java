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
    // プリファレンスの情報
    protected static int pref_setting_interval_minutes;
    protected static int pref_setting_textcolor;
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
    public static String getWidgetKey(int id) {
        return "WidgetId" + String.valueOf(id);
    }

    /***
     * 素数を判定してセットする
     * 
     * @param context
     * @param id
     */

    // 素数　String

    public static void setWidgetDisplay(Context context, int id) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        // プリファレンスの値を読み込む
        Utils.loadSetting(context);

        if (appWidgetManager != null) {

            // 素数探索処理
            String primeNumberText = "";
            primeNumberText = Integer.toString(searchPrimeNumber());

            // 画面の素数を更新する
            RemoteViews remoteviews = new RemoteViews(context.getPackageName(), R.layout.main);
            remoteviews.setTextViewText(R.id.Tv_PrimeNumber, primeNumberText);

            // 文字色設定
            if (pref_setting_textcolor != 0) {
                remoteviews.setTextColor(R.id.Tv_PrimeNumber, pref_setting_textcolor);
                remoteviews.setTextColor(R.id.Tv_showtext, pref_setting_textcolor);
            }

            // 画面の表示文字（デフォルト：素数）を更新する
            remoteviews.setTextViewText(R.id.Tv_showtext, pref_setting_showtext);

            // 画像クリックでイメージを更新する
            Intent intent = Utils.createIntent(context, id);
            PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            remoteviews.setOnClickPendingIntent(R.id.Tv_PrimeNumber, operation);

            // AppWidgetの更新を確定する
            appWidgetManager.updateAppWidget(id, remoteviews);
        }

    }

    /***
     * 素数探索処理
     * 
     * @return
     */

    private static int searchPrimeNumber() {

        int random_number = 0;

        // 素数が見つかるまでループ
        for (int i = 0; i < Integer.MAX_VALUE; i++) {

            // from to の範囲内で乱数を発生させる
            random_number = (int) java.lang.Math.floor(java.lang.Math.random()
                    * (pref_setting_range_to - pref_setting_range_from + 1)
                    + pref_setting_range_from);

            Log.d(TAG, "乱数=" + random_number);

            // お気に入りの数が設定されている場合、乱数の下一桁が0ならお気に入りの数を表示する
            if (pref_setting_favorite_check && (random_number % 10 == 0)) {
                return pref_setting_favorite;
            }
            // 一つづつ探索
            for (; random_number <= pref_setting_range_to; random_number++) {

                if (isPrimeNumber(random_number)) {
                    Log.d(TAG, "素数=" + random_number);
                    return random_number;
                }
            }

        }
        Log.d(TAG, "素数みつからんかった");
        return random_number;

    }

    /***
     * 素数判定
     * 
     * @param number
     * @return
     */
    public static boolean isPrimeNumber(long number) {
        if (number < 2) {
            return false;
        }
        for (int i = 2; i <= number - 1; i++) {
            if (number % i == 0) {
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
    protected static void loadSetting(Context context) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        pref_setting_interval_minutes = pref.getInt(
                (String) context.getResources().getText(R.string.pref_key_interval_minutes),
                context.getResources().getInteger(R.integer.default_interval_minutes));
        pref_setting_textcolor = pref.getInt(
                (String) context.getResources().getText(R.string.pref_key_textcolor), context
                        .getResources().getColor(R.color.default_text_color));
        pref_setting_range_from = pref.getInt(
                (String) context.getResources().getText(R.string.pref_key_range_from), context
                        .getResources().getInteger(R.integer.default_range_from));
        pref_setting_range_to = pref.getInt(
                (String) context.getResources().getText(R.string.pref_key_range_to), context
                        .getResources().getInteger(R.integer.default_range_to));
        pref_setting_showtext = pref.getString(
                (String) context.getResources().getText(R.string.pref_key_showtext),
                context.getString(R.string.default_show_text));
        pref_setting_favorite = pref.getInt(
                (String) context.getResources().getText(R.string.pref_key_favorite), context
                        .getResources().getInteger(R.integer.default_favorite));
        pref_setting_favorite_check = pref.getBoolean(
                (String) context.getResources().getText(R.string.pref_key_favorite_check), false);
    }

    /***
     * プリファレンスの値を保存する
     * 
     * @param mContext
     */
    protected static void saveSetting(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edt = pref.edit();

        edt.putInt((String) context.getResources().getText(R.string.pref_key_interval_minutes),
                pref_setting_interval_minutes);
        edt.putInt((String) context.getResources().getText(R.string.pref_key_textcolor),
                pref_setting_textcolor);
        edt.putInt((String) context.getResources().getText(R.string.pref_key_range_from),
                pref_setting_range_from);
        edt.putInt((String) context.getResources().getText(R.string.pref_key_range_to),
                pref_setting_range_to);
        edt.putString((String) context.getResources().getText(R.string.pref_key_showtext),
                pref_setting_showtext);
        edt.putInt((String) context.getResources().getText(R.string.pref_key_favorite),
                pref_setting_favorite);
        edt.putBoolean((String) context.getResources().getText(R.string.pref_key_favorite_check),
                pref_setting_favorite_check);

        edt.commit();
    }

    /**
     * デフォルトの値をプリファレンスにセットする
     * 
     * @param mContext
     */
    protected static void setDefaultValue(Context context) {
        pref_setting_interval_minutes = context.getResources().getInteger(
                R.integer.default_interval_minutes);
        pref_setting_textcolor = context.getResources().getColor(R.color.default_text_color);
        pref_setting_range_from = context.getResources().getInteger(R.integer.default_range_from);
        pref_setting_range_to = context.getResources().getInteger(R.integer.default_range_to);
        pref_setting_showtext = context.getResources().getString(R.string.default_show_text);
        pref_setting_favorite = context.getResources().getInteger(R.integer.default_favorite);
        pref_setting_favorite_check = false;
    }

    /***
     * Alarmに設定するIntentを生成する
     * 
     * @param context
     * @param id
     * @return
     */
    public static Intent createIntent(Context context, int id) {
        // Alarmにより実行されるIntentを作成する
        Intent intent = new Intent(context, PrimeNumberWidgetProvider.class);
        intent.setAction(PrimeNumberWidgetProvider.FILTER_ACTION);
        Uri uri = Uri.withAppendedPath(PrimeNumberWidgetProvider.PRIMENUMBERWIDGET_URI,
                String.valueOf(id));
        intent.setData(uri);
        return intent;
    }

}
