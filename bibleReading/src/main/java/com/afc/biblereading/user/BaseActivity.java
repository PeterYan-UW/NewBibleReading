package com.afc.biblereading.user;

import com.afc.biblereading.R;
import com.afc.biblereading.helper.DialogUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;


public class BaseActivity extends Activity {

    protected Context context;
    protected ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        context = this;
        progressDialog = DialogUtils.getProgressDialog(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();

        if(progressDialog!= null)
        	progressDialog.dismiss();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void fillField(TextView textView, String value) {
        if (!TextUtils.isEmpty(value)) {
            textView.setText(value);
        }
    }
}