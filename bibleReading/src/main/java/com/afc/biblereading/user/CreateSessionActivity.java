package com.afc.biblereading.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afc.biblereading.ScheduleActivity;
import com.afc.biblereading.R;
import com.afc.biblereading.group.Group;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.helper.util;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;

import java.util.ArrayList;
import java.util.List;

import static com.afc.biblereading.user.definitions.Consts.APP_ID;
import static com.afc.biblereading.user.definitions.Consts.AUTH_KEY;
import static com.afc.biblereading.user.definitions.Consts.AUTH_SECRET;
import static com.afc.biblereading.user.definitions.Consts.PREFS_NAME;

public class CreateSessionActivity extends Activity{
    private Context context;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_start);
        context = this;
        initUI();
        progressBar.setVisibility(View.VISIBLE);
        if (util.isNetworkAvailable(context)){
	        // Initialize QuickBlox application with credentials.
	        //
	        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
	
	        // Create QuickBlox session
	        //
	        SharedPreferences user = getSharedPreferences(PREFS_NAME, 0);
	        String email = user.getString("email", null);
	        String passwd = user.getString("passwd", null);
	        if (email!=null && passwd !=null){
	        	QBUser keeped = new QBUser(null, passwd, email);
	            QBAuth.createSession(keeped, new QBEntityCallbackImpl<QBSession>() {
	                @Override
	                public void onSuccess(QBSession qbSession, Bundle bundle) {
	                	int id = qbSession.getUserId();
	                	QBUsers.getUser(id, new QBEntityCallbackImpl<QBUser>() {
		                    @Override
		                    public void onSuccess(QBUser qbUser, Bundle bundle) {
		
		                        setResult(RESULT_OK);
		
		                        DataHolder.getDataHolder().setSignInQbUser(qbUser);
		                        checkUserGroup(qbUser);
		                        startMain();
		                    }
		
							@Override
		                    public void onError(List<String> errors) {
		                        DialogUtils.showLong(context, errors.get(0));
		                        SharedPreferences user = getSharedPreferences(PREFS_NAME, 0);
		                        SharedPreferences.Editor editor = user.edit();
		                        editor.putString("email", null);
		                        editor.putString("passwd", null);
		                        editor.commit();
		                        onCreate(savedInstanceState);
		                    }
	                	});
	                }
	
	                @Override
	                public void onError(List<String> errors) {
	                    // print errors that came from server
	                    DialogUtils.showLong(context, errors.get(0));
	                    SharedPreferences user = getSharedPreferences(PREFS_NAME, 0);
	                    SharedPreferences.Editor editor = user.edit();
	                    editor.putString("email", null);
	                    editor.putString("passwd", null);
	                    editor.commit();
	                    onCreate(savedInstanceState);
	                }
	            });
	        	
	        }
	        else {
	            QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
	                @Override
	                public void onSuccess(QBSession qbSession, Bundle bundle) {
	                	startSignIn();
	                }
	
	                @Override
	                public void onError(List<String> errors) {
	                    // print errors that came from server
	                    DialogUtils.showLong(context, errors.get(0));
	                    progressBar.setVisibility(View.INVISIBLE);
	                }
	            });
	        }
        }
        else{
        	Log.v("no network", "pass");
        	startMain();
        }
    }

    private void initUI() {
    	progressBar = (ProgressBar) findViewById(R.id.login_progress);
    }
    
    private void startSignIn(){
    	Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    	finish();    	
    }
    
    private void startMain(){
    	Intent intent = new Intent(this, ScheduleActivity.class);
    	startActivity(intent);
    	finish();    	
    }
    
    
    private void checkUserGroup(QBUser qbUser) {
    	QBRequestGetBuilder userGroupRequestBuilder = new QBRequestGetBuilder();
    	userGroupRequestBuilder.in("members", qbUser.getId());
    	QBCustomObjects.getObjects("group", userGroupRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>(){
    		@Override
			public void onSuccess(ArrayList<QBCustomObject> groups, Bundle bundle) {
    			// Logically each user can only join one group
    			// So the condition should be (groups.size() == 1)
    			if (groups.size() >= 1){
    				ArrayList<Group> userGroup = util.QBGroups2Groups(groups);
    				DataHolder.getDataHolder().setSignInUserGroup(userGroup);
    				DataHolder.getDataHolder().setSignInUserQbGroup(groups);
    			}
			}
			
			@Override
			public void onError(List<String> errors) {
				DialogUtils.showLong(context, errors.get(0));				
			}
    	});
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