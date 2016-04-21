package com.afc.biblereading.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afc.biblereading.R;
import com.afc.biblereading.Task;
import com.afc.biblereading.adapter.GroupListAdapter.ViewHolder;
import com.afc.biblereading.helper.DataHolder;

public class UserLogListAdapter extends ArrayAdapter<String> {


    private ArrayList<String> logList;
    
    public UserLogListAdapter(Context context, int resource, 
			ArrayList<String> logList) {
		super(context, resource, logList);
		this.logList = new ArrayList<String>();
		this.logList.addAll(logList);
    }

	private class ViewHolder {
		TextView userLog;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Log.v("ConvertView", String.valueOf(position));
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_item_user_log, null);
			holder = new ViewHolder();
			holder.userLog = (TextView) convertView.findViewById(R.id.user_log_textview);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		String log = logList.get(position);
		Log.v("log adapter", log);
		holder.userLog.setText(log);		
		return convertView;
	}

}
