package com.afc.biblereading;



import com.afc.biblereading.group.ShowUserGroupActivity;
import com.afc.biblereading.group.UserGroupListActivity;
import com.afc.biblereading.helper.util;
import com.afc.biblereading.user.UserActivity;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class Tabs extends TabActivity{
	private TabHost tabhost;
	private RadioGroup main_radiogroup;  
	 private RadioButton tab_icon_weixin, tab_icon_address, tab_icon_friend,tab_icon_setting;  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
       
        main_radiogroup = (RadioGroup) findViewById(R.id.main_radiogroup);
        tab_icon_weixin = (RadioButton) findViewById(R.id.tab_icon_task);
        tab_icon_address = (RadioButton) findViewById(R.id.tab_icon_info);
        tab_icon_friend = (RadioButton) findViewById(R.id.tab_icon_group);
        tab_icon_setting = (RadioButton) findViewById(R.id.tab_icon_user);
        
        tabhost = getTabHost();
        tabhost.addTab(tabhost.newTabSpec("tag1").setIndicator("0").setContent(new Intent(this,CalenderActivity.class)));
        tabhost.addTab(tabhost.newTabSpec("tag2").setIndicator("1").setContent(new Intent(this,InfoActivity.class)));
        if (util.isNetworkAvailable(this)){
	        tabhost.addTab(tabhost.newTabSpec("tag3").setIndicator("2").setContent(new Intent(this,UserGroupListActivity.class)));
	        tabhost.addTab(tabhost.newTabSpec("tag4").setIndicator("3").setContent(new Intent(this,UserActivity.class)));
        }
        else{
        	tab_icon_friend.setEnabled(false);
        	tab_icon_setting.setEnabled(false);
        }
         
        checkListener checkradio = new checkListener();
        main_radiogroup.setOnCheckedChangeListener(checkradio);
    }

    
    public class checkListener implements OnCheckedChangeListener{
    	@Override
    	public void onCheckedChanged(RadioGroup group, int checkedId) {
    		// TODO Auto-generated method stub
    		switch(checkedId){
    		case R.id.tab_icon_task:
    			tabhost.setCurrentTab(0);
    			break;
    		case R.id.tab_icon_info:
    			tabhost.setCurrentTab(1);
    			break;
    		case R.id.tab_icon_group:
    			tabhost.setCurrentTab(2);
    			break;
    		case R.id.tab_icon_user:
    			tabhost.setCurrentTab(3);
    			break;
    		}    		
    	}
    }
}
