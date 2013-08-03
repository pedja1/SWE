package com.sonyericsson.extras.liveware.extension.controlsample;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BTReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		DevicePolicyManager mDPM =
			    (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		BluetoothAdapter 
		        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		System.out.println("bt receiver" + device.getName());
		
		if (device.getName().equals("SmartWatch"))
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
