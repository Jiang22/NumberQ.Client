package langotec.numberq.client.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import langotec.numberq.client.R;
import langotec.numberq.client.adapter.RecyclerViewAdapter;
import langotec.numberq.client.menu.Cart;

public class CartFragment extends Fragment {

    Cart cart;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        cart = Cart.getInstance();
        RecyclerView cartRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_cart,
                container, false);

        //取得Cart裡Menu的內容
        Object[] menus = new Object[cart.size()];

        if (!cart.isEmpty()) {
            for (int i = 0 ; i < cart.size(); i++){
                menus[i] =  cart.get(i);
            }

            RecyclerViewAdapter adapter = new RecyclerViewAdapter(menus);
            LinearLayoutManager manager = new LinearLayoutManager(getActivity());

            cartRecycler.setAdapter(adapter);
            cartRecycler.setLayoutManager(manager);
        }
        return cartRecycler;
    }
}
