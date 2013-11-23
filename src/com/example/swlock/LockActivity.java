package com.example.swlock;

import java.util.Timer;

import com.example.swlock.utility.AppData;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LockActivity extends Activity implements Runnable, OnClickListener
{
	DevicePolicyManager mDPM;
	int running = 20;
	Handler handler;
	TextView message;
	EditText password;
	Button unlock, cancel;
	Thread thread;
	static boolean lock = true;
	SharedPreferences prefs;
	static LockActivity activity = null;
	static LockHandler lockHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		activity = this;
		lockHandler = new LockHandler();
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		lock = true;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lock);
		message = (TextView)findViewById(R.id.message);
		password = (EditText)findViewById(R.id.password);
		unlock = (Button)findViewById(R.id.unlock);
		cancel = (Button)findViewById(R.id.cancel);
		mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		handler = new Handler();
		thread = new Thread(this);
		thread.start();
		cancel.setOnClickListener(this);
		unlock.setOnClickListener(this);
	}
	
	public static void exit()
	{
		if(activity != null)
		lockHandler.sendEmptyMessage(AppData.CANCEL_LOCK);
	}

	@Override
	public void run()
	{
		while(running != 0)
		{
			handler.post(new Runnable(){

				@Override
				public void run()
				{
					unlock.setText("Unlock ("+running+")");
				}});
			try
			{
				Thread.sleep(1000);
				running--;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		running = 20;
		if(lock && !prefs.getBoolean(AppData.SP_KEY_DEVICE_CONNECTED, false))
		{
			mDPM.lockNow();
			finish();
		}
		
	}

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.unlock:
				if(AppData.md5(password.getText().toString()).
						equals(prefs.getString(AppData.SP_KEY_PASSWORD, "")))
				{
					lock = false;
					finish();
				}
				else
				{
					password.setError(getString(R.string.wrong_password));
				}
			break;
			case R.id.cancel:
				finish();
				//lock = false;
				mDPM.lockNow();
				break;
		}
	}

	class LockHandler extends Handler
	{

		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == AppData.CANCEL_LOCK)
				lock = false;
				finish();
			super.handleMessage(msg);
		}
		
	}

}
