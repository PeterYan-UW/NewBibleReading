package com.afc.biblereading.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.afc.biblereading.R;
import com.afc.biblereading.Tabs;
import com.afc.biblereading.adapter.UserListAdapter;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.helper.util;
import com.afc.biblereading.user.BaseActivity;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import static com.afc.biblereading.user.definitions.Consts.POSITION;

public class ShowGroupActivity extends BaseActivity {

    private TextView groupNameTextView;
    private TextView groupNumTextView;
    private ListView groupMemberListView;
    private int position;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_group);
		initialUI();
	    Bundle extras = getIntent().getExtras();
	    if(extras == null) {
	    	position= -1;
	    } else {
	    	position= extras.getInt(POSITION);
	    }
	    Group group = DataHolder.getDataHolder().getGroup(position);
	    applyGroupInfo(group);
	}

	private void initialUI() {
        groupNameTextView = (TextView) findViewById(R.id.full_name_textview);	
        groupNumTextView = (TextView) findViewById(R.id.group_number_textview);
        groupMemberListView = (ListView) findViewById(R.id.member_listview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClick(View view) {
        switch (view.getId()) {
        	case R.id.join_this_group:
        		joinGroup();
        		break;
        }
    }

	private void joinGroup() {
	    QBCustomObject oldQBGroup = DataHolder.getDataHolder().getQBGroup(position);
	    List<Integer> members = DataHolder.getDataHolder().getGroup(position).getMembersIdInteger();
        int signInUserId = DataHolder.getDataHolder().getSignInUserId();
        if (!members.contains(signInUserId)){
            members.add(signInUserId);
        }
	    QBCustomObject qbGroup = new QBCustomObject();
	    qbGroup.setClassName("group");
	    HashMap<String, Object> fields = new HashMap<String, Object>();
	    fields.put("members", members);
	    qbGroup.setFields(fields);
	    qbGroup.setCustomObjectId(oldQBGroup.getCustomObjectId());
	    QBCustomObjects.updateObject(qbGroup, new QBEntityCallbackImpl<QBCustomObject>() {
	        @Override
	        public void onSuccess(QBCustomObject newQBGroup, Bundle params) {
	        	Group group = util.QBGroup2Group(newQBGroup);
	        	DataHolder.getDataHolder().addSignInUserGroup(group);	
	        	DataHolder.getDataHolder().addSignInUserQbGroup(newQBGroup);	 
	        	backToUserGroupsActivity();
	        }
	     
	        @Override
	        public void onError(List<String> errors) {
				DialogUtils.showLong(context, errors.get(0));			     
	        }
	    });		
	}
	
    private void applyGroupInfo(Group group) {
    	QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
	    groupNameTextView.setText(group.getName());
    	groupNumTextView.setText(String.valueOf(group.getGroupSize()));
    	List<Integer> usersIds = group.getMembersIdInteger();
    	 
    	QBUsers.getUsersByIDs(usersIds, pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
    		@Override
            public void onSuccess(final ArrayList<QBUser> qbUsers, Bundle bundle) {
    	    	final UserListAdapter memberAdapter = new UserListAdapter(context, R.layout.list_item_user, qbUsers);
				List<String> membersId = new ArrayList<>();
				for (QBUser qbUser : qbUsers){
					membersId.add(String.valueOf(qbUser.getId()));
				}
				Date today = new Date();
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				QBRequestGetBuilder logRequestBuilder = new QBRequestGetBuilder();
				logRequestBuilder.in("user_id", membersId);
				logRequestBuilder.eq("done", true);
				logRequestBuilder.gt("created_at", today.getTime() / 1000);
				QBCustomObjects.getObjects("DailyRecords", logRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {

					@Override
					public void onSuccess(ArrayList<QBCustomObject> records, Bundle params) {
						for (QBCustomObject record : records){
							int finishedUserId = record.getUserId();
							if (!memberAdapter.finishedUser.contains(finishedUserId)){
								memberAdapter.finishedUser.add(finishedUserId);
							}
						}
						groupMemberListView.setAdapter(memberAdapter);
					}

					@Override
					public void onError(List<String> errors) {
					}
				});
    	    }
    	 
    	    @Override
    	    public void onError(List<String> errors) {
    	 
    	    }
    	});
    }
    
	private void backToUserGroupsActivity() {
		Intent userGroup = new Intent(this, Tabs.class);
		startActivity(userGroup);
		finish();
	}	
}
