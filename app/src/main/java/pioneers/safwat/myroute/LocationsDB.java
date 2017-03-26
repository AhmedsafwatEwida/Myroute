package pioneers.safwat.myroute;

/**
 * Created by Ahmad on 16/01/2017.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LocationsDB extends SQLiteOpenHelper{

    /** Database name */
    private static final String LOCATIONS_DATABASE_NAME = "locationroute";

    /** Version number of the database */
   public static final int LOCATION_DATABASE_VERSION = 1;

    /** Field 1 of the table locations, which is the primary key */
    public static final String FIELD_ROW_ID = "_id";

    /** Field 2 of the table locations, stores the latitude */
    public static final String FIELD_LAT = "lat";

    /** Field 3 of the table locations, stores the longitude*/
    public static final String FIELD_LNG = "lng";

    /** Field 4 of the table locations, stores the zoom level of map*/
    public static final String FIELD_NAME = "nam";

    /** A constant, stores the the table name */
    public static final String MY_DATABASE_TABLE = "rlocations";

    /** An instance variable for SQLiteDatabase */
    private SQLiteDatabase mDB;


    /** Constructor */
    public LocationsDB(Context context) {
        super(context, LOCATIONS_DATABASE_NAME, null, LOCATION_DATABASE_VERSION);
        this.mDB = getWritableDatabase();
    }


    /** This is a callback method, invoked when the method getReadableDatabase() / getWritableDatabase() is called
     * provided the database does not exists
     * */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + MY_DATABASE_TABLE + "(" +
                FIELD_ROW_ID + " integer primary key autoincrement," +
                FIELD_NAME +" text," +
                FIELD_LNG + " double," +
                FIELD_LAT + " double" +")";
        db.execSQL(sql);
    }

    /** Inserts a new location to the table locations */
    public long insert(ContentValues contentValues){
        long rowID = mDB.insert(MY_DATABASE_TABLE, null, contentValues);
        return rowID;
    }
    /** Deletes all locations from the table */
    public int del(){
        int cnt = mDB.delete(MY_DATABASE_TABLE, null , null);
        return cnt;
    }

    /** Returns all the locations from the table */
    public Cursor getAllLocations(){
        return mDB.query(MY_DATABASE_TABLE, new String[] { FIELD_ROW_ID,FIELD_NAME,FIELD_LNG,FIELD_LAT  } , null, null, null, null, null);
    }
    public Cursor getnationality(String nationality){
        this.mDB=getReadableDatabase();
        return mDB.query(MY_DATABASE_TABLE, new String[] { FIELD_ROW_ID,FIELD_NAME,FIELD_LNG,FIELD_LAT  },FIELD_NAME+"like?",
                new String[] { nationality }, null, null, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
