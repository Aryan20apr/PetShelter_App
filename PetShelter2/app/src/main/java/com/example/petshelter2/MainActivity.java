package com.example.petshelter2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import androidx.loader.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.example.petshelter2.data.PetContract.PetEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //Arbitrarily choosen integer loader constant for the loader
    private static final int PET_LOADER=0;

    //Adapter used in all callback methods
    PetCursorAdapter mCursorAdapter;
    //private PetDbHelper mDbHelper;Not needed after using ContentProvider

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("MainActivity","IN ONCREATE() OF MAIN ACTIVITY");
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MainActivity","Inside onClick() of fab and starting EditorActivity");
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });



        //mDbHelper = new PetDbHelper(this);

        //Find the listview which will be populated with loaded data
        ListView petListView=findViewById(R.id.text_view_pet);
        //Find and set empty view on the listview so that it only shows when the list has 0 items
        View emptyView=findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        //Set up an Adapter to create a list item for each row of pet data in the Cursor.
        //There is no pet data yet (until the loader finishes) so pass in null for the Cursor.

        mCursorAdapter=new PetCursorAdapter(this,null);
       petListView.setAdapter(mCursorAdapter);

       //Setup item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) //long param is the id of the item. It is useful since we will be generating the URI for the pet in order to pass along an intent extra. Therefore needed to identify the item we clicked on
            {
                Log.v("MainActivity","Inside onItemClick() of of setOnItemClickListener ");
                //Create a new intent to go to {@Link EditorActivity}
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);

                //From the the content URI that represents the specific pet that was clicked on,
                //by appending the "id" (passed as input to this method) onto the {@Link PetEntry#Content_URI).
                //For example, the URI would be "content://com.example.android.pets/pets/2"
                //if the pet with ID 2 was clicked on.
                Uri currentUri= ContentUris.withAppendedId(PetEntry.CONTENT_URI,id);
                // Set the URI on the data firld of the intent
                intent.setData(currentUri);
                //Launch the {@Link EditorActivity} to display the data for the current pet.
                Log.v("MainActivity","Launching EditorActivity to display data for the current pet ");
                startActivity(intent);
            }
        });

        //Kick off the loader
        LoaderManager.getInstance(this).initLoader(PET_LOADER,null,this);
        Log.v("MainActivity","LoadManager started ");

    }

//    @Override
//    protected void onStart() {
//
//        super.onStart();
//        displayDatabaseInfo();
//    }Used before using CursorLoader

    private void insertPet() {
//        //Gets the data repository in write mode
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//// Create a ContentValues object where column names are the keys,
//        // and Toto's pet attributes are the values. Here Dummy Values are used for demonstration.
//        ContentValues values = new ContentValues();
//        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
//        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
//        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
//        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
//
//        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);
//        Log.i("MainActivity", "new raw ID " + newRowId); Till here used before using ContentProvider
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Log.v("MainActivity","Inside InsertPet of MainActivity and calling insert() ,method of ContentResolver");
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }


    /**
     * This method gets called automatically when thw MainActivity is first
     * being created and displays the items defined in menu.xml
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        Log.v("MainActivity","Inflated the menu options from the menu_catalog.xml in onCCreateOptionsMenu");
        return true;
    }

    /**
     * This method is used to add behaviour to each of these menu items.
     * It is called when the user clciks on a menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                Log.v("MainActivity","Inside onOptionsItemSelected() of MainActivity and case of Dummy data satisfied");
                insertPet();
                //displayDatabaseInfo();Used before implementing Cursor Loader
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                Log.v("MainActivity","Inside onOptionsItemSelected() of MainActivity and case of Delete all entries satisfied");
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
//    private void displayDatabaseInfo() {
//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        // PetDbHelper mDbHelper = new PetDbHelper(this);
//
//       /* // Create and/or open a database to read from it
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//       *//* // Perform this raw SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
//        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);*//*
//
//        String projection[] = {PetEntry._ID, PetEntry.COLUMN_PET_NAME, PetEntry.COLUMN_PET_BREED, PetEntry.COLUMN_PET_GENDER, PetEntry.COLUMN_PET_WEIGHT};
//
//        Cursor cursor = db.query(PetEntry.TABLE_NAME, projection, null, null, null, null, null);*///The Lines commented within multiline comments till here communicated with the database directly . But now we us ContentResolver
//
//        String projection[] = {PetEntry._ID, PetEntry.COLUMN_PET_NAME, PetEntry.COLUMN_PET_BREED, PetEntry.COLUMN_PET_GENDER, PetEntry.COLUMN_PET_WEIGHT};
//        //The query method calls the PetProvider query() method and receive a cursor result
//        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projection, null, null, null, null);
////        try {
////            // Display the number of rows in the Cursor (which reflects the number of rows in the
////            // pets table in the database).
////            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
////            displayView.setText("Number of rows in pets database table: " + cursor.getCount());
////        } finally {
////            // Always close the cursor when you're done reading from it. This releases all its
////            // resources and makes it invalid.
////            cursor.close();
////        }
//        /*TextView displayView = findViewById(R.id.text_view_pet);
//        try {
//
//            // Create a header in the Text View that looks like this:
//            //
//            // The pets table contains <number of rows in Cursor> pets.
//            // _id - name - breed - gender - weight
//            //
//            // In the while loop below, iterate through the rows of the cursor and display
//            // the information from each column in this order.
//            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
//            displayView.append(PetEntry._ID + " - " +
//                    PetEntry.COLUMN_PET_NAME + " - " +
//                    PetEntry.COLUMN_PET_BREED + " - " +
//                    PetEntry.COLUMN_PET_GENDER + " - " +
//                    PetEntry.COLUMN_PET_WEIGHT + "\n");
//
//            // Figure out the index of each column
//            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
//            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
//            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
//            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
//            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
//
//            // Iterate through all the returned rows in the cursor
//            while (cursor.moveToNext()) {
//                // Use that index to extract the String or Int value of the word
//                // at the current row the cursor is on.
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentName = cursor.getString(nameColumnIndex);
//                String currentBreed = cursor.getString(breedColumnIndex);
//                int currentGender = cursor.getInt(genderColumnIndex);
//                int currentWeight = cursor.getInt(weightColumnIndex);
//                // Display the values from each column of the current row in the cursor in the TextView
//                displayView.append(("\n" + currentID + " - " + currentName + " - " +
//                        currentBreed + " - " +
//                        currentGender + " - " +
//                        currentWeight));
//
//
//            }
//        } finally {
//            cursor.close();
//        }
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//        *///Used before using cursor adapter
//
//
//        //Find the ListView which will be populated with the pet data
//        ListView petListView=findViewById(R.id.text_view_pet);
//        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
//        View emptyView = findViewById(R.id.empty_view);
//        petListView.setEmptyView(emptyView);
//
//        //Setup an Adapter to create a list item for each of pet data int the Cursor
//        PetCursorAdapter adapter=new PetCursorAdapter(this,cursor);
//        //Atach the adapter to the list view
//        petListView.setAdapter(adapter);
//    }//Commented out after inplementing CursorLoader

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * <p>This will always be called from the process's main thread.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        //Define a projection taht specifies the columns from the table we care about
        String[] projection={PetEntry._ID,PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED};
        //This loader will execute the ContentProvider'a query method on a background thread
        Log.v("MainActivity","Inside onCreateLoader() of MainActivity and creating new CursorLoader object which call ContentProvider's query method on a background thread");
        return new CursorLoader(this,//Parent activity context
                PetEntry.CONTENT_URI,//Provider content URI to query
                projection, //Columns to include in the resulting Cursor
                null, //No selection clause
                null,//No selection arguments
                null);//Default sort order
        //Creates a fully-specified CursorLoader. See ContentResolver.query() for documentation on the meaning of the parameters. These will be passed as-is to that call.

    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     *
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     *
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * <p>This will always be called from the process's main thread.
     *  @param loader The Loader that has finished.
     *
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    //Update {@link PetCursorAdapter } with this new Cursor containing updated pet data
        Log.v("MainActivity","Inside onLoadFinished() of MainActivity and calling mCursorAdapter.swapCursor(data)");
        mCursorAdapter.swapCursor(data);}

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    //Callback called when data needs to be deleted
        Log.v("MainActivity","Inside onLoadReset() of MainActivity and calling mCursorAdapter.swapCursor(null)");
        mCursorAdapter.swapCursor(null);
    }
}