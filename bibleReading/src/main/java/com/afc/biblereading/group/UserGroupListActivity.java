package com.afc.biblereading.group;

import java.util.ArrayList;
import java.util.List;

import com.afc.biblereading.R;
import com.afc.biblereading.adapter.UserGroupListAdapter;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.helper.util;
import com.afc.biblereading.user.BaseActivity;
import com.afc.biblereading.user.CreateSessionActivity;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import static com.afc.biblereading.user.definitions.Consts.POSITION;

public class UserGroupListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
	
    private UserGroupListAdapter groupsListAdapter;
    private ListView groupList;
	private TextView titleTextView;
	private TextView joinGroupTextView;
	private TextView userGroupTextView;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_list);
        initUI();
        initGroupList();
	}
	
	private void initUI() {
        groupList = (ListView) findViewById(R.id.groups_listview);
		titleTextView = (TextView) findViewById(R.id.group_page_content_textView);
		joinGroupTextView = (TextView) findViewById(R.id.join_group_page_content_textView);
		userGroupTextView = (TextView) findViewById(R.id.user_group_content_textView);
    }
    
    private void initGroupList() {
		if (DataHolder.getDataHolder().getSignInUserGroupListSize() <= 0){
			titleTextView.setVisibility(View.VISIBLE);
			userGroupTextView.setVisibility(View.INVISIBLE);
			joinGroupTextView.setVisibility(View.INVISIBLE);
		} else {
			titleTextView.setVisibility(View.INVISIBLE);
			userGroupTextView.setVisibility(View.VISIBLE);
			joinGroupTextView.setVisibility(View.INVISIBLE);
			groupsListAdapter = new UserGroupListAdapter(this);
			groupList.setAdapter(groupsListAdapter);
			groupList.setOnItemClickListener(this);
		}
    }
    
    @Override
    public void onResume(){
		super.onResume();
		if (DataHolder.getDataHolder().getSignInQbUser()== null){
    		Intent user = new Intent(this, CreateSessionActivity.class);
    		startActivity(user);
		} else{
            Log.v("userGroupList activity", "resume called");
            initUI();
            initGroupList();
        }
	}

	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startShowGroupActivity(position);
    }

    private void startShowGroupActivity(int position) {
        Intent intent = new Intent(this, ShowUserGroupActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_group_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
	        case R.id.create_group:
	        	createGroup();
	        	return true;
	        case R.id.join_group:
        		progressDialog.show();
        		GroupHelper.getOtherGroup(context, progressDialog);
	        	return true;
			default:
			    return super.onOptionsItemSelected(item);
		}    
    }
    
	private Boolean exit = false;
	@Override
    public void onBackPressed() {
        if (exit) {
            DataHolder.getDataHolder().setSignInQbUser(null);
            DataHolder.getDataHolder().setSignInUserGroup(null);
            DataHolder.getDataHolder().setSignInUserQbGroup(null);
        	Intent intent = new Intent(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_HOME);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(intent);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
    
	private void createGroup(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Create New Bible Reading Group");
		builder.setMessage("Group Name");
		final EditText inputField = new EditText(this);
		builder.setView(inputField);
		builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
	    		progressDialog.show();
				String group_name = inputField.getText().toString();
				Integer currentUserID = DataHolder.getDataHolder().getSignInUserId();
				ArrayList<Integer> members = new ArrayList<Integer>();
				members.add(currentUserID);
				
				QBCustomObject group = new QBCustomObject();
				group.putString("group_name", group_name);
				group.putInteger("num_member", 1);
				group.putArray("members", members);
				group.setClassName("group");
				QBCustomObjects.createObject(group, new QBEntityCallbackImpl<QBCustomObject>() {
		    	    @Override
		    	    public void onSuccess(QBCustomObject createdObject, Bundle bundle) {
		    	    	DataHolder.getDataHolder().addSignInUserQbGroup(createdObject);
		    	    	Group g = util.QBGroup2Group(createdObject);
		    	    	DataHolder.getDataHolder().addSignInUserGroup(g);
		    	    	initGroupList();
		        		progressDialog.hide();
		    	    }
		    	 
		    	    @Override
		    	    public void onError(List<String> errors) {
						DialogUtils.showLong(context, errors.get(0));		    	 
		    	    }
		    	});
			}
		});
		builder.setNegativeButton("Cancel",null);
		builder.create().show();
	}
    
}
