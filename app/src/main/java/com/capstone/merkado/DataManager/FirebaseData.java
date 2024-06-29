package com.capstone.merkado.DataManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class FirebaseData {
    private final DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    public FirebaseData() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public interface FirebaseDataCallback {
        void onDataReceived(@Nullable DataSnapshot dataSnapshot);
    }

    public interface BooleanCallback {
        /**
         * Returns Boolean callback value.
         * @param bool callback value.
         */
        void callback(@Nullable Boolean bool);
    }

    // REAL-TIME DATABASE
    public void retrieveData(String childPath, final FirebaseDataCallback callback) {
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Pass the retrieved data to the callback method
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
                callback.onDataReceived(null);
            }
        });
    }

    public void retrieveDataRealTime(String childPath, final FirebaseDataCallback callback) {
        DatabaseReference childRef = databaseRef.child(childPath);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Pass the retrieved data to the callback method
                callback.onDataReceived(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
                callback.onDataReceived(null);
            }
        };
        childRef.addValueEventListener(valueEventListener);
    }

    public void stopRealTimeUpdates(String childPath) {
        if (valueEventListener != null) {
            DatabaseReference childRef = databaseRef.child(childPath);
            childRef.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
    }

    public void setValue(String childPath, Object value) {
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.setValue(value);
    }

    public Boolean setValues(String childPath, Map<String, Object> value){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DatabaseReference childRef = databaseRef.child(childPath);
        childRef.updateChildren(value)
                .addOnSuccessListener(unused -> future.complete(true))
                .addOnFailureListener(e -> future.complete(false));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeData(String childPath){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference nodeRef = databaseRef.child(childPath);
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // check if dataSnapshot exists (has data in it).
                booleanCallback.callback(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
                booleanCallback.callback(null);
            }
        });
    }

}
