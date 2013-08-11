package com.example.swlock;


import com.example.swlock.utility.AppData;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class ScreenStatReceiver extends BroadcastReceiver
{
	Context context;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
		{
			if (AppData.LOGGING)
				System.out.println("screen off");
		}
		else if (intent.getAction().equals(/*Intent.ACTION_SCREEN_ON*/Intent.ACTION_USER_PRESENT))
		{
			if (AppData.LOGGING)
				System.out.println("screen on");

			if (!prefs.getBoolean(AppData.SP_KEY_DEVICE_CONNECTED, false) && prefs.getBoolean(AppData.SP_KEY_LOCK_ON_WAKE, true))
			{
				context.startActivity(new Intent(context, LockActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP));
			}

		}
	}
	
	
	
}
