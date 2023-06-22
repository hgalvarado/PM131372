package com.hgalvarado.pm1e10372;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hgalvarado.pm1e10372.configuraciones.SQLiteConexion;
import com.hgalvarado.pm1e10372.configuraciones.Transacciones;

import java.io.ByteArrayOutputStream;

public class ActivityRegistro extends AppCompatActivity {
    private static final int SELECT_IMAGE_REQUEST = 1;
    private ImageView imgvFotoPerfil;

    Spinner cmbRegistrarPais;
    EditText txtRegistrarNombre, txtRegistrarTelefono, txtRegistrarNota;

    Button btnSalvarContactos,btnVisualizarContactos;
    ImageButton btnSeleccionarImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        cmbRegistrarPais = (Spinner) findViewById(R.id.cmbRegistrarPais);
        txtRegistrarNombre = (EditText) findViewById(R.id.txtRegistrarNombre);
        txtRegistrarTelefono = (EditText) findViewById(R.id.txtRegistrarTelefono);
        txtRegistrarNota = (EditText) findViewById(R.id.txtARegistrarNota);
        btnSalvarContactos = (Button) findViewById(R.id.btnSalvarContacto);
        imgvFotoPerfil = (ImageView) findViewById(R.id.imgvFotoPerfil);
        btnSeleccionarImagen = (ImageButton) findViewById(R.id.btnSeleccionarImagen);


        // Obtén los nombres de los países y los números de área desde los recursos
        String[] nombresPaises = getResources().getStringArray(R.array.paisArray);
        String[] numerosArea = getResources().getStringArray(R.array.numeros_area);
        // Crea un ArrayAdapter utilizando los nombres de los países
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresPaises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Asigna el ArrayAdapter al Spinner
        cmbRegistrarPais.setAdapter(adapter);
        cmbRegistrarPais.setSelection(2);//Seleccionar un valor por defecto
        // Agrega un listener al Spinner para actualizar el EditText cuando se selecciona un país
        cmbRegistrarPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String numeroArea = numerosArea[position];
                txtRegistrarTelefono.setText(numeroArea);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó ningún país
                txtRegistrarTelefono.setText("");
            }
        });

        //Seleccionar Imagen
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        //Salvar Contactos
        btnSalvarContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datoNombre = txtRegistrarNombre.getText().toString();
                String datoTelefono = txtRegistrarTelefono.getText().toString();
                String datoNota = txtRegistrarNota.getText().toString();

                if (datoNombre.isEmpty()){
                    txtRegistrarNombre.setError("Requerido: Debe de incluir un nombre");
                }
                if (datoTelefono.isEmpty()){
                    txtRegistrarTelefono.setError("Requerido: Debe de incluir un Numero de Telefono");
                }
                if (datoNota.isEmpty()){
                    txtRegistrarNota.setError("Requerido: Debe de incluir una Nota");
                }
                if (!datoNombre.isEmpty() && !datoTelefono.isEmpty() && !datoNota.isEmpty()){
                    AgregarContactos();
                }else{
                    Toast.makeText(getApplicationContext(),"Favor llene todos los campos requeridos",Toast.LENGTH_LONG).show();
                }

            }
        });

        //Visualizar Contactos
        btnVisualizarContactos = (Button) findViewById(R.id.btnVisuallizarContactos);
        btnVisualizarContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityVistaRegistro.class);
                startActivity(intent);
            }
        });
    }
    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imagenSeleccionada = data.getData();
            imgvFotoPerfil.setImageURI(imagenSeleccionada);
        }
    }
    private void AgregarContactos() {
        BitmapDrawable drawable = (BitmapDrawable) imgvFotoPerfil.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();


        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.nameDatabase,null,2);
        SQLiteDatabase db= conexion.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(Transacciones.pais, cmbRegistrarPais.getSelectedItem().toString());
        valores.put(Transacciones.nombres, txtRegistrarNombre.getText().toString());
        valores.put(Transacciones.telefono, txtRegistrarTelefono.getText().toString());
        valores.put(Transacciones.nota, txtRegistrarNota.getText().toString());
        valores.put("imagen",byteArray);

        Long result = db.insert(Transacciones.tableContactos,Transacciones.id,valores);
        Toast.makeText(getApplicationContext(), "Registro ingresado: " + result.toString(),Toast.LENGTH_LONG).show();
        db.close();
        CleanScreen();
    }
    private void CleanScreen() {
        txtRegistrarNombre.setText("");
        imgvFotoPerfil.setImageResource(R.drawable.imagenpordefecto);
        cmbRegistrarPais.setSelection(2);
        txtRegistrarTelefono.setText("+504");
        txtRegistrarNota.setText("");
    }

}