package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

/**
 * Created by Maino96-10022 on 12/15/2016.
 */

public class InventoryDbHelper extends SQLiteOpenHelper
{
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "shoeStore.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 6;

    /**
     * Constructs a new instance of {@link InventoryDbHelper}.
     *
     * @param context of the app
     */
    public InventoryDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create a String that contains the SQL statement to create the shoes table
        String SQL_CREATE_SHOES_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_SHOES_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_SHOES_BRAND + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_SHOES_SIZE + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_SHOES_QUANTITY + " NUMERIC NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_SHOES_PRICE + " DECIMAL NOT NULL DEFAULT 0.0, "
                + InventoryEntry.COLUMN_SHOES_IMAGE + " TEXT );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_SHOES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
