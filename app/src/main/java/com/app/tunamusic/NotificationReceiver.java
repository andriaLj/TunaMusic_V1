package com.app.tunamusic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
public class NotificationReceiver extends BroadcastReceiver {
    private MyService mservice = null;
    private boolean isBound = false;

    public void onCreate(){
//        Toast.makeText(mservice, "coucou", Toast.LENGTH_SHORT).show();

        final ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                MyService.LocalBinder binder = (MyService.LocalBinder) service;
                mservice = binder.getService();
                isBound = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName className) {
                mservice = null;
                isBound = false;
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        switch (action){
            case"Pause":
                if (isBound) mservice.pauseMusic();
                break;

            case "Play":
               if (isBound) mservice.playMusic();
               break;

            case "Next":
                if (isBound && mservice != null) mservice.setPath(mservice.getNextPathMusic());
                break;

            case "Previous":
                if (isBound && mservice != null) mservice.setPath(mservice.getPreviousPathMusic());
                break;

            default:break;
        }
    }
}