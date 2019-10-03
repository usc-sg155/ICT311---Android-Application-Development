package com.example.mycheckins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    FeedReaderDbHelper dbHelper;
    SQLiteDatabase db;

    public DBManager(Context c) {
        dbHelper = new FeedReaderDbHelper(c);
    }

    public void InsertNewRecord(String title, String place, String details, String date, String location, String image) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PLACE, place);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DETAILS, details);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DATE, date);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION, location);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE, image);
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);
        db.close();
    }

    public void DeleteAllRows() {
        db = dbHelper.getWritableDatabase();
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void DeleteOneRow(String title, String place, String detail) {
        db = dbHelper.getWritableDatabase();
        String[] whereArgs = {title, place, detail};
        db.delete(FeedReaderContract.FeedEntry.TABLE_NAME,  FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + "=? and " + FeedReaderContract.FeedEntry.COLUMN_NAME_PLACE + "=? and " + FeedReaderContract.FeedEntry.COLUMN_NAME_DETAILS +"=?", whereArgs);
        db.close();
    }

    public String[] GetOneRecord(String title) {
        db = dbHelper.getReadableDatabase();

        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {title};

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List itemIds = new ArrayList<>();
        String res = "";
        if (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            res += cursor.getString(1);
            res += "!@#";
            res += cursor.getString(2);
            res += "!@#";
            res += cursor.getString(3);
            res += "!@#";
            res += cursor.getString(4);
            res += "!@#";
            res += cursor.getString(5);
            res += "!@#";
            res += cursor.getString(6);
            itemIds.add(itemId);
        }
        cursor.close();
        db.close();
        if (res == "")
            return null;
        return res.split("!@#");
    }

    public String[] GetAllRecords() {
        db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PLACE,
                FeedReaderContract.FeedEntry.COLUMN_NAME_DATE,
        };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List itemIds = new ArrayList<>();
        String res = "";
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            res += cursor.getString(1);
            res += "!@#";
            res += cursor.getString(2);
            res += "!@#";
            res += cursor.getString(3);
            res += "@newline@";
            itemIds.add(itemId);
        }
        cursor.close();
        db.close();
        if (res == "")
            return null;
        return res.split("@newline@");
    }

    public final class FeedReaderContract {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private FeedReaderContract() {}

        /* Inner class that defines the table contents */
        public class FeedEntry implements BaseColumns {
            public static final String TABLE_NAME = "checkins";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_PLACE = "place";
            public static final String COLUMN_NAME_DETAILS = "details";
            public static final String COLUMN_NAME_DATE = "date";
            public static final String COLUMN_NAME_LOCATION = "location";
            public static final String COLUMN_NAME_IMAGE = "image";
        }
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLACE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DETAILS + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
     /*   public void onDelete(SQLiteDatabase db) {
            db.execSQL(SQL_DELETE_ENTRIES);
        }*/
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }


}
