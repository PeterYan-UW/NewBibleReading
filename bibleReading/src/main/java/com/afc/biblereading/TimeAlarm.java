package com.afc.biblereading;



 

import com.afc.biblereading.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

 

// The class has to extend the BroadcastReceiver to get the notification from the system

public class TimeAlarm extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent paramIntent) {
		// Request the notification manager
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// Create a new intent which will be fired if you click on the notification
		Intent intent = new Intent(context,Tabs.class);

		//intent.setData(Uri.parse("http://www.papers.ch"));
		// Attach the intent to a pending intent
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		// Create the notification
		Notification notification = new Notification.Builder(context)
		.setTicker("读经小伙伴温馨提醒您")
		.setContentTitle("读经小伙伴")
		.setContentText("还没有完成今天的读经任务哦~")
		.setSmallIcon(R.drawable.logo)
		.setContentIntent(pendingIntent).getNotification();
		notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
//		Notification notification = new Notification(R.drawable.ic_launcher, "Visit our homepage", System.currentTimeMillis());
//
//		notification.setLatestEventInfo(context, "Visit our homepage", "http://www.papers.ch",pendingIntent);

 

		// Fire the notification

		if (CalenderActivity.unfinish!=0) {
			Log.v("notify0","notify");
			notificationManager.notify(1, notification);
			Log.v("notify","notify");
		}
		

	}

}
