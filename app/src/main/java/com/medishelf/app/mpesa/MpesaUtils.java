package com.medishelf.app.mpesa;

import android.os.Handler;
import android.os.Looper;

public class MpesaUtils {

    // Sandbox credentials (Demo only - not real charges)
    // REPLACED WITH PLACEHOLDERS FOR GITHUB SECURITY
    public static final String CONSUMER_KEY = "YOUR_CONSUMER_KEY_HERE";
    public static final String CONSUMER_SECRET = "YOUR_CONSUMER_SECRET_HERE";
    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "YOUR_PASSKEY_HERE";
    public static final String CALLBACK_URL = "https://mydomain.com/callback";

    /**
     * Simulates M-Pesa STK Push for demo purposes
     */
    public static void simulateStkPush(String phoneNumber, double amount, PaymentCallback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            callback.onSuccess("DEMO" + System.currentTimeMillis(), "Payment simulated successfully");
        }, 3000);
    }

    public static String formatPhoneNumber(String phone) {
        phone = phone.replaceAll("[\\s\\-+]", "");
        if (phone.startsWith("0")) phone = "254" + phone.substring(1);
        if (!phone.startsWith("254")) phone = "254" + phone;
        return phone;
    }

    public static boolean isValidKenyanPhone(String phone) {
        String formatted = formatPhoneNumber(phone);
        return formatted.matches("^254[17]\\d{8}$");
    }

    public interface PaymentCallback {
        void onSuccess(String transactionId, String message);
        void onFailure(String error);
    }
}