package rs.pedjaapps.md.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;


public class GetImdbWatchlist extends AsyncTask<String, Void, String>
{

	/*final Context context;

	public GetImdbWatchlist(Context context)
	{
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);

	}


	final SharedPreferences preferences;*/
	
	@Override
	protected String doInBackground(String... args)
	{

		 try {
	            String line;
	            Process process = Runtime.getRuntime().exec("su");
	            OutputStream stdin = process.getOutputStream();
	            InputStream stderr = process.getErrorStream();
	            InputStream stdout = process.getInputStream();

	            stdin.write(("cp /data/data/com.imdb.mobile/databases/watchlist* /sdcard/\n").getBytes());
			    stdin.write(("rm /sdcard/watchlist*-journal\n").getBytes());
			 stdin.write(("mv /sdcard/watchlist* /sdcard/watchlist.db\n").getBytes());
				stdin.flush();

	            stdin.close();
	            BufferedReader brCleanUp =
	                    new BufferedReader(new InputStreamReader(stdout));
	            while ((line = brCleanUp.readLine()) != null) {
	                Log.d("[Output]", line);
	            }
	            brCleanUp.close();
	            brCleanUp =
	                    new BufferedReader(new InputStreamReader(stderr));
	            while ((line = brCleanUp.readLine()) != null) {
	            	Log.e("[Error]", line);
	            }
	            brCleanUp.close();
				

	        } catch (IOException ex) {
	        }
		return "";
	}
}	

