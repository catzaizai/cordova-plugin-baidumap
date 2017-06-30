package com.cat.cordova.plugin.BaiduMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.LocationMode;
import com.cordovaPlugin.JsonUtil;

import android.widget.Toast;

import java.util.Map;

import static com.baidu.trace.LocationMode.Battery_Saving;
import static com.baidu.trace.LocationMode.Device_Sensors;
import static com.baidu.trace.LocationMode.High_Accuracy;

public class TraceService {

    private LBSTraceClient client;
    private Trace trace;
    static String debugTag = "BaiduTrace";

    public TraceService(Context context){

    }

}