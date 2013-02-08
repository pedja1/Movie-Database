package rs.pedjaapps.md.entries;

public final class MoviesEntry
{


	private final String title;
    private final int year;
    private final double rating;
    private final String image;
    private final String genres;
    private final String actors;
    
    
	public String getTitle() {
		return title;
	}

	public int getYear() {
		return year;
	}

	public double getRating() {
		return rating;
	}

	public String getImage() {
		return image;
	}

	public String getGenres() {
		return genres;
	}

	public String getActors() {
		return actors;
	}

	public MoviesEntry(String title, int year, double rating, String image,
			String genres, String actors) {
		super();
		this.title = title;
		this.year = year;
		this.rating = rating;
		this.image = image;
		this.genres = genres;
		this.actors = actors;
	}

	

}
