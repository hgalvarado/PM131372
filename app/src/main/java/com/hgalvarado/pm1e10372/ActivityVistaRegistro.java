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
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hgalvarado.pm1e10372.Modelo.ActivityImagen;
import com.hgalvarado.pm1e10372.Modelo.Contactos;
import com.hgalvarado.pm1e10372.configuraciones.SQLiteConexion;
import com.hgalvarado.pm1e10372.configuraciones.Transacciones;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
                dialogoOpciones(lista.get(position).getNombre(),lista.get(position).getTelefono(), String.valueOf(lista.get(position).getId()),position);
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
            ArregloContactos.add(lista.get(i).getId() + " | "
                    + lista.get(i).getNombre() + " | "
                    + lista.get(i).getTelefono()
            );
        }
    }
    private void dialogoOpciones(String nombre,String telefono,String id,int posicion){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] elementos = {"Llamar a "+ nombre,"Compartir Contacto", "Ver Imagen", "Eliminar Contacto","Editar Contacto"};
        builder.setTitle("Acciones")
                .setItems(elementos, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        String elementoSeleccionado = elementos[which];

                        if (elementoSeleccionado == elementos[0]){ //Llamar
                            hacerLlamada(telefono);
                        }
                        if (elementoSeleccionado == elementos[1]){ //Compartir Contacto
                            Intent compartirIntent = new Intent(Intent.ACTION_SEND);
                            compartirIntent.setType("text/plain");
                            compartirIntent.putExtra(Intent.EXTRA_TEXT,"Contacto: "+nombre +" "+  telefono);
                            startActivity(Intent.createChooser(compartirIntent, "Compartir usando"));
                        }
                        if (elementoSeleccionado == elementos[2]){ //Ver Imagen
                            Intent intent = new Intent(getApplicationContext(), ActivityImagen.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                        if (elementoSeleccionado==elementos[3]){//Eliminar Contacto
                            EliminarContacto(id);
                        }
                        if (elementoSeleccionado==elementos[4]){
                            Intent intent = new Intent(getApplicationContext(), ActivityEditarContacto.class);
                            intent.putExtra("id", id);
                            intent.putExtra("pais",lista.get(posicion).getPais());
                            intent.putExtra("nombre",lista.get(posicion).getNombre());
                            intent.putExtra("telefono",lista.get(posicion).getTelefono());
                            intent.putExtra("nota",lista.get(posicion).getNota());
                            startActivity(intent);
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
    private void EliminarContacto(String identificador) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar contacto");
        builder.setMessage("¿Estás seguro de que deseas eliminar este contacto?");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtén una instancia de la base de datos SQLite
                SQLiteDatabase db = conexion.getWritableDatabase();

                // Definir la tabla y la cláusula WHERE para eliminar el dato
                String tableName = "contactos";
                String whereClause = "id = ?";
                String[] whereArgs = { identificador };

                // Ejecutar la sentencia DELETE
                int rowsDeleted = db.delete(tableName, whereClause, whereArgs);

                // Verificar la cantidad de filas eliminadas
                if (rowsDeleted > 0) {
                    // El dato se eliminó exitosamente
                    Toast.makeText(getApplicationContext(),"CONTACTO ELIMINADO",Toast.LENGTH_LONG).show();
                } else {
                    // No se encontró el dato o no se pudo eliminar
                    Toast.makeText(getApplicationContext(),"NO SE ELIMINO EL CONTACTO",Toast.LENGTH_LONG).show();
                }

                // Cerrar la base de datos
                db.close();
                Intent intent = new Intent(getApplicationContext(), ActivityVistaRegistro.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}