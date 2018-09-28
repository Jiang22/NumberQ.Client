package langotec.numberq.client.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import langotec.numberq.client.R;

public class SelectedActivity extends AppCompatActivity {
    private ImageView foodImage, subtractImage, plusImage;
    private TextView nameText, descText, waitText, timeText, totalText;
    private EditText quantityText;
    private Intent intent;
    private Menu menu;
    private Cart cart;
    private Context context;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected);
        context = this;
        intent = getIntent();

        //region 第一次進入Activity還是翻轉螢幕的判斷及處理
        if (savedInstanceState == null)
            menu = (Menu) intent.getSerializableExtra("Menu");
        else
            menu = (Menu) savedInstanceState.getSerializable("Menu");
        //endregion

        findEntity();
        setLayout();
        subtractImage.setOnClickListener(new QuantityChange());
        plusImage.setOnClickListener(new QuantityChange());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Menu", menu);
    }

    private void findEntity(){
        rootView = (View) findViewById(R.id.selected_constraintLayout);
        foodImage = (ImageView) findViewById(R.id.foodImage);
        subtractImage = (ImageView) findViewById(R.id.subtractView);
        plusImage = (ImageView) findViewById(R.id.plusView);
        nameText = (TextView) findViewById(R.id.nameText);
        descText = (TextView) findViewById(R.id.descText);
        waitText = (TextView) findViewById(R.id.waitText);
        timeText = (TextView) findViewById(R.id.timeText);
        subtractImage = (ImageView) findViewById(R.id.subtractView);
        quantityText = (EditText) findViewById(R.id.quantityText);
        plusImage = (ImageView) findViewById(R.id.plusView);
        totalText = (TextView) findViewById(R.id.totalText);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})//這行不知道幹嘛，系統想加進去而已
    private void setLayout(){
        setTitle(menu.getHeadName() + "-" + menu.getBranchName());
        menu.setImageView(foodImage);
        nameText.setText(menu.getProductName() + "   " + getResources().getString(R.string.menu_NT) + menu.getPrice());
        descText.setText(menu.getDesc());
        waitText.setText(getResources().getString(R.string.menu_waitNum) + " " +  menu.getWaitNum());
        timeText.setText(getResources().getString(R.string.menu_waitTime) + " " + menu.getWaitTime());
        quantityText.setText("" + menu.getQuantityNum());
        //設定EditText的輸入類型
        quantityText.setInputType(InputType.TYPE_CLASS_NUMBER);
        //加入鍵盤是否跳出的監聽器
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new MyTypingListener());
        setTotalText();
    }

    //region 設定最下方的總價文字
    private void setTotalText(){
        totalText.setText(getResources().getString(R.string.menu_NT) + (Integer.valueOf(menu.getPrice()) * menu.getQuantityNum()));
    }
    //endregion

    //region 按下+-圓形按鈕的Listener
    private class QuantityChange implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            int quantity = menu.getQuantityNum();
            switch (view.getId()){
                case R.id.subtractView:
                    menu.setQuantityNum(--quantity);
                    quantityText.setText("" + menu.getQuantityNum());
                    break;
                case R.id.plusView:
                    menu.setQuantityNum(++quantity);
                    quantityText.setText("" + menu.getQuantityNum());
                    break;
            }
            setTotalText();
        }
    }
    //endregion

    //region 按下加到購物車的處理method
    public void onCartClick(View view){
        cart = Cart.getInstance();
        showDialog();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        int i = 0;
        for (Menu menus : cart ) {
            if (menus.getProductName().equals(menu.getProductName())) {
                i ++;
                builder.setMessage(getResources().getString(R.string.menu_cartHas) + i +
                        getResources().getString(R.string.menu_an) + menu.getProductName()
                        + getResources().getString(R.string.menu_addAnother));
            }
        }
        builder.setTitle(R.string.menu_cartInfo)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.menu_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //強制製造一個新的相同Menu實體，防止物件指標重複指到相同Menu
                        Menu m = new Menu(menu.getImageURL(), menu.getProductName(),
                                menu.getPrice(), menu.getDesc(), menu.getHeadName(), menu.getBranchName());
                        m.setQuantityNum(menu.getQuantityNum());
                        cart.add(m);
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.selected_constraintLayout),
                                R.string.menu_added, Snackbar.LENGTH_SHORT );
                        snackbar.show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
    //endregion

    //region 判斷使用者是否正在輸入，及輸入完成後處理quantityText後的內容 && focusListener
    private class MyTypingListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
//            Log.e("heightDiff", heightDiff + "");
//            Log.e("dptoPx", dpToPx(context, 200) + "");
            if (heightDiff > dpToPx(context, 200)) {
//                Log.e("SelectedActivity", "keyboard opened");
                quantityText.requestFocus();    //focus EditText (requestFocus is a method from View )
                quantityText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //移動EditText的游標放在內容字串的最後面
                        quantityText.setSelection(quantityText.getText().length());
                    }
                }, 50);//一定要delay動作才有移動游標的效果，不知為什麼
            }else {
//                Log.e("SelectedActivity", "keyboard closed");
                menu.setQuantityNum(Integer.parseInt(quantityText.getText().toString()));
                quantityText.setText("" + menu.getQuantityNum());
                quantityText.clearFocus();  //clear EditText focus
                setTotalText();
            }
        }
    }

    private static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    //endregion
}
