package com.afc.biblereading.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afc.biblereading.R;
import com.afc.biblereading.adapter.UserLogListAdapter;
import com.afc.biblereading.group.ShowUserGroupActivity;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;

import java.util.ArrayList;
import java.util.List;

import static com.afc.biblereading.user.definitions.Consts.POSITION;
import static com.afc.biblereading.user.definitions.Consts.PREFS_NAME;

public class UserActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private LinearLayout userInfo;
    private TextView emailTextView;
    private TextView fullNameTextView;
    private ListView userLogListView;
    private Button logOutButton;
    private Button selfEditButton;
    private Button groupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initUI();
        if (DataHolder.getDataHolder().getSignInQbUser() != null) {
        	fillAllFields();
        }
    }

    private void initUI() {
    	userInfo = (LinearLayout) findViewById(R.id.user_info_linearview);
        emailTextView = (TextView) findViewById(R.id.email_textview);
        fullNameTextView = (TextView) findViewById(R.id.full_name_textview);
        userLogListView = (ListView) findViewById(R.id.user_log_listview);
        logOutButton = (Button) findViewById(R.id.logout_button);
        selfEditButton = (Button) findViewById(R.id.self_edit_button);
    }

    private void fillAllFields() {
        fillField(emailTextView, DataHolder.getDataHolder().getSignInUserEmail());
        fillField(fullNameTextView, DataHolder.getDataHolder().getSignInUserFullName());
        QBRequestGetBuilder logRequestBuilder = new QBRequestGetBuilder();
    	logRequestBuilder.eq("user_id", DataHolder.getDataHolder().getSignInUserId());
    	QBCustomObjects.getObjects("DailyRecords", logRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
			
			@Override
			public void onSuccess(ArrayList<QBCustomObject> logs, Bundle bundle) {
				ArrayList<String> logList = new ArrayList<String>();
				for (QBCustomObject log : logs){
					String g = (String) log.getFields().get("task");
					logList.add(g);
				}
				UserLogListAdapter logAdapter = new UserLogListAdapter(context, R.layout.list_item_user_log, logList);
		        userLogListView.setAdapter(logAdapter);
			}
			
			@Override
			public void onError(List<String> errors) {
				DialogUtils.showLong(context, errors.get(0));				
			}
		});        
    }

    @Override
    public void onResume() {
        super.onResume();    
		if (DataHolder.getDataHolder().getSignInQbUser()== null){
    		Intent user = new Intent(this, CreateSessionActivity.class);
    		startActivity(user);  			
		}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            fillAllFields();
        }
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.sign_in_button:
                intent = new Intent(this, SignInActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.sign_up_button:
                intent = new Intent(this, SignUpUserActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_button:
                progressDialog.show();
                // Logout
                QBUsers.signOut(new QBEntityCallbackImpl() {
                    @Override
                    public void onSuccess() {
                        progressDialog.hide();
                        DialogUtils.showLong(context, getResources().getString(R.string.user_log_out_msg));
                        updateDataAfterLogOut();
                        SharedPreferences user = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = user.edit();
                        editor.putString("email", null);
                        editor.putString("passwd", null);
                        editor.commit();
                        backToCreateSession();
                    }
                    @Override
                    public void onError(List list) {
                        progressDialog.hide();
                        DialogUtils.showLong(context, list.get(0).toString());
                    }
                });
                break;
            case R.id.self_edit_button:
                intent = new Intent(this, UpdateUserActivity.class);
                startActivity(intent);
                break;
        }
    }
    
    protected void backToCreateSession() {
    	Intent sessionStart = new Intent(this, CreateSessionActivity.class);
    	startActivity(sessionStart);
    	finish();		
	}

	private void updateDataAfterLogOut() {
        DataHolder.getDataHolder().setSignInQbUser(null);
        DataHolder.getDataHolder().setSignInUserGroup(null);
        DataHolder.getDataHolder().setSignInUserQbGroup(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startShowUserActivity(position);
    }

    private void startShowUserActivity(int position) {
        Intent intent = new Intent(this, ShowUserActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
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
}