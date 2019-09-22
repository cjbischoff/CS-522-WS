package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer_id";

    private ChatDbAdapter chatDbAdapter;

    private TextView userNameTextView;
    private TextView timeStampTextView;
    private TextView addressTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("ViewPeerActivity","onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        ChatDbAdapter chatDbAdapter = new ChatDbAdapter(this);
        chatDbAdapter.open();


        // TODO init the UI
        userNameTextView = (TextView)findViewById(R.id.view_user_name);
        timeStampTextView = (TextView)findViewById(R.id.view_timestamp);
        addressTextView = (TextView)findViewById(R.id.view_address);
        //messageTextView -  addressTextView = (TextView)findViewById(R.id.view_address);

        long id = getIntent().getExtras().getLong(PEER_ID_KEY);
        Log.i("ViewPeerActivity","PeerID: " + id);


        Peer peer = chatDbAdapter.fetchPeer(id);

        userNameTextView.setText(peer.name);
        timeStampTextView.setText(peer.timestamp.toString());
        addressTextView.setText(peer.address.getHostAddress());

        //Peer peerMesg = chatDbAdapter.fetchPeer(id);


        chatDbAdapter.close();

    }



}
