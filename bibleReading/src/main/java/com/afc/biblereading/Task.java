package com.afc.biblereading;

import android.content.Context;

public class Task {
	int ID;
	String book = null;
	int beginChp;
	int endChp;
	boolean done = false;
	
	public Task(int ID, int bookNum, int beginChp, int endChp, Boolean done) {
		super();
		this.ID = ID;
		setBook(bookNum);
		this.beginChp = beginChp;
		this.endChp = endChp;
		this.done = done;
	}
	public String getBook() {
		return this.book;
	}
	public void setBook(int bookNum) {
		BibleIndex bookIndex = new BibleIndex();
		this.book = bookIndex.BibleBookName[bookNum];
	}
	
	public int getID(){
		return this.ID;
	}
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public int getBeginChp(){
		return this.beginChp;
	}
	public void setBeginChp(int beginChp) {
		this.beginChp = beginChp;
	}
	
	public int getEndChp(){
		return this.endChp;
	}
	public void setEndChp(int endChp){
		this.endChp = endChp;
	}
	
	public Boolean getDone(){
		return this.done;
	}
	public void setDone(Context context, Boolean done){
		this.done = done;
		LocalDataManage LDM = new LocalDataManage(context);
		LDM.SetTaskStatus(LDM, this);
	}
	
	public String asString(){
		String result = this.book + " 第" + this.beginChp;
		if (this.beginChp ==this.endChp) {
			result += "章";
		}
		else {
			result +=  "-" + this.endChp + "章";
		}
		return result;
	}
}
