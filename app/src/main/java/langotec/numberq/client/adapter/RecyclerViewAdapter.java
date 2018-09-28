package langotec.numberq.client.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import langotec.numberq.client.R;
import langotec.numberq.client.menu.Menu;
import langotec.numberq.client.menu.MenuActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    //先用Object拿東西進來再轉型
    private Object[] data;

    public RecyclerViewAdapter(Object[] data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (data[0] instanceof String) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.cardview_store, viewGroup, false);

        }else if (data[0] instanceof Menu){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.cardview_cart, viewGroup, false);
        }
        // ViewHolder參數一定要是Layout的根節點。
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        View view = viewHolder.view;
        final Context context = view.getContext();

        if (data[position] instanceof String) {
            String[] data = (String[]) this.data;
            TextView textStoreName = (TextView) view.findViewById(R.id.textView1);
            ImageView storeIconImage = (ImageView) view.findViewById(R.id.store_icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MenuActivity.class);
                    context.startActivity(intent);
                }
            });

            if (position % 2 == 0) {
                textStoreName.setTextColor(0xFF000000);
                storeIconImage.setImageDrawable(context.getDrawable(R.drawable.ding));
            } else {
                textStoreName.setTextColor(0xFFAAAAAA);
                storeIconImage.setImageDrawable(context.getDrawable(R.drawable.bafun));
            }
            textStoreName.setText(data[position]);
        }

        else if (data[position] instanceof Menu){
            Menu menu = (Menu) data[position];

            ImageView cartIconImage = (ImageView) view.findViewById(R.id.cart_imageView);
            TextView cartStoreName = (TextView) view.findViewById(R.id.cartStoreName_textView);
            TextView cartDesc = (TextView) view.findViewById(R.id.cartDesc_textView);
            TextView cartTotal = (TextView) view.findViewById(R.id.cartTotal_textView);

            menu.setImageView(cartIconImage);
            cartStoreName.setText(menu.getHeadName() + "-" + menu.getBranchName());
            cartDesc.setText(menu.getProductName() + "\t\t" +
                    context.getResources().getString(R.string.menu_NT) + menu.getPrice());
            cartTotal.setText(context.getResources().getString(R.string.menu_quantity) +
                    menu.getQuantityNum() + "\t\t" +
                    context.getResources().getString(R.string.menu_totalPrice)
                    + (Integer.parseInt(menu.getPrice()) * menu.getQuantityNum()));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;

        public ViewHolder(View view){
            super(view);
            this.view = view;
        }
    }
}

