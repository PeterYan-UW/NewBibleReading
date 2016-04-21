package com.afc.biblereading.group;

import static com.afc.biblereading.user.definitions.Consts.POSITION;
import static com.afc.biblereading.user.definitions.Consts.GROUP_POSITION;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.afc.biblereading.MemberInfoActivity;
import com.afc.biblereading.R;
import com.afc.biblereading.Tabs;
import com.afc.biblereading.adapter.UserListAdapter;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.user.BaseActivity;
import com.afc.biblereading.user.CreateSessionActivity;
import com.afc.biblereading.user.SearchUserActivity;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ShowUserGroupActivity extends BaseActivity {
    private TextView userGroupNameTextView;
    private TextView userGroupNumTextView;
    private ListView groupMemberListView;
    private int position;
    private Group userGroup;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_group);
		initUI();
        initUserGroup();
	}
	private void initUI() {        
        userGroupNameTextView = (TextView) findViewById(R.id.user_group_name_textview);
        userGroupNumTextView = (TextView) findViewById(R.id.user_group_number_textview);
        groupMemberListView = (ListView) findViewById(R.id.member_listview);
    }

    private void initUserGroup() {
	    Bundle extras = getIntent().getExtras();
	    if(extras == null) {
	    	position= -1;
	    } else {
	    	position= extras.getInt(POSITION);
	    }
    	userGroup = DataHolder.getDataHolder().getSignInUserGroup(position);
        if (userGroup != null) {
        	applyUserGroupInfo(userGroup.getName(), userGroup.getGroupSize());
        	groupMemberListView.setOnItemClickListener(
        			new OnItemClickListener(){
        				@Override
        				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        					Log.v("user group activity", "click Item");
        					startShowUserActivity(position);
        				}
        			});
        }
	}
    
    private void applyUserGroupInfo(String groupName, int memberNum) {
    	QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
    	userGroupNameTextView.setText(groupName);
    	userGroupNumTextView.setText(String.valueOf(memberNum));
    	List usersIds = userGroup.getMembersIdInteger();
    	 
    	QBUsers.getUsersByIDs(usersIds, pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
    		@Override
            public void onSuccess(final ArrayList<QBUser> qbUsers, Bundle bundle) {
    			userGroup.setMembersQB(qbUsers);
    	    	final UserListAdapter memberAdapter = new UserListAdapter(context, R.layout.list_item_user, qbUsers);
				List<Integer> membersId = new ArrayList<>();
				for (QBUser qbUser : qbUsers){
					membersId.add(qbUser.getId());
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
    
    @Override
    public void onResume(){
		super.onResume(); 
		if (DataHolder.getDataHolder().getSignInQbUser()== null){
    		Intent user = new Intent(this, CreateSessionActivity.class);
    		startActivity(user);  			
		}
		initUserGroup();  	
    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_group, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	        case R.id.leave_group:
	        	leaveGroup();
	        	return true;
			default:
			    return super.onOptionsItemSelected(item);
		}    
    }
    public void inviteFriend(View v){
    	switch (v.getId()) {
	    	case R.id.invite_friend:
	    		Intent userList = new Intent(this, SearchUserActivity.class);
	    		userList.putExtra(GROUP_POSITION, position);
	    		startActivity(userList);
	    		break;
    	}
    }
	private void leaveGroup() {
		QBCustomObject oldQBGroup = DataHolder.getDataHolder().getSignInUserQbGroup(position);
	    List<String> memberList = userGroup.getMembersIdString();
	    Integer currentUserId = DataHolder.getDataHolder().getSignInUserId();
	    Log.v("current user id", String.valueOf(currentUserId));
	    Log.v("before leave group", memberList.toString());
	    memberList.remove(String.valueOf(currentUserId));
	    Log.v("leave group", memberList.toString());
        final String groupId = oldQBGroup.getCustomObjectId();
	    if (memberList.size() == 0){
	    	QBCustomObjects.deleteObject("group", groupId, new QBEntityCallbackImpl<Object>() {
	    		@Override
		        public void onSuccess() {
                    DataHolder.getDataHolder().removeSignInUserGroup(groupId);
                    DataHolder.getDataHolder().removeSignInUserQbGroup(groupId);
                    backToMain();
		        }
		     
		        @Override
		        public void onError(List<String> errors) {
					DialogUtils.showLong(context, errors.get(0));			     
		        }
	    	});
	    }
	    else{
	    	QBCustomObject qbGroup = new QBCustomObject();
		    qbGroup.setClassName("group");
		    HashMap<String, Object> fields = new HashMap<String, Object>();
		    fields.put("members", memberList);
		    qbGroup.setFields(fields);
		    qbGroup.setCustomObjectId(groupId);
		    QBCustomObjects.updateObject(qbGroup, new QBEntityCallbackImpl<QBCustomObject>() {
		        @Override
		        public void onSuccess(QBCustomObject newQBGroup, Bundle params) {
		        	DataHolder.getDataHolder().removeSignInUserGroup(groupId);
		        	DataHolder.getDataHolder().removeSignInUserQbGroup(groupId);
                    backToMain();
		        }
		     
		        @Override
		        public void onError(List<String> errors) {
					DialogUtils.showLong(context, errors.get(0));			     
		        }
		    });
	    }		
	}



    private void startShowUserActivity(int member_position) {
        Intent intent = new Intent(this, MemberInfoActivity.class);
        intent.putExtra(POSITION, member_position);
        intent.putExtra(GROUP_POSITION, position);
        startActivity(intent);
	}
    private void backToMain() {
        Intent intent = new Intent(this, Tabs.class);
        startActivity(intent);
        finish();
    }
}
