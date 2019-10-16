package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by dduggan.
 */

public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    // TODO define column names, getters for cursors, setters for contentvalues

    public static final String NAME = "name";

    public static final String TIMESTAMP = "timestamp";

    public static final String ADDRESS = "address";

    private static final String _ID = "_id";

    private static int idColumn = -1;

    private static int nameColumn = -1;

    private static int timestampColumn = -1;

    private static int addressColumn = -1;


    public static String getName(Cursor cursor) {
        Log.i("chatserver:PeerContract", "getName");

        if (nameColumn < 0) {
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues out, String name) {
        Log.i("chatserver:PeerContract", "putName");

        out.put(NAME, name);
    }



    public static long getTimestamp(Cursor cursor) {
        Log.i("chatserver:PeerContract", "getTimestamp");

        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getLong(timestampColumn);
    }

    public static void putTimestamp(ContentValues out, long timestamp) {
        Log.i("chatserver:PeerContract", "putTimestamp");

        out.put(TIMESTAMP, timestamp);
    }


    public static byte[] getAddress(Cursor cursor) {
        Log.i("chatserver:PeerContract", "getAddress");

        if (addressColumn < 0) {
            addressColumn = cursor.getColumnIndexOrThrow(ADDRESS);
        }
        return cursor.getBlob(addressColumn);
    }

    public static void putAddress(ContentValues out, byte[] address) {
        Log.i("chatserver:PeerContract", "putAddress");

        out.put(ADDRESS, address);
    }


    public static long getId(Cursor cursor) {
        Log.i("chatserver:PeerContract", "getId");

        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(_ID);
        }
        return Long.parseLong(cursor.getString(idColumn));
    }


}
