package com.example.android.inventoryapp.adapters;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.provider.Settings.Global.getString;
import static com.example.android.inventoryapp.R.string.sale;

/**
 * Created by Maino96-10022 on 12/22/2016.
 */

public class InventoryCursorAdapter extends CursorAdapter
{
    public InventoryCursorAdapter(Context context, Cursor c)
    {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final int itemInventoryId = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryContract.InventoryEntry._ID));

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView imageView = (ImageView) view.findViewById(R.id.list_item_image);

        // Find the columns of inventory attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SHOES_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SHOES_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SHOES_PRICE);
        int imageColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_SHOES_IMAGE);

        // Read the  attributes from the Cursor for the current inventory attribute
        String inventoryName = cursor.getString(nameColumnIndex);
        String inventoryQuantity = cursor.getString(quantityColumnIndex);
        String inventoryPrice = cursor.getString(priceColumnIndex);
        String inventoryImage = cursor.getString(imageColumnIndex);

        // Update the TextViews with the attributes for the current inventory
        nameTextView.setText(inventoryName);
        quantityTextView.setText(inventoryQuantity);
        priceTextView.setText(inventoryPrice);

         // If the inventory style is empty string or null, then use some default text
         // that says "Style not specified", so the TextView isn't blank.
        if (TextUtils.isEmpty(inventoryName))
        {
            inventoryName = context.getString(R.string.name_specify);
        }

        if (inventoryImage != null)
        {
            imageView.setImageURI(Uri.parse(inventoryImage));
        }

        // Update the TextViews with the attributes for the current inventory item
        nameTextView.setText(inventoryName);

        //Subtract one when the sale button is clicked
        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int quantitySell = Integer.parseInt(quantityTextView.getText().toString());

                if (quantitySell > 0)
                {
                    quantitySell--;

                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_SHOES_QUANTITY, quantitySell);

                    ContentResolver resolver = view.getContext().getContentResolver();
                    Uri uri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, itemInventoryId);
                    resolver.update(uri, values, null, null);
                }
                else
                    {
                    Toast.makeText(context, "No quantity for sale. Please order more!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


