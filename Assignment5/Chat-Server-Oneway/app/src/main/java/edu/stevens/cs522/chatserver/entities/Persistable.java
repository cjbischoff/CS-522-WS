package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;

public interface Persistable {

    void writeToProvider(ContentValues out);

}
