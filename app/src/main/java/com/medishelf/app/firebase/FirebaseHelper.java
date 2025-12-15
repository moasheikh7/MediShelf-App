package com.medishelf.app.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.medishelf.app.models.Order;

public class FirebaseHelper {
    private static FirebaseHelper instance;
    private FirebaseAuth auth;
    private DatabaseReference database;

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getDatabase() {
        return database;
    }

    public DatabaseReference getProductsRef() {
        return database.child("Products");
    }

    public DatabaseReference getUsersRef() {
        return database.child("users");
    }

    public DatabaseReference getOrdersRef() {
        return database.child("Orders");
    }

    public void saveOrder(Order order, OnOrderSavedListener listener) {
        String orderId = getOrdersRef().push().getKey();
        if (orderId != null) {
            order.setOrderId(orderId);
            getOrdersRef().child(orderId).setValue(order)
                    .addOnSuccessListener(aVoid -> {
                        if (listener != null) {
                            listener.onSuccess(orderId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (listener != null) {
                            listener.onFailure(e.getMessage());
                        }
                    });
        }
    }

    public interface OnOrderSavedListener {
        void onSuccess(String orderId);
        void onFailure(String error);
    }
}