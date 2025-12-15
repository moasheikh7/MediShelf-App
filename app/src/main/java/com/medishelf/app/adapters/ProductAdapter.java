package com.medishelf.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.medishelf.app.R;
import com.medishelf.app.models.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void updateProducts(List<Product> newProducts) {
        this.productList = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.nameText.setText(product.getName());
        holder.authorText.setText(product.getAuthor());

        // FIX: Display price safely using the helper
        holder.priceText.setText(String.format("KES %.0f", product.getPriceAsDouble()));

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            Glide.with(context)
                    .load(product.getImage())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.productImage);
        }

        holder.addToCartButton.setOnClickListener(v -> listener.onAddToCartClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, authorText, priceText;
        ImageView productImage;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.productName);
            authorText = itemView.findViewById(R.id.productAuthor);
            priceText = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}