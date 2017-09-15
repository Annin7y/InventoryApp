package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Maino96-10022 on 12/15/2016.
 */

public class InventoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_SHOES = "shoes";

    /**
     * Inner class that defines constant values for the shoes database table.
     * Each entry in the table represents a single pair of shoes.
     */
    public static final class InventoryEntry implements BaseColumns {

        /**
         * To make this a usable URI, we use the parse method which takes in a URI string and returns a Uri.
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SHOES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of shoes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a pair of shoes.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOES;
        /**
         * Name of database table for shoes
         */
        public final static String TABLE_NAME = "shoes";

        /**
         * Unique ID number for the shoes table(only for use in the database table). Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Shoe style. Type: TEXT
         */
        public final static String COLUMN_SHOES_NAME = "name";

        /**
         * Shoe brand. Type: TEXT
         */
        public final static String COLUMN_SHOES_BRAND = "brand";

        /**
         * Shoe size. The only possible values are {@link #SIZE_UNKNOWN},{@link #SIZE_FIVE}, {@link #SIZE_SIX}, {@link #SIZE_SEVEN},
         * {@link #SIZE_EIGHT},{@link #SIZE_NINE}, {@link #SIZE_TEN}, {@link #SIZE_ELEVEN} or {@link #SIZE_TWELVE}. Type: INTEGER
         */
        public final static String COLUMN_SHOES_SIZE = "size";

        /**
         * Shoe quantity. Type: NUMERIC
         */
        public final static String COLUMN_SHOES_QUANTITY = "quantity";

        /**
         * Shoe quantity. Type: DOUBLE
         */
        public final static String COLUMN_SHOES_PRICE = "price";

        /**
         * Shoe image. Type: TEXT
         */
        public final static String COLUMN_SHOES_IMAGE = "image";

        /**
         * Possible values for the size of the shoes.
         */
        public static final int SIZE_UNKNOWN = 0;
        public static final int SIZE_FIVE = 1;
        public static final int SIZE_SIX = 2;
        public static final int SIZE_SEVEN = 3;
        public static final int SIZE_EIGHT = 4;
        public static final int SIZE_NINE = 5;
        public static final int SIZE_TEN = 6;
        public static final int SIZE_ELEVEN = 7;
        public static final int SIZE_TWELVE = 8;

        public static boolean isValidSize(int size) {
            if (size == SIZE_UNKNOWN || size == SIZE_FIVE || size == SIZE_SIX || size == SIZE_SEVEN || size == SIZE_EIGHT ||
                    size == SIZE_NINE || size == SIZE_TEN || size == SIZE_ELEVEN || size == SIZE_TWELVE) {
                return true;
            }
            return false;
        }
    }
}