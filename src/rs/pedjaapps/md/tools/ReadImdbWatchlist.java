package rs.pedjaapps.md.tools;

import android.database.*;
import android.database.sqlite.*;
import java.util.*;
import rs.pedjaapps.md.entries.*;

public class ReadImdbWatchlist
{

    public ReadImdbWatchlist(){
		
	}
    
	public List<String> read(){
		List<String> ids = new ArrayList<String>();
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/watchlist.db", null, SQLiteDatabase.OPEN_READONLY);
	    String selectQuery = "SELECT  * FROM watchlist";

        
        
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
		{
            do {
            	
                ids.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // return list
        db.close();
	    cursor.close();
		
		return ids;
	}
	
}
