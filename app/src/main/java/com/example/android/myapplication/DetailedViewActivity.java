package com.example.android.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapplication.data.BookContract;
import com.example.android.myapplication.data.BookContract.BookEntry;

public class DetailedViewActivity extends AppCompatActivity {

    TextView nameTextView;
    TextView priceTextView;
    TextView quantityTextView;
    TextView supplierNameTextView;
    TextView supplierPhoneTextView;
    Button minusButton;
    Button plusButton;
    Button callSupplierButton;
    Uri mNewUri;
    Uri mCurrentUri;
    Integer quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_view);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        Intent intent1 = getIntent();
        mNewUri = intent1.getData();
        nameTextView = (TextView) findViewById(R.id.detailed_view_book_name);
        priceTextView = (TextView) findViewById(R.id.detailed_view_book_price);
        quantityTextView = (TextView) findViewById(R.id.detailed_view_quantity);
        supplierNameTextView = (TextView) findViewById(R.id.detailed_view_book_supplier);
        supplierPhoneTextView = (TextView) findViewById(R.id.detailed_view_book_supplier_phone);
        minusButton = (Button) findViewById(R.id.detailed_view_minus_button);
        plusButton = (Button) findViewById(R.id.detailed_view_plus_button);
        callSupplierButton = (Button) findViewById((R.id.detailed_view_call_supplier));

        Cursor cursor = managedQuery(mCurrentUri, null, null, null, "name");

        if (cursor.moveToFirst()) {
            do {
                String bookName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
                String bookPrice = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_PRICE));
                String bookQuantity = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
                quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY));
                String supplierName = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME));
                String supplierPhone = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER));

                nameTextView.setText(" " + bookName);
                priceTextView.setText(" " + bookPrice);
                quantityTextView.setText(" " + bookQuantity);
                supplierNameTextView.setText(" " + supplierName);
                supplierPhoneTextView.setText(" " + supplierPhone);

            } while (cursor.moveToNext());

        }

        Cursor cursorNew = managedQuery(mNewUri, null, null, null, "name");

        if (cursorNew.moveToFirst()) {
            do {
                String bookName = cursorNew.getString(cursorNew.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME));
                String bookPrice = cursorNew.getString(cursorNew.getColumnIndex(BookEntry.COLUMN_PRICE));
                String bookQuantity = cursorNew.getString(cursorNew.getColumnIndex(BookEntry.COLUMN_QUANTITY));
                quantity = cursorNew.getInt(cursorNew.getColumnIndex(BookEntry.COLUMN_QUANTITY));
                String supplierName = cursorNew.getString(cursorNew.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME));
                String supplierPhone = cursorNew.getString(cursorNew.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER));

                nameTextView.setText(" " + bookName);
                priceTextView.setText(" " + bookPrice);
                quantityTextView.setText(" " + bookQuantity);
                supplierNameTextView.setText(" " + supplierName);
                supplierPhoneTextView.setText(" " + supplierPhone);

            } while (cursorNew.moveToNext());

        }

        minusButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (quantity > 0) {

                    quantity = quantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, quantity);
                    getContentResolver().update(mCurrentUri, values, null, null);

                    quantityTextView.setText(quantity.toString());

                }

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (quantity < 50) {

                    quantity = quantity + 1;

                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_QUANTITY, quantity);
                    getContentResolver().update(mCurrentUri, values, null, null);
                    quantityTextView.setText(quantity.toString());

                }

            }
        });

        callSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneTextView));
                intent.setData(Uri.parse("tel:" + supplierPhoneTextView.getText().toString()));
                startActivity(intent);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detailed, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.detailed_action_edit:

                Intent intent = new Intent
                        (DetailedViewActivity.this, EditorActivity.class);

                intent.setData(mCurrentUri);

                startActivity(intent);
                return true;
            case R.id.detailed_action_delete:
                showDeleteConfirmationDialog();

                return true;

        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();

                }

            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteBook() {
        if (mCurrentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();

            }

        }

        finish();

    }


}



