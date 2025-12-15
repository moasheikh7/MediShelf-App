package com.medishelf.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.medishelf.app.R;
import com.medishelf.app.models.Order;
import com.medishelf.app.models.OrderItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder> {
    private List<Order> orders;

    public AdminOrdersAdapter(List<Order> orders) { this.orders = orders; }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() { return orders.size(); }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, customerEmailText, dateText, itemsText, totalText, addressText, statusText;

        OrderViewHolder(View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            customerEmailText = itemView.findViewById(R.id.customerEmailText);
            dateText = itemView.findViewById(R.id.dateText);
            itemsText = itemView.findViewById(R.id.itemsText);
            totalText = itemView.findViewById(R.id.totalText);
            addressText = itemView.findViewById(R.id.addressText);
            statusText = itemView.findViewById(R.id.statusText);
        }

        void bind(Order order) {
            orderIdText.setText("Order ID: " + order.getOrderId());
            customerEmailText.setText("Customer: " + order.getCustomerEmail());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
                dateText.setText("Date: " + sdf.format(new Date(order.getTimestamp())));
            } catch (Exception e) { dateText.setText("Date: --"); }

            StringBuilder itemsBuilder = new StringBuilder("Items:\n");
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    itemsBuilder.append("• ").append(item.getProductName())
                            .append(" x ").append(item.getQuantity())
                            .append(" = KES ").append(String.format("%.0f", item.getTotalPrice()))
                            .append("\n");
                }
            }
            itemsText.setText(itemsBuilder.toString());
            totalText.setText(String.format("Total: KES %.0f", order.getTotalAmount()));
            addressText.setText("Delivery: " + order.getDeliveryAddress() + ", " + order.getCity());
            statusText.setText("Status: " + order.getPaymentStatus());
        }
    }
}