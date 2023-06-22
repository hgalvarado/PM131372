package com.hgalvarado.pm1e10372;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ActivityEditarContacto extends AppCompatActivity {
    private static final int SELECT_IMAGE_REQUEST = 1;

    EditText txtEditarNombre,txtEditarTelefono,txtEditarNota,txtId;
    Spinner cmbEditarPais;
    SQLiteConexion conexion;
    ImageView verImagen;
    ImageButton btnEditarFotoPerfil;
    Button btnSalvarEdicion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);
        String idContacto = getIntent().getStringExtra("id");
        String paisContacto = getIntent().getStringExtra("pais");
        String nombreContacto = getIntent().getStringExtra("nombre");
        String telefonoContacto = getIntent().getStringExtra("telefono");
        String notaContacto = getIntent().getStringExtra("nota");
        txtEditarNombre = (EditText) findViewById(R.id.txtEditarNombre);
        txtEditarTelefono = (EditText) findViewById(R.id.txtEditarTelefono);
        txtEditarNota = (EditText) findViewById(R.id.txtEditarNota);
        txtEditarNombre = (EditText) findViewById(R.id.txtEditarNombre);
        cmbEditarPais = (Spinner) findViewById(R.id.cmbEditarPais);
        txtId = (EditText) findViewById(R.id.txtId);
        txtId.setEnabled(false);
        verImagen=(ImageView) findViewById(R.id.imgEditarFotoPerfil);
        btnEditarFotoPerfil = (ImageButton) findViewById(R.id.btnEditarFotoPerfil);
        btnSalvarEdicion = (Button) findViewById(R.id.btnSalvarEdicion);

        conexion = new SQLiteConexion(this, Transacciones.nameDatabase, null, 2);

        // Obtén los nombres de los países y los números de área desde los recursos
        String[] nombresPaises = getResources().getStringArray(R.array.paisArray);
        String[] numerosArea = getResources().getStringArray(R.array.numeros_area);
        // Crea un ArrayAdapter utilizando los nombres de los países
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresPaises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Asigna el ArrayAdapter al Spinner
        cmbEditarPais.setAdapter(adapter);
        cmbEditarPais.setSelection(2);//Seleccionar un valor por defecto
        // Agrega un listener al Spinner para actualizar el EditText cuando se selecciona un país
        cmbEditarPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String TelefonoOriginal = "";

                if (telefonoContacto.length() > 4) {
                    TelefonoOriginal = telefonoContacto.substring(4);
                }
                String numeroArea = numerosArea[position]+TelefonoOriginal;

                txtEditarTelefono.setText(numeroArea);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se seleccionó ningún país
                txtEditarTelefono.setText("");
            }
        });

        txtId.setText(idContacto);
        txtEditarNombre.setText(nombreContacto);
        txtEditarTelefono.setText(telefonoContacto);
        txtEditarNota.setText(notaContacto);

        // Obtén el adaptador del Spinner
        ArrayAdapter<String> dap = (ArrayAdapter<String>) cmbEditarPais.getAdapter();

        // Obtén la posición del elemento según el texto
        int posicion = dap.getPosition(paisContacto);

        // Selecciona el elemento en la posición obtenida
        cmbEditarPais.setSelection(posicion);
        obtenerImagen(idContacto);

        btnEditarFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        //Salvar Contactos
        btnSalvarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String datoNombre = txtEditarNombre.getText().toString();
                String datoTelefono = txtEditarTelefono.getText().toString();
                String datoNota = txtEditarNota.getText().toString();

                if (datoNombre.isEmpty()){
                    txtEditarNombre.setError("Requerido: Debe de incluir un nombre");
                }
                if (datoTelefono.isEmpty()){
                    txtEditarTelefono.setError("Requerido: Debe de incluir un Numero de Telefono");
                }
                if (datoNota.isEmpty()){
                    txtEditarNota.setError("Requerido: Debe de incluir una Nota");
                }
                if (!datoNombre.isEmpty() && !datoTelefono.isEmpty() && !datoNota.isEmpty()){
                    AgregarEdicionContactos(idContacto);
                }else{
                    Toast.makeText(getApplicationContext(),"Favor llene todos los campos requeridos",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void obtenerImagen(String id) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Bitmap bitmap;
        String selectQuery = "SELECT imagen FROM contactos WHERE id = ?";
        // Ejecuta la consulta
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        // Verifica si se encontraron resultados
        if (cursor.moveToFirst()) {
            // Obtiene los datos de la imagen en forma de arreglo de bytes
            byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow("imagen"));
            // Convierte los datos de la imagen en un objeto Bitmap
            bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        else{
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imagenpordefecto);
        }
        verImagen.setImageBitmap(bitmap);
        // Cierra el cursor y la conexión a la base de datos
        cursor.close();
        db.close();
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
            verImagen.setImageURI(imagenSeleccionada);
        }
    }


    private void AgregarEdicionContactos(String idContactoActualizar) {
        BitmapDrawable drawable = (BitmapDrawable) verImagen.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        conexion = new SQLiteConexion(this, Transacciones.nameDatabase,null,2);
        SQLiteDatabase db= conexion.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(Transacciones.pais, cmbEditarPais.getSelectedItem().toString());
        valores.put(Transacciones.nombres, txtEditarNombre.getText().toString());
        valores.put(Transacciones.telefono, txtEditarTelefono.getText().toString());
        valores.put(Transacciones.nota, txtEditarNota.getText().toString());
        valores.put("imagen",byteArray);

        String condicion = "id = ?";
        String[] argumentos = {String.valueOf(idContactoActualizar)};
        int filasActualizadas = db.update("contactos", valores, condicion, argumentos);

        // Verificar la cantidad de filas eliminadas
        if (filasActualizadas > 0) {
            // El dato se eliminó exitosamente
            Toast.makeText(getApplicationContext(),"CONTACTO ACTUALIZADO",Toast.LENGTH_LONG).show();
        } else {
            // No se encontró el dato o no se pudo eliminar
            Toast.makeText(getApplicationContext(),"NO SE ACTUALIZO EL CONTACTO",Toast.LENGTH_LONG).show();
        }


        db.close();

        //Volver a la pantalla de listas de contactos
        Intent intent = new Intent(getApplicationContext(), ActivityVistaRegistro.class);
        startActivity(intent);

    }
}