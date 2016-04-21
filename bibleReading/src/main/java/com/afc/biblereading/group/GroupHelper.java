package com.afc.biblereading.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.afc.biblereading.helper.DataHolder;
import com.afc.biblereading.helper.DialogUtils;
import com.afc.biblereading.helper.util;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;

public class GroupHelper {
	
    public static void getOtherGroup(final Context context, final ProgressDialog progressDialog) {
    	QBRequestGetBuilder groupRequestBuilder = new QBRequestGetBuilder();
    	groupRequestBuilder.nin("members", DataHolder.getDataHolder().getSignInUserId());
    	QBCustomObjects.getObjects("group", groupRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
			
			@Override
			public void onSuccess(ArrayList<QBCustomObject> groups, Bundle bundle) {
				List<Group> groupList = new ArrayList<Group>();
				for (int i=0; i<groups.size(); i++){
					Group g = util.QBGroup2Group(groups.get(i));
					groupList.add(g);
				}
    			DataHolder.getDataHolder().setGroupList(groupList);
    			DataHolder.getDataHolder().setQBGroupList(groups);
        		Intent intent = new Intent(context, GroupListActivity.class);
        		context.startActivity(intent);
        		progressDialog.hide();
			}
			
			@Override
			public void onError(List<String> errors) {
				DialogUtils.showLong(context, errors.get(0));				
			}
		});
    }
    
    public static void getUserGroups(final Context context, final ProgressBar progressBar) {
    	QBRequestGetBuilder groupRequestBuilder = new QBRequestGetBuilder();
    	groupRequestBuilder.in("members", DataHolder.getDataHolder().getSignInUserId());
    	QBCustomObjects.getObjects("group", groupRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
			
			@Override
			public void onSuccess(ArrayList<QBCustomObject> groups, Bundle bundle) {
				List<Group> groupList = new ArrayList<Group>();
				for (int i=0; i<groups.size(); i++){
					Group g = util.QBGroup2Group(groups.get(i));
					groupList.add(g);
				}
    			DataHolder.getDataHolder().setGroupList(groupList);
    			DataHolder.getDataHolder().setQBGroupList(groups);
        		Intent intent = new Intent(context, GroupListActivity.class);
        		context.startActivity(intent);
        		progressBar.setVisibility(View.GONE);
			}
			
			@Override
			public void onError(List<String> errors) {
				DialogUtils.showLong(context, errors.get(0));				
			}
		});
    }
}
