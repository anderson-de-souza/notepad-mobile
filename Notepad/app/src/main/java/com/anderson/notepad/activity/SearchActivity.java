package com.anderson.notepad.activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anderson.notepad.Notepad;
import com.anderson.notepad.R;
import com.anderson.notepad.activity.note.UpdateActivity;
import com.anderson.notepad.adapter.HistoryItemAdapter;
import com.anderson.notepad.adapter.NoteAdapter;
import com.anderson.notepad.database.NoteTable;
import com.anderson.notepad.databinding.ActivitySearchBinding;
import com.anderson.notepad.filter.NoteFilter;
import com.anderson.notepad.manager.ActivityThemeManager;
import com.anderson.notepad.model.Note;
import com.anderson.notepad.provider.SearchHistoryProvider;
import com.anderson.notepad.provider.SearchHistorySuggestions;

import java.util.List;

public class SearchActivity extends AppCompatActivity implements NoteFilter.OnPublishResultsListener {

    private String query;
    private ActivitySearchBinding binding;
    private NoteFilter filter;
    private NoteTable noteTable;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ActivityThemeManager themeManager = new ActivityThemeManager(this);
        themeManager.initTheme(false);

        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        ActionBar bar = getSupportActionBar();
        if(bar != null) bar.setDisplayHomeAsUpEnabled(true);
        
        var contract = new ActivityResultContracts.StartActivityForResult();

        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(contract, result -> {

            if (result.getResultCode() == RESULT_OK) {

                doSearch();

            }

        });


        binding.recyclerView.setHasFixedSize(true);

        noteTable = new NoteTable(this);

        var layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);

        var divider = new DividerItemDecoration(this,LinearLayoutManager.VERTICAL);
        binding.recyclerView.addItemDecoration(divider);

        adapter = new NoteAdapter(this, noteTable.toList());
        adapter.setOnItemClickListener((note, position) -> {
            Intent intent = new Intent(this, UpdateActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("note", note);
            resultLauncher.launch(intent);
        });

        onSearch(getIntent());

        binding.recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_activity_search,menu);
        
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(info);

        SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                searchAutoComplete.setAdapter(getHistoryItemAdapter());
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchAutoComplete.setAdapter(null);
                return true;
            }

        });

        return super.onCreateOptionsMenu(menu);

    }

    public void onSearch(@NonNull Intent intent) {
      
        query = intent.getStringExtra(SearchManager.QUERY);
        filter = new NoteFilter(noteTable.toList());
        filter.setOnPublishResultsListener(this);
        doSearch();
        saveQuery();

    }

    public void doSearch() {
        if(query.length() == 0) {
            binding.toolbar.setTitle(R.string.app_name);
        }else {
            binding.toolbar.setTitle(query);
            filter.filter(query);
        }
    }
    
    public void saveQuery() {
      
        SearchHistorySuggestions provider = new SearchHistorySuggestions(
                this,
                SearchHistoryProvider.AUTHORITY,
                SearchHistoryProvider.MODE
        );
        
        provider.saveRecentQuery(query.trim(), (String) Notepad.getTimeString(this));
        
    }

    @Override
    @SuppressLint("MissingSuperCall")
    public void onNewIntent(Intent intent) {
        setIntent(intent);
        onSearch(intent);
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public void onPublishResults(@Nullable CharSequence query, @NonNull List<Note> results) {
        binding.textView.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setNotes(results);
        adapter.notifyDataSetChanged();
    }

    public HistoryItemAdapter getHistoryItemAdapter() {
        return new HistoryItemAdapter(this,SearchHistoryProvider.getSearchSuggestions(this));
    }

}
