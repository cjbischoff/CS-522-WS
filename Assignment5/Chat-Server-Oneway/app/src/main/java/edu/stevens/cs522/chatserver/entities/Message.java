package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chatserver.contracts.MessageContract;

import static edu.stevens.cs522.chatserver.contracts.MessageContract.TIMESTAMP;

/**
 * Created by dduggan.
 */

//public class Message implements Parcelable, Persistable {
public class Message implements Parcelable {


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
        timestamp = DateUtils.getDate(cursor, 0);
        sender = MessageContract.getSender(cursor);
        senderId = MessageContract.getSenderId(cursor);
    }

    public Message(Parcel in) {
        // TODO
        id = in.readLong();
        messageText = in.readString();
        timestamp = DateUtils.readDate(in);
        sender = in.readString();
        senderId = in.readLong();
    }


    public void writeToProvider(ContentValues out) {
        // TODO
        MessageContract.putMessageText(out, this.messageText);
        DateUtils.putDate(out, TIMESTAMP, this.timestamp);
        MessageContract.putSender(out, this.sender);
        MessageContract.putSenderId(out, this.senderId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeLong(id);
        dest.writeString(messageText);
        DateUtils.writeDate(dest, timestamp);
        dest.writeString(sender);
        dest.writeLong(senderId);
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

