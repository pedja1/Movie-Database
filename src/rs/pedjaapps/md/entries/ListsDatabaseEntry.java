package rs.pedjaapps.md.entries;

public class ListsDatabaseEntry
{

	//private variables
    int _id;
    String _name;
    int _icon;

    // Empty constructor
    public ListsDatabaseEntry()
	{

    }

	public ListsDatabaseEntry(int _id, String _name, int _icon) {
		
		this._id = _id;
		this._name = _name;
		this._icon = _icon;
	}

	public ListsDatabaseEntry( String _name, int _icon) {

		this._name = _name;
		this._icon = _icon;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public int get_icon() {
		return _icon;
	}

	public void set_icon(int _icon) {
		this._icon = _icon;
	}

	
	
}
