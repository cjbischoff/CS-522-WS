package edu.stevens.cs522.chatserver.async;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by dduggan.
 */

public class AsyncContentResolver extends AsyncQueryHandler {

    private final String TAG = this.getClass().getCanonicalName();

    public AsyncContentResolver(ContentResolver cr) {
        super(cr);
    }

    public void insertAsync(Uri uri,
                            ContentValues values,
                            IContinue<Uri> callback) {
        Log.i("chatserver:AsyncQueryResolver", "insertAsync");

        this.startInsert(0, callback, uri, values);
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        Log.i("chatserver:AsyncQueryResolver", "onInsertComplete");

        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Uri> callback = (IContinue<Uri>) cookie;
            callback.kontinue(uri);
        }
    }

    public void queryAsync(Uri uri, String[] columns, String select, String[] selectArgs, String order, IContinue<Cursor> callback) {
        Log.i("chatserver:AsyncQueryResolver", "queryAsync");

        this.startQuery(0, callback, uri, columns, select, selectArgs, order);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        Log.i("chatserver:AsyncQueryResolver", "onQueryComplete");

        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
            callback.kontinue(cursor);
        }
    }

    public void deleteAsync(Uri uri,
                            String select, String[] selectArgs) {
        Log.i("chatserver:AsyncQueryResolver", "deleteAsync");

        IContinue<Integer> cb = new IContinue<Integer>() {
            @Override
            public void kontinue(Integer value) {
                if (value.intValue() > 0) {
                    Log.i(TAG, " successfully deleted " + value.toString() + " rows.");
                } else {
                    Log.e(TAG, " unsuccessful delete operation.");
                }
            }
        };
        this.startDelete(0, cb, uri, select, selectArgs);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        Log.i("chatserver:AsyncQueryResolver", "onDeleteComplete");

        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Integer> callback = (IContinue<Integer>) cookie;
            callback.kontinue(result);
        }
    }

    public void updateAsync(Uri uri,
                            ContentValues values,
                            String select, String[] selectArgs) {
        Log.i("chatserver:AsyncQueryResolver", "updateAsync");

        IContinue<Integer> cb = new IContinue<Integer>() {
            @Override
            public void kontinue(Integer value) {
                if (value.intValue() > 0) {
                    Log.i(TAG, " successfully updated " + value.toString() + " rows.");
                } else {
                    Log.e(TAG, " unsuccessful update operation.");
                }
            }
        };
        this.startUpdate(0, cb, uri, values, select, selectArgs);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        Log.i("chatserver:AsyncQueryResolver", "onUpdateComplete");

        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Integer> callback = (IContinue<Integer>) cookie;
            callback.kontinue(result);
        }
    }

}
