package pioneers.safwat.myroute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmad on 09/12/2016.
 */

public class MyprojectHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "mroutes";

    // Contacts table name
    public static final String TABLE_ROUTES = "route2";

    // Contacts Table Columns names
    public static final String KEY_ID ="id";
    public static final String KEY_NAME ="routename";
    public static final String KEY_ROUTE ="path";
    public static final String KEY_DIST ="distance";
    public static final String KEY_SIZE ="size";

    public MyprojectHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {


        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_ROUTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"+ KEY_ROUTE + " TEXT,"+ KEY_DIST + " TEXT,"+ KEY_SIZE + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);

    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
        // Create tables again
        onCreate(sqLiteDatabase);
    }
    // Adding new contact

    public void addroute(RouteDB route) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, route.getroutename()); // Contact Name
        values.put(KEY_ROUTE, route.getroutepath());
        values.put(KEY_DIST, route.getroutedist());
        values.put(KEY_SIZE, route.getroutesize());
        // Inserting Row
        db.insert(TABLE_ROUTES, null, values);
        db.close(); // Closing database connection
    }

    public RouteDB getroute(String routenamee) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ROUTES, new String[]{KEY_ID,
                        KEY_NAME,KEY_ROUTE,KEY_DIST,KEY_SIZE},KEY_NAME + "=?",
                new String[]{String.valueOf(routenamee)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        RouteDB route = new RouteDB(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_ROUTE)),
                cursor.getString(cursor.getColumnIndex(KEY_DIST)),
                cursor.getString(cursor.getColumnIndex(KEY_SIZE)));
        return route;
    }
    public List<String> getAllroutes() {
        List<String> routeList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ROUTES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

             //   RouteDB route = new RouteDB();
              //  route.setrouteid(Integer.parseInt(cursor.getString(0)));
          //      route.setroutename(cursor.getString(1));
            //    route.setroutepath(cursor.getString(2));
           //     route.setroutedist(cursor.getString(3));
                // Adding contact to list
            //    routeList.add(route);
                routeList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // return contact list
        return routeList;
    }

    public String updateroute(RouteDB route) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, route.getroutename()); // Contact Name
        values.put(KEY_ROUTE, route.getroutepath());
        values.put(KEY_DIST, route.getroutedist());
        values.put(KEY_SIZE, route.getroutesize());
        // updating row
        return String.valueOf(db.update(TABLE_ROUTES, values,KEY_NAME+ " = ?",
                new String[]{String.valueOf(route.getroutename())}));
    }
    // Deleting single contact

    public void deleteroute(RouteDB route) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROUTES,KEY_NAME + " = ?",
                new String[]{String.valueOf(route.getroutename())});
        db.close();
    }
}
