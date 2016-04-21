package com.afc.biblereading.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.afc.biblereading.ScheduleActivity;
import com.afc.biblereading.R;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class SignUpUserActivity extends BaseActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initUI();
    }

    private void initUI() {
        emailEditText = (EditText) findViewById(R.id.email_edittext);
        passwordEditText = (EditText) findViewById(R.id.password_edittext);
        firstNameEditText = (EditText) findViewById(R.id.first_name_edittext);
        lastNameEditText = (EditText) findViewById(R.id.last_name_edittext);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_button:
                progressDialog.show();

                // Sign Up user
                //
                QBUser qbUser = new QBUser();
                qbUser.setEmail(emailEditText.getText().toString());
                qbUser.setPassword(passwordEditText.getText().toString());
                String FullName = 
                		firstNameEditText.getText().toString()
                		+" "+lastNameEditText.getText().toString();
                qbUser.setFullName(FullName);
                QBUsers.signUpSignInTask(qbUser, new QBEntityCallbackImpl<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        progressDialog.hide();

                        DataHolder.getDataHolder().addQbUserToList(qbUser);
                        DataHolder.getDataHolder().setSignInQbUser(qbUser);
                        DataHolder.getDataHolder().setSignInUserPassword(passwordEditText.getText().toString());

                        startMainAcitvity();
                    }

                    @Override
                    public void onError(List<String> strings) {
                        progressDialog.hide();
                        DialogUtils.showLong(context, strings.get(0));
                    }
                });

                break;
        }
    }
    protected void startMainAcitvity() {
		Intent main = new Intent(this, ScheduleActivity.class);
		startActivity(main);
		finish();		
	}
}