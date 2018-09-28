package langotec.numberq.client.menu;

import java.io.Serializable;
import java.util.ArrayList;

//實作Serializable，讓intent或bundle可以丟此物件或存檔
public class Cart extends ArrayList<Menu> implements Serializable {

    //一位消費者應只有一台購物車，所以使用singleton模式
    private volatile static Cart singletonCart = new Cart();

    private Cart(){}

    public static Cart getInstance(){
        //先檢查一次是否為null, 如果是才進入同步化區間
        if (singletonCart == null){
            //鎖定Cart只能被一個Thread執行
            synchronized (Cart.class){
                //在同步化狀態下檢查static的變數是否為null, 目的為避免不同Thread的狀態下建立實體
                if (singletonCart == null)
                    singletonCart = new Cart();
            }
        }
        return singletonCart;
    }
}
