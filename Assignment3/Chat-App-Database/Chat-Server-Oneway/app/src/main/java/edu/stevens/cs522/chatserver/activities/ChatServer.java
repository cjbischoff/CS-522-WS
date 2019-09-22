/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

public class ChatServer extends Activity implements OnClickListener {

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

    /*
    * Constructor SimpleCursorAdapter is deprecated. I went with ArrayAdapter cause I didn't know
    * how to solve this issue and I was familair with ArrayAdapter. I am ware that CursorAdapter is
    * more appropriate when there is a database because it does not load all the records as ArrayAdapter
    *
    */

    //private SimpleCursorAdapter messagesAdapter;

    ArrayAdapter arrayAdapter;
    ArrayList<String> messagesArrayList;


    private ListView messageList;

    private ChatDbAdapter chatDbAdapter;

    //private Cursor messageCursor;

    private Button next;



    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        Log.i("ChatServer","onCreate");

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

        // TODO open the database using the database adapter
        chatDbAdapter = new ChatDbAdapter(this);
        chatDbAdapter.open();

        // TODO query the database using the database adapter, and manage the cursor on the messages thread
        messagesArrayList = chatDbAdapter.fetchAllMessages();
        chatDbAdapter.close();

        // TODO set the adapter for the list view
        arrayAdapter = new ArrayAdapter(this, R.layout.message, messagesArrayList);
        messageList = (ListView)findViewById(R.id.message_list);

        // TODO bind the button for "next" to this activity as listener
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);

        // TODO use SimpleCursorAdapter to display the messages received.
        arrayAdapter = new ArrayAdapter(this, R.layout.message, messagesArrayList);
        messageList.setAdapter(arrayAdapter);

	}

    public void onClick(View v) {
        Log.i("ChatServer","onClick");
		
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			
			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet");

			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address: " + sourceIPAddress);

            String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");
            Log.i(TAG, "msgContents0:" +  msgContents[0] + " msgContents1:" + msgContents[1]);

            Message message = new Message();
            message.sender = msgContents[0];
            message.timestamp = DateUtils.now();;
            message.messageText = msgContents[1];

			Log.i(TAG, "message.sender:" + message.sender + " message.timestamp:" + message.timestamp + " message.messageText:" + message.messageText);

			/*
			 * TODO upsert peer and insert message into the database
			 */
            Peer peer = new Peer();
            peer.name = message.sender;
            peer.timestamp = message.timestamp;
            peer.address = receivePacket.getAddress();

            /*
             * End TODO
             */

            // Perform Persist of Peer and then Message
            chatDbAdapter.open();
            message.senderId = chatDbAdapter.persist(peer);
            Log.i(TAG, "peer.name:" + peer.name + " peer.timestamp:" + peer.timestamp + " peer.address:" + peer.address);
            chatDbAdapter.persist(message);
            Log.i(TAG, "message.senderId:" + message.senderId + " message.sender:" + message.sender + " message.timestamp:" + message.timestamp + " message.messageText:" + message.messageText);
            chatDbAdapter.close();


            chatDbAdapter.open();
            messagesArrayList = chatDbAdapter.fetchAllMessages();
            chatDbAdapter.close();

            messageList = (ListView) findViewById(R.id.message_list);
            arrayAdapter = new ArrayAdapter(this, R.layout.message, messagesArrayList);
            messageList.setAdapter(arrayAdapter);

		} catch (Exception e) {
			
			Log.i(TAG, "Problems receiving packet: " + e.getMessage(), e);
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

    public void onDestroy() {
        super.onDestroy();
        chatDbAdapter.close();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("ChatServer","onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS option
        getMenuInflater().inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("ChatServer","onOptionsItemSelected");
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.peers:
                // TODO PEERS provide the UI for viewing list of peers
                Intent peersIntent = new Intent(getApplicationContext(), ViewPeersActivity.class);
                startActivity(peersIntent);
                break;

            default:
        }
        return false;
    }

}