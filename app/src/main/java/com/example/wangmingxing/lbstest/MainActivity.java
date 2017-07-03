package com.example.wangmingxing.lbstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public LocationClient locationClient;
    private TextView textView;

    private MapView mapView;
    private BaiduMap baiduMap;

    private boolean isFirstLocation=true;


    private void navigateTo(BDLocation bdLocation){
        if(isFirstLocation){
            LatLng latLng=new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            MapStatusUpdate mapStatusUpdate=MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.setMapStatus(mapStatusUpdate);
            mapStatusUpdate=MapStatusUpdateFactory.zoomTo(16F);
            baiduMap.animateMapStatus(mapStatusUpdate);
            isFirstLocation=false;
        }

        MyLocationData.Builder mylocationBuilder=new MyLocationData.Builder();
        mylocationBuilder.latitude(bdLocation.getLatitude());
        mylocationBuilder.longitude(bdLocation.getLongitude());
        MyLocationData myLocationData=mylocationBuilder.build();
        baiduMap.setMyLocationData(myLocationData);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        locationClient=new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(new MyLocationListener());
        textView=(TextView)findViewById(R.id.position_text_view);

        mapView=(MapView)findViewById(R.id.bmapView);

        baiduMap= mapView.getMap();
        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.zoomTo(12.5F);

        baiduMap.setMapStatus(mapStatusUpdate);
        baiduMap.setMyLocationEnabled(true);

        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);

        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);

        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
        if(!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(
                    new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else{
            requestLocation();
        }

    }

   private void requestLocation(){
       initLocation();
       locationClient.start();
   }

   private void initLocation(){
       LocationClientOption option=new LocationClientOption();
       option.setScanSpan(5000);
       option.setIsNeedAddress(true);
       option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
       locationClient.setLocOption(option);
   }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this,"发生未知错误",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

             navigateTo(bdLocation);

           final StringBuilder currentPosition=new StringBuilder();
            currentPosition.append("维度：")
                           .append(bdLocation.getLatitude())
                           .append("\n")
                           .append("经度")
                           .append(bdLocation.getLongitude())
                    .append("\n")
            .append("国家：")
            .append(bdLocation.getCountry())
            .append("\n")
            .append("省")
            .append(bdLocation.getProvince())
            .append("\n")
            .append("市：")
            .append(bdLocation.getCity())
            .append("\n")
            .append("区")
            .append(bdLocation.getDistrict())
            .append("\n")
            .append("街道：")
            .append(bdLocation.getStreet())
            .append("\n");
            currentPosition.append("定位方式：");
            if(bdLocation.getLocType()==BDLocation.TypeGpsLocation){
            currentPosition.append("GPS");
            }else{
                currentPosition.append("网络");
            }
            Log.d("xiaomi", "============");
            Log.d("xiaomi", JSONObject.toJSONString(bdLocation));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(currentPosition);
                }
            });


        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
