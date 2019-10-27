package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Persistable;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;

    public int port;

    public Peer() {
    }

    public Peer(Cursor cursor) {
        // TODO
        id = PeerContract.getId(cursor);
        name = PeerContract.getName(cursor);
        timestamp = new Date(PeerContract.getTimestamp(cursor));
        try {
            address = InetAddress.getByAddress(PeerContract.getAddress(cursor));
        } catch (Exception exception) {
            address = null;
        }
    }

    public Peer(Parcel in) {
        Log.i("chatserver:Peer", "Peer");

        // TODO
        id = in.readLong();
        timestamp = new Date(in.readLong());
        address = (InetAddress) in.readValue(InetAddress.class.getClassLoader());
        name = in.readString();
        port = in.readInt();
    }

    public void writeToProvider(ContentValues out) {
        Log.i("Peer", "writeToProvider");

        // TODO
        PeerContract.putName(out, name);
        PeerContract.putTimestamp(out, timestamp.getTime());
        PeerContract.putAddress(out, address.getAddress());
        PeerContract.putPort(out, port);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Log.i("chatserver:Peer", "writeToParcel");

        out.writeLong(id);
        out.writeLong(timestamp.getTime());
        out.writeValue(address);
        out.writeString(name);
        out.writeInt(port);
    }

        public static final Creator<Peer> CREATOR = new Creator<Peer>() {

            @Override
            public Peer createFromParcel(Parcel source) {
                // TODO
                return new Peer(source);
            }

            @Override
            public Peer[] newArray(int size) {
                // TODO
                return new Peer[size];
            }

        };

}
