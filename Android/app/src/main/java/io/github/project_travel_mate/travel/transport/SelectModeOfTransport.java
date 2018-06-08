package io.github.project_travel_mate.travel.transport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.project_travel_mate.R;

public class SelectModeOfTransport extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.car)
    LinearLayout car;
    @BindView(R.id.train)
    LinearLayout train;
    @BindView(R.id.bus)
    LinearLayout bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode_of_transport);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        car.setOnClickListener(this);
        train.setOnClickListener(this);
        bus.setOnClickListener(this);

        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Select Mode of Transport");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.car:
                i = new Intent(SelectModeOfTransport.this, CarDirections.class);
                startActivity(i);
                break;
            case R.id.bus:
                i = new Intent(SelectModeOfTransport.this, BusList.class);
                startActivity(i);
                break;
            case R.id.train:
                i = new Intent(SelectModeOfTransport.this, TrainList.class);
                startActivity(i);
                break;
        }
    }
}
