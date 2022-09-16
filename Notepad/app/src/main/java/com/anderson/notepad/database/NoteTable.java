package com.anderson.notepad.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anderson.notepad.model.Note;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public final class NoteTable extends SQLiteHelper {

	private SQLiteDatabase database;

	public NoteTable(Context context) {
		super(context);
	}

	public void insert(Note note) {
		database = getWritableDatabase();
		database.insert("Note", null, getValues(note));
		database.close();
	}

	public void update(Note note) {
		database = getWritableDatabase();
		database.update("Note",getValues(note),"code = ?",new String[]{ String.valueOf(note.getCode()) });
		database.close();
	}

	public void delete(long code) {
		database = getWritableDatabase();
		database.delete("Note","code = ?",new String[]{ String.valueOf(code) });
		database.close();
	}

	@NonNull
	public List<Note> toList() {
		List<Note> notes = new ArrayList<>();
		database = getReadableDatabase();
		Cursor cursor = database.rawQuery("SELECT * FROM Note ORDER BY code DESC",null);
		if(cursor.moveToFirst()) {
			do {
				notes.add(getNote(cursor));
			}while(cursor.moveToNext());
		}
		cursor.close();
		database.close();
		return notes;
	}

	@Nullable
	public Note select(long code) {
		database = getReadableDatabase();
		Cursor cursor = database.query("Note",null,"code = ?",new String[]{ String.valueOf(code) },null,null,null,null);
		if(cursor.getCount() > 0) cursor.moveToFirst(); else return null;
		Note note = getNote(cursor);
		cursor.close();
		database.close();
		return note;
	}

	@NonNull
	public ContentValues getValues(@NonNull Note note) {
		ContentValues values = new ContentValues();
		values.put("title", note.getTitle());
		values.put("content", note.getContent());
		values.put("time", note.getTime());
		values.put("last", note.getLast());
		return values;
	}

	@NonNull
	@Contract("_ -> new")
	@SuppressLint("Range")
	public Note getNote(@NonNull Cursor cursor) {
		return new Note(
			cursor.getLong(cursor.getColumnIndex("code")),
			cursor.getString(cursor.getColumnIndex("title")),
			cursor.getString(cursor.getColumnIndex("content")),
			cursor.getLong(cursor.getColumnIndex("time")),
			cursor.getLong(cursor.getColumnIndex("last"))
		);
	}

	private class ReferencedTable {
		
		@SuppressLint("Range")
		protected Note getNote(@NonNull Cursor cursor) {
			return select(cursor.getLong(cursor.getColumnIndex("note_code")));
		}

		protected ContentValues getValues(long noteCode) {
			ContentValues values = new ContentValues();
			values.put("note_code", noteCode);
			return values;
		}

	}

	public final class FavoriteTable extends ReferencedTable {
		
		public void insert(long noteCode) {
			database = getWritableDatabase();
			database.insert("Favorite", null, getValues(noteCode));
			database.close();
		}

		public boolean exists(long noteCode) {
			database = getReadableDatabase();
			Cursor cursor = database.query("Favorite",null,"note_code = ?",new String[]{ String.valueOf(noteCode) }, null, null, null, null);
			boolean result = cursor.getCount() > 0;
			cursor.close();
			return result;
		}

		public void delete(long noteCode) {
			database = getWritableDatabase();
			database.delete("Favorite","note_code = ?",new String[]{ String.valueOf(noteCode) });
			database.close();
		}

		@NonNull
		public List<Note> toList() {
			List<Note> notes = new ArrayList<>();
			database = getReadableDatabase();
			Cursor cursor = database.rawQuery("SELECT * FROM Favorite ORDER BY note_code DESC",null);
			if (cursor.moveToFirst()) {
				do {
					notes.add(getNote(cursor));
				} while (cursor.moveToNext());
			}
			cursor.close();
			database.close();
			return notes;
		}

	}

	public final class LockedTable extends ReferencedTable {
		
		public void insert(long noteCode) {
			database = getWritableDatabase();
			database.insert("Locked", null, getValues(noteCode));
			database.close();
		}

		public boolean exists(long noteCode) {
			database = getReadableDatabase();
			Cursor cursor = database.query("Locked",null,"note_code = ?",new String[]{ String.valueOf(noteCode) },null,null,null,null);
			boolean result = cursor.getCount() > 0;
			cursor.close();
			return result;
		}

		public void delete(long noteCode) {
			database = getWritableDatabase();
			database.delete("Locked","note_code = ?",new String[] { String.valueOf(noteCode) });
			database.close();
		}

		@NonNull
		public List<Note> toList() {
			List<Note> notes = new ArrayList<>();
			database = getReadableDatabase();
			Cursor cursor = database.rawQuery("SELECT * FROM Locked ORDER BY note_code DESC",null);
			if (cursor.moveToFirst()) {
				do {
					notes.add(getNote(cursor));
				} while (cursor.moveToNext());
			}
			cursor.close();
			database.close();
			return notes;
		}

	}

	@NonNull
	@Contract(" -> new")
	public LockedTable getLocked() {
		return new LockedTable();
	}

	@NonNull
	@Contract(" -> new")
	public FavoriteTable getFavorite() {
		return new FavoriteTable();
	}

}