package com.example.swlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.example.swlock.adapter.PairedDeviceAdapter;
import com.example.swlock.model.PairedDevice;
import com.example.swlock.utility.AppData;

import android.app.*;
import android.app.admin.*;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnClickListener
{
	static final String TAG = "MainActivity";
	static final int ACTIVATION_REQUEST = 47; // identifies our request id
	static final int REQUEST_ENABLE_BT = 48;
	static final int REQUEST_PAIR_DEVICE = 49;
	DevicePolicyManager devicePolicyManager;
	ComponentName demoDeviceAdmin;
	SharedPreferences prefs;
	BluetoothAdapter mBluetoothAdapter;
	AlertDialog dialog;
	Button btnChangePassword;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null)
		{
			bluetoothError("Looks like you device doesnt support Bluetooth\nBluetooth is required for this application to work!");
		}
		else
		{
			if (prefs.getBoolean(AppData.SP_KEY_FIRST_RUN, true))
			{
				if (!mBluetoothAdapter.isEnabled())
				{
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				}
				else
				{
					setupPassword(true);
				}
			}
			else if(prefs.getString(AppData.SP_KEY_DEVICE_MAC, "").length() == 0)
			{
				setupSmartWatch();
			}
			else
			{
				continueApp();
			}
		}
		btnChangePassword = (Button)findViewById(R.id.btnChangePassword);
		btnChangePassword.setOnClickListener(this);

	}

	private void setupPassword(final boolean setupSmartWatch)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Set Unlock Password");
		b.setMessage("Enter New Password");
		b.setCancelable(false);
		final EditText ed = new EditText(this);
		b.setPositiveButton("Save", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString(AppData.SP_KEY_PASSWORD, AppData.md5(ed.getText().toString()));
				editor.apply();
				if(setupSmartWatch)setupSmartWatch();
			}
		});
		b.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(setupSmartWatch)finish();
			}
		});
		b.setView(ed);
		b.create().show();
	}
	
	private void setupSmartWatch()
	{
		final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if(AppData.LOGGING)System.out.println(pairedDevices.size());
		if (pairedDevices.size() > 0) 
		{
			
			final List<PairedDevice> devices = new ArrayList<PairedDevice>();
		    for (BluetoothDevice device : pairedDevices) 
		    {
		        devices.add(new PairedDevice(device.getName(), device.getAddress()));
		    }
		    LayoutInflater inflater = getLayoutInflater();
		    View v = inflater .inflate(R.layout.paired_devices_layout, null);
		    ListView list = (ListView)v.findViewById(R.id.list);
		    PairedDeviceAdapter adapter = new PairedDeviceAdapter(MainActivity.this, android.R.layout.simple_list_item_2, devices);
		    list.setAdapter(adapter);
		    list.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id)
				{
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(AppData.SP_KEY_FIRST_RUN, false);
					editor.putString(AppData.SP_KEY_DEVICE_MAC, devices.get(position).getMac());
					editor.apply();
					dialog.dismiss();
					continueApp();
				}
			});
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Paired Devices Detected");
			b.setCancelable(false);
			
			b.setNeutralButton("Not Paired Yet", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					
				}
			});
			b.setView(v);
			dialog = b.create();
			dialog.show();
		}
		else
		{
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setCancelable(false);
			b.setTitle("Smart Watch Not Paired");
			b.setMessage("Please Pair your Smart Watch. Click OK to open Bluetooth Settings");
			b.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					final Intent intent = new Intent(Intent.ACTION_MAIN, null);
		            intent.addCategory(Intent.CATEGORY_LAUNCHER);
		            final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings");
		            intent.setComponent(cn);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            startActivityForResult(intent, REQUEST_PAIR_DEVICE);
				}
			});
			b.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			});
			b.create().show();
			
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
		setContentView(R.layout.activity_main);
		// Initialize Device Policy Manager service and our receiver class
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(this, DeviceAdminReceiver.class);
		if (!devicePolicyManager.isAdminActive(demoDeviceAdmin))
		{
			showDialog(devicePolicyManager.isAdminActive(demoDeviceAdmin));
		}

		ToggleButton lockToggle = (ToggleButton) findViewById(R.id.toggleLock);
		CheckBox lockOnWake = (CheckBox)findViewById(R.id.relockOnWake);
		if (prefs.getBoolean(AppData.SP_KEY_SERVICE_ENABLED, true))
		{
			if (!isServiceRunning())
			{
				startService(new Intent(this, ScreenStatService.class));
			}
			lockToggle.setChecked(true);
		}

		lockToggle.setOnCheckedChangeListener(this);
		lockOnWake.setChecked(prefs.getBoolean(AppData.SP_KEY_LOCK_ON_WAKE, true));
		lockOnWake.setOnCheckedChangeListener(this);
		
	}

	

	private void showDialog(final boolean isEnabled)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setCancelable(false);
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
							"Enabling this application as a Device Administrator will allow it to lock device when Smart Watch has been disconnected!");
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
				/*
				 * PackageManager p = getPackageManager();
				 * p.setComponentEnabledSetting(getComponentName(),
				 * PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				 * PackageManager.DONT_KILL_APP);
				 */
				Toast.makeText(this, "Device admin enabled", Toast.LENGTH_LONG)
						.show();
				//finish();

			}
			else
			{
				Toast.makeText(this, "Error enabling device admin",
						Toast.LENGTH_LONG).show();
				Log.i(TAG, "Administration enable FAILED!");
				finish();
			}
			return;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK)
			{
				setupPassword(true);
			}
			else
			{
				bluetoothError("Filed to enable Bluetooth.\nBluetooth is required for this application to work!");
			}
			return;
		case REQUEST_PAIR_DEVICE:
			setupSmartWatch();
			return;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean isServiceRunning()
	{
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE))
		{
			if ((getPackageName() + ".ScreenStatService")
					.equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		SharedPreferences.Editor editor = prefs.edit();
		switch(buttonView.getId())
		{
		case R.id.toggleLock:
			if (isChecked)
			{
				startService(new Intent(MainActivity.this,
						ScreenStatService.class));
				editor.putBoolean(AppData.SP_KEY_SERVICE_ENABLED,
						true);
			}
			else
			{
				stopService(new Intent(MainActivity.this,
						ScreenStatService.class));
				editor.putBoolean(AppData.SP_KEY_SERVICE_ENABLED,
						false);
			}
			break;
		case R.id.relockOnWake:
			if (isChecked)
			{
				editor.putBoolean(AppData.SP_KEY_LOCK_ON_WAKE,
						true);
			}
			else
			{
				editor.putBoolean(AppData.SP_KEY_LOCK_ON_WAKE,
						false);
			}
			break;
		}
		editor.apply();
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.btnChangePassword:
			setupPassword(false);
			break;
		}
		
	}

}
