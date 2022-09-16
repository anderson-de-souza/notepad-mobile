package com.anderson.notepad.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Notepad.sqlite";
    public static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase database) {
        super.onConfigure(database);
        database.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase database) {
        database.execSQL("CREATE TABLE Note (code INTEGER PRIMARY KEY, title CHAR(100), content TEXT, time INTEGER, last INTEGER)");
        database.execSQL("CREATE TABLE Favorite (code INTEGER PRIMARY KEY, note_code INTEGER, FOREIGN KEY (note_code) REFERENCES Note (code) ON DELETE CASCADE)");
        database.execSQL("CREATE TABLE Locked (code INTEGER PRIMARY KEY, note_code INTEGER, FOREIGN KEY (note_code) REFERENCES Note (code) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS Note");
        database.execSQL("DROP TABLE IF EXISTS Favorite");
        database.execSQL("DROP TABLE IF EXISTS Locked");
    }

}
