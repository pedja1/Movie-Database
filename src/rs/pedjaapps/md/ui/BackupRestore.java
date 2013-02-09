package rs.pedjaapps.md.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import rs.pedjaapps.md.R;

public class BackupRestore extends Activity {

	
	private String prefBackupMessage;
	private String dbBackupMessage;
	private String prefRestoreMessage;
	private String dbRestoreMessage;
	private class Backup extends AsyncTask<String, Integer, String> {

		ProgressDialog pd;
		@Override
		protected String doInBackground(String... args) {
			
			
		      File ktDir = new File(Environment.getExternalStorageDirectory() + "/MDb");
		      if(ktDir.exists()==false){
		      ktDir.mkdir();
		      }
			publishProgress(0);
			backupUserPrefs(BackupRestore.this);
			publishProgress(1);
			backupDb(BackupRestore.this);
				
			
			return "";
		}

		@Override
		protected void onPreExecute(){
			pd = new ProgressDialog(BackupRestore.this);
			pd.setIndeterminate(true);
			pd.setTitle("Backing Up Application Settings");
			pd.show();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values)
		{
			switch(values[0]){
			case 0:
				pd.setMessage("Backing up Database");
				pd.setProgress(1);
				break;
			case 1:
				pd.setMessage("Backing up Shared Preferences");
				pd.setProgress(0);
				break;
			}
			super.onProgressUpdate();
		}
		
		@Override
		protected void onPostExecute(String result) {
			pd.cancel();
			alert("Backup", prefBackupMessage, dbBackupMessage, "");
		}

	}

	private class Restore extends AsyncTask<String, Integer, String> {

		ProgressDialog pd;
		@Override
		protected String doInBackground(String... args) {
			
			publishProgress(0);
			restoreUserPrefs(BackupRestore.this);
			publishProgress(1);
			restoreDb(BackupRestore.this);
				
			
			return "";
		}

		@Override
		protected void onPreExecute(){
			pd = new ProgressDialog(BackupRestore.this);
			pd.setIndeterminate(true);
			pd.setTitle("Restoring Application Settings");
			pd.show();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values)
		{
			switch(values[0]){
			case 0:
				pd.setMessage("Restoring Database");
				pd.setProgress(1);
				break;
			case 1:
				pd.setMessage("Restoring Shared Preferences");
				pd.setProgress(0);
				break;
			}
			super.onProgressUpdate();
		}
		
		@Override
		protected void onPostExecute(String result) {
			pd.cancel();
			alert("Restore", prefRestoreMessage, dbRestoreMessage, "Restart Application for changes to take effect");
			System.out.println(""+prefRestoreMessage);
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String them = sharedPrefs.getString("theme", "light");

		if (them.equals("light")) {
			setTheme(android.R.style.Theme_Holo_Light);
		} else if (them.equals("dark")) {
			setTheme(android.R.style.Theme_Holo);

		} else if (them.equals("light_dark_action_bar")) {
			setTheme(android.R.style.Theme_Holo_Light_DarkActionBar);

		}
		setContentView(R.layout.backup_restore);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		Button backup = (Button) findViewById(R.id.backup);
		Button restore = (Button) findViewById(R.id.restore);

		backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//backup();
				File sd = Environment.getExternalStorageDirectory();
				if(sd.canWrite()){
					new Backup().execute();
				}
				else{
					Toast.makeText(BackupRestore.this, "Unable to write to external storage",
							Toast.LENGTH_LONG).show();
				}
			}

		});

		restore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				File sd = Environment.getExternalStorageDirectory();
				if(sd.canRead()){
					new Restore().execute();
				}
				else{
					Toast.makeText(BackupRestore.this, "Unable to read from external storage",
							Toast.LENGTH_LONG).show();
				}
			}

		});
	}

	

	
	public void backupUserPrefs(Context context) {
		  final File prefsFile = new File(context.getFilesDir(), "../shared_prefs/" + 
		      context.getPackageName()+"_preferences.xml");
		  final File backupFile = new File(Environment.getExternalStorageDirectory(), 
		      "MDb/preferenceBackup.xml");
		  
		  
		  try {
			  FileInputStream fin = new FileInputStream(prefsFile);
			  FileOutputStream fout = new FileOutputStream(backupFile);
		    FileChannel src = fin.getChannel();
		    FileChannel dst = fout.getChannel();
		    
		    dst.transferFrom(src, 0, src.size());

		    src.close();
		    dst.close();
		    fin.close();
		    fout.close();
		    prefBackupMessage = "Backup Successful";
		    //return true;
		  } catch (FileNotFoundException e) {
		    prefBackupMessage = e.getMessage();
		    //return false;
		  } catch (IOException e) {
			  prefBackupMessage = e.getMessage();
		    //return false;
		  }
		}

	public void backupDb(Context context) {
		  final File prefsFile = new File(context.getFilesDir(), "../databases/MDb.db");
		  final File backupFile = new File(Environment.getExternalStorageDirectory(), 
		      "MDb/MDb.db");
		  
		final File prefsSFile = new File(context.getFilesDir(), "../databases/suggestions.db");
		final File backupSFile = new File(Environment.getExternalStorageDirectory(), 
										 "MDb/suggestions.db");
		
		  
		  try {
			  FileInputStream fin = new FileInputStream(prefsFile);
			  FileOutputStream fout = new FileOutputStream(backupFile);
		    FileChannel src = fin.getChannel();
		    FileChannel dst = fout.getChannel();
		    
		    dst.transferFrom(src, 0, src.size());

		    src.close();
		    dst.close();
		    fin.close();
		    fout.close();
		    dbBackupMessage = "Backup Successful";
		    //return true;
		  } catch (FileNotFoundException e) {
		    dbBackupMessage = e.getMessage();
		    //return false;
		  } catch (IOException e) {
			  dbBackupMessage = e.getMessage();
		    //return false;
		  }
		  
		try {
			FileInputStream fin = new FileInputStream(prefsSFile);
			FileOutputStream fout = new FileOutputStream(backupSFile);
		    FileChannel src = fin.getChannel();
		    FileChannel dst = fout.getChannel();

		    dst.transferFrom(src, 0, src.size());

		    src.close();
		    dst.close();
		    fin.close();
		    fout.close();
		    dbBackupMessage = "Backup Successful";
		    //return true;
		} catch (FileNotFoundException e) {
		    dbBackupMessage = e.getMessage();
		    //return false;
		} catch (IOException e) {
			dbBackupMessage = e.getMessage();
		    //return false;
		}
		  
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
	
	public void restoreDb(Context context) {
		  final File prefsFile = new File(context.getFilesDir(), "../databases/MDb.db");
		  final File backupFile = new File(Environment.getExternalStorageDirectory(), 
		      "MDb/MDb.db");
		  
		final File prefsSFile = new File(context.getFilesDir(), "../databases/suggestions.db");
		final File backupSFile = new File(Environment.getExternalStorageDirectory(), 
										 "MDb/suggestions.db");
		  
		  try {
			  FileInputStream fin = new FileInputStream(backupFile);
			  FileOutputStream fout = new FileOutputStream(prefsFile);
		    FileChannel src = fin.getChannel();
		    FileChannel dst = fout.getChannel();
		    
		    dst.transferFrom(src, 0, src.size());

		    src.close();
		    dst.close();
		    fin.close();
		    fout.close();
		    dbRestoreMessage = "Restore Successful";
		    //return true;
		  } catch (FileNotFoundException e) {
		    dbRestoreMessage = e.getMessage();
		    //return false;
		  } catch (IOException e) {
			  dbRestoreMessage = e.getMessage();
		    //return false;
		  }
		  
		try {
			FileInputStream fin = new FileInputStream(backupSFile);
			FileOutputStream fout = new FileOutputStream(prefsSFile);
		    FileChannel src = fin.getChannel();
		    FileChannel dst = fout.getChannel();

		    dst.transferFrom(src, 0, src.size());

		    src.close();
		    dst.close();
		    fin.close();
		    fout.close();
		    dbRestoreMessage = "Restore Successful";
		    //return true;
		} catch (FileNotFoundException e) {
		    dbRestoreMessage = e.getMessage();
		    //return false;
		} catch (IOException e) {
			dbRestoreMessage = e.getMessage();
		    //return false;
		}
		  
		}
	
	
	public void restoreUserPrefs(Context context) {
		  final File backupFile = new File(Environment.getExternalStorageDirectory(), 
		      "MDb/preferenceBackup.xml");
		  
		  try {
		      
		    SharedPreferences sharedPreferences = PreferenceManager
		        .getDefaultSharedPreferences(context);
		    
		    Editor editor = sharedPreferences.edit();
		      
		    InputStream inputStream = new FileInputStream(backupFile);

		    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		    Document doc = docBuilder.parse(inputStream);        
		    Element root = doc.getDocumentElement();
		        
		    Node child = root.getFirstChild();
		    while(child!=null) {  
		      if(child.getNodeType() == Node.ELEMENT_NODE) {
		          
		        Element element = (Element)child;
		            
		        String type = element.getNodeName();
		        String name = element.getAttribute("name");
		            
		        // In my app, all prefs seem to get serialized as either "string" or
		        // "boolean" - this will need expanding if yours uses any other types!    
		        if(type.equals("string")) {
		          String value = element.getTextContent();
		          editor.putString(name, value);
		        }
		        else if(type.equals("boolean")) {
		          String value = element.getAttribute("value");
		          editor.putBoolean(name, value.equals("true"));
		        }
		      }
		            
		      child = child.getNextSibling();
		          
		    }
		        
		    editor.commit();

		   prefRestoreMessage = "Restore Successful"; 
		    
		    
		  } catch (FileNotFoundException e) {
			  prefRestoreMessage = e.getMessage(); 
		    
		  } catch (ParserConfigurationException e) {
			  prefRestoreMessage = e.getMessage();
		    
		  } catch (SAXException e) {
			  prefRestoreMessage = e.getMessage();
		  } catch (IOException e) {
			  prefRestoreMessage = e.getMessage();
		  }
		  
		  
		  
		}

	private void alert(String title, String prefMessage, String dbMessage, String restoreMessage){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Shared Preferences - ");
		strBuilder.append(prefMessage+"\n\n");
		strBuilder.append("Database - ");
		strBuilder.append(dbMessage+"\n\n");
		strBuilder.append(restoreMessage);
		builder.setMessage(strBuilder.toString());
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				prefBackupMessage = null;
				dbBackupMessage = null;
				prefRestoreMessage = null;
				dbRestoreMessage = null;
				
			}
		});
		AlertDialog alert = builder.create();

		alert.show();
	}
	
}
