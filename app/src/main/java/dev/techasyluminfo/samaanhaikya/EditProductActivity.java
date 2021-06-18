package dev.techasyluminfo.samaanhaikya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import dev.techasyluminfo.samaanhaikya.data.ProductContract.*;
import dev.techasyluminfo.samaanhaikya.data.ProductDbHelper;
import dev.techasyluminfo.samaanhaikya.databinding.ActivityEditProductBinding;
import dev.techasyluminfo.samaanhaikya.databinding.ActivityHomeBinding;

import static android.widget.Toast.LENGTH_LONG;

public class EditProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ActivityEditProductBinding binding;
    ProductDbHelper productDbHelper;
    Uri itemUri;
    private boolean mPetHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        productDbHelper = new ProductDbHelper(this);

        //checking for update or calling for insert data
        Intent intent = getIntent();
        itemUri = intent.getData();
        if (itemUri != null) {

            setTitle("Edit product");
            //get item data
            LoaderManager.getInstance(this).initLoader(1,null,EditProductActivity.this);

        } else {
            setTitle("Add a product");
            invalidateOptionsMenu();//trigger the onPrepareMenuOption method

        }

        binding.nameEditText.setOnTouchListener(mTouchListener);
        binding.supplierEditText.setOnTouchListener(mTouchListener);
        binding.priceEditText.setOnTouchListener(mTouchListener);
        binding.quantityEditText.setOnTouchListener(mTouchListener);



    }
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public Boolean insertAndUpdateData() {

        String name = binding.nameEditText.getText().toString().trim();
        String supplier = binding.supplierEditText.getText().toString().trim();
        String price = binding.priceEditText.getText().toString().trim();
        String quantity = binding.quantityEditText.getText().toString().trim();
        if (name.isEmpty() || supplier.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "fill all the detail", LENGTH_LONG).show();

            return false;
        }
        if (quantity.isEmpty()) {
            quantity = "0";
        }

        ContentValues cv = new ContentValues();
        cv.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        cv.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplier);
        cv.put(ProductEntry.COLUMN_PRODUCT_PRICE, Integer.valueOf(price));
        cv.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.valueOf(quantity));
        if(itemUri!=null){
            long rowUpdate=getContentResolver().update(itemUri,cv,null,null);
            if(rowUpdate!=0) {
                Toast.makeText(this, "update complete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "error with updating pet", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Uri uri = getContentResolver().insert(ProductEntry.CONTENT_URI, cv);

            if (uri != null)
                Toast.makeText(this, "product added to database", LENGTH_LONG).show();
            else
                Toast.makeText(this, "error in adding of product", LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    public Boolean saveProduct() {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_save:
               boolean complete= insertAndUpdateData();
               if (complete) {
                   finish();
                   return true;
               }
               else return false;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete:
               showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditProductActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (itemUri== null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        LoaderManager.getInstance(this).destroyLoader(1);

        long deleteRows=getContentResolver().delete(itemUri,null,null);
        if(deleteRows!=0){
            Toast.makeText(this,"delete completed",LENGTH_LONG).show();
        }
        else  Toast.makeText(this," error in deleting",LENGTH_LONG).show();
    }
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                BaseColumns._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this, itemUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME);
            int supplierIndex=cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int priceIndex=cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityIndex=cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            cursor.moveToFirst();
            String name=cursor.getString(nameIndex);
            String supplier=cursor.getString(supplierIndex);
            int price=cursor.getInt(priceIndex);
            int quantity=cursor.getInt(quantityIndex);
            binding.nameEditText.setText(name);
            binding.supplierEditText.setText(supplier);
            binding.priceEditText.setText(String.valueOf(price));
            binding.quantityEditText.setText(String.valueOf(quantity));

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            binding.nameEditText.setText("");
            binding.supplierEditText.setText("");
            binding.priceEditText.setText("");
            binding.quantityEditText.setText("");
            loader.reset();
    }
}