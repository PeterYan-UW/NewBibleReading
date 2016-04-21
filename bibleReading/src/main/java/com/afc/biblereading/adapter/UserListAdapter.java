package com.afc.biblereading.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afc.biblereading.R;
import com.afc.biblereading.group.Member;
import com.afc.biblereading.helper.DataHolder;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.xmlpull.v1.XmlPullParser;

public class UserListAdapter extends ArrayAdapter<QBUser> {
	private ArrayList<QBUser> memberList = new ArrayList<>();
    private Map<Integer, Integer> userStatus = new HashMap<>();
    private Map<Integer, ViewHolder> userViewHolders = new HashMap<>();
    public List<Integer> finishedUser = new ArrayList<>();
    private int resource;
    private int index = 0;
    public UserListAdapter(Context context, int resource, 
			ArrayList<QBUser> memberList) {
    	super(context, resource, memberList);
    	this.memberList = memberList;
    	this.resource = resource;
    }
	
	private class ViewHolder {
		TextView userName;
		ImageView statusIcon;
		TextView statusTextview;
	}

    private Boolean wait = true;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        Log.v("user List adapter","get view for position"+String.valueOf(position));
        QBUser member = this.memberList.get(position);
        String name = member.getFullName();
        final Integer id = member.getId();
        if (convertView == null) {
            index++;
            Log.v("user List adapter","get view convertView null "+String.valueOf(index));
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(resource, null);
            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView) convertView.findViewById(
            		R.id.user_name_textview);
            viewHolder.statusIcon = (ImageView) convertView.findViewById(
            		R.id.today_task_imageview);
            viewHolder.statusTextview = (TextView) convertView.findViewById(
            		R.id.status_textview);
            userViewHolders.put(id, viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.userName.setText(name);

        if (userViewHolders.containsKey(id)){
            userViewHolders.remove(id);
        }
        userViewHolders.put(id, viewHolder);
        if (finishedUser.contains(id)){
            userViewHolders.get(id).statusIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.done));
            userViewHolders.get(id).statusTextview.setText("已完成今日任务");
        } else {
            userViewHolders.get(id).statusIcon.setImageDrawable(getContext().getResources().getDrawable(R.drawable.fail));
            userViewHolders.get(id).statusTextview.setText("还未完成今日任务");
        }

        return convertView;
    }
}