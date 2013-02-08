package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.google.ads.*;
import java.text.*;
import java.util.*;
import rs.pedjaapps.md.*;
import rs.pedjaapps.md.entries.*;
import rs.pedjaapps.md.helpers.*;

import android.view.View.OnClickListener;
import rs.pedjaapps.md.R;

public class Lists extends Activity {

	
	boolean isLight;
	String theme;
	SharedPreferences sharedPrefs;
	
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
		

		
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		
}
}
