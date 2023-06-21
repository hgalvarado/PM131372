package com.hgalvarado.pm1e10372;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hgalvarado.pm1e10372.Modelo.Contactos;
import com.hgalvarado.pm1e10372.configuraciones.SQLiteConexion;
import com.hgalvarado.pm1e10372.configuraciones.Transacciones;

import java.util.ArrayList;

public class ActivityVistaRegistro extends AppCompatActivity {
    private static final int REQUIERE_LLAMAR_TELEFONO = 1;

    SQLiteConexion conexion;
    ListView listaContactos;
    ArrayList<Contactos>lista;
    ArrayList<String>ArregloContactos;

    Button llamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_registro);

        llamar = (Button) findViewById(R.id.btnCompartirContacto);

        conexion = new SQLiteConexion(this, Transacciones.nameDatabase,null,2);
        listaContactos =(ListView)findViewById(R.id.ListaContactos);
        obtenerTabla();
        ArrayAdapter adp =new ArrayAdapter(this, android.R.layout.simple_list_item_1,ArregloContactos);
        listaContactos.setAdapter(adp);
        listaContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el elemento seleccionado en la lista
                String selectedItem = (String) parent.getItemAtPosition(position);
                // Realizar alguna acción con el elemento seleccionado
//                mostrarDialogo(lista.get(position).getNombre(),lista.get(position).getTelefono());
                dialogoOpciones(lista.get(position).getNombre(),lista.get(position).getTelefono());
            }
        });
    }

    private void obtenerTabla() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos contac = null;
        lista = new ArrayList<Contactos>();
        //Cursor de base de datos
        Cursor cursor = db.rawQuery(Transacciones.SelectTableContactos,null);

        //Recorremos el cursor
        while (cursor.moveToNext()){
            contac = new Contactos();
            contac.setId(cursor.getInt(0));
            contac.setPais(cursor.getString(1));
            contac.setNombre(cursor.getString(2));
            contac.setTelefono(cursor.getString(3));
            contac.setNota(cursor.getString(4));

            lista.add(contac);
        }
        cursor.close();

        fillList();

    }

    private void fillList() {
        ArregloContactos = new ArrayList<String>();
        for (int i = 0; i < lista.size(); i++) {
            ArregloContactos.add(lista.get(i).getNombre() + " | "
                    + lista.get(i).getTelefono()
            );
        }
    }
/* Se realizo una pequeña modificacion
    private void mostrarDialogo(String nombre, String telefono){
        // Realizar alguna acción con el elemento seleccionado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Realizar la llamada").setMessage("¿Desea llamar a " + nombre +"?").setPositiveButton("Llamar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hacerLlamada(telefono);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción de cancelar
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
*/

    private void dialogoOpciones(String nombre,String telefono){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] elementos = {"Llamar a "+ nombre,"Compartir Contacto", "Ver Imagen", "Eliminar Contacto","Editar Contacto"};
        builder.setTitle("Acciones")
                .setItems(elementos, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        String elementoSeleccionado = elementos[which];
                        Toast.makeText(getApplicationContext(),""+elementoSeleccionado,Toast.LENGTH_LONG).show();
                        if (elementoSeleccionado == elementos[0]){
                            hacerLlamada(telefono);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void hacerLlamada(String numeroTelefono) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUIERE_LLAMAR_TELEFONO);
        }else{
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + numeroTelefono));
            startActivity(intent);
        }
    }

}