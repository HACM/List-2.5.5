package modelo;

import java.util.ArrayList;

import android.util.Log;
import setget.Alumnos;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import setget.Clases;

public class ModeloAlumno{
	Sqlite modelo;
	protected ArrayList<Alumnos> alumnos;
	 
	public ModeloAlumno(Context contexto){
		modelo = new Sqlite(contexto);
		alumnos = new ArrayList<Alumnos>();
	}

	// Agregar nuevo alumno
	public void agregarAlumno(Alumnos instancia, int nuevaCantidadAlumnos) {
		try { 
			SQLiteDatabase db = modelo.getWritableDatabase();
			
			ContentValues values1 = new ContentValues(); 
			values1.put(Sqlite.KEY_CANTIDAD_ALUMNOS, nuevaCantidadAlumnos);
			db.update(Sqlite.TABLE_CLASES, values1, Sqlite.KEY_ID + " = ?", new String[] { String.valueOf(instancia.getId_su_clase()) });			 
						
			ContentValues valoresAlumnoNuevo = new ContentValues();
			valoresAlumnoNuevo.put(Sqlite.KEY_AL_APELLIDOS, instancia.getApellido());
			valoresAlumnoNuevo.put(Sqlite.KEY_AL_NOMBRES, instancia.getNombre());
			valoresAlumnoNuevo.put(Sqlite.KEY_AL_CORREO, instancia.getCorreo());
			valoresAlumnoNuevo.put(Sqlite.KEY_ID_CLASE, instancia.getId_su_clase());
		 	int idNuevo = (int) db.insert(Sqlite.TABLE_ALUMNOS, null, valoresAlumnoNuevo);
			
			//agregar en cada lista de la clase 
		 	//traer todas las listas de asistencias de la clase 
			Cursor cursor = db.query(Sqlite.TABLE_LISTAS, new String[] { Sqlite.KEY_ID }, Sqlite.KEY_ID_CLASE_LISTA + "=?",
					new String[] { String.valueOf(instancia.getId_su_clase()) }, null, null,null, null);
			//insertar el alumno nuevo en cada lista
			if (cursor.moveToFirst()) {
				do { 					
					ContentValues valoresInsertar = new ContentValues(); 
					valoresInsertar.put(Sqlite.KEY_ID_LISTA, cursor.getString(0));
					valoresInsertar.put(Sqlite.KEY_ID_CLASE, instancia.getId_su_clase());
					valoresInsertar.put(Sqlite.KEY_ASISTENCIA, 0);
					valoresInsertar.put("IDALUM", idNuevo);
					db.insert(Sqlite.TABLE_ASISTENCIAS, null, valoresInsertar);  
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception ex) {
            Log.e("Problemas Agregar Nuevo Alumno: ",ex.toString());
        }
	}

	public int actualizarAlumno(Alumnos instancia) {
		try {
			SQLiteDatabase db = modelo.getWritableDatabase();
			ContentValues valores = new ContentValues();
			valores.put(Sqlite.KEY_AL_APELLIDOS, instancia.getApellido());
            valores.put(Sqlite.KEY_AL_NOMBRES, instancia.getNombre());
            valores.put(Sqlite.KEY_AL_CORREO, instancia.getCorreo());
            return db.update(Sqlite.TABLE_ALUMNOS, valores, Sqlite.KEY_ID + " = ?",new String[] { String.valueOf(instancia.getId()) });
		} catch (Exception ex) { 
			return 0;
		}
	}
	
	public int eliminarAlumno(int idEstudiante, int idClase, int nuevaCantidadAlumnos) {
		try { 
			SQLiteDatabase db = modelo.getWritableDatabase();
			ContentValues values = new ContentValues(); 
			values.put(Sqlite.KEY_CANTIDAD_ALUMNOS, nuevaCantidadAlumnos);
			db.update(Sqlite.TABLE_CLASES, values, Sqlite.KEY_ID + " = ?", new String[] { String.valueOf(idClase) });			 
			db.delete(Sqlite.TABLE_ASISTENCIAS, "IDALUM = ?", new String[] { String.valueOf(idEstudiante) });
			db = modelo.getWritableDatabase(); 
			return db.delete(Sqlite.TABLE_ALUMNOS, Sqlite.KEY_ID + " = ?",new String[] { String.valueOf(idEstudiante) });
		} catch (Exception ex) { 
			return 0;
		}
	}

	// todas los alumnos
    public ArrayList<Alumnos> cargarTodosLosAlumnos(int id_CLASE) {
        try {
            alumnos.clear();
            SQLiteDatabase db = modelo.getWritableDatabase();
            Cursor cursor = db.query(Sqlite.TABLE_ALUMNOS, new String[] { Sqlite.KEY_ID,
                    Sqlite.KEY_AL_APELLIDOS, Sqlite.KEY_AL_NOMBRES, Sqlite.KEY_AL_CORREO,
                    Sqlite.KEY_ID_CLASE }, Sqlite.KEY_ID_CLASE + "=?",new String[] { String.valueOf(id_CLASE) }, null, null,null, null);
            if (cursor.moveToFirst()) {
                do {
                    Alumnos datos = new Alumnos();
                    datos.setId(Integer.parseInt(cursor.getString(0)));
                    datos.setApellido(cursor.getString(1));
                    datos.setNombre(cursor.getString(2));
                    datos.setCorreo(cursor.getString(3));
                    datos.setId_su_clase(Integer.parseInt(cursor.getString(4)));
                    alumnos.add(datos);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return alumnos;
        } catch (Exception ex) {
        }
        return alumnos;
    }
    public String cargarTodosLosAlumnosNUBE(int id_CLASE) {
       String alumnosLista="";
       //Log.e("ID CLASE: ",""+id_CLASE);
        try {
            alumnos.clear();
            SQLiteDatabase db = modelo.getWritableDatabase();
            Cursor cursor = db.query(Sqlite.TABLE_ALUMNOS, new String[] { Sqlite.KEY_ID,
                    Sqlite.KEY_AL_APELLIDOS, Sqlite.KEY_AL_NOMBRES, Sqlite.KEY_AL_CORREO,
                    Sqlite.KEY_ID_CLASE }, Sqlite.KEY_ID_CLASE + "=?",new String[] { String.valueOf(id_CLASE) }, null, null,null, null);
            if (cursor.moveToFirst()) {
                do {
                    alumnosLista = alumnosLista + cursor.getString(0)+"!"+cursor.getString(1)+"!"+cursor.getString(2)+"!"+cursor.getString(3)+"!"+cursor.getString(4)+"FAL";
                    //Log.e("ALUMNOS LISTA: ",alumnosLista);
                } while (cursor.moveToNext());
            }
            //Log.e("ALUMNOS LISTA--- ",alumnosLista);
            cursor.close();
            return alumnosLista;
        } catch (Exception ex) {
            Log.e("cargarTodosLosAlumnosNUBE: ",ex.toString());
        }
        return alumnosLista;
    }

    public ArrayList<Alumnos> agregarAlumnosNube(ArrayList<Alumnos> listAlumnos,ArrayList<Clases> listClases, int idUsuario){
        ArrayList<Alumnos> nAlumnos = new ArrayList<Alumnos>();
        int bucleAlumno = 0;
        SQLiteDatabase db = modelo.getWritableDatabase();
        for (int i=0; i < listClases.size(); i++) {
            for (int ii=bucleAlumno; ii < listAlumnos.size(); ii++) {
                if ( listClases.get(i).get_idViejo() != listAlumnos.get(ii).getId_su_clase() ){
                    bucleAlumno = (ii);
                    break;
                }
                ContentValues valoresalumnos = new ContentValues();
                valoresalumnos.put(Sqlite.KEY_AL_APELLIDOS, listAlumnos.get(ii).getApellido());
                valoresalumnos.put(Sqlite.KEY_AL_NOMBRES, listAlumnos.get(ii).getNombre());
                valoresalumnos.put(Sqlite.KEY_AL_CORREO, listAlumnos.get(ii).getCorreo());
                valoresalumnos.put(Sqlite.KEY_ID_CLASE, listClases.get(i).get_id());

                int idNuevo = (int) db.insert(Sqlite.TABLE_ALUMNOS, null, valoresalumnos);

                Alumnos nuev = new Alumnos();
                nuev.setId( idNuevo );
                nuev.setIdViejo(listAlumnos.get(ii).getId());
                nAlumnos.add( nuev );
            }
        }
        return nAlumnos;
    }
}
