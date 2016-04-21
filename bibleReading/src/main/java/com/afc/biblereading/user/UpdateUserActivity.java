package com.afc.biblereading.user;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afc.biblereading.R;
import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.List;

public class UpdateUserActivity extends BaseActivity {

    private TextView emailEditText;
    private EditText fullNameEditText;
    private EditText phoneEditText;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_update_user);

        initUI();
        fillAllFields();
    }

    private void initUI() {
        emailEditText = (TextView) findViewById(R.id.email_edittext);
        fullNameEditText = (EditText) findViewById(R.id.full_name_edittext);
        phoneEditText = (EditText) findViewById(R.id.phone_edittext);
    }

    private void fillAllFields() {
        fillField(emailEditText, DataHolder.getDataHolder().getSignInUserEmail());
        fillField(fullNameEditText, DataHolder.getDataHolder().getSignInUserFullName());
        fillField(phoneEditText, DataHolder.getDataHolder().getSignInUserPhone());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_button:
//                progressDialog.show();

                // Update user
                //
                // create QBUser object
                QBUser qbUser = new QBUser();
                if (DataHolder.getDataHolder().getSignInUserId() != -1) {
                    qbUser.setId(DataHolder.getDataHolder().getSignInUserId());
                }
                qbUser.setFullName(fullNameEditText.getText().toString());
                qbUser.setEmail(emailEditText.getText().toString());
                qbUser.setPhone(phoneEditText.getText().toString());
                StringifyArrayList<String> tagList = new StringifyArrayList<String>();
                qbUser.setTags(tagList);

                QBUsers.updateUser(qbUser, new QBEntityCallbackImpl<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {

                        DataHolder.getDataHolder().setSignInQbUser(qbUser);
                        DialogUtils.showLong(context, getResources().getString(
                                R.string.user_successfully_updated));
                        finish();
                    }

                    @Override
                    public void onError(List<String> strings) {

                    }
                });

                break;
        }
    }
}