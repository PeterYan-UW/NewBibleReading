package com.afc.biblereading;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.util.Log;

public class CreateReadingPlan {

	private static int TOTAL_BOOKS;
	private static int TOTAL_CHAPTERS;
	private static int startsFrom;
	private static int endsAt;

	private final static BibleIndex Bibleindex = new BibleIndex();
	
	public static void CreatePlan(LocalDataManage DOP, String planName, 
			int beginBook, int endBook, DateTime startDate, DateTime endDate, 
			ScheduleActivity context) {
		startsFrom = beginBook;
		endsAt = endBook;
		TOTAL_BOOKS = endBook-beginBook + 1;
		TOTAL_CHAPTERS = 0;
		for (int i=beginBook; i<=endBook; i++){
			TOTAL_CHAPTERS += Bibleindex.BibleChapterNum[i];
		}
		int planId = 0;
		DOP.AddPlan(DOP, planId, planName, startDate, endDate);
        int totalReadingDays = 
        		Days.daysBetween(startDate, endDate).getDays()+1;

        Log.v("create plan",Integer.toString(beginBook));
        Log.v("create plan",Integer.toString(endBook));
        Log.v("create plan",Integer.toString(TOTAL_BOOKS));
        Log.v("create plan",Integer.toString(TOTAL_CHAPTERS));

        Log.v(Integer.toString(totalReadingDays),"timetime");
        Log.v(Integer.toString(TOTAL_CHAPTERS/totalReadingDays),"Chapter");
        Log.v(Integer.toString(TOTAL_CHAPTERS%totalReadingDays),"remainder");
        
		dailyTask(totalReadingDays, planId, DOP);
		
	}
	
	private static void dailyTask(int totalReadingDays, int planId, LocalDataManage DOP){
		
        int dailyChapters = TOTAL_CHAPTERS/totalReadingDays;
        int backUpdailyChapters = dailyChapters;
        int remainderChapters = TOTAL_CHAPTERS%totalReadingDays;
        
        int StartBook = startsFrom;
        int StartChapter = 1;
        int EndBook = 0;
        int EndChapter = 1;
        
        for(int CurrentDay = 1; CurrentDay <= totalReadingDays; CurrentDay++) {
        	
        	if (CurrentDay <= remainderChapters) {
        		dailyChapters = backUpdailyChapters;
        	}
        	else {
        		dailyChapters = backUpdailyChapters - 1; 
        	}
    		for (int CurrentBook=StartBook; CurrentBook <= endsAt; CurrentBook++) {
    			int CurrentBookChaptersNum = Bibleindex.BibleChapterNum[StartBook];
    			if (CurrentBookChaptersNum-StartChapter >= dailyChapters) {
    				EndBook = StartBook;
	        		EndChapter = StartChapter+dailyChapters;
    				DOP.AddTask(DOP, planId, CurrentDay, CurrentBook, StartChapter, EndChapter);
    				StartChapter = EndChapter+1;
    				break; 
    			}
    			else if (CurrentBookChaptersNum < StartChapter) {
    				StartBook = CurrentBook+1;
    				StartChapter = 1;
    			}
    			else {
    				EndBook = StartBook;
    				EndChapter = Bibleindex.BibleChapterNum[CurrentBook];
    				DOP.AddTask(DOP, planId, CurrentDay, CurrentBook, StartChapter, EndChapter);
    				dailyChapters = dailyChapters - (EndChapter-StartChapter) - 1;
    				StartBook++;
    				StartChapter = 1;    				
    			}
    		}
        }
	}
}
