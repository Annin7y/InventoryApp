package com.example.android.inventoryapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static com.example.android.inventoryapp.data.InventoryProvider.LOG_TAG;


/**
 * Created by Maino96-10022 on 12/16/2016.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>
{
    /**
     * Identifier for the inventory data loader
     */
    private static final int EXISTING_INVENTORY_LOADER = 0;
    private static final int PICK_IMAGE_REQUEST = 0;
    private Uri mCurrentInventoryUri;
    public Uri mCurrentImageUri;
    Button orderButton;
    Button receiveButton;
    Button sellButton;
    Button imageButton;

    /**
     * EditText field to enter the name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the brand
     */
    private EditText mBrandEditText;

    /**
     * EditText field to enter the quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the price
     */
    private EditText mPriceEditText;

    /**
     * Spinner to enter the size
     */
    private Spinner mSizeSpinner;
    private ImageView mImageView;
    private TextView mImageUriText;
    private int mSize = InventoryContract.InventoryEntry.SIZE_UNKNOWN;
    private boolean mInventoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        orderButton = (Button) findViewById(R.id.order_button);
        receiveButton = (Button) findViewById(R.id.receive_button);
        sellButton = (Button) findViewById(R.id.sell_button);
        imageButton = (Button) findViewById(R.id.image_button);
        mImageView = (ImageView) findViewById(R.id.image);
        mImageUriText = (TextView) findViewById(R.id.image_uri);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating new inventory or editing an existing one.
        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        // If the intent DOES NOT contain an inventory content URI, then we know that we are
        //  creating a new inventory.
        if (mCurrentInventoryUri == null)
        {
            // This is a new inventory, so change the app bar to say "Add Inventory"
            setTitle(getString(R.string.editor_activity_title_new_inventory));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete inventory that hasn't been created yet.)
            invalidateOptionsMenu();
        }
        else
            {
            // Otherwise this is an existing inventory so change app bar to say "Edit Inventory"
            setTitle(getString(R.string.editor_activity_title_edit_inventory));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
        }

// Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_name);
        mBrandEditText = (EditText) findViewById(R.id.edit_brand_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_price_name);
        mSizeSpinner = (Spinner) findViewById(R.id.spinner_size);
        mImageView = (ImageView) findViewById(R.id.image);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBrandEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSizeSpinner.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);

        setupSpinner();

        orderButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText nameField = mNameEditText;
                String name = nameField.getText().toString();

                EditText brandField = mBrandEditText;
                String brand = brandField.getText().toString();

                EditText quantityField = mQuantityEditText;
                String quantity = quantityField.getText().toString();

                String emailOrderSummary = ("Please send " + quantity + " pairs of " + name + " by " + brand);

                //Call the intent that launches the mail client to send the email including the subject and body
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        "Shoe order " + name);
                intent.putExtra(Intent.EXTRA_TEXT, emailOrderSummary);
                if (intent.resolveActivity(getPackageManager()) != null)
                {
                    startActivity(intent);
                }
            }
        });

        receiveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int quantityReceived = Integer.parseInt(mQuantityEditText.getText().toString());

                if (quantityReceived >= 0)
                {
                    quantityReceived++;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_SHOES_QUANTITY, quantityReceived);

                    int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

                    if (rowsAffected == 0)
                    {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(EditorActivity.this, getString(R.string.editor_insert_inventory_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int quantitySold = Integer.parseInt(mQuantityEditText.getText().toString());

                if (quantitySold > 0)
                {
                    quantitySold--;
                    ContentValues values = new ContentValues();
                    values.put(InventoryContract.InventoryEntry.COLUMN_SHOES_QUANTITY, quantitySold);

                    int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

                    if (rowsAffected == 0)
                    {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(EditorActivity.this, getString(R.string.editor_insert_inventory_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openImageSelector();
            }
        });
    }

    private void setupSpinner()
    {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter sizeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_size_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        sizeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSizeSpinner.setAdapter(sizeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection))
                {
                    if (selection.equals(getString(R.string.size_five)))
                    {
                        mSize = InventoryEntry.SIZE_FIVE;
                    }
                    else if (selection.equals(getString(R.string.size_six)))
                    {
                        mSize = InventoryEntry.SIZE_SIX;
                    }
                    else if (selection.equals(getString(R.string.size_seven))) {
                        mSize = InventoryEntry.SIZE_SEVEN;
                    }
                    else if (selection.equals(getString(R.string.size_eight)))
                    {
                        mSize = InventoryEntry.SIZE_EIGHT;
                    }
                    else if (selection.equals(getString(R.string.size_nine)))
                    {
                        mSize = InventoryEntry.SIZE_NINE;
                    }
                    else if (selection.equals(getString(R.string.size_ten)))
                    {
                        mSize = InventoryEntry.SIZE_TEN;
                    }
                    else if (selection.equals(getString(R.string.size_eleven)))
                    {
                        mSize = InventoryEntry.SIZE_ELEVEN;
                    }
                    else if (selection.equals(getString(R.string.size_twelve)))
                    {
                        mSize = InventoryEntry.SIZE_TWELVE;
                    }
                    else {
                        mSize = InventoryEntry.SIZE_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                mSize = InventoryContract.InventoryEntry.SIZE_UNKNOWN;
            }
        });
    }

    /**
     * Get user input from editor and save inventory into database.
     */
    public void saveInventory()
    {
         // Read from input fields & use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String brandString = mBrandEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        String imageString = "";
        if (mCurrentImageUri != null)
        {
            imageString = mCurrentImageUri.toString();
        }

        Log.v("editor Activity",
                "Name " + nameString +
                        " Brand " + brandString +
                        " Price " + priceString +
                        " Quantity " + quantityString +
                        "image string : " + imageString);

        // Check if this is supposed to be a new inventory item
        // and check if all the fields in the editor are blank
        if (mCurrentInventoryUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(brandString) ||
                TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(imageString) || mSize == InventoryEntry.SIZE_UNKNOWN) {

            // Since no fields were modified, we can return early without creating a new inventory item.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, "Please fill all the entries.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and inventory attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_SHOES_NAME, nameString);
        values.put(InventoryEntry.COLUMN_SHOES_BRAND, brandString);
        values.put(InventoryEntry.COLUMN_SHOES_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_SHOES_QUANTITY, quantityString);
        values.put(InventoryEntry.COLUMN_SHOES_SIZE, mSize);
        values.put(InventoryEntry.COLUMN_SHOES_IMAGE, imageString);

        if (TextUtils.isEmpty(nameString))
        {
            Toast.makeText(this, getString(R.string.editor_insert_inventory_style),
                    Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(brandString))
        {
            Toast.makeText(this, getString(R.string.editor_insert_inventory_brand),
                    Toast.LENGTH_SHORT).show();
        }

        int quantity = 0;
        if (TextUtils.isEmpty(quantityString) || quantityString.equals("0"))
        {
            Toast.makeText(this, getString(R.string.editor_insert_inventory_quantity),
                    Toast.LENGTH_SHORT).show();
        }
        else
            {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(InventoryEntry.COLUMN_SHOES_QUANTITY, quantity);

        // If the price is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        double price = 0.0;
        if (!TextUtils.isEmpty(priceString))
        {
            price = Double.parseDouble(priceString);
        }
        else
            {
            Toast.makeText(this, getString(R.string.editor_insert_inventory_price),
                    Toast.LENGTH_SHORT).show();
        }
        values.put(InventoryEntry.COLUMN_SHOES_PRICE, price);

        // Determine if this is a new or existing inventory by checking if mCurrentInventoryUri is null or not
        if (mCurrentInventoryUri == null)
        {
            // This is a NEW inventory item, so insert a new inventory item into the provider,
            // returning the content URI for the new inventory item.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null)
            {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else
                {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else
            {
            // Otherwise this is an EXISTING inventory item, so update the inventory with content URI: mCurrentInventoryUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentInventoryUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentInventoryUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0)
            {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else
                {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }

    public void openImageSelector()
    {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19)
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        else
            {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK)
        {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"
            if (resultData != null) {
                mCurrentImageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mCurrentImageUri.toString());
                mImageUriText.setText(mCurrentImageUri.toString());
                mImageView.setImageBitmap(getBitmapFromUri(mCurrentImageUri));
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try
        {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        }
        catch (FileNotFoundException fne)
        {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        }
        finally
        {
            try
            {
                input.close();
            } catch (IOException ioe)
            {
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        // If this is a new inventory item, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null)
        {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save inventory to database
                saveInventory();
                // Exit activity
               // if you want to keep this activity when performing the validation, you need to move the finish()
                // method to the bottom of the saveInventory() method. It will make the validation perfect.
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If inventory hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInventoryHasChanged)
                {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {

        // If inventory hasn't changed, continue with handling back button press
        if (!mInventoryHasChanged)
        {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        // Since the editor shows all inventory attributes, define a projection that contains
        // all columns from the inventory table
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_SHOES_NAME,
                InventoryEntry.COLUMN_SHOES_BRAND,
                InventoryEntry.COLUMN_SHOES_QUANTITY,
                InventoryEntry.COLUMN_SHOES_PRICE,
                InventoryEntry.COLUMN_SHOES_SIZE,
                InventoryEntry.COLUMN_SHOES_IMAGE,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentInventoryUri,   // Query the content URI for the current inventory item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor)
    {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1)
        {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst())
        {
            // Find the columns of inventory attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SHOES_NAME);
            int brandColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SHOES_BRAND);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SHOES_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SHOES_PRICE);
            int sizeColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SHOES_SIZE);
            final int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SHOES_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String brand = cursor.getString(brandColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            int size = cursor.getInt(sizeColumnIndex);
            final String image = cursor.getString(imageColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mBrandEditText.setText(brand);
            mQuantityEditText.setText(Integer.toString(quantity));
            mPriceEditText.setText(Double.toString(price));
            mImageUriText.setText(image);

            ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Uri mLoadImageUri = Uri.parse(mImageUriText.getText().toString());
                    mImageView.setImageBitmap(getBitmapFromUri(mLoadImageUri));
                    mImageUriText.setText("");
                }
            });

            // Size is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Five... 8 is Twelve).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (size)
            {
                case InventoryEntry.SIZE_FIVE:
                    mSizeSpinner.setSelection(1);
                    break;
                case InventoryEntry.SIZE_SIX:
                    mSizeSpinner.setSelection(2);
                    break;
                case InventoryEntry.SIZE_SEVEN:
                    mSizeSpinner.setSelection(3);
                    break;
                case InventoryEntry.SIZE_EIGHT:
                    mSizeSpinner.setSelection(4);
                    break;
                case InventoryEntry.SIZE_NINE:
                    mSizeSpinner.setSelection(5);
                    break;
                case InventoryEntry.SIZE_TEN:
                    mSizeSpinner.setSelection(6);
                    break;
                case InventoryEntry.SIZE_ELEVEN:
                    mSizeSpinner.setSelection(7);
                    break;
                case InventoryEntry.SIZE_TWELVE:
                    mSizeSpinner.setSelection(8);
                    break;
                default:
                    mSizeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mBrandEditText.setText("");
        mQuantityEditText.setInputType(0);
        mPriceEditText.setText("");
        mSizeSpinner.setSelection(0); // Select "Unknown" size
        mImageUriText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener)
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the inventory item.
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this inventory item.
     */
    private void showDeleteConfirmationDialog()
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // User clicked the "Delete" button, so delete the inventory item.
                deleteInventory();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the inventory item.
                if (dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteInventory()
    {
        // Only perform the delete if this is an existing inventory item.
        if (mCurrentInventoryUri != null)
        {
            // Call the ContentResolver to delete the inventory item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentInventoryUri
            // content URI already identifies the inventory item that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0)
            {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_failed),
                        Toast.LENGTH_SHORT).show();
            }
            else
                {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_inventory_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
