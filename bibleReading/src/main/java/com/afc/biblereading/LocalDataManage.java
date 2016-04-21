package com.afc.biblereading;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.afc.biblereading.helper.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDataManage extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "BibleReading.db";
    private static final String PLAN_TABLE = "ReadingPlan";
    private static final String DAILY_TASK_TABLE = "DailyTask";
//    private static final String  = "ReadingPlan";
    
    private static final String PLAN_TABLE_CREATE = "CREATE TABLE " + PLAN_TABLE + 
    		" (plan_id INTEGER, plan_name TEXT, start_date INTEGER, end_date INTEGER);";
    
    private static final String DAILY_TASK_TABLE_CREATE = " CREATE TABLE " + DAILY_TASK_TABLE + 
    		" (day INGEGER, plan_id INTEGER," +
    		" book INTEGER, start_chapter INTEGER, end_chapter INTEGER, status INTEGER, " +
    		" FOREIGN KEY(plan_id) REFERENCES " + PLAN_TABLE + "(rowid));";

    LocalDataManage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d("Database operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PLAN_TABLE_CREATE);
		Log.d("Database operations", "Plan Table created");
        db.execSQL(DAILY_TASK_TABLE_CREATE);
		Log.d("Database operations", "Task Table created");
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public ArrayList<HashMap<String, Object>> getPlanInfo(LocalDataManage ldm){
		ArrayList<HashMap<String, Object>> planList; 
		planList = new ArrayList<HashMap<String, Object>>(); 
		String selectQuery = "SELECT plan_id, plan_name, start_date, end_date FROM "+PLAN_TABLE; 
		SQLiteDatabase SQ = ldm.getReadableDatabase();
		Cursor cursor = SQ.rawQuery(selectQuery, null); 
		if (cursor.moveToFirst()) { 
			do { 
				HashMap<String, Object> map = new HashMap<String, Object>(); 
				map.put("plan_id", cursor.getInt(0));
				map.put("plan_name", cursor.getString(1));
				map.put("start_day", cursor.getString(2));  
				map.put("end_day", cursor.getString(3));  
				planList.add(map); 
			} while (cursor.moveToNext()); 
		} // return contact list return wordList;
		return planList;
	}
	
	public ArrayList<Integer> getUnfinishedDay(LocalDataManage ldm, int planId, Date startDay){
		ArrayList<Integer> dayList; 
		dayList = new ArrayList<Integer>();
		String selectQuery = "SELECT day FROM " + DAILY_TASK_TABLE
				+ " WHERE plan_id = " + planId + " AND status = 0;";
		SQLiteDatabase SQ = ldm.getReadableDatabase();
		Cursor cursor = SQ.rawQuery(selectQuery, null); 
		if (cursor.moveToFirst()) { 
			do {
				dayList.add(cursor.getInt(0)); 
			} while (cursor.moveToNext()); 
		} // return contact list return wordList;
		Log.v("days: ", dayList.toString());
		return dayList;
	}
	
	public ArrayList<HashMap<String, Object>> getTodayTask(LocalDataManage ldm, int planId, Date startDay){
		return getDailyTask(ldm, planId, new Date(), startDay);
	}
	
	public ArrayList<HashMap<String, Object>> getDailyTask(LocalDataManage ldm, int planId, Date targetDay, Date startDay){
		ArrayList<HashMap<String, Object>> taskList; 
		taskList = new ArrayList<HashMap<String, Object>>();
		int day = Days.daysBetween(new DateTime(startDay),new DateTime(targetDay)).getDays()+1;
		String selectQuery = "SELECT rowid, book, start_chapter, end_chapter, status FROM "
								+ DAILY_TASK_TABLE
								+ " WHERE plan_id = " + planId + " AND day = " + day + " ORDER BY book;";

		Log.v("db today task query", selectQuery);
		SQLiteDatabase SQ = ldm.getReadableDatabase();
		Cursor cursor = SQ.rawQuery(selectQuery, null); 
		if (cursor.moveToFirst()) { 
			do { 
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", cursor.getInt(0));
				map.put("book", cursor.getInt(1));  
				map.put("start_chapter", cursor.getInt(2));  
				map.put("end_chapter", cursor.getInt(3));  
				map.put("status", cursor.getInt(4));  
				taskList.add(map); 
			} while (cursor.moveToNext()); 
		} // return contact list return wordList;
		Log.v("tasks: ", taskList.toString());
		return taskList;
	}

	public void AddPlan(LocalDataManage ldm, int planId, String planName, DateTime startDate, DateTime endDate) {
		SQLiteDatabase SQ = ldm.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("plan_id", planId);
		cv.put("plan_name", planName);
		cv.put("start_date", util.getDateTime(startDate));
		cv.put("end_date", util.getDateTime(endDate));
		long k = SQ.insert(PLAN_TABLE, null, cv);
		Log.d("Database operations", "One plan inserted");			
	}

	public void AddTask(LocalDataManage ldm, int planId, int day, int book,
			int startChapter, int endChapter) {
		SQLiteDatabase SQ = ldm.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("plan_id", planId);
		cv.put("day", day);
		cv.put("book", book);
		cv.put("start_chapter", startChapter);
		cv.put("end_chapter", endChapter);
		cv.put("status", 0);
		long k = SQ.insert(DAILY_TASK_TABLE, null, cv);
		Log.d("Database operations", "One day task inserted");				
	}
	
	public void SetTaskStatus(LocalDataManage ldm, Task task){
		int newStatus = 0;
		if (task.getDone()){
			newStatus = 1;
		}
		SQLiteDatabase SQ  = ldm.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("status", newStatus);
		SQ.update(DAILY_TASK_TABLE, values, "rowid="+String.valueOf(task.getID()), null);
		
	}
	
	public void DeletePlan(LocalDataManage ldm){
		SQLiteDatabase SQ = ldm.getWritableDatabase();
		SQ.delete(DAILY_TASK_TABLE, null, null);
		SQ.delete(PLAN_TABLE, null, null);
	}
}
