/*********************************************************************

    Client for sending chat messages to the server..

    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chatclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.base.InetAddressUtils;

/*
 * @author dduggan
 * 
 */
public class ChatClient extends Activity implements OnClickListener {

	final static private String TAG = ChatClient.class.getCanonicalName();

	/*
	 * Socket used for sending
	 */
	//  private DatagramSocket clientSocket;
	private DatagramSendReceive clientSocket;


	/*
	 * Widgets for dest address, chat name, message text, send button.
	 */
	// modified variable names to me preference
	private EditText editDestinationHost;
	private EditText editChatName;
	private EditText editMessageText;
	private Button sendButton;

	/*
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_client);

		
		/**
		 * Let's be clear, this is a HACK to allow you to do network communication on the chat_client thread.
		 * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
		 * this right in a future assignment (using a Service managing background threads).
		 */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy);

		// TODO initialize the UI.
		editDestinationHost = findViewById(R.id.destination_host);
		editMessageText = findViewById(R.id.message_text);
		editChatName = findViewById(R.id.chat_name);
		sendButton = findViewById(R.id.send_button);
		// End todo

		try {

			int port = getResources().getInteger(R.integer.app_port);
			clientSocket = new DatagramSendReceive(port);
			// clientSocket = new DatagramSocket(port);

		} catch (IOException e) {
		    IllegalStateException ex = new IllegalStateException("ERROR:Cannot open socket", e);
			throw ex;
		}
		sendButton.setOnClickListener(this);

	}

	/*
	 * Callback for the SEND button.
	 */
	public void onClick(View v) {

		try {
			/*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

			InetAddress destAddr;

			int destPort = getResources().getInteger(R.integer.destination_port_default);
			Log.d(TAG, String.valueOf(destPort));

			//variable not used?
			String clientName;


			byte[] sendData;  // Combine sender and message text; default encoding is UTF-8


			// TODO get data from UI (no-op if chat name is blank)
			destAddr = InetAddress.getByName(editDestinationHost.getText().toString());
			Log.d(TAG, String.valueOf(destAddr));

			String ChatName = editChatName.getText().toString();
			Log.d(TAG, ChatName);


			if(ChatName.isEmpty()){
				Toast.makeText(getApplicationContext(), "Can not send with EMPTY ChatName!!", Toast.LENGTH_SHORT).show();

			}else {
				String MessageText = editMessageText.getText().toString();

				Log.d(TAG, MessageText);
				String Message = ChatName + " : " + MessageText;

				//byte buffer
				sendData = Message.getBytes();

				// This outputs null : -1
				Log.d(TAG, String.format("Sending data from address %s : %d", clientSocket.getInetAddress(), clientSocket.getPort()));

				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destAddr, destPort);

				clientSocket.send(sendPacket);

				Log.d(TAG, "Sent packet: " + sendData);
			}
			// End todo

		} catch (UnknownHostException e) {
			throw new IllegalStateException("Unknown host exception: " + e.getMessage());
		} catch (IOException e) {
            throw new IllegalStateException("IO exception: " + e.getMessage());
		}

		editMessageText.setText("");

	}

    @Override
    public void onDestroy() {
	    super.onDestroy();
	    if (clientSocket != null) {
            clientSocket.close();
        }
    }

}