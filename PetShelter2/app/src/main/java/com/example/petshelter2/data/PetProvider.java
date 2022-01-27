package com.example.petshelter2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.petshelter2.MainActivity;
import com.example.petshelter2.data.PetContract.PetEntry;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    //Database helper object
    private PetDbHelper mDbhelper;
    /**
     * Initialize the provider and the database helper object.
     */

    /**
     * uri MATCHER CODE FOR THE CONTENT URI for the pets table
     */
    private static final int PETS = 100;
    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int PET_ID = 101;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constrictor represents the code to return for the root URI*.
     * IT'S common to use NO_Match as the input for this case
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    @Override

    public boolean onCreate() {

        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbhelper = new PetDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection,selection,selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        /*return null;*///Initial given code

        //Get readable database
        SQLiteDatabase database = mDbhelper.getReadableDatabase();

        //Thisi cursor will hold the result of the query
        Cursor cursor;

        //Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        Log.i("PetProvider", "URI id is " + uri.toString());
        switch (match) {
            case PETS:
                Log.i("PetProvider", "case matching PET satisfied");
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TODO: Perform database query on pets table
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PET_ID:
                Log.i("PetProvider", "case matching PET_ID satisfied");
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId((uri)))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }
        //Set notification URI on the Cursor
        //So we know what content URI the Cursor was created for.
        //If the data at this URI changed, then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        //This is so that the listener which in our case is a catalog activity that's
        //attached to this resolver will automatically be notified.   Next parameter is a URI which is the content URI we want to watch


        return cursor;

    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
//        return null;//in sample code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);

            default:
                throw new IllegalArgumentException("Insertion is not supported " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);

        if (name.equals(""))//we cannot write it name==null as the edittext by default sets it to empty string
        {
//            Context c = getContext();
//            Toast.makeText(c, "Length of name is 0", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Pet requires a name");
        }


        Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");

        }
        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }
        // No need to check the breed, any value is valid (including null).
        // TODO: Insert a new pet into the pets database table with the given ContentValues
        /* We need to continue walking down the diagram and get a database object, and then do the insertion.*/
        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        /*Once we have a database object, we can call the insert() method on it, passing in the pet table name and the ContentValues object. The return value is the ID of the new row that was just created, in the form of a long data type (which can store numbers larger than the int data type).*/
        long id = database.insert(PetEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri,null);//Second param is the otional contentResolver param. Content observer is meant as class that receives callbacks or changes to the content. by default if we pass null here, cursor adapter will be notified. Therefore the loader callbacks will still be automatically triggered
        // // Once we know the ID of the new row in the table,return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        /*return 0;*/
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};/*Converts the last path segment to a long.

This supports a common convention for content URIs where an ID is stored in the last segment.

*/
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported");
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected pets in the pets database table with the given ContentValues

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
            if (name.length() == 0) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }
        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }
        // No need to check the breed, any value is valid (including null).


        // TODO: Return the number of rows that were affected

//        return 0;
        //Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbhelper.getWritableDatabase();

       /* //Returns the number of database rows affected but the update statement
        return database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);*///Used before writing below statements

        //Perform the update on thr database and get the number of rows affected
        int rowsUpdated=database.update(PetEntry.TABLE_NAME,values,selection,selectionArgs);
        //If 1 or more rows were updated,then notify all listeners that thedata at the given URI has changed
        if(rowsUpdated!=0)
        {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        // Get writeable database
//        SQLiteDatabase database = mDbhelper.getWritableDatabase();
//
//        final int match = sUriMatcher.match(uri);
//        switch (match) {
//            case PETS:
//                // Delete all rows that match the selection and selection args
//                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
//            case PET_ID:
//                // Delete a single row given by the ID in the URI
//                selection = PetEntry._ID + "=?";
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
//                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
//            default:
//                throw new IllegalArgumentException("Deletion is not supported for " + uri);
//
//        }
        // Get writeable database
        SQLiteDatabase database = mDbhelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args

            rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            break;
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    /**
     * The purpose of this method is to return a String that describes the type of the data stored at the input Uri. This String is known as the MIME type, which can also be referred to as content type.
     */
    @Override
    public String getType(Uri uri) {
//        return null;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }

    }
}
