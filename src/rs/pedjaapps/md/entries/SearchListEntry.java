package rs.pedjaapps.md.entries;

public class SearchListEntry {

	private final String title;
	private final String id;
	private final int year;
	private final String plot;
	
	public SearchListEntry(String title, String id, int year, String plot) {
		super();
		this.title = title;
		this.id = id;
		this.year = year;
		this.plot = plot;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}
	
	public String getPlot() {
		return plot;
	}
	
	public int getYear(){
		return year;
	}

}
