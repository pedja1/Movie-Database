package rs.pedjaapps.md.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rs.pedjaapps.md.entries.*;
import rs.pedjaapps.md.helpers.DatabaseHandler;
import android.content.DialogInterface;

public class UpdateAllMovies
{

	
	private DatabaseHandler db;
	private Context context;
	private int updated = 0;
	private int error = 0;
	private ProgressDialog pd;
	private String extStorage = Environment.getExternalStorageDirectory().toString();
	
    public UpdateAllMovies(Context context){
    	 db = new DatabaseHandler(context);
    	 this.context = context;
	}
    
    public void updateMovies(String[] list){
    	new DownloadMovieInfo().execute(list);
    }

	public class DownloadMovieInfo extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... args)
		{
			 for(String s : args){
			List<MoviesDatabaseEntry> movies = db.getAllMovies(s, "", "", 0, "", "", 0.0, 10);
			
			int lenght = movies.size();
			for(int in = 0; in<lenght; in++){
				publishProgress("Updating: "+in+"/"+(lenght-1)+"\n"+movies.get(in).get_title());
				//String murl= movies.get(in).get_url();
				//String mid = murl.substring(murl.lastIndexOf("tt"), murl.length());
			DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpGet httpget = new HttpGet("http://imdbapi.org/?id="+movies.get(in).get_imdb_id()+"&type=json&plot=simple&episode=0&lang=en-US&aka=simple&release=simple&business=0&tech=0");
			InputStream inputStream = null;
			String result = "";
			
			
			try {
				HttpResponse response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();
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
						posterFile = extStorage+"/MDb/posters"+poster.substring(poster.lastIndexOf("/"));
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
				
					
					DownloadFromUrl(poster, posterFile);
					db.updateMovie(new MoviesDatabaseEntry(title, runtime, rating, genres, type,
							lang, posterFile, url, directors, actors, plot, year, country, date, movies.get(in).get_ur(), id), movies.get(in).get_title(), args[0]);
					updated++;
				    
			//	return res;
			} catch (ClientProtocolException e) {
				error++;
			} catch (IOException e) {
				error++;
			} catch (JSONException e) {
				error++;
			}
			}
			
			 }
			return "";           
			
			
			
		}

		@Override
		protected void onPreExecute(){
			pd = new ProgressDialog(context);
			pd.setIndeterminate(true);
			pd.setTitle("Updating movies");
			pd.show();
			pd.setOnCancelListener(new ProgressDialog.OnCancelListener(){

					public void onCancel(DialogInterface p1)
					{
						DownloadMovieInfo.this.cancel(true);
					}
					
				
			});
			 }
		
		@Override
		protected void onProgressUpdate(String... progress){
			pd.setMessage(progress[0]);
		}
		@Override
		protected void onPostExecute(String result)
		{
			pd.dismiss();
			
			
				Toast.makeText(context, "Added:"+updated+" Failed:"+error, Toast.LENGTH_LONG).show();
				
			
		}
	}
	
	public String DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
        try {
        	File ktDir = new File(Environment.getExternalStorageDirectory() + "/MDb/posters");
		      if(ktDir.exists()==false){
		      ktDir.mkdirs();
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
