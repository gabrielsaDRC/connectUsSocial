package com.example.connectussocial;

import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.google.firebase.auth.*;
import com.squareup.picasso.*;
import com.xwray.groupie.*;

public class PerfilActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_perfil);

    }

    public void nextActivity(View view) {
        Intent intent = new Intent(PerfilActivity.this, MessagesActivity.class);
        startActivity(intent);
    }

    public void colocaImagem() {
        String uid = FirebaseAuth.getInstance().getUid();

    }

}
