package rs.pedjaapps.md.entries;

public class ListEntry {

	private final String title;
	private final int count;
	private final int icon;
	
	public ListEntry(String title, int count, int icon) {
		this.title = title;
		this.count = count;
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}
	
	public int getCount(){
		return count;
	}
	
	public int getIcon(){
		return icon;
	}

}
