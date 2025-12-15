package com.medishelf.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medishelf.app.R;
import com.medishelf.app.models.CartItem;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private TextView emptyView, totalText;
    private Button checkoutButton;
    private List<CartItem> cartItems;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Shopping Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cartItems = ProductsActivity.getCartItems();

        initViews();
        setupRecyclerView();
        updateTotal();
    }

    private void initViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        emptyView = findViewById(R.id.emptyView);
        totalText = findViewById(R.id.totalText);
        checkoutButton = findViewById(R.id.checkoutButton);

        checkoutButton.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (cartItems.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
            checkoutButton.setEnabled(false);
        } else {
            emptyView.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(true);

            adapter = new CartAdapter(cartItems, this::onCartItemChanged);
            cartRecyclerView.setAdapter(adapter);
        }
    }

    private void onCartItemChanged() {
        updateTotal();

        if (cartItems.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
            checkoutButton.setEnabled(false);
        }
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        totalText.setText(String.format("Total: KES %.0f", total));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    interface OnCartChangedListener {
        void onCartChanged();
    }

    static class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
        private final List<CartItem> items;
        private final OnCartChangedListener listener;

        CartAdapter(List<CartItem> items, OnCartChangedListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            CartItem item = items.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class CartViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, priceText, quantityText;
            Button decreaseBtn, increaseBtn, removeBtn;

            CartViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.productName);
                priceText = view.findViewById(R.id.productPrice);
                quantityText = view.findViewById(R.id.quantityText);
                decreaseBtn = view.findViewById(R.id.decreaseBtn);
                increaseBtn = view.findViewById(R.id.increaseBtn);
                removeBtn = view.findViewById(R.id.removeBtn);
            }

            void bind(CartItem item) {
                nameText.setText(item.getProduct().getName());
                priceText.setText(String.format("KES %.0f", item.getTotalPrice()));
                quantityText.setText(String.valueOf(item.getQuantity()));

                decreaseBtn.setOnClickListener(v -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        notifyItemChanged(getAdapterPosition());
                        listener.onCartChanged();
                    }
                });

                increaseBtn.setOnClickListener(v -> {
                    item.setQuantity(item.getQuantity() + 1);
                    notifyItemChanged(getAdapterPosition());
                    listener.onCartChanged();
                });

                removeBtn.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    items.remove(position);
                    notifyItemRemoved(position);
                    listener.onCartChanged();
                });
            }
        }
    }
}