/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

public class ChatServer extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	final static public String TAG = ChatServer.class.getCanonicalName();
		
	/*
	 * Socket used both for sending and receiving
	 */
	private DatagramSendReceive serverSocket;
//  private DatagramSocket serverSocket;

	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true; 

    /*
     * UI for displayed received messages
     */
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private Button next;

    static final private int LOADER_ID = 1;

	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.i("ChatServer", "OnCreate");
		super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /*
             * Get port information from the resources.
             */
            int port = getResources().getInteger(R.integer.app_port);

            // serverSocket = new DatagramSocket(port);

            serverSocket = new DatagramSendReceive(port);

        } catch (Exception e) {
            throw new IllegalStateException("Cannot open socket", e);
        }

        setContentView(R.layout.messages);

        // TODO use SimpleCursorAdapter (with flags=0) to display the messages received.
        String[] from = new String[] { MessageContract.SENDER, MessageContract.MESSAGE_TEXT };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

        messagesAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0);

        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

        // TODO bind the button for "next" to this activity as listener
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);

        // TODO use loader manager to initiate a query of the database
        //getLoaderManager().initLoader(LOADER_ID, null, this);
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

	}



    public void onClick(View v) {
        Log.i("ChatServer", "OnClick");
		
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			
			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet");

			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address:" + sourceIPAddress);
			
			String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

            Log.i("ChatServer/onClick", "msgContents[0]:" + msgContents[0] + "msgContents[1]:" + msgContents[1]);

            final Message message = new Message();
            message.sender = msgContents[0];
            //message.timestamp = new Date(Long.parseLong(msgContents[1]));
            message.timestamp = DateUtils.now();;
            //message.messageText = msgContents[2];
            message.messageText = msgContents[1];

			Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);


            /*
             * TODO upsert the peer and message into the content provider.
             */
            // For this assignment, OK to do CP insertion on the main thread.
            Peer sender = new Peer();
            sender.name = message.sender;
            sender.timestamp = message.timestamp;
            sender.address = receivePacket.getAddress();

            ContentResolver resolver = getContentResolver();
            ContentValues peerData = new ContentValues();
            sender.writeToProvider(peerData);
            long senderId = PeerContract.getId(resolver.insert(PeerContract.CONTENT_URI, peerData));
            message.senderId = senderId;
            Log.i("ChatServer", "senderId:" + message.senderId);

            ContentValues msgData = new ContentValues();
            message.writeToProvider(msgData);
            resolver.insert(MessageContract.CONTENT_URI, msgData);
            //getContentResolver().insert(MessageContract.CONTENT_URI, msgData);

            /*
             * End TODO
             */


        } catch (Exception e) {
			
			Log.e(TAG, "Problems receiving packet: " + e.getMessage(), e);
			socketOK = false;
		} 

	}

	/*
	 * Close the socket before exiting application
	 */
    public void closeSocket() {
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }

	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
	}

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.i("ChatServer", "onCreateLoader");
        // TODO use a CursorLoader to initiate a query on the database
        // return null;
        Uri baseUri = MessageContract.CONTENT_URI;

        return new CursorLoader(this, baseUri, null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.i("ChatServer", "onLoadFinished");
        // TODO populate the UI with the result of querying the provider
        messagesAdapter.swapCursor((Cursor)data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.i("ChatServer", "onLoaderReset");
        // TODO reset the UI when the cursor is empty
        messagesAdapter.swapCursor(null);
    }

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("ChatServer", "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("ChatServer", "onOptionsItemSelected");
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.peers:
                // TODO PEERS provide the UI for viewing list of peers
                Intent viewPeers = new Intent(this, ViewPeersActivity.class);
                startActivity(viewPeers);
                break;


            default:
        }
        return false;
    }


}