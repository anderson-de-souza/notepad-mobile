package com.anderson.notepad.activity;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anderson.notepad.R;
import com.anderson.notepad.activity.note.NoteActivity;
import com.anderson.notepad.activity.note.UpdateActivity;
import com.anderson.notepad.adapter.HistoryItemAdapter;
import com.anderson.notepad.adapter.NoteAdapter;
import com.anderson.notepad.database.NoteTable;
import com.anderson.notepad.database.SQLiteHelper;
import com.anderson.notepad.databinding.ActivityMainBinding;
import com.anderson.notepad.manager.ActivityThemeManager;
import com.anderson.notepad.provider.SearchHistoryProvider;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RESULT_THEME_CHANGE = 4;

    private ActivityThemeManager themeManager;
    private ActivityMainBinding binding;

    private NoteTable noteTable;
    private NoteTable.FavoriteTable favoriteTable;
    private NoteTable.LockedTable lockedTable;
    private NoteAdapter adapter;

    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        themeManager = new ActivityThemeManager(this);
        themeManager.initTheme(false);
        themeManager.initThemeMode();

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        noteTable = new NoteTable(this);
        favoriteTable = noteTable.getFavorite();
        lockedTable = noteTable.getLocked();

        var drawerToggle = new ActionBarDrawerToggle(
           this,
           binding.drawerLayout,
           binding.toolbar,
           R.string.open_drawer_navigation_menu,
           R.string.close_drawer_navigation_menu
        );

        drawerToggle.setDrawerIndicatorEnabled(true);

        binding.drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        binding.navigationView.setCheckedItem(R.id.notes);
        binding.navigationView.setNavigationItemSelectedListener(this);

        var contract = new ActivityResultContracts.StartActivityForResult();

        resultLauncher = registerForActivityResult(contract, result -> {

            if (result.getResultCode() == RESULT_OK) {

                Intent data = result.getData();

                assert data != null;

                int position = data.getIntExtra("position",-1);
                String action = data.getAction();

                adapter.setNotes(noteTable.toList());

                switch (action) {
                    case "insert":
                        adapter.notifyItemInserted(position);
                        break;
                    case "update":
                        adapter.notifyItemChanged(position);
                        break;
                    case "delete":
                        adapter.notifyItemRemoved(position);
                        break;
                }

            }else if(result.getResultCode() == RESULT_THEME_CHANGE) {

                themeManager.initTheme(true);

            }

        });

        binding.recyclerView.setHasFixedSize(true);

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

        binding.recyclerView.setAdapter(adapter);

        binding.buttonAddNote.setOnClickListener(view -> resultLauncher.launch(new Intent(this, NoteActivity.class)));

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_activity_main,menu);

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

    public void closeSearchView() {
        binding.toolbar.collapseActionView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.clear_database) {
            if(deleteDatabase(SQLiteHelper.DATABASE_NAME)) finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else if(binding.toolbar.hasExpandedActionView()) {
            closeSearchView();
        }else super.onBackPressed();
    }

    @Override
    @SuppressLint("NotifyDataSetChanged")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.notes) {

            adapter.setNotes(noteTable.toList());
            adapter.notifyDataSetChanged();

        }else if(id == R.id.favorites) {

            adapter.setNotes(favoriteTable.toList());
            adapter.notifyDataSetChanged();

        }else if(id == R.id.locked) {

            adapter.setNotes(lockedTable.toList());
            adapter.notifyDataSetChanged();

        }

        if(id == R.id.settings) {

            resultLauncher.launch(new Intent(this, SettingsActivity.class));

        }else if(id == R.id.info) {

            new AlertDialog.Builder(this)
            .setTitle(R.string.info)
            .setMessage("Version Beta 1.0")
            .show();

        }

        return true;

    }

    public HistoryItemAdapter getHistoryItemAdapter() {
        return new HistoryItemAdapter(this,SearchHistoryProvider.getSearchSuggestions(this));
    }

}