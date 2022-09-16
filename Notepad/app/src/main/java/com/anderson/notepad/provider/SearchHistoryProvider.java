package com.anderson.notepad.provider;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.anderson.notepad.R;
import com.anderson.notepad.model.HistoryItem;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "com.anderson.notepad.provider.SearchHistoryProvider";
    public static final int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;

    public SearchHistoryProvider() {
        setupSuggestions(AUTHORITY,MODE);
    }

    public static Uri getUri() {
        return Uri.parse("content://"+AUTHORITY+"/"+ SearchManager.SUGGEST_URI_PATH_QUERY);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return new Wrapper(super.query(uri, projection, selection, selectionArgs, sortOrder));

    }

    static class Wrapper extends CursorWrapper {

        public Wrapper(Cursor cursor) {
            super(cursor);
        }

        @Override
        public String getString(int columnIndex) {

            if(columnIndex != -1 && columnIndex == getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1)) {
                return "android.resource://com.anderson.notepad/drawable/icon_access_time";
            }

            return super.getString(columnIndex);
        }

    }

    @NonNull
    @SuppressLint("Range")
    public static List<HistoryItem> getSearchSuggestions(@NonNull Context context) {

        ContentResolver resolver = context.getContentResolver();

        Cursor cursor = resolver.query(getUri(), null, null, new String[] { "" }, null);

        List<HistoryItem> items = new ArrayList<>();

        if (cursor.moveToFirst()) {

            do {

                items.add(new HistoryItem(
		         			cursor.getString(cursor.getColumnIndex(HistoryItem.ICON)),
				        	cursor.getString(cursor.getColumnIndex(HistoryItem.SEARCH)),
					        cursor.getString(cursor.getColumnIndex(HistoryItem.TIME))
                ));

            } while (cursor.moveToNext());

        }

        cursor.close();

        return items;

    }

}
