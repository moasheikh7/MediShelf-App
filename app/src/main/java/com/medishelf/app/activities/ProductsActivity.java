package com.medishelf.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.medishelf.app.R;
import com.medishelf.app.adapters.ProductAdapter;
import com.medishelf.app.firebase.FirebaseHelper;
import com.medishelf.app.firebase.UserSession;
import com.medishelf.app.models.CartItem;
import com.medishelf.app.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private ProgressBar progressBar;
    private TextView emptyView, cartBadge;
    private FirebaseHelper firebaseHelper;
    private UserSession userSession;

    // Static cart to persist across screens
    private static final List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Initialize Firebase & Session
        firebaseHelper = FirebaseHelper.getInstance();
        userSession = new UserSession(this);

        // SAFELY Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("MediShelf Books");
            }
        }

        initViews();
        loadProducts();
    }

    private void initViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        // Grid Layout (2 columns)
        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize Adapter
        productAdapter = new ProductAdapter(this, new ArrayList<>(), this::addToCart);
        productsRecyclerView.setAdapter(productAdapter);
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        firebaseHelper.getProductsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();

                // CRASH FIX: Try-Catch loop to prevent crashing on bad data
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    try {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            product.setId(productSnapshot.getKey());
                            products.add(product);
                        }
                    } catch (Exception e) {
                        Log.e("ProductsActivity", "Error parsing product: " + e.getMessage());
                    }
                }

                progressBar.setVisibility(View.GONE);

                if (products.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    productsRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    productsRecyclerView.setVisibility(View.VISIBLE);
                    productAdapter.updateProducts(products);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProductsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToCart(Product product) {
        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            cartItems.add(new CartItem(product, 1));
        }

        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        invalidateOptionsMenu(); // Force refresh of the menu to update badge
    }

    // --- MENU & CART BADGE LOGIC ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem cartMenuItem = menu.findItem(R.id.action_cart);
        if (cartMenuItem != null) {
            View cartView = cartMenuItem.getActionView();
            if (cartView != null) {
                cartBadge = cartView.findViewById(R.id.cart_badge);
                updateCartBadge(); // Update immediately

                cartView.setOnClickListener(v -> {
                    Intent intent = new Intent(ProductsActivity.this, CartActivity.class);
                    startActivity(intent);
                });
            }
        }
        return true;
    }

    private void updateCartBadge() {
        if (cartBadge != null) {
            int totalItems = 0;
            for (CartItem item : cartItems) {
                totalItems += item.getQuantity();
            }

            if (totalItems > 0) {
                cartBadge.setVisibility(View.VISIBLE);
                cartBadge.setText(String.valueOf(totalItems));
            } else {
                cartBadge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            firebaseHelper.getAuth().signOut();
            userSession.logout();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu(); // Refresh badge when returning from Cart
    }

    // Helper methods for Cart access
    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
    }
}