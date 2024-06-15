package com.capstone.merkado.DataManager;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class FirebaseData {
    private final DatabaseReference databaseRef;

    public FirebaseData() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public interface FirebaseDataCallback {
        void onDataReceived(DataSnapshot dataSnapshot);
    }

    public interface BooleanCallback {
        /**
         * Returns Boolean callback value.
         * @param bool callback value.
         */
        void callback(Boolean bool);
    }

    // REAL-TIME DATABASE

    public void retrieveData(Context context, String childPath, final FirebaseDataCallback callback) {
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Pass the retrieved data to the callback method
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error fetching data. Please try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void retrieveData(String childPath, final FirebaseDataCallback callback) {
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Pass the retrieved data to the callback method
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataReceived(null);
            }
        });
    }

    public void addValue(String childPath, Object value) {
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.setValue(value);
    }

    public void updateValue(String childPath, Object value) {
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.setValue(value);
    }

    public void addValues(String childPath, Map<String, Object> value){
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.updateChildren(value);
    }

    public <T> void addValues(String childPath, T value){
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.setValue(value);
    }

    public void updateValues(String childPath, Map<String, Object> value){
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.updateChildren(value);
    }

    public void removeData(String childpath){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference nodeRef = databaseRef.child(childpath);
        nodeRef.removeValue();
    }

    /**
     * Checks if key exists in the Firebase RTDB node.
     * @param node the path where the key will be checked.
     * @param key the key to be checked.
     * @param booleanCallback the return callback.
     */
    public void isKeyExists(String node, String key, BooleanCallback booleanCallback) {
        // combine the node and the key to navigate to that node.
        DatabaseReference childRef = databaseRef.child(String.format("%s/%s", node, key));

        // check the node for data
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // check if dataSnapshot exists (has data in it).
                booleanCallback.callback(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
