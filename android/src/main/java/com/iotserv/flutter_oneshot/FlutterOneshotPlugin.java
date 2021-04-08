package com.iotserv.flutter_oneshot;

import androidx.annotation.NonNull;

import com.winnermicro.smartconfig.ConfigType;
import com.winnermicro.smartconfig.IOneShotConfig;
import com.winnermicro.smartconfig.SmartConfigFactory;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterOneshotPlugin
 */
public class FlutterOneshotPlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private int timeout = 60;//miao
    private FlutterPluginBinding myFlutterPluginBinding;
    private MethodChannel channel;

    private String ssid;
    private String password = null;
    private IOneShotConfig oneshotConfig = null;
    private SmartConfigFactory factory = null;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        myFlutterPluginBinding = flutterPluginBinding;
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_oneshot");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("start")) {
            //参数获取
            ssid = call.argument("ssid");
            password = call.argument("password");
            try {
                timeout = call.argument("timeout");
            } catch (Exception e) {
                e.printStackTrace();
                timeout = 30;
            }
            factory = new SmartConfigFactory();
            oneshotConfig = factory.createOneShotConfig(ConfigType.UDP);
            oneshotConfig.start(ssid, password, timeout, myFlutterPluginBinding.getApplicationContext());
            final Map<String, String> ret = new HashMap<String, String>();
            ret.put("result", "success");
            result.success(ret);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
