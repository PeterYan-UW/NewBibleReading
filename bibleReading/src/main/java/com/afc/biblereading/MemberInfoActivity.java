package com.afc.biblereading;

import static com.afc.biblereading.user.definitions.Consts.POSITION;
import static com.afc.biblereading.user.definitions.Consts.GROUP_POSITION;

import java.util.ArrayList;
import java.util.List;

import com.afc.biblereading.adapter.UserLogListAdapter;
import com.afc.biblereading.group.Group;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.user.BaseActivity;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MemberInfoActivity extends BaseActivity { 

    private TextView emailTextView;
    private TextView fullNameTextView;
    private ListView memberLogListView;
    private int member_position;
    private int group_position;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member_info);
		initUI();
	    Bundle extras = getIntent().getExtras();
	    if(extras == null) {
	    	member_position = -1;
	    	group_position = -1;
	    } else {
	    	member_position= extras.getInt(POSITION);
	    	group_position= extras.getInt(GROUP_POSITION);
	    }
	    ArrayList<QBUser> members = DataHolder.getDataHolder().getSignInUserGroup(group_position).getMembersQB();
	    QBUser member = members.get(member_position);
	    applyMemberInfo(member);
	}
	
	private void applyMemberInfo(QBUser member) {
		progressDialog.show();
		emailTextView.setText(member.getEmail());
		fullNameTextView.setText(member.getFullName());
		QBRequestGetBuilder logRequestBuilder = new QBRequestGetBuilder();
    	logRequestBuilder.eq("user_id", member.getId());
    	QBCustomObjects.getObjects("DailyRecords", logRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
			
			@Override
			public void onSuccess(ArrayList<QBCustomObject> logs, Bundle bundle) {
				ArrayList<String> logList = new ArrayList<String>();
				for (QBCustomObject log : logs){
					String g = (String) log.getFields().get("task");
					logList.add(g);
				}
				UserLogListAdapter logAdapter = new UserLogListAdapter(context, R.layout.list_item_user_log, logList);
				memberLogListView.setAdapter(logAdapter);
				progressDialog.hide();
			}
			
			@Override
			public void onError(List<String> errors) {
				DialogUtils.showLong(context, errors.get(0));				
			}
		});    
		
	}
	
	private void initUI() {
        emailTextView = (TextView) findViewById(R.id.email_textview);
        fullNameTextView = (TextView) findViewById(R.id.full_name_textview);
        memberLogListView = (ListView) findViewById(R.id.member_log_listview);
	}
}
