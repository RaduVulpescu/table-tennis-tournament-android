package com.example.tabletennistournament.services;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;

public class TTTAmplify extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("TTTAmplify", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("TTTAmplify", "Could not initialize Amplify", error);
        }
    }

}
