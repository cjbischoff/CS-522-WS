package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    final static public String TAG = ChatServer.class.getCanonicalName();

    public static final String PEERS_KEY = "peers";

    private ArrayList<Peer> peers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"IN: onCreate/ViewPeersActivity");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        peers = getIntent().getParcelableArrayListExtra(PEERS_KEY);
        if (peers == null) {
            Log.i(TAG,"In ViewPeersActivity/Peers is NULL");
        }

        ArrayList<String> peerStrings = new ArrayList<String>();

        // TODO display the list of peers, set this activity as onClick listener
        ListView peerListView = (ListView) findViewById(R.id.peer_list);
        ArrayAdapter peerAdapter = new ArrayAdapter(this, R.layout.view_peers, peerStrings);
        peerListView.setAdapter(peerAdapter);
        peerListView.setOnItemClickListener(this);
        //peerAdapter.notifyDataSetChanged();



    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Peer peer = peers.get(position);
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
        startActivity(intent);
    }
}
