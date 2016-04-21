package com.afc.biblereading.user;

import static com.afc.biblereading.user.definitions.Consts.GROUP_POSITION;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.afc.biblereading.R;
import com.afc.biblereading.Tabs;
import com.afc.biblereading.R.id;
import com.afc.biblereading.R.layout;
import com.afc.biblereading.R.menu;
import com.afc.biblereading.adapter.UserListAdapter;
import com.afc.biblereading.group.Group;
import com.afc.biblereading.group.GroupListActivity;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.helper.util;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SearchUserActivity extends BaseActivity {

	 private EditText emailEditText;
	 private TextView noUserTextView;
	 private ListView userListView;
	 private int result_user_id;
	 private int position;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_user);
		emailEditText = (EditText) findViewById(R.id.find_user_email);
		noUserTextView = (TextView) findViewById(R.id.no_match);
		userListView = (ListView) findViewById(R.id.search_user_listview);
		Bundle extras = getIntent().getExtras();
	    if(extras == null) {
	    	position= -1;
	    } else {
	    	position= extras.getInt(GROUP_POSITION);
	    }
	}
	
	public void search(View v){
		switch (v.getId()){
		case R.id.start_search:
			progressDialog.show();
			String targetEmail = emailEditText.getText().toString();
			QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
			pagedRequestBuilder.setPage(1);
			pagedRequestBuilder.setPerPage(50);
			 
			ArrayList<String> usersEmails = new ArrayList<String>();
			usersEmails.add(targetEmail);

			Log.v("test", usersEmails.toString());
			QBUsers.getUsersByEmails(usersEmails, pagedRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBUser>>() {
			    @Override
			    public void onSuccess(ArrayList<QBUser> users, Bundle params) {
			    	if (users.size() == 0){
			    		userListView.setVisibility(View.INVISIBLE);
			    		noUserTextView.setVisibility(View.VISIBLE);
			    	}
			    	else{
			    		noUserTextView.setVisibility(View.INVISIBLE);
			    		userListView.setVisibility(View.VISIBLE);
			    		result_user_id = users.get(0).getId();
			    		final UserListAdapter userAdapter = new UserListAdapter(context, R.layout.list_item_invite_user, users);
						List<Integer> membersId = new ArrayList<>();
						for (QBUser qbUser : users){
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
									if (!userAdapter.finishedUser.contains(finishedUserId)){
										userAdapter.finishedUser.add(finishedUserId);
									}
								}
								userListView.setAdapter(userAdapter);
							}

							@Override
							public void onError(List<String> errors) {
							}
						});
			    	}
			    }
			 
			    @Override
			    public void onError(List<String> errors) {
			 
			    }
			});
			progressDialog.hide();
			break;
		default:
			break;
		}
	}
	
	public void invite(View v){
		switch (v.getId()){
		case R.id.invite_user:
			QBCustomObject oldQBGroup = DataHolder.getDataHolder().getSignInUserQbGroup(position);
		    List<Integer> members = DataHolder.getDataHolder().getSignInUserGroup(position).getMembersIdInteger();
			for (Integer id : members){
				Log.v("invite user", "member id: "+String.valueOf(id));
			}
			Log.v("invite user", "new member id: "+String.valueOf(result_user_id));
		    if (!members.contains(result_user_id)){
			    members.add(result_user_id);
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
		        	backToUserGroupsActivity();
		        }
		     
		        @Override
		        public void onError(List<String> errors) {
					DialogUtils.showLong(context, errors.get(0));			     
		        }
		    });		
		}
	}
	
	private void backToUserGroupsActivity() {
		Intent userGroup = new Intent(this, Tabs.class);
		startActivity(userGroup);
		finish();
	}	
}
