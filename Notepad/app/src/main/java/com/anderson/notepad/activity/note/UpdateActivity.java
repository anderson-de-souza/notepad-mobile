package com.anderson.notepad.activity.note;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;

import com.anderson.notepad.R;

import com.anderson.notepad.database.NoteTable;

public class UpdateActivity extends NoteActivity {

    private NoteTable.FavoriteTable favoriteTable;
    private NoteTable.LockedTable lockedTable;
    private int adapterPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        favoriteTable = noteTable.getFavorite();
        lockedTable = noteTable.getLocked();
        
        Bundle box = getIntent().getExtras();
        note = box.getParcelable("note");
        adapterPosition = box.getInt("position");

        setTexts();

    }

    @Override
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        super.onCreateOptionsMenu(menu);

        MenuBuilder builder = (MenuBuilder) menu;
        builder.setOptionalIconsVisible(true);

        MenuItem trash = menu.findItem(R.id.delete);
        MenuItem heart = menu.findItem(R.id.heart);
        MenuItem padlock = menu.findItem(R.id.padlock);

        showOptionsMenu(trash, heart, padlock);

        if(favoriteTable.exists(note.getCode())) {

            heart.setIcon(R.drawable.icon_heart);
            heart.setTitle(R.string.remove_from_favorites);

        }

        if(lockedTable.exists(note.getCode())) {

            padlock.setIcon(R.drawable.icon_lock);
            padlock.setTitle(R.string.unlock);

        }

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.heart) {

            if(favoriteTable.exists(note.getCode())) {

                favoriteTable.delete(note.getCode());
                item.setIcon(R.drawable.icon_heart_outline);
                item.setTitle(R.string.add_to_favorites);

            }else {

                favoriteTable.insert(note.getCode());
                item.setIcon(R.drawable.icon_heart);
                item.setTitle(R.string.remove_from_favorites);

            }

        }else if(id == R.id.padlock) {

            if(lockedTable.exists(note.getCode())) {

                lockedTable.delete(note.getCode());
                item.setIcon(R.drawable.icon_lock_open);
                item.setTitle(R.string.lock);


            }else {

                lockedTable.insert(note.getCode());
                item.setIcon(R.drawable.icon_lock);
                item.setTitle(R.string.unlock);

            }

        }

        Intent intent = new Intent();
        intent.putExtra("position", adapterPosition);

        if(id == R.id.save) {

            if(validate()) {
                noteTable.update(note);
                intent.setAction("update");
                setResult(RESULT_OK,intent);
                finish();
            }

        }else if(id == R.id.delete) {
            noteTable.delete(note.getCode());
            intent.setAction("delete");
            setResult(RESULT_OK,intent);
            finish();
        }

        return true;

    }

    public void setTexts() {

        binding.editTextTitle.setText(note.getTitle());
        binding.editTextContent.setText(note.getContent());

    }

    public void showOptionsMenu(@NonNull MenuItem... items) {
          
          for(MenuItem item: items) {
                item.setVisible(true);
          }
               
    }

}
