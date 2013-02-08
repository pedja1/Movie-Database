package rs.pedjaapps.md.ui;




import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import android.view.*;
import rs.pedjaapps.md.*;



public class Preferences extends PreferenceActivity
{

	private ListPreference themePrefList;

	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{        
	

	SharedPreferences sharedPrefs = PreferenceManager
			.getDefaultSharedPreferences(this);
	String them = sharedPrefs.getString("theme", "light");
	
		if(them.equals("light")){
			setTheme(android.R.style.Theme_Holo_Light);
		}
		else if(them.equals("dark")){
			setTheme(android.R.style.Theme_Holo);
			
		}
		else if(them.equals("light_dark_action_bar")){
			setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);
			
		}
			super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences); 
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		themePrefList = (ListPreference) findPreference("theme");
        themePrefList.setDefaultValue(themePrefList.getEntryValues()[0]);
        String theme = themePrefList.getValue();
        if (theme == null) {
            themePrefList.setValue((String)themePrefList.getEntryValues()[0]);
            theme = themePrefList.getValue();
        }
        themePrefList.setSummary(themePrefList.getEntries()[themePrefList.findIndexOfValue(theme)]);


        themePrefList.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                themePrefList.setSummary(themePrefList.getEntries()[themePrefList.findIndexOfValue(newValue.toString())]);
                return true;
            }
        }); 
        
       
        
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, Lists.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            return true;
	        
	            
	    }
	    return super.onOptionsItemSelected(item);
	}
	
}
