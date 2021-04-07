package com.iotserv.flutter_oneshot;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import com.winnermicro.smartconfig.*;

import java.util.HashMap;
import java.util.Map;

/** FlutterOneshotPlugin */
public class FlutterOneshotPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private int timeout = 60;//miao
  private FlutterPluginBinding flutterPluginBinding;
  private MethodChannel channel;

  private String ssid;
  private String password = null;
  private IOneShotConfig oneshotConfig = null;
  private SmartConfigFactory factory = null;

  public FlutterOneshotPlugin(FlutterPluginBinding flutterPluginBinding,MethodChannel channel) {
    this.flutterPluginBinding = flutterPluginBinding;
    this.channel = channel;
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_oneshot");
    channel.setMethodCallHandler(new FlutterOneshotPlugin(flutterPluginBinding,channel));
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("start")){
      //参数获取
      ssid = call.argument("ssid");
      password = call.argument("password");
      try {
        timeout = call.argument("timeout");
      }catch (Exception e){
        e.printStackTrace();
        timeout = 30;
      }
      new Thread(new UDPReqThread(result)).start();
    }
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  class UDPReqThread implements Runnable {
    private Result result;
    public UDPReqThread(Result result)
    {
      this.result = result;
    }

    public void run() {
      final Map<String, String> ret = new HashMap<String, String>();
      factory = new SmartConfigFactory();
      oneshotConfig = factory.createOneShotConfig(ConfigType.UDP);
      //      start config
      try {
        oneshotConfig.start(ssid, password, timeout, flutterPluginBinding.getApplicationContext());
      }
      catch (OneShotException e) {
        Log.d("===oneshot-OneShotE===",e.getMessage());
        e.printStackTrace();
        int code = e.getErrorID();
        Log.d("onshot err", String.valueOf(code));
      }
      catch (Exception e) {
        Log.d("===oneshot-exception===",e.getMessage());
        e.printStackTrace();
      } finally {
        oneshotConfig.stop(	);
        ret.put("result","success");
        Log.d("===oneshot-success===","success");
        result.success(ret);
      }
    }
  }
}
