package dev.techasyluminfo.samaanhaikya;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import dev.techasyluminfo.samaanhaikya.data.ProductCursorAdapter;
import dev.techasyluminfo.samaanhaikya.data.ProductDbHelper;
import dev.techasyluminfo.samaanhaikya.data.ProductContract.*;
import dev.techasyluminfo.samaanhaikya.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor> {
    ProductDbHelper productDbHelper;
    private ActivityHomeBinding binding;


    ProductCursorAdapter productCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        productDbHelper=new ProductDbHelper(this);

        productCursorAdapter=new ProductCursorAdapter(this,null);
        binding.listProduct.setAdapter(productCursorAdapter);


        LoaderManager.getInstance(this).initLoader(1,null,this);
       binding.fabAddProductBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(HomeActivity.this,EditProductActivity.class));
           }
       });
       binding.listProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
               Uri itemUri=Uri.withAppendedPath(ProductEntry.CONTENT_URI,String.valueOf(id));
               Intent intent=new Intent(HomeActivity.this,EditProductActivity.class);
               intent.setData(itemUri);
               startActivity(intent);
           }
       });

    }

    public void insertData(){
        SQLiteDatabase db=productDbHelper.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(ProductEntry.COLUMN_PRODUCT_NAME,getString(R.string.product_name));
        cv.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER,"Hypersonic");
        cv.put(ProductEntry.COLUMN_PRODUCT_PRICE,799);
        cv.put(ProductEntry.COLUMN_PRODUCT_QUANTITY,10);
       Uri uri=getContentResolver().insert(ProductEntry.CONTENT_URI,cv);

        if (uri!=null) Snackbar.make(binding.listProduct.getRootView(),"product added to database", Snackbar.LENGTH_LONG).show();
        else Snackbar.make(binding.getRoot(),"error in adding of product",Snackbar.LENGTH_LONG).show();
    }
    public void displayData(){
        SQLiteDatabase db=productDbHelper.getReadableDatabase();
        String[] projection={
        ProductEntry.COLUMN_PRODUCT_NAME,
        ProductEntry.COLUMN_PRODUCT_SUPPLIER,
        ProductEntry.COLUMN_PRODUCT_PRICE,
        ProductEntry.COLUMN_PRODUCT_QUANTITY};

       Cursor cursor=getContentResolver().query(ProductEntry.CONTENT_URI,projection,null
       ,null,null);

         productCursorAdapter.swapCursor(cursor);

        Snackbar.make(binding.getRoot(),"can`t display data"+cursor, Snackbar.LENGTH_LONG).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
               insertData();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntry();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllEntry() {

        long deleteRows=getContentResolver().delete(ProductEntry.CONTENT_URI,null,null);
        if(deleteRows!=0){
            Snackbar.make(binding.getRoot(),"delete completed",Snackbar.LENGTH_LONG).show();
        }
        else  Snackbar.make(binding.getRoot()," error in deleting",Snackbar.LENGTH_LONG).show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        SQLiteDatabase db=productDbHelper.getReadableDatabase();
        String[] projection={
                BaseColumns._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};


        Toast.makeText(this, ""+ProductEntry.CONTENT_URI, Toast.LENGTH_SHORT).show();
        return new CursorLoader(this,ProductEntry.CONTENT_URI,projection,null
        ,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data!=null) {
            productCursorAdapter.swapCursor(data);
            return;
        }
        Snackbar.make(binding.getRoot(),"can`t display data"+data, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }
}