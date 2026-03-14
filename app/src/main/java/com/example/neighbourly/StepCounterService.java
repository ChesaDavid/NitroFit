package com.example.neighbourly;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class StepCounterService extends Service {
    public StepCounterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}