package com.afc.biblereading.adapter;

import com.afc.biblereading.R;
import com.afc.biblereading.helper.DataHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UserGroupListAdapter extends BaseAdapter  {

    private LayoutInflater layoutInflater;
    
    public UserGroupListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }
	@Override
	public int getCount() {
        return DataHolder.getDataHolder().getSignInUserGroupListSize();
	}

	@Override
	public Object getItem(int index) {
        return DataHolder.getDataHolder().getSignInUserGroup(index);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_group, null);
            viewHolder = new ViewHolder();
            viewHolder.groupNameTextView = (TextView) convertView.findViewById(R.id.group_name_textview);
            viewHolder.groupSizeTextView = (TextView) convertView.findViewById(R.id.group_size_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        applyGroupName(viewHolder, 
        		DataHolder.getDataHolder().getSignInUserGroup(position).getName(),
        		DataHolder.getDataHolder().getSignInUserGroup(position).getGroupSize()
        		);

        if (position%2 == 0){
            convertView.setBackgroundColor(Color.GRAY);
        }
        else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }
        return convertView;
	}
	
    private void applyGroupName(ViewHolder viewHolder, String groupName, int groupSize) {
        viewHolder.groupNameTextView.setText(groupName);
    	viewHolder.groupSizeTextView.setText(String.valueOf(groupSize));
    }

    public static class ViewHolder {

        TextView groupNameTextView;
        TextView groupSizeTextView;
        ProgressBar progressBar;
    }

}
