package edu.stevens.cs522.chat.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<Message> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Message> creator = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = getAsyncResolver();
    }

    public void getAllMessagesAsync(IQueryListener<Message> listener) {
        // use QueryBuilder to complete this
        executeQuery(MessageContract.CONTENT_URI, null, null, null, listener);
    }

    public void persistAsync(final Message message) {
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        contentResolver.insertAsync(MessageContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri uri) {
                message.id = MessageContract.getId(uri);
            }
        });
    }

}
