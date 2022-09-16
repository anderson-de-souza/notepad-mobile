package com.anderson.notepad.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.SearchRecentSuggestions;

public class SearchHistorySuggestions extends SearchRecentSuggestions {

    private static final int MAX_ROWS = 5;

    public SearchHistorySuggestions(Context context,String authority, int mode) {
        super(context, authority, mode);
    }

    @Override
    protected void truncateHistory(ContentResolver resolver, int maxEntries) {
        super.truncateHistory(resolver, MAX_ROWS);
    }

}
