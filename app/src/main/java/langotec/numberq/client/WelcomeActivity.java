package langotec.numberq.client;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WelcomeActivity extends AppCompatActivity {

    //Location
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    public static LocationManager lm;
    public static Location currentLocation = null;
    public static LocationListener ll;
    private static double lat, lng;

    private static final String MENU_SERVER =
            "https://ivychiang0304.000webhostapp.com/numberq/menuquery.php";
    private static final String STORE_SERVER =
            "https://flashmage.000webhostapp.com/query.php?p=pass&w=storeList&n=10";
    private String qResult = "no record";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        context = this;

        new OkHttpHandler().execute(MENU_SERVER);
        //new OkHttpHandler().execute(STORE_SERVER);

        //Location
        // 取得定位服務的LocationManager物件
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 檢查是否有啟用GPS
        try{
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // 顯示對話方塊啟用GPS
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.locationManager)
                        .setMessage(R.string.locationMessage)
                        .setPositiveButton(R.string.setPositiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 使用Intent物件啟動設定程式來更改GPS設定
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(R.string.setNegativeButton, null).create().show();
            }
        }catch (NullPointerException e){
            Log.e("Location",e.toString());
        }

        //檢查版本和權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ll = new MyLocationListener();
        int minTime = 1000; // 毫秒
        float minDistance = 1; // 公尺
        try {  // 註冊更新的傾聽者物件
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime, minDistance, ll);
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime, minDistance, ll);
        }
        catch(SecurityException sex) {
            Log.e("GPS", "GPS權限失敗..." + sex.getMessage());
            Toast.makeText(this, "GPS權限失敗...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {  // 取消註冊更新的傾聽者物件
            lm.removeUpdates(ll);
        }
        catch(SecurityException sex) {
            Log.e("GPS", "GPS權限失敗..." + sex.getMessage());
            Toast.makeText(this, "GPS權限失敗...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 已經取得權限
                Toast.makeText(this, "取得權限取得GPS資訊",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "直到取得權限, 否則無法取得GPS資訊",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OkHttpHandler extends AsyncTask <String, Void, Void> {

        OkHttpClient client = new OkHttpClient();

        @Override
        protected Void doInBackground(String... urls) {

            OkHttpClient okHttpClient = new OkHttpClient();

            //String... urls傳進來的是陣列，所以用for迴圈跑完全部的內容
            for (int index = 0; index < urls.length; index++) {

                // FormBody放要傳的參數和值
                FormBody formBody = new FormBody.Builder()
                        .add("sname", "鼎泰豐")
                        .build();

                // 建立Request，設置連線資訊
                Request request = new Request.Builder()
                        .url(urls[index])
                        .post(formBody) // 使用post連線
                        .build();

                // 建立Call
                Call call = okHttpClient.newCall(request);

                // 執行Call連線到網址
                try {
                    Response response = call.execute();//call.execute為同步工作
                    if (response.isSuccessful() && response.code() == 200) {
                        //同步方式下得到返回结果
                        // response.code() return the HTTP status
                        qResult = response.body().string().trim();
                        if (qResult.equals("no record")) {
                            Log.d("OkHttp result", "no record");
                        } else {
                            Log.d("OkHttp result", qResult);
                        }
//                    createFile(qResult);
                        response.close();
                    } else {
                        Log.e("failed", " no Data!");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

            Bundle bundle = new Bundle();
            bundle.putDouble("lat",lat);
            bundle.putDouble("lng",lng);

            if (qResult != "no record") {
                startActivity(new Intent()
                        .setClass(getApplicationContext(),
                                MainActivity.class)
                .putExtras(bundle));
                finish();
            }else{
                showDialog();
            }

        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提醒訊息")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("目前無法連線，請檢查您的網路設定，謝謝您")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new OkHttpHandler().execute(MENU_SERVER );
                    }
                })
                .setNegativeButton("離開", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create().show();
    }

    class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location current) {

            if (current != null) {
                currentLocation = current;
                // 取得經緯度
                lat = current.getLatitude();
                lng = current.getLongitude();
                Toast.makeText(WelcomeActivity.this, "經緯度座標變更....", Toast.LENGTH_SHORT).show();
                Log.e("GPS","緯度: " + lat + " 經度: " + lng);
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}
