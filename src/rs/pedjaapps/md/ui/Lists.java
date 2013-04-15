package rs.pedjaapps.md.ui;

import android.widget.*;

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
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import rs.pedjaapps.md.R;
import rs.pedjaapps.md.entries.ListEntry;
import rs.pedjaapps.md.helpers.DatabaseHandler;
import rs.pedjaapps.md.helpers.ListAdapter;
import rs.pedjaapps.md.tools.ReadImdbWatchlist;
import rs.pedjaapps.md.tools.UpdateAllMovies;
import android.view.Window;
import rs.pedjaapps.md.entries.ListsDatabaseEntry;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.ActionMode;
import rs.pedjaapps.md.helpers.IconsAdapter;

public class Lists extends Activity {

	
	boolean isLight;
	String theme;
	SharedPreferences sharedPrefs;
	RelativeLayout watch;
	RelativeLayout fav;
	DatabaseHandler db;
	List<ListEntry> entries;
	TextView tv1;
	LinearLayout ll;
	ListAdapter listAdapter;
	ActionMode aMode;
	int icon = R.drawable.ic_launcher;
	AlertDialog alert;
    AlertDialog.Builder builder;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		db = new DatabaseHandler(this);
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
		setContentView(R.layout.lists2);
		tv1 = (TextView) findViewById(R.id.tv1);
		ll = (LinearLayout) findViewById(R.id.ll1);
		new CheckUpdate().execute();
		getActionBar().setSubtitle("Movie Database");
		
		
		ImageView plus = (ImageView)findViewById(R.id.action_plus);
		plus.setImageResource(isLight ? R.drawable.add_light : R.drawable.add_dark);
		
		
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}
		
		GridView listView = (GridView) findViewById(R.id.lists);
		listAdapter = new ListAdapter(this, R.layout.lists_row);

		listView.setAdapter(listAdapter);
	
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					Intent i = new Intent(Lists.this, MoviesActivity.class);
					i.putExtra("listName", entries.get(p3).getTitle());
					startActivity(i);
				}
			});
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
						if (aMode != null)
		{
			aMode.finish();
		}
		aMode = startActionMode(new ItemActionMode(p3));
		
					return true;
				}
			});
			
	
		setUI();
}
		
	private void setUI()
	{
		if (listAdapter.isEmpty() == false)
		{
			tv1.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
		}
		else
		{
			tv1.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
		}
	}
	
	public void onResume(){
		new PopulateList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		super.onResume();
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
		menu.add(0,0,2,"Preferences")
			.setIcon(isLight ? R.drawable.settings_light : R.drawable.settings_dark)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0,2,0,"Add")
			.setIcon(isLight ? R.drawable.add_light : R.drawable.add_dark)
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
			case 2:
			    addListDialog();
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
					System.out.println(true);
					return true;
				}
				else{
					System.out.println(false);
					return false;
				}

				
			} catch (ClientProtocolException e) {
				System.out.println(false);
				return false;
			} catch (IOException e) {
				System.out.println(false);
				return false;
			} catch (JSONException e) {
				System.out.println(false);
				return false;
			} catch (Exception e) {
				System.out.println(false);
				return false;
			}             



		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			System.out.println(result);
			if(result == true){
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
	
	public class PopulateList extends AsyncTask<String, ListsDatabaseEntry, String>
	{

		List<ListsDatabaseEntry> dbEntry = db.getAllLists();
		@Override
		protected String doInBackground(String... args)
		{
			entries = new ArrayList<ListEntry>();
		
		for (ListsDatabaseEntry s: dbEntry)
		{

			entries.add(new ListEntry(s.get_name(), db.getMoviesCount(s.get_name()), s.get_icon()));
		    publishProgress(s);
		}
		return "";

		}

		@Override
		protected void onPreExecute(){
			listAdapter.clear();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected void onPostExecute(String result)
		{
			setProgressBarIndeterminateVisibility(false);
			setUI();
		}
		@Override
		protected void onProgressUpdate(ListsDatabaseEntry... s){
			listAdapter.add(new ListEntry(s[0].get_name(), db.getMoviesCount(s[0].get_name()), s[0].get_icon()/*R.drawable.favs*/));
			listAdapter.notifyDataSetChanged();
		}
	}	
	
	private void addListDialog(){
	AlertDialog.Builder builder = new AlertDialog.Builder(this);

	LayoutInflater inflater = (LayoutInflater)Lists.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View view = inflater.inflate(R.layout.edit_list_layout, null);
		final Button color = (Button)view.findViewById(R.id.icon);	
		final EditText input = (EditText)view.findViewById(R.id.name);
		input.setHint("List Name");
		//final int icon = R.drawable.ic_launcher;
		color.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				iconDialog();
			}

			
			
		});
		
		builder.setTitle("Create New List");
		builder.setIcon(isLight ? R.drawable.add_light : R.drawable.add_dark);


	builder.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{

				String inputText = input.getText().toString();

				if(inputText.length()==0){
					Toast.makeText(Lists.this, "List Name cannot be empty!", Toast.LENGTH_LONG).show();
				}
				else if(db.listExists(inputText)){
					Toast.makeText(Lists.this, "List Already Exist, Try different name!", Toast.LENGTH_LONG).show();
				}
				
				else{
					db.addList(new ListsDatabaseEntry(inputText, icon));
					listAdapter.add(new ListEntry(inputText,0, icon));
					listAdapter.notifyDataSetChanged();
					entries.add(new ListEntry(inputText,0, icon));
					
				}
				setUI();
			}
		});

	builder.setNegativeButton(getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{


			}
		});
	builder.setView(view);

	AlertDialog alert = builder.create();

	alert.show();
}
	
	private final class ItemActionMode implements ActionMode.Callback
	{
		int id;
		public ItemActionMode(int id)
		{
			this.id = id;
		}
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu)
		{

			//getSupportMenuInflater().inflate(R.menu.list_action, menu);
	    	if (theme.equals("light"))
			{
				isLight = true;
			}
			else if (theme.equals("dark"))
			{
				isLight = false;
			}
			else if (theme.equals("light_dark_action_bar"))
			{
				isLight = false;
			}
			menu.add(2, 2, 2, getResources().getString(R.string.delete))
				.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

	        return true;
	    }

	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
		{
	        return false;
	    }

	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
		{


	    	if (item.getItemId() == 2)
			{
	    		deleteItemDialog(entries.get(id).getTitle(), id);
	    	}
		
	    	mode.finish();
	        return true;
	    }

	    @Override
	    public void onDestroyActionMode(ActionMode mode)
		{
	    }
	}
	
	private void deleteItemDialog(final String name, final int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Remove List?");
		builder.setMessage("All movies from this list will be removed!\nAre you sure?");
		builder.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark);

		builder.setPositiveButton(getResources()
			.getString(android.R.string.yes),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{

					db.deleteList(name);

					listAdapter.remove(listAdapter.getItem(id));
					entries.remove(id);
					listAdapter.notifyDataSetChanged();
					

				}
			});

		builder.setNegativeButton(
			getResources().getString(android.R.string.no),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which)
				{

				}
			});

		AlertDialog alert = builder.create();

		alert.show();
	}
	
	private void iconDialog(){
	    builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater)Lists.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.icons_layout, null);
		
		final int[] icons = {
		R.raw.ic_launcher,
		R.raw.fav,
		R.raw.watchlist,
		R.raw.bt,
		R.raw.ut,
		R.raw.database,
		R.raw.gom,
		R.raw.mx,
		R.raw.imdb,
		R.raw.imdb1,
		R.raw.tv,
		R.raw.tv1,
		R.raw.tv2,
		R.raw.tv3,
		R.raw.tv4,
		R.raw.tv5,
		R.raw.tv6,
		R.raw.tv7,
		R.raw.camera,
		R.raw.vcamera,
		R.raw.gallery,
		R.raw.yt,
		R.raw.video1,
		R.raw.video2,
		R.raw.video3,
		};
		final GridView grid = (GridView) view.findViewById(R.id.grid);

	     IconsAdapter adapter = new IconsAdapter(this, R.layout.icon_item);

		grid.setAdapter(adapter);

		for(int i : icons){
			adapter.add(i);
        }
		
		grid.setOnItemClickListener(new AdapterView.OnItemClickListener(){

				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
				    icon = icons[p3];
					alert.dismiss();
				}
			});
		builder.setView(view);

		 alert = builder.create();

		alert.show();
	}
	
}
