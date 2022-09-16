package com.anderson.notepad.activity.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.anderson.notepad.R;
import com.anderson.notepad.database.NoteTable;
import com.anderson.notepad.databinding.ActivityNoteBinding;
import com.anderson.notepad.manager.ActivityThemeManager;
import com.anderson.notepad.model.Note;

public class NoteActivity extends AppCompatActivity {

    protected ActivityThemeManager themeManager;
    protected ActivityNoteBinding binding;
    protected Menu menu;
    protected NoteTable noteTable;
    protected Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        themeManager = new ActivityThemeManager(this);
        themeManager.initTheme(false);

        super.onCreate(savedInstanceState);
        binding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        noteTable = new NoteTable(this);
        note = new Note();

        setSupportActionBar(binding.toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_activity_note,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.save) {
            if(validate()) {
                noteTable.insert(note);
                Intent intent = new Intent();
                intent.setAction("insert");
                intent.putExtra("position", 0);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean validate() {
        String title = binding.editTextTitle.getText().toString().trim();
        String content = binding.editTextContent.getText().toString().trim();
        if(title.isEmpty()) return false;
        note.setTitle(title);
        note.setContent(content);
        note.setTime(System.currentTimeMillis());
        return true;
    }

}
