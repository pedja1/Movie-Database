package rs.pedjaapps.md.providers;

import android.content.*;

public class SuggestionProvider extends SearchRecentSuggestionsProvider
 {
    public final static String AUTHORITY = "rs.pedjaapps.md.providers.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
