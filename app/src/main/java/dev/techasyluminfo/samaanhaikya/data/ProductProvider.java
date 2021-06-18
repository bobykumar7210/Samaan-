package dev.techasyluminfo.samaanhaikya.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.techasyluminfo.samaanhaikya.data.ProductContract.*;

import static dev.techasyluminfo.samaanhaikya.data.ProductDbHelper.LOG_TAG;

public class ProductProvider extends ContentProvider {
    ProductDbHelper productDbHelper;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PRODUCTS = 100;


    private static final int PRODUCTS_ID = 101;

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {


        SQLiteDatabase db = productDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:

                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs,
                        null,
                        null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:

                return insertProduct(uri, contentValues);

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }


    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = productDbHelper.getWritableDatabase();

        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.withAppendedPath(uri, String.valueOf(id));

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                int rowDelete=db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowDelete != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowDelete;
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                int rowsDelete=db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDelete != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDelete;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }


    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = productDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = productDbHelper.getWritableDatabase();
        int rowUpdate =database.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if(rowUpdate!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // Returns the number of database rows affected by the update statement
        return rowUpdate ;
    }
}
