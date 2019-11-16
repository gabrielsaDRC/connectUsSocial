package com.example.connectussocial;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.*;

public class DashboardActivitity extends AppCompatActivity {
    private SessionHandler session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_activitity);
        session = new SessionHandler(getApplicationContext());
        PerfilUser user = session.getUserDetails();
        TextView welcomeText = findViewById(R.id.welcomeText);

        welcomeText.setText("Bem vindo "+user.getFullName()+", Sua sessão vai terminar em "+user.getSessionExpiryDate());

        Button logoutBtn = findViewById(R.id.btnLogoutPerfil);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUserPerfil();
                Intent i = new Intent(DashboardActivitity.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        });
    }
}
