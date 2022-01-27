package com.example.petshelter2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

//We make the class final since we don't need to extend this class
public final class PetContract {
    public static final String CONTENT_AUTHORITY="com.example.android.pets";

    //we concatenate the CONTENT_AUTHORITY constant with the scheme “content://”
    // we will create the BASE_CONTENT_URI which will be shared by every URI associated with
    // PetContract:
    /*"content://" + CONTENT_AUTHORITY
To make this a usable URI, we use the parse method which takes in a URI string and returns a Uri.*/

    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);

    //The constants stores the path for each of the tables which will be appended to the base
    public static final String PATH_PETS = "pets";
    //To prevent someone from accidently instantiating the contract class,
    //give it an empty constructor
    private PetContract()
    {

    }
    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class PetEntry implements BaseColumns{
        /**Name of the database table for pets*/
        public final static String TABLE_NAME="pets";
        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID=BaseColumns._ID;
        /**
         * Name of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PET_NAME ="name";
        /**
         * Breed of the pet.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PET_BREED = "breed";
        /**
         * Gender of the pet.
         *
         * The only possible values are {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PET_GENDER = "gender";
        /**
         * Weight of the pet.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PET_WEIGHT = "weight";

        /**
         * Possible values for the gender of the pet.
         */
        public static final int GENDER_UNKNOWN=0;
        public static final int GENDER_MALE=1;
        public static final int GENDER_FEMALE=2;

        /** inside each of the Entry classes in the contract, we create
         * a full URI for the class as a constant called CONTENT_URI.
         * The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
         * (which contains the scheme and the content authority) to the
         * path segment*/
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */

        /**
         * The MIMEtype of the {@link #CONTENT_URI} for a list of pets
         * */
        public static final String CONTENT_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;


        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }
}
