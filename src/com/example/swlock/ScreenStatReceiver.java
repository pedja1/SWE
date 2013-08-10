package com.example.swlock;

import com.example.swlock.utility.AppData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenStatReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context arg0, Intent intent)
	{
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) 
		{
			if(AppData.LOGGING)System.out.println("screen off");
        } 
		else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) 
        {
			if(AppData.LOGGING)System.out.println("screen on");
        }
	}

}
