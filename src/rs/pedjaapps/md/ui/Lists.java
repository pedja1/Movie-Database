package rs.pedjaapps.md.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import rs.pedjaapps.md.R;
import rs.pedjaapps.md.tools.ReadImdbWatchlist;
import rs.pedjaapps.md.tools.UpdateAllMovies;

public class Lists extends Activity {

	
	boolean isLight;
	String theme;
	SharedPreferences sharedPrefs;
	RelativeLayout watch;
	RelativeLayout fav;
	
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
		setContentView(R.layout.lists);
		new CheckUpdate().execute();
		watch = (RelativeLayout)findViewById(R.id.watch);
		fav = (RelativeLayout)findViewById(R.id.fav);
		Animation l2r = AnimationUtils.loadAnimation(this, R.anim.animation_l2r);
		Animation r2l = AnimationUtils.loadAnimation(this, R.anim.animation_r2l);
		watch.startAnimation(l2r);
		fav.startAnimation(r2l);
		
		watch.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Lists.this, MoviesActivity.class);
				i.putExtra("listName", "watchlist");
				startActivity(i);
			}
			
		});
		fav.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Lists.this, MoviesActivity.class);
				i.putExtra("listName", "favorites");
				startActivity(i);
			}
			
		});
		
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}
		
		System.out.println(Environment.getExternalStorageDirectory());
}
	@Override
	public void onBackPressed() {
		Animation l2r = AnimationUtils.loadAnimation(this, R.anim.animation_l2r_end);
		Animation r2l = AnimationUtils.loadAnimation(this, R.anim.animation_r2l_end);
		
		watch.startAnimation(l2r);
		fav.startAnimation(r2l);
		r2l.setAnimationListener(new Animation.AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				watch.setVisibility(View.GONE);
				fav.setVisibility(View.GONE);
				finish();
				
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(theme.equals("light")){
			isLight = true;
		}
		else if(theme.equals("dark")){
			isLight = false;
		}
		else if(theme.equals("light_dark_action_bar")){
			isLight = false;
		}
		menu.add(0,0,0,"Preferences")
			.setIcon(isLight ? R.drawable.settings_light : R.drawable.settings_dark)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0,1,1,"Import Watchlist From IMDb");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch(item.getItemId()){
			case 0:
	            Intent intent = new Intent(this, Preferences.class);
	            startActivity(intent);
	            break;
			case 1:
				ReadImdbWatchlist read = new ReadImdbWatchlist(this);
				read.addMovies();
				break;
		}

		return super.onOptionsItemSelected(item);

	}
	
	public class CheckUpdate extends AsyncTask<String, Void, Boolean>
	{
        String db;
		@Override
		protected Boolean doInBackground(String... args)
		{
			DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpGet httpget = new HttpGet("http://imdbapi.org/api/version?type=json");
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
				db = "";
				if(jO.has("database")){
					db = jO.getString("database");
					return true;
				}
				else{
					return false;
				}

				
			} catch (ClientProtocolException e) {
				return false;
			} catch (IOException e) {
				return false;
			} catch (JSONException e) {
				return false;
			}           



		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			
			if(result = true){
				String dbVersion =sharedPrefs.getString("db_version","");
				if(!dbVersion.equals(db)){
					updateDialog(dbVersion, db);
				}
			}
		}
	}	
	
	private void updateDialog(String currentVersion, final String newVersion){

		AlertDialog.Builder builder = new AlertDialog.Builder(
			Lists.this);

		builder.setTitle("Update Lists?");
	    builder.setMessage("Database version updated.\nCurrent Version: "+currentVersion+"\nNew Version: "+newVersion+
		"\n\nUpdate all movies now?");
	    
    	builder.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
                  new UpdateAllMovies(Lists.this).updateMovies(new String[]{"favorites","watchlist"});
				  SharedPreferences.Editor editor = sharedPrefs.edit();
				  editor.putString("db_version",newVersion);
				  editor.apply();
				}

			});
		builder.setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
				
				}

			});
		
		AlertDialog alert = builder.create();
		alert.show();


	}
	
}
