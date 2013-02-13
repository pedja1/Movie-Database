package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.text.*;
import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.apache.http.util.*;
import org.json.*;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import rs.pedjaapps.md.*;
import rs.pedjaapps.md.entries.*;
import rs.pedjaapps.md.helpers.*;

public class MovieDetails extends Activity {

	
	boolean isLight;
	String theme;
	SharedPreferences sharedPrefs;
	DatabaseHandler db;
	MoviesDatabaseEntry e;
	String table;
	ProgressDialog pd;
	double ur;
	String title;
	
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
		setContentView(R.layout.movie_layout);
		Intent i = getIntent();
		title = i.getExtras().getString("title");
		table = i.getExtras().getString("table");
		db = new DatabaseHandler(this);
		
		
	    setUI();
	((RelativeLayout)findViewById(R.id.ur_layout)).setOnClickListener(new View.OnClickListener(){

				public void onClick(View p1)
				{
					rateDialog();
				}
				
			
		});
		ur = e.get_ur();
		/*RatingBar rating = (RatingBar)findViewById(R.id.rating);
		rating.setEnabled(false);
		rating.setRating((float) e.get_rating()/2);*/
		
		((TextView)findViewById(R.id.usr_text)).setText(e.get_ur()+"/10");
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		
}
	
	private void setUI(){
		e = db.getMovieByName(table, title);
		getActionBar().setTitle(title);
		String type = e.get_type();
		if(type.equals("M")){
			getActionBar().setSubtitle("Movie");
		}
		else if(type.equals("TVS")){
			getActionBar().setSubtitle("TV Series");
		}
		else if(type.equals("TV")){
			getActionBar().setSubtitle("TV Movie");
		}
		else if(type.equals("V")){
			getActionBar().setSubtitle("Video");
		}
		else if(type.equals("VG")){
			getActionBar().setSubtitle("Video Game");
		}
		((ImageView)findViewById(R.id.image)).setImageURI(Uri.parse(e.get_poster()));
		((TextView)findViewById(R.id.title)).setText(e.get_title()+"("+e.get_year()+")");
		((TextView)findViewById(R.id.runtime)).setText(e.get_runtime());
		((TextView)findViewById(R.id.plot)).setText(e.get_plot());
		((TextView)findViewById(R.id.lang)).setText(e.get_lang());
		((TextView)findViewById(R.id.director)).setText(e.get_director());
		((TextView)findViewById(R.id.genre)).setText(e.get_genres());
		((TextView)findViewById(R.id.cast)).setText(e.get_actors());
		((TextView)findViewById(R.id.country)).setText(e.get_country());
		
		((TextView)findViewById(R.id.imdb_text)).setText(e.get_rating()+"/10");
		try
		{
		String date = new SimpleDateFormat("dd.MM.yyyy").format(new SimpleDateFormat("yyyyMMdd").parse(e.get_date() + ""));
			((TextView)findViewById(R.id.date)).setText(date);
		}
		catch (java.text.ParseException e)
		{}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0,0,0,"More")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0,1,1,"Update Details")
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch(item.getItemId()){
			case 0:
				Uri uri = Uri.parse(e.get_url());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				
	            break;
			case 1:
			    new DownloadMovieInfo().execute(new String[]{/*e.get_url().substring(e.get_url().lastIndexOf("tt"), e.get_url().length()-1)*/e.get_imdb_id()});
			    break;
		}

		return super.onOptionsItemSelected(item);

	}
	
	private void rateDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(
			MovieDetails.this);

		builder.setTitle("Rate "+e.get_title());
	    //builder.setMessage("List: " + listName+"\nNumber of Movies: "+db.getMoviesCount(listName));
	    final EditText ed = new EditText(MovieDetails.this);
		ed.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(4);
		ed.setFilters(FilterArray);
		ed.setHint(""+e.get_ur());
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{

				}

			});
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (ed.getText().length()==0 || Double.parseDouble(ed.getText().toString())>10) {
                        Toast.makeText(MovieDetails.this, "Enter number between 0.0 and 10.0", Toast.LENGTH_LONG).show();
                    } 
                
				else{
					ur = Double.parseDouble(ed.getText().toString());
                    db.updateMovie(new MoviesDatabaseEntry(e.get_title(), e.get_runtime(), e.get_rating(), e.get_genres(), e.get_type(),
														   e.get_lang(), e.get_poster(), e.get_url(), e.get_director(), e.get_actors(), e.get_plot(), e.get_year(), e.get_country(), e.get_date(), ur, e.get_imdb_id()), e.get_title(), table);
					((TextView)findViewById(R.id.usr_text)).setText(ur+"/10");
					}
				}

			});
		builder.setView(ed);
		AlertDialog alert = builder.create();
		alert.show();

	
	}
	
	public class DownloadMovieInfo extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... args)
		{
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
					posterFile = Environment.getExternalStorageDirectory()+"/MDb/posters"+poster.substring(poster.lastIndexOf("/"));
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

				String id = "";
				if(jO.has("imdb_id")){
					id = jO.getString("imdb_id");
				}
				
			
				
					String res = DownloadFromUrl(poster, posterFile);
					db.updateMovie(new MoviesDatabaseEntry(title, runtime, rating, genres, type,
														lang, posterFile, url, directors, actors, plot, year, country, date, ur, id), e.get_title(), table);
					
				
				
					
				
					return res;
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
			pd = new ProgressDialog(MovieDetails.this);
			pd.setMessage("Updating Movie Info");
			pd.setIndeterminate(true);
			pd.setCancelable(false);
			pd.setCanceledOnTouchOutside(true);
			pd.show();
		}

		@Override
		protected void onPostExecute(String result)
		{
			pd.dismiss();
			setUI();
			if(result.length()!=0){
				Toast.makeText(MovieDetails.this, result, Toast.LENGTH_LONG).show();
			}
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
