package com.medishelf.app.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import com.medishelf.app.R;
import com.medishelf.app.firebase.FirebaseHelper;
import com.medishelf.app.firebase.UserSession;
import com.medishelf.app.models.CartItem;
import com.medishelf.app.models.Order;
import com.medishelf.app.models.OrderItem;
import com.medishelf.app.mpesa.MpesaUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CheckoutActivity extends AppCompatActivity {
    private EditText nameInput, phoneInput, addressInput, cityInput;
    private TextView orderSummaryText, totalAmountText;
    private Button payButton;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;
    private UserSession userSession;
    private List<CartItem> cartItems;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Checkout");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firebaseHelper = FirebaseHelper.getInstance();
        userSession = new UserSession(this);
        cartItems = ProductsActivity.getCartItems();

        initViews();
        displayOrderSummary();
        setupListeners();
    }

    private void initViews() {
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        addressInput = findViewById(R.id.addressInput);
        cityInput = findViewById(R.id.cityInput);
        orderSummaryText = findViewById(R.id.orderSummaryText);
        totalAmountText = findViewById(R.id.totalAmountText);
        payButton = findViewById(R.id.payButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void displayOrderSummary() {
        StringBuilder summary = new StringBuilder();
        totalAmount = 0;
        for (CartItem item : cartItems) {
            summary.append(item.getProduct().getName())
                    .append(" x ").append(item.getQuantity())
                    .append(" = KES ").append(String.format("%.0f", item.getTotalPrice()))
                    .append("\n");
            totalAmount += item.getTotalPrice();
        }
        orderSummaryText.setText(summary.toString());
        totalAmountText.setText(String.format("Total: KES %.0f", totalAmount));
    }

    private void setupListeners() {
        payButton.setOnClickListener(v -> validateAndShowStk());
    }

    private void validateAndShowStk() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { nameInput.setError("Required"); return; }
        if (TextUtils.isEmpty(phone)) { phoneInput.setError("Required"); return; }
        if (!MpesaUtils.isValidKenyanPhone(phone)) { phoneInput.setError("Invalid Phone"); return; }
        if (TextUtils.isEmpty(address)) { addressInput.setError("Required"); return; }
        if (TextUtils.isEmpty(city)) { cityInput.setError("Required"); return; }

        showFakeStkDialog(name, phone, address, city);
    }

    private void showFakeStkDialog(String name, String phone, String address, String city) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("M-PESA Menu");
        builder.setMessage("Pay KES " + String.format("%.0f", totalAmount) + " to MediShelf Business?\nEnter M-PESA PIN:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String pin = input.getText().toString();
            if (pin.length() < 4) {
                Toast.makeText(CheckoutActivity.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
            } else {
                processFakePayment(name, phone, address, city);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void processFakePayment(String name, String phone, String address, String city) {
        progressBar.setVisibility(View.VISIBLE);
        payButton.setEnabled(false);

        Toast.makeText(this, "Processing M-Pesa...", Toast.LENGTH_SHORT).show();

        // Simulate 3 second delay
        new Handler().postDelayed(() -> {
            String fakeTransactionId = "QJH" + (10000 + new Random().nextInt(90000));

            // --- THE FAKE SMS TRIGGER ---
            triggerFakeSmsNotification(fakeTransactionId);
            // ----------------------------

            saveOrder(name, phone, address, city, fakeTransactionId);
        }, 3000);
    }

    private void triggerFakeSmsNotification(String trxId) {
        try {
            String message = trxId + " Confirmed. Ksh" + String.format("%.0f", totalAmount) +
                    " sent to MediShelf Business. New M-PESA balance is Ksh 4,500. Transaction cost, Ksh 0.00.";

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "mpesa_sms_channel";

            // Create Channel (Required for Android 8.0+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId, "M-Pesa Messages", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Uses your app icon
                    .setContentTitle("M-PESA")
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message)) // Expands to show full SMS
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            notificationManager.notify(1, builder.build());
        } catch (Exception e) {
            // If permission is denied on newer Androids, we just ignore it so the app doesn't crash
        }
    }

    private void saveOrder(String name, String phone, String address, String city, String transactionId) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getProduct().getId(),
                    cartItem.getProduct().getName(),
                    cartItem.getProduct().getPriceAsDouble(),
                    cartItem.getQuantity()
            );
            orderItems.add(orderItem);
        }

        Order order = new Order(
                null, userSession.getEmail(), name, phone, address, city,
                orderItems, totalAmount, "Paid", System.currentTimeMillis()
        );

        firebaseHelper.saveOrder(order, new FirebaseHelper.OnOrderSavedListener() {
            @Override
            public void onSuccess(String orderId) {
                progressBar.setVisibility(View.GONE);
                ProductsActivity.clearCart();
                Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("address", address + ", " + city);
                startActivity(intent);
                finish();
            }
            @Override
            public void onFailure(String error) {
                progressBar.setVisibility(View.GONE);
                payButton.setEnabled(true);
                Toast.makeText(CheckoutActivity.this, "Save Failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}