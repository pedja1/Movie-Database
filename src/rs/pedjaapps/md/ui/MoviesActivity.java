package rs.pedjaapps.md.ui;

import android.app.*;
import android.content.*;
import android.os.*;
import android.preference.*;
import android.view.*;
import android.widget.*;
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
	SharedPreferences sharedPrefs;
	RelativeLayout container;
	SearchView searchView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		setContentView(R.layout.activity_list);
		
		Intent intent = getIntent();
		listName ="Watchlist";// intent.getExtras().getString("name");
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
	        
		ImageView plus = (ImageView)findViewById(R.id.action_plus);
		plus.setImageResource(isLight ? R.drawable.add_light : R.drawable.add_dark);
		
		container = (RelativeLayout)findViewById(R.id.item_container);
		container.setBackgroundResource(isLight ? R.drawable.background_holo_light : R.drawable.background_holo_dark);
		
		
		
		
		boolean ads = sharedPrefs.getBoolean("ads", true);
		if (ads == true) {
			AdView adView = (AdView) findViewById(R.id.ad);
			adView.loadAd(new AdRequest());
		}

		tv1 = (TextView) findViewById(R.id.tv1);
		ll = (LinearLayout) findViewById(R.id.ll1);

	
		moviesListView = (ListView) findViewById(R.id.movie_list);
		
		
		moviesAdapter = new MoviesAdapter(this, R.layout.movie_row);

		moviesListView.setAdapter(moviesAdapter);

		for (final MoviesEntry entry : getItemsEntries()) {
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
	
	
	
	
	
	private List<MoviesEntry> getItemsEntries() {

		final List<MoviesEntry> entries = new ArrayList<MoviesEntry>();
		/*List<MoviesDatabaseEntry> dbEntry = db.getAllMovies(listName);
		for (MoviesDatabaseEntry e : dbEntry) {
			entries.add(new MoviesEntry(e.get_title(), e.get_year(), e
					.get_rating(), e.get_poster(), e.get_genres(), e.get_actors()));
		}*/
		entries.add(new MoviesEntry("Lost", 2006, 8.4,
				"", "Action, Sci_Fi", "Actor 1, Actor 2"));
		return entries;
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
		menu.add(1, 1, 1, getResources().getString(R.string.add))
        .setIcon(isLight ? R.drawable.add_light : R.drawable.add_dark)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Search")
        .setIcon(isLight ? R.drawable.search_lite : R.drawable.search_dark)
        .setActionView(searchView)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == 1) {
			// addListDialog();
			Intent intent = new Intent(this, Lists.class);
			intent.putExtra("name", "");
			intent.putExtra("listName", listName);
			startActivityForResult(intent, ADD_ITEM);

		}

		
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
		
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode ==ADD_ITEM ) {
				addItem(data);
				System.out.println("add");
			}
			
		}

	}

	public void addItem(Intent data){
		
		moviesAdapter.add(new MoviesEntry(data.getExtras().getString(
				"title"), data.getExtras().getInt(
						"year"), data.getExtras().getDouble(
								"rating"), data.getExtras().getString(
										"image"), data.getExtras().getString(
												"genres"), data.getExtras().getString(
														"actors")));
		moviesAdapter.notifyDataSetChanged();
		setUI();
		
	
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

	/*private void deleteAllDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(R.string.delete_all_items));
		builder.setMessage(getResources().getString(R.string.are_you_sure));
		builder.setIcon(isLight ? R.drawable.delete_light : R.drawable.delete_dark);

		builder.setPositiveButton(getResources()
				.getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<MoviesDatabaseEntry> entry = db
								.getAllItems(listName);
						for (MoviesDatabaseEntry e : entry) {
							db.deleteItem(e, listName);
						}
						itemsAdapter.clear();
						itemsAdapter.notifyDataSetChanged();
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
	}*/

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
	    		deleteItemDialog(id);
	    	}
	    	mode.finish();
	        return true;
	    }

	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	    }
	}
	
	private void deleteItemDialog(final int id) {
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
								.getMovie(listName, id);
						
							db.deleteMovie(entry, listName);
						
						moviesAdapter.remove(moviesAdapter.getItem(id-1));
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
	
	/*@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_SEARCH){
	    	onSearchRequested();
	    	return false;
	    }else{
	        return super.onKeyUp(keyCode, event); 
	    }
	}
	
	@Override
	 public boolean onSearchRequested() {

	     // your logic here

		return super.onSearchRequested();

	 }*/
	
}
