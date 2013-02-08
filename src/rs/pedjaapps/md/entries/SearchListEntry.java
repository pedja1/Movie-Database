package rs.pedjaapps.md.entries;

public class SearchListEntry {

	private final String title;
	private final String id;
	private final int year;
	
	public SearchListEntry(String title, String id, int year) {
		super();
		this.title = title;
		this.id = id;
		this.year = year;
	}

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}
	public int getYear(){
		return year;
	}

}
