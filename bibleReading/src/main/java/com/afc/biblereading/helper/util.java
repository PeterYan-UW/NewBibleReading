package com.afc.biblereading.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.afc.biblereading.Task;
import com.afc.biblereading.group.Group;
import com.quickblox.customobjects.model.QBCustomObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.util.Log;

public class util {
	public static String getDateTime(DateTime date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(date.toDate());
	}
	
	public static String printDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日", Locale.getDefault());
        return dateFormat.format(date);
	}
	
	public static Date formatDateTime(String timeToFormat) {
	    SimpleDateFormat iso8601Format = new SimpleDateFormat(
	            "yyyy-MM-dd HH:mm:ss");
	    Date date = null;
	    if (timeToFormat != null) {
            try {
				date = iso8601Format.parse(timeToFormat);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		return date;
	}
	
	public static HashMap<Date, Integer> initCalenderDates(DateTime enday, DateTime startday, 
			ArrayList<Integer> failedDays, int failColor, int passColor, int futureColor){
		HashMap<Date, Integer> eventDates = new HashMap<Date, Integer>();
		int beforeToday = Days.daysBetween(startday, new DateTime()).getDays();
		int afterToday = Days.daysBetween(new DateTime(), enday).getDays()+1;
		int days = 0;
		while(days < beforeToday){
			DateTime dt = startday.plusDays(days);
			Date d = dt.toDate();
			if (failedDays.contains(days+1)){
				eventDates.put(d, failColor);				
			}
			else if(days < afterToday+beforeToday){
				eventDates.put(d, passColor);				
			}
			days++;
		}
		days++;
		while(days <= afterToday+beforeToday){
			DateTime dt = startday.plusDays(days);
			Date d = dt.toDate();
			if (failedDays.contains(days+1)){
				eventDates.put(d, futureColor);				
			}		
			else {
				eventDates.put(d, passColor);				
			}
			days++;
		}
		return eventDates;
	}
	
	public static ArrayList<Group> QBGroups2Groups(ArrayList<QBCustomObject> QBGroups){
		ArrayList<Group> result = new ArrayList<Group>();
		for (QBCustomObject QBGroup : QBGroups){
			Group g = QBGroup2Group(QBGroup);
			result.add(g);
		}
		return result;
	}
	public static Group QBGroup2Group(QBCustomObject QBGroup){
		HashMap<String, Object> fields = QBGroup.getFields();
		String ID = (String) fields.get("id");
		String name = (String) fields.get("group_name");
        List<Integer> members = new ArrayList<>();
        for (String s : (ArrayList<String>)fields.get("members")){
            Log.v("group members", "group members "+s);
            members.add(Integer.parseInt(s));
        }
		Group group = new Group(ID, name, members);
		return group;
	}
	
	public static ArrayList<Task> DBTasks2Tasks(ArrayList<HashMap<String, Object>> tasks){
		ArrayList<Task> todayTaskList = new ArrayList<Task>();
		for (int i=0;i<tasks.size();i++) {
			int id = (Integer) tasks.get(i).get("id");
			int book = (Integer) tasks.get(i).get("book");
			int start_chapter = (Integer) tasks.get(i).get("start_chapter");
			int end_chapter = (Integer) tasks.get(i).get("end_chapter");
			Boolean done = (Integer) tasks.get(i).get("status") == 1;			
			Task task = new Task(id, book, start_chapter, end_chapter, done);
			todayTaskList.add(task);
		}
		return todayTaskList;
	}
	
	public static String GenCheckInMessage(int total, int miss, String finish){
		String checkInString = util.printDate(new Date()) 
    			+ " " + DataHolder.getDataHolder().getSignInUserFullName();
		if (total == 0){
    		checkInString += "今天休息";
    	}
    	else if (miss == 0){
    		checkInString += "完成今天任务:\n";
    		checkInString += finish;
    	}
    	else if (miss == total){
    		checkInString += "今天偷懒一天";
    	}
    	else{
    		checkInString += "只"+finish;
    	}
		return checkInString;
	}
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
