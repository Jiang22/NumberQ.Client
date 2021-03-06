package langotec.numberq.client.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import langotec.numberq.client.MainActivity;
import langotec.numberq.client.R;
import langotec.numberq.client.dbConnect.MenuDBConn;
import langotec.numberq.client.fragment.CartFragment;
import langotec.numberq.client.menu.Menu;
import langotec.numberq.client.menu.SelectedActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    //先用ArrayList拿東西進來再轉型
    private ArrayList data;
    private Context context;

    public RecyclerViewAdapter(ArrayList data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (data.get(0) instanceof String) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.cardview_store, viewGroup, false);

        }else if (data.get(0) instanceof Menu){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.cardview_cart, viewGroup, false);
        }
        // ViewHolder參數一定要是Layout的根節點。
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        View view = viewHolder.view;
        context = view.getContext();

        if (data.get(0) instanceof String) {
            final String data = (String) this.data.get(position);
            TextView textStoreName = (TextView) view.findViewById(R.id.textView1);
            ImageView storeIconImage = (ImageView) view.findViewById(R.id.store_icon);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MenuDBConn(data, context).execute();
//                    Intent intent = new Intent(v.getContext(), MenuActivity.class);
//                    context.startActivity(intent);
                }
            });

            if (position % 2 == 0) {
                textStoreName.setTextColor(0xFF000000);
                storeIconImage.setImageDrawable(context.getDrawable(R.drawable.ding));
            } else {
                textStoreName.setTextColor(0xFFAAAAAA);
                storeIconImage.setImageDrawable(context.getDrawable(R.drawable.bafun));
            }
            textStoreName.setText(data);
        }

        else if (data.get(position) instanceof Menu){
            Menu menu = (Menu) data.get(position);

            ImageView cartIconImage = (ImageView) view.findViewById(R.id.cart_imageView);
            TextView cartStoreName = (TextView) view.findViewById(R.id.cartStoreName_textView);
            TextView cartDesc = (TextView) view.findViewById(R.id.cartDesc_textView);
            TextView cartTotal = (TextView) view.findViewById(R.id.cartTotal_textView);

            menu.setImageView(cartIconImage);
            cartStoreName.setText(menu.getHeadName() + "-" + menu.getBranchName());
            cartDesc.setText(menu.getProductName() + "\t\t" +
                    context.getResources().getString(R.string.menu_singlePrice) + menu.getPrice());
            cartTotal.setText(context.getResources().getString(R.string.menu_quantity) +
                    menu.getQuantityNum() + "\t\t" +
                    context.getResources().getString(R.string.menu_NT)
                    + (Integer.parseInt(menu.getPrice()) * menu.getQuantityNum()));

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDialog(position);
                    return false;
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, context.getResources().getString(R.string.cart_click),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ViewHolder(View view){
            super(view);
            this.view = view;
        }
    }

    //修改購物車內容的對話框功能
    private void showDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.cart_modify))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(context.getResources().getString(R.string.cart_deleteMenu),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //移除Cart內選定的商品
                                data.remove(position);

                                //更新Adapter的畫面
                                RecyclerViewAdapter.this.notifyDataSetChanged();

                                //更新CartFragment的畫面
                                CartFragment cartFragment = MainActivity.cartFragment;
                                android.support.v4.app.FragmentTransaction ft =
                                        cartFragment.getFragmentManager().beginTransaction();
                                ft.detach(cartFragment).attach(cartFragment).commit();
                            }
                        })
                .setNegativeButton(context.getResources().getString(R.string.cart_modifyQuantity),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Menu menu = (Menu) data.get(position);
                                menu.setFrom("fromCartFragment");
                                Intent intent = new Intent();
                                intent.putExtra("Menu", menu);
                                intent.setClass(context, SelectedActivity.class);
                                ((Activity)context).startActivity(intent);
                            }
                        })
                .setNeutralButton(context.getResources().getString(R.string.menu_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

}

