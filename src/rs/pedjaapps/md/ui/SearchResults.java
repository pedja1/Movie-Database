package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.preference.*;
import android.view.View;
import android.view.Window;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import rs.pedjaapps.md.entries.*;
import rs.pedjaapps.md.helpers.*;

import rs.pedjaapps.md.R;


public class SearchResults extends Activity {

	
	boolean isLight;
	String theme;
	SharedPreferences sharedPrefs;
	ListView searchListView;
	SearchAdapter searchAdapter;
	List<SearchListEntry> entry;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		theme = sharedPrefs.getString("theme", "light");
		if(theme.equals("light")){
			isLight = true;
			setTheme(android.R.style.Theme_Holo_Light);
			}
			else if(theme.equals("dark")){
				setTheme(android.R.style.Theme_Holo);
				isLight = false;
			}
			else if(theme.equals("light_dark_action_bar")){
				setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
				isLight = true;
			}
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.search_result);
		
		
		
		searchListView = (ListView) findViewById(R.id.list);
		
		
		searchAdapter = new SearchAdapter(this, R.layout.search_row);

		searchListView.setAdapter(searchAdapter);

		handleIntent(getIntent());
		
		searchListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				new DownloadMovieInfo().execute(new String[]{entry.get(position).getId()});
			}
			
		});
		


		
}
	

	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		    String query = intent.getStringExtra(SearchManager.QUERY).replaceAll(" ", "%20");
		    
		    new TitleSearchParser().execute(new String[] {query});
		    }
	}
	
	public class DownloadMovieInfo extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... args)
		{
			List<SearchListEntry> entry = new ArrayList<SearchListEntry>();
			//DatabaseHandler db = new DatabaseHandler(context);
			DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpGet httpget = new HttpGet("http://imdbapi.org/?id="+args[0]+"&type=json&plot=simple&episode=0&lang=en-US&aka=simple&release=simple&business=0&tech=0");
			// Depends on your web service
			//httppost.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			String result = "";
			
			try {
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				// json is UTF-8 by default i beleive
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null)
				{
				    sb.append(line + "\n");
				}
				result = sb.toString();
				
					JSONObject jO = new JSONObject(result);
					
					String title = jO.getString("title");
					String runtime = jO.getJSONArray("runtime").getString(0);
					Double rating = jO.getDouble("rating");
					StringBuilder genresB = new StringBuilder();
					JSONArray genresArray = jO.getJSONArray("genres");
					for(int i = 0; i<genresArray.length(); i++ ){
						genresB.append(genresArray.getString(i));
						genresB.append(", ");
					}
					String genres = genresB.toString();
					
					StringBuilder langB = new StringBuilder();
					JSONArray langArray = jO.getJSONArray("language");
					for(int i = 0; i<langArray.length(); i++ ){
						langB.append(langArray.getString(i));
						langB.append(", ");
					}
					String lang = langB.toString();
					String poster = jO.getString("poster");
					int year = jO.getInt("year");
					String url = jO.getString("imdb_url");
					StringBuilder actorsB = new StringBuilder();
					JSONArray actorsArray = jO.getJSONArray("actors");
					for(int i = 0; i<actorsArray.length(); i++ ){
						actorsB.append(actorsArray.getString(i));
						actorsB.append(", ");
					}
					String actors = actorsB.toString();
					String plot = jO.getString("plot_simple");
					int date = jO.getInt("release_date");
					
					StringBuilder countryB = new StringBuilder();
					JSONArray countryArray = jO.getJSONArray("country");
					for(int i = 0; i<countryArray.length(); i++ ){
						countryB.append(countryArray.getString(i));
						countryB.append(", ");
					}
					String country = countryB.toString();
					DownloadFromUrl(poster, poster.substring(poster.lastIndexOf("/"), poster.length()));
					DatabaseHandler db = new DatabaseHandler(SearchResults.this);
					db.addMovie(new MoviesDatabaseEntry(title, runtime, rating, genres, "",
							lang, "/sdcard/MDb/posters"+poster.substring(poster.lastIndexOf("/"), poster.length()), url, "", actors, plot, year, country, date), "watched");
				
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}           
			
			
			return "";
		}

		@Override
		protected void onPreExecute(){
			setProgressBarIndeterminateVisibility(true);
			 }
		
		@Override
		protected void onPostExecute(String result)
		{
			setProgressBarIndeterminateVisibility(false);
		}
	}	
	
	public class TitleSearchParser extends AsyncTask<String, Void, List<SearchListEntry>>
	{

		

		

		@Override
		protected List<SearchListEntry> doInBackground(String... args)
		{
			entry = new ArrayList<SearchListEntry>();
			//DatabaseHandler db = new DatabaseHandler(context);
			DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpGet httpget = new HttpGet("http://imdbapi.org/?title="+args[0]+"&type=json&plot=none&episode=0&limit=10&yg=0&mt=none&lang=en-US&business=0&tech=0");
			// Depends on your web service
			//httppost.setHeader("Content-type", "application/json");

			InputStream inputStream = null;
			String result = "";
			
			try {
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
				// json is UTF-8 by default i beleive
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null)
				{
				    sb.append(line + "\n");
				}
				result = sb.toString();
				JSONArray jA = new JSONArray(result);
				
				for(int i = 0; i<jA.length(); i++){
					JSONObject jO = jA.getJSONObject(i);
					String title = jO.getString("title");
					String id = jO.getString("imdb_id");
					int year = jO.getInt("year");
					entry.add(new SearchListEntry(title, id, year));
					System.out.println(title+id+year);
				}
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}           
			
			
			return entry;
		}

		@Override
		protected void onPreExecute(){
			setProgressBarIndeterminateVisibility(true);
			 }
		
		@Override
		protected void onPostExecute(List<SearchListEntry> result)
		{
			for (final SearchListEntry entry : result) {
				searchAdapter.add(entry);
				searchAdapter.notifyDataSetChanged();
			}
			 setProgressBarIndeterminateVisibility(false);
		}
	}	

	public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {
        	File ktDir = new File(Environment.getExternalStorageDirectory() + "/MDb/posters");
		      if(ktDir.exists()==false){
		      ktDir.mkdir();
		      }
                URL url = new URL(imageURL);
                File file = new File(fileName);

                
               /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();

                /*
                 * Define InputStreams to read from the URLConnection.
                 */
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                /*
                 * Read bytes to the Buffer until there is nothing more to read(-1).
                 */
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                }

                /* Convert the Bytes read to a String. */
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baf.toByteArray());
                fos.close();
                

        } catch (IOException e) {
                
        }

}
 
}
