package com.example.swlock;

import com.example.swlock.utility.AppData;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BTReceiver extends BroadcastReceiver
{
	SharedPreferences prefs;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(prefs.getBoolean(AppData.SP_KEY_SERVICE_ENABLED, true))
		{
			handleIntent(context, intent);
		}
	}
	
	private void handleIntent(Context context, Intent intent)
	{
		DevicePolicyManager mDPM =
			    (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		BluetoothAdapter 
		        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(AppData.LOGGING)System.out.println("bt receiver" + device.getName());
		
		if (device.getAddress().equals(prefs.getString(AppData.SP_KEY_DEVICE_MAC, "")))
		{
			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED))
			{
				Log.d("Z", "SmartWatch Connected");
			}
			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)
					|| intent.equals(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED))
			{
				Log.d("Z", "SmartWatch Disconnected");
				if(bluetoothAdapter.isEnabled())
				{
					mDPM.lockNow();
				}
			}
		}
	}

}
