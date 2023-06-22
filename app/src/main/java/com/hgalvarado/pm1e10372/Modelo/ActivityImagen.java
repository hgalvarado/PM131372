package com.hgalvarado.pm1e10372.Modelo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.hgalvarado.pm1e10372.R;
import com.hgalvarado.pm1e10372.configuraciones.SQLiteConexion;
import com.hgalvarado.pm1e10372.configuraciones.Transacciones;

import java.util.ArrayList;

public class ActivityImagen extends AppCompatActivity {
    SQLiteConexion conexion;
    ImageView verImagen;
    ArrayList<Contactos> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);
        conexion = new SQLiteConexion(this, Transacciones.nameDatabase, null, 2);
         verImagen = findViewById(R.id.previwImagen);
        obtenerImagen(getIntent().getStringExtra("id"));
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

}