package com.afc.biblereading.group;

import java.util.ArrayList;

public class Member {
	private int id;
	private ArrayList<String> log;
	private Boolean todayStatus;
	
	public Member(int id, boolean status){
		super();
		this.id = id;
		this.log = new ArrayList<String>();
		this.todayStatus = status;
	}
	
	public void setId (int id){
		this.id = id;
	}
	public int getId(){
		return this.id;
	}
	
	public void setLog (ArrayList<String> log){
		this.log = new ArrayList<String>();
		this.log.addAll(log);
	}
	public ArrayList<String> getLog(){
		return this.log;
	}
	
	public void setStatus (Boolean status){
		this.todayStatus = status;
	}
	public Boolean getStatus(){
		return this.todayStatus;
	}
	
}
