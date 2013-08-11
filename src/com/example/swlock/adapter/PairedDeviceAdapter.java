package com.example.swlock.adapter;

import java.util.List;
import com.example.swlock.model.PairedDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PairedDeviceAdapter extends ArrayAdapter<PairedDevice>
{
	int resId;
	public PairedDeviceAdapter(Context context,
			int resId, List<PairedDevice> devices)
	{
		super(context, resId, devices);
		this.resId = resId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		PairedDevice device = getItem(position);
		ViewHolder holder;
		if(convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resId, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(android.R.id.text1);
			holder.subTitle = (TextView) convertView.findViewById(android.R.id.text2);
			convertView.setTag(holder);
		}
		else
		{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.title.setText(device.getName());
		holder.subTitle.setText(device.getMac());
		return convertView;
	}
	
	class ViewHolder
	{
		TextView title;
		TextView subTitle;
	}

}
