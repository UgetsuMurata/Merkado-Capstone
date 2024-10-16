package com.capstone.merkado.DataManager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class FirebaseData {
    private final DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    /**
     * Access to main Merkado database.
     */
    public FirebaseData() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Access to Merkado developers database.
     * @param context - application context.
     */
    public FirebaseData(Context context) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyDpN3qNxUiM9d61qQazcpJaOxeIAtAS3lM")
                .setDatabaseUrl("https://merkado-devs-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .setApplicationId("1:1064865953109:android:a2f217b82e213b5f8fabdc")
                .setProjectId("merkado-devs")
                .setStorageBucket("merkado-devs.appspot.com")
                .build();

        FirebaseApp secondApp;

        try {
            secondApp = FirebaseApp.initializeApp(context, options, "merkado-devs");
        } catch (IllegalStateException e) {
            secondApp = FirebaseApp.getInstance("merkado-devs");
        }

        FirebaseDatabase secondDatabase = FirebaseDatabase.getInstance(secondApp);

        databaseRef = secondDatabase.getReference("");
    }

    public interface FirebaseDataCallback {
        void onDataReceived(@Nullable DataSnapshot dataSnapshot);
    }

    public interface ValueCallback<T> {
        /**
         * Returns Object callback value.
         * @param t callback value.
         */
        void callback(@Nullable T t);
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
                Log.e("retrieveData", String.format("%s", databaseError.toException().getMessage()));
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
                Log.e("retrieveDataRealTime", String.format("%s", databaseError.toException().getMessage()));
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

    public CompletableFuture<Boolean> setValues(String childPath, Map<String, Object> value) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DatabaseReference childRef = databaseRef.child(childPath);

        childRef.updateChildren(value)
                .addOnSuccessListener(unused -> future.complete(true))
                .addOnFailureListener(e -> {
                    Log.e("setValues", String.format("%s", e));
                    future.complete(false);
                });

        return future;
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
    public void isKeyExists(String node, String key, ValueCallback<Boolean> booleanCallback) {
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
                Log.e("isKeyExists", String.format("%s", databaseError.toException().getMessage()));
                booleanCallback.callback(null);
            }
        });
    }

    public void getServerTimeOffset(ValueCallback<Long> doubleValueCallback){
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long offset = dataSnapshot.getValue(Long.class);
                doubleValueCallback.callback(offset);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getServerTime", String.format("%s", databaseError.toException().getMessage()));
                doubleValueCallback.callback(null);
            }
        });
    }

}
