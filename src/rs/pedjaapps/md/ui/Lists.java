package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.View.*;
import android.view.animation.*;
import android.view.animation.Animation.*;
import android.widget.*;
import com.google.ads.*;
import rs.pedjaapps.md.*;
import rs.pedjaapps.md.tools.*;

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
		
		watch = (RelativeLayout)findViewById(R.id.watch);
		fav = (RelativeLayout)findViewById(R.id.fav);
		Animation l2r = AnimationUtils.loadAnimation(this, R.anim.animation_l2r);
		Animation r2l = AnimationUtils.loadAnimation(this, R.anim.animation_r2l);
		watch.startAnimation(l2r);
		fav.startAnimation(r2l);
		
		watch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Lists.this, MoviesActivity.class);
				i.putExtra("listName", "watchlist");
				startActivity(i);
			}
			
		});
		fav.setOnClickListener(new OnClickListener(){

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
		r2l.setAnimationListener(new AnimationListener(){

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
	
}
