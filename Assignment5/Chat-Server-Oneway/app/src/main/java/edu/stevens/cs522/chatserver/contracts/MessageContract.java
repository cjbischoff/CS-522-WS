

package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    public static final String SENDER_ID = "sender_id";

    // TODO remaining columns in Messages table

    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        Log.i("chatserver:MessageContract", "getMessageText");

        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        Log.i("chatserver:MessageContract", "putMessageText");

        out.put(MESSAGE_TEXT, messageText);
    }

    private static int timestampColumn = -1;

    public static long getTimestamp(Cursor cursor) {
        Log.i("chatserver:MessageContract", "getTimestamp");

        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getLong(timestampColumn);
    }

    public static void putTimestamp(ContentValues out, Long timestamp) {
        Log.i("chatserver:MessageContract", "putTimestamp");

        out.put(TIMESTAMP, timestamp);
    }

    // TODO remaining getter and putter operations for other columns

    private static int senderColumn = -1;

    public static String getSender(Cursor cursor) {
        Log.i("chatserver:MessageContract", "getSender");

        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putSender(ContentValues out, String sender) {
        Log.i("chatserver:MessageContract", "putSenderId");

        out.put(SENDER, sender);
    }

    private static int idColumn = -1;

    public static long getId(Cursor cursor) {
        Log.i("chatserver:MessageContract", "getId");

        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(_ID);
        }
        return Long.parseLong(cursor.getString(idColumn));
    }

    private static int senderIdColumn = -1;

    public static long getSenderId(Cursor cursor) {
        Log.i("chatserver:MessageContract", "putSenderId");

        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDER_ID);
        }
        return Long.parseLong(cursor.getString(senderIdColumn));
    }

    public static void putSenderId(ContentValues out, long senderId) {
        Log.i("chatserver:MessageContract", "putSenderId");

        out.put(SENDER_ID, senderId);
    }
}
