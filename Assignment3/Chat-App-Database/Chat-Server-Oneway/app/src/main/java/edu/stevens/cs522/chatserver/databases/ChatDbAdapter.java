package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import edu.stevens.cs522.chatserver.activities.ChatServer;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ChatDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "peers";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        //private static final String DATABASE_CREATE = null; // TODO

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String MESSAGE_CREATE =
                "create table " + MESSAGE_TABLE + " ("
                        + MessageContract._ID + " integer primary key, "
                        + MessageContract.MESSAGE_TEXT + " text, "
                        + MessageContract.TIMESTAMP + " integer, "
                        + MessageContract.SENDER + " text, "
                        + MessageContract.SENDER_ID + " integer "
                        + ")";

        private static final String PEER_CREATE =
                "create table " + PEER_TABLE + " ("
                        + PeerContract._ID + " integer primary key, "
                        + PeerContract.NAME + " text, "
                        + PeerContract.TIMESTAMP + " text, "
                        + PeerContract.ADDRESS + " text "
                        + ")";

        private static final String INDEX1 =
                "CREATE INDEX MessagesPeerIndex ON Messages(SENDER_ID)";

        private static final String INDEX2 =
                "CREATE INDEX PeerNameIndex ON Peers(name)";


        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO
            Log.i("ChatDbAdapter","onCreate");
            db.execSQL(MESSAGE_CREATE);
            db.execSQL(PEER_CREATE);
            db.execSQL(INDEX1);
            db.execSQL(INDEX2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
            Log.i("ChatDbAdapter","onUpgrade");
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            onCreate(db);
        }


    }

    public ChatDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
        dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        Log.i("ChatDbAdapter","open");
        // TODO
        try {
            db = dbHelper.getWritableDatabase();
            db.execSQL("PRAGMA	foreign_keys=ON;");
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
            db.execSQL("PRAGMA	foreign_keys=ON;");
        }

    }

    public ArrayList<String> fetchAllMessages() {
        Log.i("ChatDbAdapter","fetchAllMessages");
        // TODO
        ArrayList<String> messagesString = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+MESSAGE_TABLE,null);
        if(cursor.moveToFirst()){
            do{
                Message message = new Message(cursor);
                messagesString.add(message.sender+":"+message.timestamp+":"+message.messageText);
            }while(cursor.moveToNext());
        }
        return messagesString;
    }

    public ArrayList<Peer> fetchAllPeers() {
        Log.i("ChatDbAdapter","fetchAllPeers");
        // TODO

        ArrayList<Peer> peers = new ArrayList<Peer>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+PEER_TABLE,null);
        if(cursor.moveToFirst()){
            do{
                Peer peer = new Peer(cursor);
                peers.add(peer);
            }while(cursor.moveToNext());
        }
        return peers;

    }

    public Peer fetchPeer(long peerId) {
        Log.i("ChatDbAdapter","fetchPeer");
        // TODO

        Cursor cursorPeer = db.rawQuery("SELECT * FROM "+PEER_TABLE+" WHERE "+PeerContract._ID+" = ?", new String[]{Long.toString(peerId)});
        if( cursorPeer != null && cursorPeer.moveToFirst() ){
            Peer peer = new Peer(cursorPeer);
            Log.i("fetchPeer",peer.name);
            return peer;
        }
        return null;

    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TODO
        //return null;

        //Cursor cursorPeer = db.rawQuery("SELECT * FROM "+PEER_TABLE +" WHERE "+PeerContract._ID+" = ?", new String[]{Long.toString(peerId)});

        String[] projection = { MessageContract._ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER, MessageContract.SENDER_ID };
        String selection = MessageContract.SENDER_ID + "=" + Long.toString(peer.id);
        return db.query(MESSAGE_TABLE, projection, selection, null, null, null, null);


    }

    public void persist(Message message) throws SQLException {
        Log.i("ChatDbAdapter","Message/persist");
        // TODO
        ContentValues messageContentValues = new ContentValues();
        message.writeToProvider(messageContentValues);
        db = dbHelper.getWritableDatabase();
        message.id = db.insert(MESSAGE_TABLE, null, messageContentValues);
        Log.i("ChatDbAdapter","message.id: " + message.id);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     */
    public long persist(Peer peer) throws SQLException {
        Log.i("ChatDbAdapter","Peer/persist");

        // TODO
        ContentValues peerContentValues = new ContentValues();
        peer.writeToProvider(peerContentValues);
        db = dbHelper.getWritableDatabase();
        //return peer.id = db.insert(PEER_TABLE, null, peerContentValues);

        String selection = "SELECT * FROM "+PEER_TABLE+" WHERE "+PeerContract.NAME+" = ?";
        Cursor cursor = db.rawQuery(selection, new String[]{peer.name});
        if (cursor == null || !cursor.moveToFirst()) {
            peer.id  = db.insert(PEER_TABLE, null, peerContentValues);
            Log.i("ChatDbAdapter","Peer/persist/new" + " peer.id:" + peer.id);
        } else {
            String clause = PeerContract.NAME+" = ? ";
            String args[] = {peer.name};
            peer.id  = db.update(PEER_TABLE, peerContentValues,clause,args);
            Log.i("ChatDbAdapter","Peer/persist/update" + " peer.id:" + peer.id);
        }
        return peer.id;
    }

    public void close() {
        // TODO
        db.close();
    }



}