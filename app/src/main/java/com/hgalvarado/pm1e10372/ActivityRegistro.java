package com.hgalvarado.pm1e10372;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ActivityRegistro extends AppCompatActivity {

    Spinner cmbRegistrarPais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        cmbRegistrarPais = (Spinner) findViewById(R.id.cmbRegistrarPais);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.paisArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbRegistrarPais.setAdapter(adapter);

    }
}