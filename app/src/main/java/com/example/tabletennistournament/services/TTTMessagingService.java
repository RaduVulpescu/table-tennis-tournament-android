package com.example.tabletennistournament.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.tabletennistournament.models.DeviceInformationModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.tabletennistournament.services.Common.increaseTimeout;

public class TTTMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        registerDeviceForPushNotifications(s);
    }

    private void registerDeviceForPushNotifications(String token) {
        Gson gson = GsonSingleton.getInstance();
        JSONObject obj = null;

        try {
            obj = new JSONObject(gson.toJson(new DeviceInformationModel(token)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (obj != null) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiRoutes.REGISTER_DEVICE_ROUTE, obj,
                    response -> Log.e("REGISTER DEVICE", gson.toJson(response)),
                    error -> Log.e("REGISTER DEVICE", gson.toJson(error))
            );

            RequestQueueSingleton.getInstance(this).add(increaseTimeout(jsonObjectRequest));
        }
    }
}
