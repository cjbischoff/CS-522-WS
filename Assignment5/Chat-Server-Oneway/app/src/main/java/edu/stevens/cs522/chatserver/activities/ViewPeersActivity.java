package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, QueryBuilder.IQueryListener<Peer> {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private static final int LOADER_ID = 2;

    private SimpleCursorAdapter peerAdapter;
    private PeerManager peerManager;

    //private String[] from = { PeerContract.NAME, PeerContract.ADDRESS };
    //private int[] to = { android.R.id.text1, android.R.id.text2 };
    String[] from = {PeerContract.NAME};
    int[] to = {R.id.messageText};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ViewPeersActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with empty cursor (null)
        ListView peerList = findViewById(R.id.peer_list);
        //peerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_2, null, from, to,0);
        peerAdapter = new SimpleCursorAdapter(this, R.layout.message, null, from, to,0);
        peerList.setAdapter(peerAdapter);
        peerList.setOnItemClickListener(this);

        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("ViewPeersActivity", "onItemClick");

        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public void handleResults(TypedCursor<Peer> results) {
        // TODO
        peerAdapter.swapCursor(results.getCursor());
        //peerAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        // TODO
        peerAdapter.swapCursor(null);
        //peerAdapter.notifyDataSetChanged();
    }
}
