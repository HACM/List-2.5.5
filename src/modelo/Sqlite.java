package modelo;

import java.util.ArrayList;

import android.content.ContentValues;
import android.util.Log;
import setget.Clases;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Sqlite extends SQLiteOpenHelper {
	// All Static variables
	// Database version
	private static final int DATABASE_VERSION = 1;
	// Database nombre
	private static final String DATABASE_NAME = "listin01";
	// Tabla usuarios
	protected static final String TABLE_USUARIOS = "usuarios";
	protected static final String TABLE_CLASES = "clases";
	protected static final String TABLE_LISTAS = "listas";
	protected static final String TABLE_ALUMNOS = "alumnos";
	protected static final String TABLE_ASISTENCIAS = "asistencias";

	// CLASES Table Columns names
	protected static final String KEY_NAME_CLASE = "nombre";
	protected static final String KEY_DESCRIPCION_CLASE = "descripcion";
	protected static final String KEY_LIMITE_ASISTENCIAS = "cantidad";
	protected static final String KEY_CANTIDAD_ALUMNOS = "cantidad_al";
	protected static final String IDUSUARIO = "IDUSUARIO";
	
	// USUARIOS Table Columns names
	protected static final String KEY_ID = "id";
	protected static final String KEY_NAME = "nombre";
	protected static final String KEY_LASTNAME = "apellido";
	protected static final String KEY_CORREO = "correo";
	protected static final String KEY_CONTRA = "contra";
	protected static final String ACTIVO = "ACTIVO";
	// LISTAS Table Columns names
	protected static final String KEY_FECHA = "fecha";
	protected static final String KEY_PRESENTES = "presentes";
	protected static final String KEY_TARDES = "tardes";
	protected static final String KEY_AUSENTES = "ausentes";
	protected static final String KEY_ID_CLASE_LISTA = "id_clase";

	// ESTUDIANTES
	protected static final String KEY_AL_NOMBRES = "nombres";
	protected static final String KEY_AL_APELLIDOS = "apellidos";
	protected static final String KEY_AL_CORREO = "correo";
	// Detalle lista
	protected static final String KEY_ID_LISTA = "id_lista";
	protected static final String KEY_ID_CLASE = "id_clase";

	// se agregan las 2 columna de nombre y apellido del estudiante, y la
	// asitencia en integer 1=P 2=T 3=A
	protected static final String KEY_ASISTENCIA = "asistencia";
	protected static final String IDALUM = "IDALUM";

    //licencia aceptada
    protected static final String ace = "ace";
    protected static final String lic = "lic";

	protected final static ArrayList<Clases> clase_lista = new ArrayList<Clases>();

	// iniciar BD
	public Sqlite(Context contexto) {
		super(contexto, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        try {

            // crear tabla usuarios
            String CREATE_USUARIOS_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER)",
                    TABLE_USUARIOS, KEY_ID, KEY_NAME, KEY_LASTNAME,KEY_CORREO, KEY_CONTRA, ACTIVO);
            db.execSQL(CREATE_USUARIOS_TABLE);

            // crear tabla clase
            String CREATE_CLASE_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER)",
                    TABLE_CLASES, KEY_ID, KEY_NAME_CLASE, KEY_DESCRIPCION_CLASE,KEY_LIMITE_ASISTENCIAS, KEY_CANTIDAD_ALUMNOS, IDUSUARIO);
            db.execSQL(CREATE_CLASE_TABLE);

            // crear tabla listas
            String CREATE_LISTAS_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s DATE, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
                    TABLE_LISTAS, KEY_ID, KEY_FECHA, KEY_PRESENTES,KEY_TARDES, KEY_AUSENTES, KEY_ID_CLASE_LISTA);
            db.execSQL(CREATE_LISTAS_TABLE);

            // crear tabla alumnos
            String CREATE_ALUMNOS_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s INTEGER)",
                    TABLE_ALUMNOS, KEY_ID, KEY_AL_APELLIDOS, KEY_AL_NOMBRES,KEY_AL_CORREO, KEY_ID_CLASE);
            db.execSQL(CREATE_ALUMNOS_TABLE);

            // crear tabla asistencias
            String CREATE_ASISTENCIAS_TABLE = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)",
                    TABLE_ASISTENCIAS, KEY_ID, KEY_ID_LISTA, KEY_ID_CLASE, KEY_ASISTENCIA, IDALUM);
            db.execSQL(CREATE_ASISTENCIAS_TABLE);

            // crear tabla asistencias
            String licc = String.format("CREATE TABLE %s (%s INTEGER)",lic, ace);
            db.execSQL(licc);
            db.execSQL("INSERT INTO lic VALUES (0);");
        }
        catch(Exception ex){
            Log.e("Problemas BD:",ex.toString());
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS + " and " + TABLE_CLASES + " and " + TABLE_LISTAS + " and " + TABLE_ALUMNOS + " and "+ TABLE_ASISTENCIAS );
            onCreate(db);
        }catch(Exception ex){
            Log.e("Problemas UPGRADE BD:",ex.toString());
        }
	}
}
