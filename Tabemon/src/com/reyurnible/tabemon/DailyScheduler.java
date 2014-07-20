package com.reyurnible.tabemon;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class DailyScheduler {
	 
	private Context context;
	
	public DailyScheduler(Context context) {
		this.context = context;
	}
	/*
	 * duration_time(ミリ秒)後 launch_serviceを実行する
	 * service_idはどのサービスかを区別する為のID(同じなら上書き)
	 * 一回起動するとそのタイミングで毎日1回動き続ける
	 */
	public <T> void set(long duration_time,int service_id) {
		
		//設定した日時で発行するIntentを生成
		Intent intent = new Intent(context, RemindLaterReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, service_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//日時と発行するIntentをAlarmManagerにセットします
		AlarmManager manager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);
		manager.setRepeating(AlarmManager.RTC,duration_time,AlarmManager.INTERVAL_DAY, sender);		
	}
 
	/*
	 * 起動したい時刻(hour:minuite)を指定するバージョン
	 * 指定した時刻で毎日起動する
	 */
	public <T> void setByTime(int hour, int minuite, int service_id) {
		// 日本(+9)以外のタイムゾーンを使う時はここを変える
		TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
		
		//今日の目標時刻のカレンダーインスタンス作成
		Calendar cal_target = Calendar.getInstance();
		cal_target.setTimeZone(tz);		
		cal_target.set(Calendar.HOUR_OF_DAY, hour);
		cal_target.set(Calendar.MINUTE, minuite);
		cal_target.set(Calendar.SECOND, 0);
 
		//現在時刻のカレンダーインスタンス作成
		Calendar cal_now = Calendar.getInstance();
		cal_now.setTimeZone(tz);
		
		//ミリ秒取得
		long target_ms = cal_target.getTimeInMillis();
		long now_ms = cal_now.getTimeInMillis();
 
		//今日ならそのまま指定
		if (target_ms >= now_ms) {
			set(target_ms,service_id);
		//過ぎていたら明日の同時刻を指定
		} else {
			cal_target.add(Calendar.DAY_OF_MONTH, 1);
			target_ms = cal_target.getTimeInMillis();
			set(target_ms,service_id);
		}
	}
	/*
	 * キャンセル用
	 */
	public <T> void cancel(long wake_time,int service_id) {		
		//設定した日時で発行するIntentを生成
		Intent intent = new Intent(context, RemindLaterReceiver.class);
		PendingIntent action = PendingIntent.getBroadcast(context, service_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(action);
	}
}