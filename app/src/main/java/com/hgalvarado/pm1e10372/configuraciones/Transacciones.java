package com.hgalvarado.pm1e10372.configuraciones;

public class Transacciones {
    public static final String nameDatabase = "PM1E10372";

    //Tablas de la base de datos
    public static final String tableContactos = "contactos";

    //Campos de la tabla personas
    public static final String id = "id";
    public static final String pais = "pais";
    public static final String nombres = "nombres";
    public static final String telefono = "telefono";
    public static final String nota = "nota";
    public static final String imagen = "imagen";

    //DDL Create and Drop
    public static final String CreateTableContactos = "CREATE TABLE contactos " +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, pais TEXT, nombres TEXT, telefono TEXT, nota TEXT, imagen TEXT )";

    public static final String DropTableContactos = "DROP TABLE IF EXISTS " + tableContactos;

    //DML
    public static final String SelectTableContactos = "SELECT * FROM " + tableContactos;

}
