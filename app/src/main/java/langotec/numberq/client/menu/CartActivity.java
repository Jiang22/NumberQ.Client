package langotec.numberq.client.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import langotec.numberq.client.R;

public class CartActivity extends AppCompatActivity {

    private Cart cart;
    private ListView listView;
    private Context context;
    private CustomBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setLayout();
    }

    private void setLayout(){
        cart = Cart.getInstance();

    }
}
