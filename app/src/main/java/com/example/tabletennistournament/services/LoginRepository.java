package com.example.tabletennistournament.services;

import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.example.tabletennistournament.models.LoggedInUser;
import com.example.tabletennistournament.ui.login.LoginViewModel;

public class LoginRepository {

    private final String TAG = LoginViewModel.class.getSimpleName();

    private static volatile LoginRepository instance;

    private LoggedInUser user = null;

    private LoginRepository() {
    }

    public static LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        Amplify.Auth.signOut(
                () -> Log.i(TAG, "Signed out successfully"),
                error -> Log.e(TAG, error.toString())
        );
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public void login(String username) {
        setLoggedInUser(new LoggedInUser(username));
    }
}