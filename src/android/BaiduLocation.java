package com.cat.cordova.plugin.BaiduMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import android.content.Context;


public class BaiduLocation extends CordovaPlugin {

  private LocationService locationService;

  String TAG = "BaiduLocationPlugin";

  public static CallbackContext cbCtx = null;

  Context context;

  String [] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    context = this.cordova.getActivity().getApplicationContext();
    if (locationService == null) {
      locationService = new LocationService(context);
      locationService.registerListener(mListener);
      locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }
  }

  /**
   * 插件主入口
   */
  @Override
  public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
    boolean ret = false;

    if (action.equals("getCurrentPosition")) {
      cbCtx = callbackContext;

      PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
      pluginResult.setKeepCallback(true);
      cbCtx.sendPluginResult(pluginResult);

      if(hasPermisssion()){
        locationService.start();
      } else{
        requestPermissions(0);
      }


      ret = true;
    }

    return ret;
  }

  private BDLocationListener mListener = new BDLocationListener() {

    @Override
    public void onReceiveLocation(BDLocation location) {

      if (null != location && location.getLocType() != BDLocation.TypeServerError) {

        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        /**
         * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
         * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
         */
        sb.append(location.getTime());
        sb.append("\nlocType : ");// 定位类型
        sb.append(location.getLocType());
        sb.append("\nlocType description : ");// *****对应的定位类型说明*****
        sb.append(location.getLocTypeDescription());
        sb.append("\nlatitude : ");// 纬度
        sb.append(location.getLatitude());
        sb.append("\nlongitude : ");// 经度
        sb.append(location.getLongitude());
        sb.append("\nradius : ");// 半径
        sb.append(location.getRadius());
        sb.append("\ncountryCode : ");// 国家码
        sb.append(location.getCountryCode());
        sb.append("\ncountry : ");// 国家名称
        sb.append(location.getCountry());
        sb.append("\ncitycode : ");// 城市编码
        sb.append(location.getCityCode());
        sb.append("\ncity : ");// 城市
        sb.append(location.getCity());
        sb.append("\ndistrict : ");// 区
        sb.append(location.getDistrict());
        sb.append("\nstreet : ");// 街道
        sb.append(location.getStreet());
        sb.append("\naddress : ");// 地址信息
        sb.append(location.getAddrStr());
        sb.append("\nuserIndoorState: ");// *****返回用户室内外判断结果*****
        sb.append(location.getUserIndoorState());
        sb.append("\ndirection: ");
        sb.append(location.getDirection());// 方向
        sb.append("\ndescribe: ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        sb.append("\npoi: ");// POI信息
        if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
          for (int i = 0; i < location.getPoiList().size(); i++) {
            Poi poi = (Poi) location.getPoiList().get(i);
            sb.append(poi.getName() + ";");
          }
        }
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
          sb.append("\nspeed : ");
          sb.append(location.getSpeed());// 速度 单位：km/h
          sb.append("\nsatellite : ");
          sb.append(location.getSatelliteNumber());// 卫星数目
          sb.append("\nheight : ");
          sb.append(location.getAltitude());// 海拔高度 单位：米
          sb.append("\ngps status : ");
          sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
          sb.append("\ndescribe : ");
          sb.append("gps定位成功");
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
          // 运营商信息
          if (location.hasAltitude()) {// *****如果有海拔高度*****
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
          }
          sb.append("\noperationers : ");// 运营商信息
          sb.append(location.getOperators());
          sb.append("\ndescribe : ");
          sb.append("网络定位成功");

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
          sb.append("\ndescribe : ");
          sb.append("离线定位成功，离线定位结果也是有效的");

        } else if (location.getLocType() == BDLocation.TypeServerError) {
          sb.append("\ndescribe : ");
          sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
          sb.append("\ndescribe : ");
          sb.append("网络不同导致定位失败，请检查网络是否通畅");

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
          sb.append("\ndescribe : ");
          sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, sb.toString());
        pluginResult.setKeepCallback(true);
        cbCtx.sendPluginResult(pluginResult);
        locationService.stop();
      }
    }

    public void onConnectHotSpotMessage(String s, int i) {
    }
  };

  public void onRequestPermissionResult(int requestCode, String[] permissions,
                                        int[] grantResults) throws JSONException
  {
    PluginResult result;
    //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
    if(cbCtx != null) {
      for (int r : grantResults) {
        if (r == PackageManager.PERMISSION_DENIED) {
          LOG.d(TAG, "Permission Denied!");
          result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
          cbCtx.sendPluginResult(result);
          return;
        }

      }
      locationService.start();
    }
  }

  public boolean hasPermisssion() {
    for(String p : permissions)
    {
      if(!PermissionHelper.hasPermission(this, p))
      {
        return false;
      }
    }
    return true;
  }

  public void requestPermissions(int requestCode)
  {
    PermissionHelper.requestPermissions(this, requestCode, permissions);
  }
}


