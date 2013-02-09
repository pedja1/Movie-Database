package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.AdapterView.*;

import com.google.ads.*;
import java.util.*;

import rs.pedjaapps.md.entries.*;
import rs.pedjaapps.md.helpers.*;

import rs.pedjaapps.md.R;

public class MoviesActivity extends Activity {

	private DatabaseHandler db;
//	FanView fan;
	ActionBar actionBar;

	private MoviesAdapter moviesAdapter;
	private ListView moviesListView;
	private static final int ADD_ITEM = 0;
	String listName;

	TextView tv1;
	LinearLayout ll;
	boolean isLight;
	String theme;
	int pos;
	
	ActionMode aMode;
	TextView totalText;
	
	RelativeLayout container;
	SearchView searchView;
	List<MoviesEntry> entries;
	int sortMode = 0;
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		theme = prefs.getString("theme", "light");
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
		setContentView(R.layout.activity_list);
		
		sortMode = prefs.getInt("sortOrder", 0);
		Intent intent = getIntent();
		listName = intent.getExtras().getString("listName");
		
		db = new DatabaseHandler(this);
		//fan = (FanView) findViewById(R.id.fan_view);
		//fan.setViews(R.layout.activity_list, R.layout.side_list);
		
	
		
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(listName);
		
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		 searchView = new SearchView(actionBar.getThemedContext());
	        searchView.setQueryHint("Add new Movie");
	        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	        searchView.setIconifiedByDefault(false);
			searchView.setQueryRefinementEnabled(true);
	        searchView.setOnQueryTextListener(new OnQueryTextListener() {

	    	    @Override
	    	    public boolean onQueryTextSubmit(String query) {
	    	    	Intent intent = new Intent(MoviesActivity.this, SearchResults.class);
	    			intent.putExtra("query", query);
	    	    	intent.putExtra("listName", listName);
	    			startActivity(intent);
	    	        return true;
	    	    }

	    	    @Override
	    	    public boolean onQueryTextChange(String newText) {
	    	        
	    	        return false;
	    	    }
	    	});
		ImageView plus = (ImageView)findViewById(R.id.action_plus);
		plus.setImageResource(isLight ? R.drawable.search_lite : R.drawable.search_dark);
		
		container = (RelativeLayout)findViewById(R.id.item_container);
		container.setBackgroundResource(isLight ? R.drawable.background_holo_light : R.drawable.background_holo_dark);
		
		
		
		
		boolean ads = prefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		tv1 = (TextView) findViewById(R.id.tv1);
		ll = (LinearLayout) findViewById(R.id.ll1);

	
		moviesListView = (ListView) findViewById(R.id.movie_list);
		
		
		moviesAdapter = new MoviesAdapter(this, R.layout.movie_row);

		moviesListView.setAdapter(moviesAdapter);

		for (final MoviesEntry entry : getMoviesEntries()) {
			moviesAdapter.add(entry);

		}
		setUI();
		moviesListView.setOnItemClickListener(new OnItemClickListener(){

			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				
				
			}
			
		});
		
		moviesListView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if(aMode!=null){
					aMode.finish();
				}
				aMode = startActionMode(new ItemActionMode(position));
				
				return true;
			}

			
			
		});
		
	
		
	}
	
	
	public void onResume(){
		moviesAdapter.clear();
		for (final MoviesEntry entry : getMoviesEntries()) {
			moviesAdapter.add(entry);
		}
		moviesAdapter.notifyDataSetChanged();
		setUI();
		super.onResume();
	}
	
	
	private List<MoviesEntry> getMoviesEntries() {

		entries = new ArrayList<MoviesEntry>();
		List<MoviesDatabaseEntry> dbEntry = db.getAllMovies(listName);
		for (MoviesDatabaseEntry e : dbEntry) {
			entries.add(new MoviesEntry(e.get_title(), e.get_year(), e
					.get_rating(), e.get_poster(), e.get_genres(), e.get_actors(), e.get_date()));
		}
	switch(sortMode){
		case 1:
	    	Collections.sort(entries, new SortByNameAscending()); 
	    	break;
		case 2:
			Collections.sort(entries, new SortByNameDescending()); 
			break;
		case 3:
			Collections.sort(entries, new SortByDateAscending()); 
			break;
	    case 4:
			Collections.sort(entries, new SortByDateDescending()); 
			break;
		case 5:
			Collections.sort(entries, new SortByRatingAscending()); 
			break;
		case 6:
			Collections.sort(entries, new SortByRatingDescending()); 
			break;
		}
		return entries;
	}

	
	static class SortByRatingAscending implements Comparator<MoviesEntry>{
		@Override
		public int compare(MoviesEntry p1, MoviesEntry p2) {
	        if (p1.getRating() < p2.getRating()) return -1;
	        if (p1.getRating() > p2.getRating()) return 1;
	        return 0;
	    }   
		 
		}
	static class SortByRatingDescending implements Comparator<MoviesEntry>{
		@Override
		public int compare(MoviesEntry p1, MoviesEntry p2) {
	        if (p1.getRating() < p2.getRating()) return 1;
	        if (p1.getRating() > p2.getRating()) return -1;
	        return 0;
	    }   
		 
		}
	
	static class SortByDateAscending implements Comparator<MoviesEntry>{
		@Override
		public int compare(MoviesEntry p1, MoviesEntry p2) {
	        if (p1.getDate() < p2.getDate()) return -1;
	        if (p1.getDate() > p2.getDate()) return 1;
	        return 0;
	    }   
		 
		}
	static class SortByDateDescending implements Comparator<MoviesEntry>{
		@Override
		public int compare(MoviesEntry p1, MoviesEntry p2) {
	        if (p1.getDate() < p2.getDate()) return 1;
	        if (p1.getDate() > p2.getDate()) return -1;
	        return 0;
	    }   
		 
		}

	static class SortByNameAscending implements Comparator<MoviesEntry>{
		@Override
		public int compare(MoviesEntry s1, MoviesEntry s2) {
		    String sub1 = s1.getTitle();
		    String sub2 = s2.getTitle();
		    return sub1.compareTo(sub2);
		  } 
		 
		}
	static class SortByNameDescending implements Comparator<MoviesEntry>{
		@Override
		public int compare(MoviesEntry s1, MoviesEntry s2) {
		    String sub1 = s1.getTitle();
		    String sub2 = s2.getTitle();
		    return sub2.compareTo(sub1);
		  } 
		 
		}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getSupportMenuInflater().inflate(R.menu.activity_list, menu);
		if(theme.equals("light")){
			isLight = true;
			}
			else if(theme.equals("dark")){
				isLight = false;
			}
			else if(theme.equals("light_dark_action_bar")){
				isLight = false;
			}
		menu.add(0,9,1,"Search")
        .setIcon(isLight ? R.drawable.search_lite : R.drawable.search_dark)
        .setActionView(searchView)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		menu.add(0,0,2,"Preferences")
			.setIcon(isLight ? R.drawable.settings_light : R.drawable.settings_dark)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SubMenu sort = menu.addSubMenu(1, 1, 3, "Sort Order");
		sort.add(1,2,0,"Alphabetically Ascending");
		sort.add(1,3,1,"Alphabetically Descending");
		sort.add(1,4,2,"By Release Date Ascending");
		sort.add(1,5,3,"By Release Date Descending");
		sort.add(1,6,4,"By Ratings Ascending");
		sort.add(1,7,5,"By Ratings Descending");
		sort.add(1,8,6,"List Order");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch(item.getItemId()){
			case 2:
				recreateList(1);
			    break;
			case 3:
				recreateList(2);
				break;
			case 4:
				recreateList(3);
				break;
			case 5:
				recreateList(4);
				break;
			case 6:
				recreateList(5);
				break;
			case 7:
				recreateList(6);
				break;
			case 8:
				recreateList(0);
				break;
			case 0:
				Intent i = new Intent(this, Preferences.class);
	            startActivity(i);
				break;
			case android.R.id.home:
	            // app icon in action bar clicked; go home
	            Intent intent = new Intent(this, Lists.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
	            break;
		}

		return super.onOptionsItemSelected(item);

	}

	private void recreateList(int sortMode){
	   this.sortMode = sortMode;
		moviesAdapter.clear();
		
		for (final MoviesEntry entry : getMoviesEntries()) {
			moviesAdapter.add(entry);
		}
		moviesAdapter.notifyDataSetChanged();
		editor.putInt("sortOrder", sortMode);
		editor.apply();
	}
	

	private void setUI() {
		if (moviesAdapter.isEmpty() == false) {
			tv1.setVisibility(View.GONE);
			ll.setVisibility(View.GONE);
		} else {
			tv1.setVisibility(View.VISIBLE);
			ll.setVisibility(View.VISIBLE);
		}
	}

	private final class ItemActionMode implements ActionMode.Callback {
		int id;
		public ItemActionMode(int id){
			this.id = id;
		}
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        
	      //getSupportMenuInflater().inflate(R.menu.list_action, menu);
	    	if(theme.equals("light")){
				isLight = true;
				}
				else if(theme.equals("dark")){
					isLight = false;
				}
				else if(theme.equals("light_dark_action_bar")){
					isLight = false;
				}
			menu.add(2, 2, 2,getResources().getString(R.string.delete))
	    	.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark)
	    	.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	    

	        
	        return true;
	    }

	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false;
	    }

	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	
	    	
	    	if(item.getItemId()==2){
	    		deleteItemDialog(entries.get(id).getTitle(), id);
	    	}
	    	mode.finish();
	        return true;
	    }

	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    }
	}
	
	private void deleteItemDialog(final String movieName, final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(R.string.delete_item));
		builder.setMessage(getResources().getString(R.string.are_you_sure));
		builder.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark);

		builder.setPositiveButton(getResources()
				.getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MoviesDatabaseEntry entry = db
								.getMovieByName(listName, movieName);
						
							db.deleteMovie(entry, listName);
						
						moviesAdapter.remove(moviesAdapter.getItem(id));
						moviesAdapter.notifyDataSetChanged();
						setUI();
						
					}
				});

		builder.setNegativeButton(
				getResources().getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		AlertDialog alert = builder.create();

		alert.show();
	}
	
	@Override
	public boolean onSearchRequested() {
	     Bundle appData = new Bundle();
	     appData.putString("listName", listName);
	     startSearch(null, false, appData, false);
	     //System.out.println(listName);
		
	     return true;
	 }
	
	
	
}
