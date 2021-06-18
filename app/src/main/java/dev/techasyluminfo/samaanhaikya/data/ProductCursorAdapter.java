package dev.techasyluminfo.samaanhaikya.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cursoradapter.widget.CursorAdapter;

import dev.techasyluminfo.samaanhaikya.R;
import dev.techasyluminfo.samaanhaikya.data.ProductContract.*;
public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView name=view.findViewById(R.id.product_name);
        TextView supplier=view.findViewById(R.id.supplier_name);
        TextView price=view.findViewById(R.id.product_price);
        TextView quantity=view.findViewById(R.id.product_quantity);
        name.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME)));
        supplier.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_SUPPLIER)));
        price.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE))));
        quantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY))));


    }
}
