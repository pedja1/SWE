package com.example.swlock;

import com.example.swlock.utility.AppData;

import android.app.*;
import android.app.admin.*;
import android.bluetooth.BluetoothAdapter;
import android.content.*;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.*;
import android.widget.*;


public class MainActivity extends Activity
{
	static final String TAG = "DevicePolicyDemoActivity";
	static final int ACTIVATION_REQUEST = 47; // identifies our request id
	static final int REQUEST_ENABLE_BT = 48;
	DevicePolicyManager devicePolicyManager;
	ComponentName demoDeviceAdmin;
	SharedPreferences prefs;
	

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) 
		{
		    bluetoothError("Looks like you device doesnt support Bluetooth\nBluetooth is required for this application to work!");
		}
		else
		{
			if()
			if (!mBluetoothAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			else
			{
				continueApp();
			}
		}

	}

	private void bluetoothError(String errorMessage)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setCancelable(false);
		b.setTitle("Error!");
		b.setMessage(errorMessage);
		b.setPositiveButton("Exit", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
		});
		b.create().show();
	}

	private void continueApp()
	{
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Initialize Device Policy Manager service and our receiver class
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(this, DeviceAdminReceiver.class);
		if(!devicePolicyManager.isAdminActive(demoDeviceAdmin))
		{
		    showDialog(devicePolicyManager.isAdminActive(demoDeviceAdmin));
		}
		
		ToggleButton lockToggle = (ToggleButton)findViewById(R.id.toggleLock);
		
		if(prefs.getBoolean(AppData.SP_KEY_SERVICE_ENABLED, true));
		{
			if(!isServiceRunning())
			{
				startService(new Intent(this, ScreenStatService.class));
			}
			lockToggle.setChecked(true);
		}
		
		lockToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				SharedPreferences.Editor editor = prefs.edit();
				if(isChecked)
				{
					startService(new Intent(MainActivity.this, ScreenStatService.class));
					editor.putBoolean(AppData.SP_KEY_SERVICE_ENABLED, true);
				}
				else
				{
					stopService(new Intent(MainActivity.this, ScreenStatService.class));
					editor.putBoolean(AppData.SP_KEY_SERVICE_ENABLED, false);
				}
				editor.apply();
			}
		});
		setupSW();
	}

	private void setupSW()
	{
		
	}

	private void showDialog(final boolean isEnabled)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMessage(isEnabled ? "This application is already a device admin" 
					 : "In Order to use this app you need to enable it as a device administrator");
		b.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (!isEnabled)
					{
						// Activate device administration
						Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
						intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
										demoDeviceAdmin);
						intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
										"Your boss told you to do this");
						startActivityForResult(intent, ACTIVATION_REQUEST);
					}
					else
					{
						finish();
					}
				}
			});
		if (!isEnabled)
		{
			b.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						finish();
					}
				});
		}
		b.create().show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case ACTIVATION_REQUEST:
				if (resultCode == Activity.RESULT_OK)
				{
					Log.i(TAG, "Administration enabled!");
					/*PackageManager p = getPackageManager();
					 p.setComponentEnabledSetting(getComponentName(), 
					 PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
					 PackageManager.DONT_KILL_APP);*/
					Toast.makeText(this, "Device admin enabled", Toast.LENGTH_LONG).show();
					finish();

				}
				else
				{
					Toast.makeText(this, "Error enabling device admin", Toast.LENGTH_LONG).show();
					Log.i(TAG, "Administration enable FAILED!");
					finish();
				}
				return;
			case REQUEST_ENABLE_BT:
				if (resultCode == Activity.RESULT_OK)
				{
					continueApp();
				}
				else
				{
					bluetoothError("Filed to enable Bluetooth.\nBluetooth is required for this application to work!");
				}
				return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	

	private boolean isServiceRunning() 
	{
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if ((getPackageName()+".ScreenStatService").equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
	
	

}
