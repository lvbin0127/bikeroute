/**
 * 
 */
package com.nanosheep.bikeroute.utility;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite database helper class for storing and retrieving
 * addresses entered into the navigation boxes.
 * 
 * @author jono@nanosheep.net
 * @version Jul 2, 2010
 */
public class AddressDatabase {
		private static final int DATABASE_VERSION = 2;
		private static final String ADDRESS_TABLE_NAME = "address";
		private static final String ADDRESS = "address_string";
		private static final String ID = "id";
		private static final String ADDRESS_TABLE_CREATE =
               "CREATE TABLE " + ADDRESS_TABLE_NAME + " (" + ADDRESS + " TEXT PRIMARY KEY);";
		private static final String DATABASE_NAME = "bikeroute_db";

	   private Context context;
	   private SQLiteDatabase db;

	   private SQLiteStatement insertStmt;
	   private static final String INSERT = "insert or ignore into " 
	      + ADDRESS_TABLE_NAME + "(" + ADDRESS + ") values (?);";
	   private static final String LIKE_QUERY = ADDRESS + " LIKE ";

	   public AddressDatabase(Context context) {
	      this.context = context;
	      AddressDatabaseHelper openHelper = new AddressDatabaseHelper(this.context);
	      this.db = openHelper.getWritableDatabase();
	      this.insertStmt = this.db.compileStatement(INSERT);
	   }
	   
	   /**
	    * Add an address string to the database.
	    * @param name the address string to insert.
	    * @return
	    */

	   public void insert(String name) {
		   this.insertStmt.bindString(1, name);
		   this.insertStmt.executeInsert();
	   }

	   /**
	    * Delete all rows in the database
	    */
	   
	   public void deleteAll() {
	      this.db.delete(ADDRESS_TABLE_NAME, null, null);
	   }
	   
	   /**
	    * Query the database for strings like the one given.
	    * @param ch String to match against
	    * @return a list of strings
	    */
	   
	   public List<String> selectLike(CharSequence ch) {
		   List<String> output = new ArrayList<String>();
		   String query = "%" + ch + "%";
		   StringBuilder sb = new StringBuilder(LIKE_QUERY);
		   DatabaseUtils.appendEscapedSQLString(sb, query);
		   Cursor cursor = this.db.query(ADDRESS_TABLE_NAME, new String[] { ADDRESS }, 
			        sb.toString(), null, null, null, ADDRESS + " desc", "10");
		   if (cursor.moveToFirst()) {
			   do {
				   output.add(cursor.getString(0)); 
			   } while (cursor.moveToNext());
		   }
		   if (cursor != null && !cursor.isClosed()) {
			   cursor.close();
		   }
		   return output;
	   }
	   
	   /**
	    * Get all addresses in the database.
	    * @return a list of all the addresses in the db.
	    */

	   public List<String> selectAll() {
	      List<String> list = new ArrayList<String>();
	      Cursor cursor = this.db.query(ADDRESS_TABLE_NAME, new String[] { ADDRESS }, 
	        null, null, null, null, ADDRESS + " desc");
	      if (cursor.moveToFirst()) {
	         do {
	            list.add(cursor.getString(0)); 
	         } while (cursor.moveToNext());
	      }
	      if (cursor != null && !cursor.isClosed()) {
	         cursor.close();
	      }
	      return list;
	   }
	   
	   /**
	    * 
	    * @author jono@nanosheep.net
	    * @version Jul 2, 2010
	    */

	   public static class AddressDatabaseHelper extends SQLiteOpenHelper {

	       AddressDatabaseHelper(Context context) {
	           super(context, DATABASE_NAME, null, DATABASE_VERSION);
	       }

	       @Override
	       public void onCreate(SQLiteDatabase db) {
	           db.execSQL(ADDRESS_TABLE_CREATE);
	   		}

	       /* (non-Javadoc)
	        * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	        */
	       @Override
	       public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	   db.execSQL("DROP TABLE IF EXISTS " + ADDRESS_TABLE_NAME);
	    	   onCreate(db);
	       }
	   }
}