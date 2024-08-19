package com.capstone.merkado.Exceptions;

import android.util.Log;

import androidx.annotation.NonNull;

import com.capstone.merkado.Application.Merkado;

public class Crash implements Thread.UncaughtExceptionHandler {
    private final Thread.UncaughtExceptionHandler defaultUEH;

    public Crash() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        // Trigger the desired function or action
        handleAppCrash(throwable);

        // Pass the exception to the default handler (optional)
        if (defaultUEH != null) {
            defaultUEH.uncaughtException(thread, throwable);
        }
    }

    private void handleAppCrash(Throwable throwable) {
        // Handle the crash here
        // Example: Log the crash or send crash data to a server
        Log.e("AppCrash", "App crashed due to: " + throwable.getMessage());

        Merkado merkado = Merkado.getInstance();
        merkado.logOutToServer();

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}

