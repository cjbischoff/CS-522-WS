package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.MessageContract;


/**
 * Created by dduggan.
 */

public class Message implements Parcelable, Persistable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Cursor cursor) {
        // TODO
        messageText = MessageContract.getMessageText(cursor);
        timestamp = new Date(MessageContract.getTimestamp(cursor));
        sender = MessageContract.getSender(cursor);
    }

    public Message(Parcel in) {
        // TODO
        this.id = in.readLong();
        this.messageText = in.readString();
        this.timestamp = new Date(in.readLong());
        this.sender = in.readString();
        this.senderId = in.readLong();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        MessageContract.putMessageText(out, messageText);
        MessageContract.putTimestamp(out, timestamp.getTime());
        MessageContract.putSender(out, sender);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeLong(this.id);
        dest.writeString(this.messageText);
        dest.writeLong(this.timestamp.getTime());
        dest.writeString(this.sender);
        dest.writeLong(this.senderId);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            // TODO
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            // TODO
            return new Message[size];
        }

    };

}

