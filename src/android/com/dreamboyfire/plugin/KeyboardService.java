package com.dreamobyfire.plugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class KeyboardService extends CordovaPlugin {

    private ServiceConnection connection;
    private BackgroundTaskService backgroundTaskService;
    private Intent serviceIntent;
    private boolean isConnected = false;
    private boolean isDisconnected = false;
    private boolean isVolume_Changed = false;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }

        if (action.equals("start")) {
            startBackgroundService();
            return true;
        }

        if (action.equals("stop")) {
            stopBackgroundService();
            return true;
        }

        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    /**
     * 启动服务
     */
    private void startBackgroundService() {
        Activity context = cordova.getActivity();
        serviceIntent = new Intent(context, BackgroundTaskService.class);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                isConnected = true;
                isDisconnected = false;

                BackgroundTaskService.Binder binder = (BackgroundTaskService.Binder) service;
                backgroundTaskService = binder.getService();
                backgroundTaskService.setDataCallback(new BackgroundTaskService.DataCallback() {
                    @Override
                    public void onDataChange(String data) {
                        if (data.trim().equals("android.media.VOLUME_CHANGED_ACTION")) {
                            isVolume_Changed = true;
                        } else {
                            isVolume_Changed = false;
                        }
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected = false;
                isDisconnected = true;
            }
        };

        context.startService(serviceIntent);
        context.bindService(serviceIntent, connection, context.BIND_AUTO_CREATE);
    }

    /**
     * 停止服务
     */
    private void stopBackgroundService() {
        Activity context = cordova.getActivity();
        context.stopService(serviceIntent);
        context.unbindService(connection);
    }
}
