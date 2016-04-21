package com.afc.biblereading;

import android.app.Application;

public class ApplicationSingleton extends Application {
	private LocalDataManage LDM;
	
    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    public LocalDataManage getDataBase(){
    	if (LDM == null){
    		setDataBase();
    	}
    	return LDM;
    }

	private void setDataBase() {
		LDM = new LocalDataManage(getApplicationContext());		
	}
}
