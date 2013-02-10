package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.apache.http.util.*;
import org.json.*;
import rs.pedjaapps.md.*;
import rs.pedjaapps.md.entries.*;
import rs.pedjaapps.md.helpers.*;
import rs.pedjaapps.md.providers.*;


public class SearchResults extends Activity {

	
	boolean isLight;
	String theme;
	SharedPreferences sharedPrefs;
	ListView searchListView;
	SearchAdapter searchAdapter;
	List<SearchListEntry> entry;
	String listName;
	
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
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		    String query = intent.getStringExtra(SearchManager.QUERY);
			listName = sharedPrefs.getString("list", "favorites");
		 /*   Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
			if (appData != null) {
			    listName = appData.getString("listName");
			}
			else{
				listName = getIntent().getExtras().getString("listName");
			}*/
		
			suggestions.saveRecentQuery(query, null);
		    new TitleSearchParser().execute(new String[] {query.replaceAll(" ", "%20")});
		    }
	/*	else{
			listName = getIntent().getExtras().getString("listName");
			String query = getIntent().getExtras().getString("query");
			suggestions.saveRecentQuery(query, null);
			new TitleSearchParser().execute(new String[] {query.replaceAll(" ", "%20")});
		}*/
		
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
					String title = "";
					if(jO.has("title")){
					title = jO.getString("title");
					}
					String runtime = "";
					if(jO.has("runtime")){
					runtime = jO.getJSONArray("runtime").getString(0);
					}
					
				String type = "";
				if(jO.has("type")){
					type = jO.getString("type");
				}
					
					Double rating = 0.0;
					if(jO.has("rating")){
					rating = jO.getDouble("rating");
					}
					String genres = "";
					if(jO.has("genres")){
					StringBuilder genresB = new StringBuilder();
					JSONArray genresArray = jO.getJSONArray("genres");
					for(int i = 0; i<genresArray.length(); i++ ){
						genresB.append(genresArray.getString(i));
						genresB.append(", ");
					}
					genres = genresB.toString();
					}
					String lang = "";
				    if(jO.has("language")){
					StringBuilder langB = new StringBuilder();
					JSONArray langArray = jO.getJSONArray("language");
					for(int i = 0; i<langArray.length(); i++ ){
						langB.append(langArray.getString(i));
						langB.append(", ");
					}
					lang = langB.toString();
					}
					String poster = "";
					String posterFile = "";
				    if(jO.has("poster")){
					poster = jO.getString("poster");
						posterFile = "/sdcard/MDb/posters"+poster.substring(poster.lastIndexOf("/"));
					}
					int year = 0;
				    if(jO.has("year")){
				    year = jO.getInt("year");
					}
					String url = "";
				    if(jO.has("imdb_url")){
					url = jO.getString("imdb_url");
					}
					String actors = "";
				    if(jO.has("actors")){
					StringBuilder actorsB = new StringBuilder();
					JSONArray actorsArray = jO.getJSONArray("actors");
					for(int i = 0; i<actorsArray.length(); i++ ){
						actorsB.append(actorsArray.getString(i));
						actorsB.append(", ");
					}
					actors = actorsB.toString();
					}
					String plot = "";
			    	if(jO.has("plot_simple")){
					plot = jO.getString("plot_simple");
					}
					int date = 0;
				    if(jO.has("release_date")){
					date = jO.getInt("release_date");
					}
					String country = "";
				    if(jO.has("country")){
					StringBuilder countryB = new StringBuilder();
					JSONArray countryArray = jO.getJSONArray("country");
					for(int i = 0; i<countryArray.length(); i++ ){
						countryB.append(countryArray.getString(i));
						countryB.append(", ");
					}
					country = countryB.toString();
					}
					
				String directors = "";
				if(jO.has("directors")){
					StringBuilder dirB = new StringBuilder();
					JSONArray directorsArray = jO.getJSONArray("directors");
					for(int i = 0; i<directorsArray.length(); i++ ){
						dirB.append(directorsArray.getString(i));
						dirB.append(", ");
					}
					directors = dirB.toString();
				}
					
				
					DatabaseHandler db = new DatabaseHandler(SearchResults.this);
					Log.e("error",listName);
				boolean exists = db.movieExists(listName, title);
					if(exists==false){
					String res = DownloadFromUrl(poster, posterFile);
					db.addMovie(new MoviesDatabaseEntry(title, runtime, rating, genres, type,
							lang, posterFile, url, directors, actors, plot, year, country, date), listName);
						return res;
				    }
					else{
						return "Movie Already Exists In This List";
					}
			//	return res;
			} catch (ClientProtocolException e) {
				return e.getMessage();
			} catch (IOException e) {
				return e.getMessage();
			} catch (JSONException e) {
				return e.getMessage();
			}           
			
			
			
		}

		@Override
		protected void onPreExecute(){
			setProgressBarIndeterminateVisibility(true);
			 }
		
		@Override
		protected void onPostExecute(String result)
		{
			setProgressBarIndeterminateVisibility(false);
			finish();
			if(result.length()!=0){
			Toast.makeText(SearchResults.this, result, Toast.LENGTH_LONG).show();
			}
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

	public String DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
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
			return "";

        } catch (IOException e) {
                Log.e("error saving image", e.getMessage());
        	return e.getMessage();
		}

}
 
}
