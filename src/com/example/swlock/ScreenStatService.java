package com.example.swlock;

import com.example.swlock.utility.AppData;

import android.app.*;
import android.content.*;
import android.os.*;

public class ScreenStatService extends Service
{

    int ONGOING_NOTIFICATION_ID = 2505991;
    BroadcastReceiver mReceiver;
    @Override
    public void onCreate()
	{
		Notification notification = new Notification(R.drawable.icon, getText(R.string.app_name),
													 System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.app_name),
										getText(R.string.app_name), pendingIntent);
		
		startForeground(ONGOING_NOTIFICATION_ID, notification);
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mReceiver = new ScreenStatReceiver();
        registerReceiver(mReceiver, filter);
        if(AppData.LOGGING)System.out.println("ScreenStatService onCreate");
		
	}
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        return Service.START_STICKY;
    }
	@Override
	public void onDestroy()
	{
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	
}
