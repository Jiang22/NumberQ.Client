package langotec.numberq.client.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import langotec.numberq.client.R;
import okhttp3.OkHttpClient;

public class MenuActivity extends AppCompatActivity {

    private ListView listView;
    private Context context;
    private ArrayList<Menu> menus;
    private CustomBaseAdapter adapter;
    private String headName;
    private String branchName;
    private OkHttpClient conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = this;
        setLayout();

    }

    private void setLayout() {
        listView = findViewById(R.id.list);
        menus = new ArrayList<>();
        conn = new OkHttpClient().newBuilder().build();

        headName = "鼎泰豐";
        branchName = "信義店";

        //Todo: 各種Menu所需參數從網路下載
        setTitle(headName + " - " + branchName);

        listView.setEmptyView(findViewById(R.id.emptyView));


//        menus.add(new Menu("https://ivychiang0304.000webhostapp.com/numberq/images/dintaifun/bn057.png", "小菜", "70", getDesc(R.string.dessert1), headName, branchName));
//        menus.add(new Menu("https://ivychiang0304.000webhostapp.com/numberq/images/dintaifun/bn065.png", "乾煸四季豆", "130", getDesc(R.string.dessert2), headName, branchName));
//        menus.add(new Menu("https://ivychiang0304.000webhostapp.com/numberq/images/dintaifun/bn061.png", "炸排骨", "130", getDesc(R.string.dessert3), headName, branchName));

        menus.add(new Menu("https://www.dintaifung.com.tw/archive/images/food/BN057.png", "小菜", "70", getDesc(R.string.menu_dessert1), headName, branchName));
        menus.add(new Menu("https://www.dintaifung.com.tw/archive/images/food/BN065.png", "乾煸四季豆", "130", getDesc(R.string.menu_dessert2), headName, branchName));
        menus.add(new Menu("https://www.dintaifung.com.tw/archive/images/food/BN061.png", "炸排骨", "130", getDesc(R.string.menu_dessert3), headName, branchName));

        adapter = new CustomBaseAdapter(context, menus);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Menu menu = (Menu) adapterView.getItemAtPosition(i);
                Intent intent = new Intent();
                //把被按下的Menu物件放進intent
                intent.putExtra("Menu", menu);
                intent.setClass(context, SelectedActivity.class);
                startActivity(intent);
            }
        });
    }

    //Todo: 取得Menu描述(從網路上抓資料)
    private String getDesc(int id) {
        return getResources().getString(id);
    }

}
