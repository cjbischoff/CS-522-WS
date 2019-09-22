package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private ChatDbAdapter chatDbAdapter;

    /*
     * Constructor SimpleCursorAdapter is deprecated. I went with ArrayAdapter cause I didn't know
     * how to solve this issue and I was familiar with ArrayAdapter. I am ware that CursorAdapter is
     * more appropriate when there is a database because it does not load all the records as ArrayAdapter
     *
     */

    private SimpleCursorAdapter peerAdapter;

    private ArrayList<Peer> peers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ViewPeersActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with result of DB query

        chatDbAdapter = new ChatDbAdapter(this);
        chatDbAdapter.open();

        peers = chatDbAdapter.fetchAllPeers();
        chatDbAdapter.close();

        ArrayList<String> peerStrings = new ArrayList<String>();

        for(int i=0; i<peers.size(); i++){
            peerStrings.add( peers.get(i).name);
        }

        ListView peerListView = (ListView) findViewById(R.id.peer_list);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, peerStrings);
        peerListView.setAdapter(arrayAdapter);
        peerListView.setOnItemClickListener(this);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, id);
        startActivity(intent);
    }
}
