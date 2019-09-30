package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String PEER_KEY = "peer";

    private static final int LOADER_ID = 2;

    private SimpleCursorAdapter peerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ViewPeerActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI
        TextView textView = (TextView)findViewById(R.id.view_user_name);
        textView.setText(peer.name);
        textView = (TextView) findViewById(R.id.view_timestamp);
        textView.setText(peer.timestamp.toString());
        textView = (TextView) findViewById(R.id.view_address);
        textView.setText(peer.address.toString());

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Log.i("ViewPeerActivity", "onCreateLoader");
        // TODO use a CursorLoader to initiate a query on the database
        // Filter messages with the sender id
        //return null;
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, PeerContract.CONTENT_URI, null, null, null, null);
            default:
                return null; // An invalid id was passed in
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.i("ViewPeerActivity", "onLoadFinished");
        // TODO populate the UI with the result of querying the provider
        switch(loader.getId()) {
            case LOADER_ID:
                this.peerAdapter.swapCursor(data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        // TODO reset the UI when the cursor is empty
        this.peerAdapter.swapCursor(null);


    }

}
