package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.view.animation.Animation.*;
import android.widget.*;
import com.google.ads.*;
import rs.pedjaapps.md.*;
import rs.pedjaapps.md.entries.MoviesDatabaseEntry;
import rs.pedjaapps.md.helpers.DatabaseHandler;

public class MovieDetails extends Activity {

	
	boolean isLight;
	String theme;
	SharedPreferences sharedPrefs;
	DatabaseHandler db;
	MoviesDatabaseEntry e;
	String table;
	
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
		String title = i.getExtras().getString("title");
		table = i.getExtras().getString("table");
		db = new DatabaseHandler(this);
		e = db.getMovieByName(table, title);
		((ImageView)findViewById(R.id.image)).setImageURI(Uri.parse(e.get_poster()));
		((TextView)findViewById(R.id.title)).setText(e.get_title()+"("+e.get_year()+")");
		((TextView)findViewById(R.id.runtime)).setText(e.get_runtime());
		((TextView)findViewById(R.id.plot)).setText(e.get_plot());
		((TextView)findViewById(R.id.lang)).setText(e.get_lang());
		((TextView)findViewById(R.id.director)).setText(e.get_director());
		((TextView)findViewById(R.id.genre)).setText(e.get_genres());
		((TextView)findViewById(R.id.cast)).setText(e.get_actors());
		((TextView)findViewById(R.id.country)).setText(e.get_country());
		((TextView)findViewById(R.id.date)).setText(e.get_date()+"");
		
		RatingBar rating = (RatingBar)findViewById(R.id.rating);
		rating.setEnabled(false);
		rating.setRating((float) e.get_rating()/2);
		((TextView)findViewById(R.id.rating_text)).setText(e.get_rating()+"/10");
		/*boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}*/

		
}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add(0,0,0,"More")
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
		}

		return super.onOptionsItemSelected(item);

	}
	
}
