package com.medishelf.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.medishelf.app.R;
import com.medishelf.app.adapters.AdminOrdersAdapter;
import com.medishelf.app.firebase.FirebaseHelper;
import com.medishelf.app.firebase.UserSession;
import com.medishelf.app.models.Order;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity {
    private RecyclerView ordersRecyclerView;
    private AdminOrdersAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private FirebaseHelper firebaseHelper;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        firebaseHelper = FirebaseHelper.getInstance();
        userSession = new UserSession(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Admin Orders");

        initViews();
        loadOrders();
    }

    private void initViews() {
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrdersAdapter(new ArrayList<>());
        ordersRecyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseHelper.getOrdersRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Order> orders = new ArrayList<>();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    try {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) orders.add(order);
                    } catch (Exception e) { }
                }
                Collections.sort(orders, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                progressBar.setVisibility(View.GONE);
                if (orders.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    ordersRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    ordersRecyclerView.setVisibility(View.VISIBLE);
                    adapter.updateOrders(orders);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        if (cartItem != null) cartItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            firebaseHelper.getAuth().signOut();
            userSession.logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}