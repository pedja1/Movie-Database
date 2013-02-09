package rs.pedjaapps.md.entries;

public class MoviesDatabaseEntry
{

	//private variables
    int _id;
    String _title;
    String _runtime;
    double _rating;
    String _genres;
    String _type;
    String _lang;
    String _poster;
    String _url;
    String _director;
    String _actors;
    String _plot;
    int _year;
    String _country;
    int _date;

    // Empty constructor
    public MoviesDatabaseEntry()
	{

    }

	public MoviesDatabaseEntry(int _id, String _title, String _runtime,
			double _rating, String _genres, String _type, String _lang,
			String _poster, String _url, String _director, String _actors,
			String _plot, int _year, String _country, int _date) {
		
		this._id = _id;
		this._title = _title;
		this._runtime = _runtime;
		this._rating = _rating;
		this._genres = _genres;
		this._type = _type;
		this._lang = _lang;
		this._poster = _poster;
		this._url = _url;
		this._director = _director;
		this._actors = _actors;
		this._plot = _plot;
		this._year = _year;
		this._country = _country;
		this._date = _date;
	}
   
	public MoviesDatabaseEntry(String _title, String _runtime,
			double _rating, String _genres, String _type, String _lang,
			String _poster, String _url, String _director, String _actors,
			String _plot, int _year, String _country, int _date) {
		
		this._title = _title;
		this._runtime = _runtime;
		this._rating = _rating;
		this._genres = _genres;
		this._type = _type;
		this._lang = _lang;
		this._poster = _poster;
		this._url = _url;
		this._director = _director;
		this._actors = _actors;
		this._plot = _plot;
		this._year = _year;
		this._country = _country;
		this._date = _date;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_title() {
		return _title;
	}

	public void set_title(String _title) {
		this._title = _title;
	}

	public String get_runtime() {
		return _runtime;
	}

	public void set_runtime(String _runtime) {
		this._runtime = _runtime;
	}

	public double get_rating() {
		return _rating;
	}

	public void set_rating(double _rating) {
		this._rating = _rating;
	}

	public String get_genres() {
		return _genres;
	}

	public void set_genres(String _genres) {
		this._genres = _genres;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public String get_lang() {
		return _lang;
	}

	public void set_lang(String _lang) {
		this._lang = _lang;
	}

	public String get_poster() {
		return _poster;
	}

	public void set_poster(String _poster) {
		this._poster = _poster;
	}

	public String get_url() {
		return _url;
	}

	public void set_url(String _url) {
		this._url = _url;
	}

	public String get_director() {
		return _director;
	}

	public void set_director(String _director) {
		this._director = _director;
	}

	public String get_actors() {
		return _actors;
	}

	public void set_actors(String _actors) {
		this._actors = _actors;
	}

	public String get_plot() {
		return _plot;
	}

	public void set_plot(String _plot) {
		this._plot = _plot;
	}

	public int get_year() {
		return _year;
	}

	public void set_year(int _year) {
		this._year = _year;
	}

	public String get_country() {
		return _country;
	}

	public void set_country(String _country) {
		this._country = _country;
	}

	public int get_date() {
		return _date;
	}

	public void set_date(int _date) {
		this._date = _date;
	}
    
}
