package com.example.connectussocial;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;


public class SessionHandler {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EXPIRES = "expires";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMPTY = "";
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;

    public SessionHandler(Context mContext) {
        this.mContext = mContext;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    /**
     * método que salva os dados do usuario no login
     *
     * @param username
     * @param fullName
     */
    public void loginUserPerfil(String username, String fullName) {
        mEditor.putString(KEY_USERNAME, username);
        mEditor.putString(KEY_FULL_NAME, fullName);
        Date date = new Date();

        //Método que deixa o usuário ativo por 7 dias
        long millis = date.getTime() + (7 * 24 * 60 * 60 * 1000);
        mEditor.putLong(KEY_EXPIRES, millis);
        mEditor.commit();
    }

    /**
     * Verifica se o usuário já está logado
     *
     * @return
     */
    public boolean isLoggedIn() {
        Date currentDate = new Date();

        long millis = mPreferences.getLong(KEY_EXPIRES, 0);

        if (millis == 0) {
            return false;
        }
        Date expiryDate = new Date(millis);

        return currentDate.before(expiryDate);
    }

    public PerfilUser getUserDetails() {
        if (!isLoggedIn()) {
            return null;
        }
        PerfilUser userPerfil = new PerfilUser();
        userPerfil.setUsername(mPreferences.getString(KEY_USERNAME, KEY_EMPTY));
        userPerfil.setFullName(mPreferences.getString(KEY_FULL_NAME, KEY_EMPTY));
        userPerfil.setSessionExpiryDate(new Date(mPreferences.getLong(KEY_EXPIRES, 0)));

        return userPerfil;
    }

    public void logoutUserPerfil(){
        mEditor.clear();
        mEditor.commit();
    }

}

