package com.app.tunamusic;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("ACTION_PLAY")) {
                MyService.getInstance().playMusic();
            } else if (intent.getAction().equals("ACTION_PAUSE")) {
                MyService.getInstance().pauseMusic();
            }
        }
    }
}
