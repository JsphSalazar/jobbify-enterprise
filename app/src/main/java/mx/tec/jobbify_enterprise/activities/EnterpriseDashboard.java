package mx.tec.jobbify_enterprise.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mx.tec.jobbify_enterprise.R;

public class EnterpriseDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterprise_dashboard);
        setTitle("Dashboard");
    }
}
