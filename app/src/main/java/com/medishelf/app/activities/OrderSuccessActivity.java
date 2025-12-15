package com.medishelf.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.medishelf.app.R;

public class OrderSuccessActivity extends AppCompatActivity {

    private TextView orderIdText, addressText;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        initViews();
        displayOrderInfo();
        setupListeners();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToProducts();
            }
        });
    }

    private void initViews() {
        orderIdText = findViewById(R.id.orderIdText);
        addressText = findViewById(R.id.addressText);
        continueButton = findViewById(R.id.continueButton);
    }

    private void displayOrderInfo() {
        String orderId = getIntent().getStringExtra("orderId");
        String address = getIntent().getStringExtra("address");

        if (orderId != null) {
            orderIdText.setText("Order ID: " + orderId);
        }

        if (address != null) {
            addressText.setText("Delivery Address: " + address);
        }
    }

    private void setupListeners() {
        continueButton.setOnClickListener(v -> navigateToProducts());
    }

    private void navigateToProducts() {
        Intent intent = new Intent(OrderSuccessActivity.this, ProductsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}