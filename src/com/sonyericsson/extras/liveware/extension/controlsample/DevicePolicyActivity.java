package com.sonyericsson.extras.liveware.extension.controlsample;
import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;


public class DevicePolicyActivity extends Activity
{
	static final String TAG = "DevicePolicyDemoActivity";
	static final int ACTIVATION_REQUEST = 47; // identifies our request id
	DevicePolicyManager devicePolicyManager;
	ComponentName demoDeviceAdmin;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize Device Policy Manager service and our receiver class
		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(this, DeviceAdminReceiver.class);
		if(!devicePolicyManager.isAdminActive(demoDeviceAdmin))
		{
		    showDialog(devicePolicyManager.isAdminActive(demoDeviceAdmin));
		}
		
		

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
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean isServiceRunning() 
	{
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if ("com.example.MyService".equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

}
