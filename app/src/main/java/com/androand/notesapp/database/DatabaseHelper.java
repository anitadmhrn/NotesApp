package com.androand.notesapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import com.androand.notesapp.database.model.Notes;

/**
 * Created by ravi on 15/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Notes.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Notes.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }


    //INSERTING NOTES//////////////
    public long insertNote(String note) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Notes.COLUMN_NOTE, note);

        // insert row
        long id = db.insert(Notes.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }


    //READING NOTE...........
    public Notes getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Notes.TABLE_NAME,
                new String[]{Notes.COLUMN_ID, Notes.COLUMN_NOTE, Notes.COLUMN_TIMESTAMP},
                Notes.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Notes note = new Notes(
                cursor.getInt(cursor.getColumnIndex(Notes.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Notes.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Notes.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<Notes> getAllNotes() {
        List<Notes> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Notes.TABLE_NAME + " ORDER BY " +
                Notes.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Notes note = new Notes();
                note.setId(cursor.getInt(cursor.getColumnIndex(Notes.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Notes.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Notes.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }



    //uUPDATING NOTE,,,,,,,,,
    public int updateNote(Notes note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_NOTE, note.getNote());

        // updating row
        return db.update(Notes.TABLE_NAME, values, Notes.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }


    //DELETING NOTE.........
    public void deleteNote(Notes note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Notes.TABLE_NAME, Notes.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }
}