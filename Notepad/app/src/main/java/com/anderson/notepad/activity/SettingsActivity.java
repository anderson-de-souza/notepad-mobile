package com.anderson.notepad.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.anderson.notepad.R;
import com.anderson.notepad.databinding.ActivitySettingsBinding;
import com.anderson.notepad.manager.ActivityThemeManager;
import com.anderson.notepad.provider.SearchHistoryProvider;
import com.anderson.notepad.provider.SearchHistorySuggestions;

public class SettingsActivity extends AppCompatActivity {

    private ActivityThemeManager themeManager;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        themeManager = new ActivityThemeManager(this);
        themeManager.initTheme(false);

        super.onCreate(savedInstanceState);
        var binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        createCardChangeThemeDialog();

        binding.cardChangeTheme.setOnClickListener(view -> dialog.show());
        
        binding.switchCompatDarkMode.setChecked(themeManager.getNightMode());
        binding.switchCompatDarkMode.setOnCheckedChangeListener((view, state) -> {
            
            themeManager.setNightMode(state);
            themeManager.initThemeMode();
            
        });

        binding.cardDeleteSearchHistory.setOnClickListener(view -> new AlertDialog.Builder(this)
        .setTitle(R.string.are_you_sure)
        .setPositiveButton(R.string.yes,(dialog, witch) -> {

            SearchHistorySuggestions provider = new SearchHistorySuggestions (
                    this,
                    SearchHistoryProvider.AUTHORITY,
                    SearchHistoryProvider.MODE
            );

            provider.clearHistory();

            Toast.makeText(this,R.string.delete,Toast.LENGTH_SHORT).show();

        }).show());

    }
    
    private void createCardChangeThemeDialog() {

        String[] themes = getResources().getStringArray(R.array.themes);
        String themeSet = themeManager.getStyleName();

        int checkedItem = 0;

        for (int index = 0; index < themes.length; index++) {
            if (themeSet.equals(themes[index])) checkedItem = index;
        }
        
        dialog = new AlertDialog.Builder(this)
        .setTitle(R.string.select_theme)
        .setSingleChoiceItems(themes,checkedItem,(dialog, witch) -> {
                 
            switch (witch) {
              
                case 0:
                    themeManager.setStyle(R.style.SunnySky);
                    break;
                case 1:
                    themeManager.setStyle(R.style.Nature);
                    break;
                case 2:
                    themeManager.setStyle(R.style.LightingStorm);
                    break;
                case 3:
                    themeManager.setStyle(R.style.Ocean);
                    break;
                case 4:
                    themeManager.setStyle(R.style.Fire);
                    break;    
            }
                    
            setResult(MainActivity.RESULT_THEME_CHANGE);
            finish();
                      
        }).create();
           
    }
    
}