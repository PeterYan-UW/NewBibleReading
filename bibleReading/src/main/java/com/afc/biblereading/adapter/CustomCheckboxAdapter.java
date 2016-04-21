package com.afc.biblereading.adapter;

import java.util.ArrayList;

import com.afc.biblereading.CalenderActivity;
import com.afc.biblereading.R;
import com.afc.biblereading.Task;
import com.afc.biblereading.R.id;
import com.afc.biblereading.R.layout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

public class CustomCheckboxAdapter extends ArrayAdapter<Task> {
	private ArrayList<Task> taskList;
	
	public CustomCheckboxAdapter(Context context, int resource, 
			ArrayList<Task> taskList) {
		super(context, resource, taskList);
		this.taskList = new ArrayList<Task>();
		this.taskList.addAll(taskList);
	}
	
	private class ViewHolder {
		CheckBox cbox;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Log.v("ConvertView", String.valueOf(position));
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.single_item, null);
			holder = new ViewHolder();
			holder.cbox = (CheckBox) convertView.findViewById(R.id.cBox);
			convertView.setTag(holder);

			holder.cbox.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v ;
					Task task = (Task) cb.getTag();
					if (cb.isChecked()){
						Toast.makeText(getContext().getApplicationContext(), 
								"Finish reading " + cb.getText(), 
								Toast.LENGTH_LONG).show();
						CalenderActivity.unfinish--;
						Log.v(Integer.toString(CalenderActivity.unfinish),"unfinish");
					}
					task.setDone(getContext(), cb.isChecked());
				}
			});
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		Task task = taskList.get(position);
		holder.cbox.setText(task.asString());
		holder.cbox.setChecked(task.getDone());
		holder.cbox.setTag(task);
		
		return convertView;
	}
}
