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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/***
 * 素数表示ウィジェット
 * 
 * @author sakura_fish
 */
public class PrimeNumberWidgetProvider extends AppWidgetProvider {

    static final String TAG = "PrimeNumberWidgetProvider";
    {
        Log.d(TAG, "@@@---start---@@@");
    }

    static final Uri PRIMENUMBERWIDGET_URI = Uri
            .parse("primenumber_widget://com.sakurafish.android.primenumberwidget.widget/id");
    static final String FILTER_ACTION = "com.sakurafish.android.primenumberwidget.UPDATE";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        // プリファレンスの値を読み込む
        Utils.loadSetting(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Utils.loadSetting(context);
        // プリファレンスの値をミリ秒に変換
        long interval = Utils.pref_setting_interval_minutes * 60 * 1000;
        Log.d(TAG, "onUpdate interval=" + interval);

        // AppWidget単位に更新処理を行う
        for (int i = 0; i < appWidgetIds.length; i++) {

            int id = appWidgetIds[i];
            Intent intent = Utils.createIntent(context, id);
            PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            long firstTime = System.currentTimeMillis();
            AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

            am.setRepeating(AlarmManager.RTC, firstTime, interval, operation);
            // 設定用のServiceを開始する
            Intent service = new Intent(context, PrimeNumberWidgetService.class);
            service.putExtra(PrimeNumberWidgetService.KEY_WIDGETID, id);
            context.startService(service);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        // AppWidgetが削除されたら、Alarmをキャンセルして設定も削除する
        for (int i = 0; i < appWidgetIds.length; i++) {
            // 削除するAlarmを特定するためのPendingIntentを作成する
            int id = appWidgetIds[i];
            Intent intent = Utils.createIntent(context, id);
            PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);

            // Alarm削除を実行する
            am.cancel(operation);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive");

        // Alarmから呼ばれた場合の処理を行う
        String act = intent.getAction();
        if (act != null && act.equals(FILTER_ACTION)) {
            Log.d(TAG, "onReceive2");
            Uri uri = intent.getData();
            int id = Integer.parseInt(uri.getPathSegments().get(1));
            // 設定用のServiceを開始する
            Intent service = new Intent(context, PrimeNumberWidgetService.class);
            service.putExtra(PrimeNumberWidgetService.KEY_WIDGETID, id);
            context.startService(service);
        }
    }

}
