package com.afc.biblereading.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.afc.biblereading.ScheduleActivity;
import com.afc.biblereading.R;
import com.afc.biblereading.group.Group;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.helper.util;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

import static com.afc.biblereading.user.definitions.Consts.PREFS_NAME;

public class SignInActivity extends BaseActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox stayLoginCheckBox;
    private TextView signup;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_sign_in);
        initUI();
    }

    private void initUI() {
        emailEditText = (EditText) findViewById(R.id.email_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        stayLoginCheckBox = (CheckBox) findViewById(R.id.stay_login);
        signup = (TextView)findViewById(R.id.Sign_up); 
        signup.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                progressDialog.show();

                // Sign in application with user
                //
                QBUser qbUser = new QBUser(null, passwordEditText.getText().toString(),emailEditText.getText().toString());
                final Boolean stayLogin = stayLoginCheckBox.isChecked();
                QBUsers.signIn(qbUser, new QBEntityCallbackImpl<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        progressDialog.hide();

                        setResult(RESULT_OK);
                        if (stayLogin){
                            SharedPreferences user = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = user.edit();
                            editor.putString("email", qbUser.getEmail());
                            editor.putString("passwd", passwordEditText.getText().toString());
                            editor.commit();
                        }
                        
                        DataHolder.getDataHolder().setSignInQbUser(qbUser);
                        checkUserGroup(qbUser);
                        DialogUtils.showLong(context, getResources().getString(R.string.user_successfully_sign_in));
                        startMainAcitvity();
                    }

					@Override
                    public void onError(List<String> errors) {
                        progressDialog.hide();
                        DialogUtils.showLong(context, errors.get(0));
                    }
                });
                progressDialog.hide();
                break;
            case R.id.Sign_up:
            	Intent intent = new Intent(this, SignUpUserActivity.class);
                startActivity(intent);
                break;
            	
        }
    }


    protected void startMainAcitvity() {
		Intent main = new Intent(this, ScheduleActivity.class);
		startActivity(main);
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
}