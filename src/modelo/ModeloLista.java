package modelo;

import java.util.ArrayList;
import java.util.List;

import setget.Alumnos;
import setget.AsistenciaAlumno;
import setget.Clases;
import setget.Listas;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ModeloLista {
	Sqlite modelo;
	Context contexto;

	protected ArrayList<Alumnos> alumnos;
	protected ArrayList<Listas> listas;
    private String asistencias ="";

	public ModeloLista(Context contexto){
		modelo = new Sqlite(contexto);
		this.contexto = contexto;
		listas = new ArrayList<Listas>();
		alumnos = new ArrayList<Alumnos>();
	}
	
	// todas las listas de asistencias de alguna CLASE ID
    public ArrayList<Listas> cargarTodasListas(int id_CLASE) {
        try {
            listas.clear();
            SQLiteDatabase db = modelo.getWritableDatabase();
            Cursor cursor = db.query(Sqlite.TABLE_LISTAS, new String[] { Sqlite.KEY_ID,
                            Sqlite.KEY_FECHA, Sqlite.KEY_PRESENTES, Sqlite.KEY_TARDES, Sqlite.KEY_AUSENTES },
                    Sqlite.KEY_ID_CLASE_LISTA + "=? ORDER BY CONVERT(DateTime, "+Sqlite.KEY_FECHA+",101)  DESC",
                    new String[] { String.valueOf(id_CLASE) }, null, null,null, null);
            // Toast.makeText(contexto, cursor.getCount()+"kokoko",
            // Toast.LENGTH_LONG).show();
            if (cursor.moveToFirst()) {
                do {
                    Listas datos = new Listas();
                    datos.setId(Integer.parseInt(cursor.getString(0)));
                    datos.setFecha(cursor.getString(1));
                    datos.setPresentes(Integer.parseInt(cursor.getString(2)));
                    datos.setTardes(Integer.parseInt(cursor.getString(3)));
                    datos.setAusentes(Integer.parseInt(cursor.getString(4)));
                    listas.add(datos);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return listas;
        } catch (Exception ex) {
        }
        return listas;
    }

	
	// todas las listas de asistencias de alguna CLASE ID
		public ArrayList<Listas> cargarTodasListasExportacion(int id_CLASE) {
			try {
				listas.clear();
				SQLiteDatabase db = modelo.getWritableDatabase();
				Cursor cursor = db.query(Sqlite.TABLE_LISTAS, new String[] { Sqlite.KEY_ID,
						Sqlite.KEY_FECHA, Sqlite.KEY_PRESENTES, Sqlite.KEY_TARDES, Sqlite.KEY_AUSENTES },
						Sqlite.KEY_ID_CLASE_LISTA + "=? ORDER BY "+Sqlite.KEY_FECHA+" ASC",
						new String[] { String.valueOf(id_CLASE) }, null, null,null, null);
				// Toast.makeText(contexto, cursor.getCount()+"kokoko",
				// Toast.LENGTH_LONG).show();
				if (cursor.moveToFirst()) {
					do {
						Listas datos = new Listas();
						datos.setId(Integer.parseInt(cursor.getString(0)));
						datos.setFecha(cursor.getString(1));
						datos.setPresentes(Integer.parseInt(cursor.getString(2)));
						datos.setTardes(Integer.parseInt(cursor.getString(3)));
						datos.setAusentes(Integer.parseInt(cursor.getString(4)));
						listas.add(datos);
					} while (cursor.moveToNext());
				}
				cursor.close();
				db.close();
				return listas;
			} catch (Exception ex) { 
			}
			return listas;
		}

	// todas las asistencias de los alumnos
	public ArrayList<Alumnos> asistenciasAlumnos(int id_CLASE,
			int LISTA_ID) {
		try {
			alumnos.clear();
			SQLiteDatabase db = modelo.getWritableDatabase();

			String selectQuery = "SELECT " + Sqlite.KEY_ID + ", (SELECT " + Sqlite.KEY_AL_APELLIDOS + " FROM " + Sqlite.TABLE_ALUMNOS
                    + " WHERE "+Sqlite.TABLE_ALUMNOS+"." + Sqlite.KEY_ID + "=" + Sqlite.TABLE_ASISTENCIAS + ".IDALUM ) as Apellidos"
                    + ", (SELECT " + Sqlite.KEY_AL_NOMBRES + " FROM " + Sqlite.TABLE_ALUMNOS
                    + " WHERE "+Sqlite.TABLE_ALUMNOS+"." + Sqlite.KEY_ID + "=" + Sqlite.TABLE_ASISTENCIAS + ".IDALUM ) as Nombres "
                    + ", IDALUM FROM " + Sqlite.TABLE_ASISTENCIAS
					+ " WHERE " + Sqlite.KEY_ID_LISTA + "=? and " + Sqlite.KEY_ID_CLASE
					+ "=? and " + Sqlite.KEY_ASISTENCIA + "=0 ORDER BY "+Sqlite.KEY_AL_APELLIDOS+" ASC";
			Cursor cursor = db.rawQuery(selectQuery,new String[] { String.valueOf(LISTA_ID),String.valueOf(id_CLASE) });
			// Toast.makeText(contexto, cursor.getCount()+"encontrados",
			// Toast.LENGTH_LONG).show();
					
			if (cursor.moveToFirst()) {
				do {
					Alumnos datos = new Alumnos();
					datos.setId(Integer.parseInt(cursor.getString(0)));
					//Log.e("IDDD", cursor.getString(0));
					datos.setApellido(cursor.getString(1));
					datos.setNombre(cursor.getString(2));
					String cantAsistencias = "SELECT count(*) as 'Ausensias', IDALUM FROM "+Sqlite.TABLE_ASISTENCIAS+" " +
							"WHERE "+Sqlite.KEY_ASISTENCIA+"=3  and IDALUM = "+cursor.getString(3)+" and "+Sqlite.KEY_ID_CLASE+"="+id_CLASE;
					Cursor cursorasistencia = db.rawQuery(cantAsistencias, null);
					if (cursorasistencia.moveToFirst()){ 
						datos.setCantAusensias(Integer.parseInt(cursorasistencia.getString(0)));
						//Log.e("======", ""+cursorasistencia.getString(0)+"||"+cursorasistencia.getString(1)); 
					} 
					else{
						datos.setCantAusensias(0);
					}
					alumnos.add(datos);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return alumnos;
		} catch (Exception ex) { 
		}
		return alumnos;
	}

	public int agregarLista(Listas instancia, int idClase) {
		try {
            if (verificarLista(instancia.getFecha(), idClase)==false){
                try{
                    SQLiteDatabase db = modelo.getWritableDatabase();
                    ContentValues valores = new ContentValues();
                    // agregar valores al ContentValue
                    valores.put(Sqlite.KEY_FECHA, instancia.getFecha());
                    valores.put(Sqlite.KEY_PRESENTES, 0);
                    valores.put(Sqlite.KEY_TARDES, 0);
                    valores.put(Sqlite.KEY_AUSENTES, 0);
                    valores.put(Sqlite.KEY_ID_CLASE_LISTA, instancia.getId_clase());
                    // Insertar fila
                    int id_lista_agregada = (int) db.insert(Sqlite.TABLE_LISTAS, null, valores);
                    ModeloAlumno modeloAlumno = new ModeloAlumno(contexto);
                    // mandar a traer todos los alumnos que tengan el id de la clase
                    ArrayList<Alumnos> alumnos_obtenidos = modeloAlumno.cargarTodosLosAlumnos(instancia.getId_clase());
                    // insertarlo en asistencias con el id_lista_agregada + id_clase +
                    // nombre + ape + asistencia

                    // Mostrar_Toast("Total: "+ listas_areglo_desde_db.size());
                    for (int i = 0; i < alumnos_obtenidos.size(); i++) {
                        ContentValues valores_asistencia = new ContentValues();
                        // agregar valores al ContentValue
                        valores_asistencia.put(Sqlite.KEY_ID_LISTA, id_lista_agregada);
                        valores_asistencia.put(Sqlite.KEY_ID_CLASE, instancia.getId_clase());
                        valores_asistencia.put(Sqlite.KEY_ASISTENCIA, 0);
                        valores_asistencia.put("IDALUM", alumnos_obtenidos.get(i).getId());
                        db.insert(Sqlite.TABLE_ASISTENCIAS, null, valores_asistencia);
                    }
                    db.close(); // Cerrar a BD
                    return 1;
                }
                catch(Exception ex){
                    return 2;
                }
            }
            else{
                return 3;
            }
		} catch (Exception ex) {
            return 4;
		}
	}

	public int actualizarLista(Listas instancia) {
		try {
			SQLiteDatabase db = modelo.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(Sqlite.KEY_FECHA, instancia.getFecha());
			values.put(Sqlite.KEY_PRESENTES, instancia.getPresentes());
			values.put(Sqlite.KEY_TARDES, instancia.getTardes());
			values.put(Sqlite.KEY_AUSENTES, instancia.getAusentes());
			values.put(Sqlite.KEY_ID_CLASE_LISTA, instancia.getId_clase());
			// updating row
			return db.update(Sqlite.TABLE_LISTAS, values, Sqlite.KEY_ID + " = ?",new String[] { String.valueOf(instancia.getId()) });
		} catch (Exception ex) {
			// problemas mostrar mensaje 
			return 0;
		}
	}
	
	public void eliminarLista(int idLista) {
		try {
			SQLiteDatabase db = modelo.getWritableDatabase();
			db.delete(Sqlite.TABLE_LISTAS, Sqlite.KEY_ID + " = ?",new String[] { String.valueOf(idLista) });
			db.delete(Sqlite.TABLE_ASISTENCIAS, Sqlite.KEY_ID_LISTA + " = ?",new String[] { String.valueOf(idLista) });
			db.close();
		} catch (Exception ex) {
            Log.e("Problema eliminar Lista: ",ex.toString());
		}
        //continuar eso de eliminar la lista en la tabla asistencias y lo de enviar alcorreo la asistencia no esta dando
	}

    public String cargarTodasListasNUBE(int id_CLASE) {
        String cadenaLista="";
        try {
            listas.clear();
            SQLiteDatabase db = modelo.getReadableDatabase();
            Cursor cursor = db.query(Sqlite.TABLE_LISTAS, new String[] { Sqlite.KEY_ID,
                            Sqlite.KEY_FECHA, Sqlite.KEY_PRESENTES, Sqlite.KEY_TARDES, Sqlite.KEY_AUSENTES, Sqlite.KEY_ID_CLASE_LISTA },
                    Sqlite.KEY_ID_CLASE_LISTA + "=? ORDER BY "+Sqlite.KEY_FECHA+" DESC",
                    new String[] { String.valueOf(id_CLASE) }, null, null,null, null);
            if (cursor.moveToFirst()) {
                do {
                    cadenaLista = cadenaLista + cursor.getString(0)+"!"+cursor.getString(1)+"!"+cursor.getString(2)+"!"+cursor.getString(3)+"!"+cursor.getString(4)+"!"+cursor.getString(5)+"FL";
                    asistenciasAlumnosNUBE(id_CLASE,Integer.parseInt(cursor.getString(0)));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return cadenaLista;
        } catch (Exception ex) {
            Log.e("5------", "" + ex.toString());
        }
        return cadenaLista;
    }
    public void asistenciasAlumnosNUBE(int id_CLASE, int LISTA_ID) {

        try {
            alumnos.clear();
            SQLiteDatabase db = modelo.getReadableDatabase();
            String selectQuery = "SELECT " + Sqlite.KEY_ID + ",IDALUM,"+ Sqlite.KEY_ASISTENCIA +" FROM " + Sqlite.TABLE_ASISTENCIAS
                    + " WHERE " + Sqlite.KEY_ID_LISTA + "=? and " + Sqlite.KEY_ID_CLASE + "=? ORDER BY " + Sqlite.KEY_ID + " ASC";
            Cursor cursor = db.rawQuery(selectQuery,new String[] { String.valueOf(LISTA_ID),String.valueOf(id_CLASE) });
            if (cursor.moveToFirst()) {
                do {
                    String cantAsistencias = "SELECT count(*) as 'Ausensias', IDALUM FROM "+Sqlite.TABLE_ASISTENCIAS+" " +
                            "WHERE "+Sqlite.KEY_ASISTENCIA+"=3 and IDALUM = "+cursor.getString(1)+" and "+Sqlite.KEY_ID_CLASE+"="+id_CLASE;
                    Cursor cursorasistencia = db.rawQuery(cantAsistencias, null);
                    if (cursorasistencia.moveToFirst()){
                        asistencias = asistencias +cursor.getString(0)+"!"+cursor.getString(1)+"!"+cursor.getString(2)+"!"+cursorasistencia.getString(0)+"!"+LISTA_ID+"FA";
                    }
                    else{
                        asistencias = asistencias +cursor.getString(0)+"!"+cursor.getString(1)+"!"+cursor.getString(2)+"!0"+"!"+LISTA_ID+"FA";
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            Log.e("6------", "" + ex.toString());
        }
    }
    public String cargarAsistenciaListaNUBE(){
        return asistencias;
    }
    public void limpiarAsistencias(){
        asistencias="";
    }
    private boolean verificarLista(String fecha, int idClase){
        boolean respueta = false;
        try {
            SQLiteDatabase db = modelo.getReadableDatabase();
            Cursor cursor = db.query(Sqlite.TABLE_LISTAS, new String[] { Sqlite.KEY_FECHA },
                    Sqlite.KEY_FECHA + "=?" + " and "+ Sqlite.KEY_ID_CLASE_LISTA + "=?",
                    new String[] { String.valueOf(fecha), String.valueOf(idClase) }, null, null,null, null);
            if (cursor.moveToFirst()) {
                return true;
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            return respueta;
        }
        return respueta;
    }

    public ArrayList<Listas> agregarListaNube(ArrayList<Clases> listClases, ArrayList<Listas> listListas){
        ArrayList<Listas> listas = new ArrayList<Listas>();
        int bucleLista = 0;
        SQLiteDatabase db = modelo.getWritableDatabase();
        for (int i=0; i < listClases.size(); i++) {
            for (int ii=bucleLista; ii < listListas.size(); ii++) {
                if ( listClases.get(i).get_idViejo() != listListas.get(ii).getId_clase() ){
                    bucleLista = ( ii );
                    break;
                }
                ContentValues valoresLista = new ContentValues();
                valoresLista.put(Sqlite.KEY_FECHA, listListas.get(ii).getFecha());
                valoresLista.put(Sqlite.KEY_PRESENTES, listListas.get(ii).getPresentes());
                valoresLista.put(Sqlite.KEY_TARDES, listListas.get(ii).getTardes());
                valoresLista.put(Sqlite.KEY_AUSENTES, listListas.get(ii).getAusentes());
                valoresLista.put(Sqlite.KEY_ID_CLASE_LISTA, listClases.get(i).get_id());
                int id_lista_agregada = (int) db.insert(Sqlite.TABLE_LISTAS, null, valoresLista);

                Listas nuev = new Listas();
                nuev.setId( id_lista_agregada );
                nuev.setIdViejo(listListas.get(ii).getId());
                nuev.setId_clase( listClases.get(i).get_id() );
                listas.add( nuev );
            }
        }
        return listas;
    }

    public void agregarAsistencias(ArrayList<Clases> listClases, ArrayList<Listas> listListas, ArrayList<Alumnos> listAlumnos,
                                   ArrayList<AsistenciaAlumno> listAsistencias) {
        SQLiteDatabase db = modelo.getWritableDatabase();
        int idlista, idalumno;
        for (int i=0; i < listClases.size(); i++) {
            for (int ii=0; ii < listAsistencias.size(); ii++) {
                idlista = buscarIdClase(listAsistencias.get(ii).getId_lista(), listListas);
                idalumno = buscarIdAlumno(listAsistencias.get(ii).getIdAlumno(), listAlumnos);

                ContentValues valores_asistencia = new ContentValues();
                valores_asistencia.put(Sqlite.KEY_ID_LISTA, idlista);
                valores_asistencia.put(Sqlite.KEY_ID_CLASE, listClases.get(i).get_id());
                valores_asistencia.put(Sqlite.KEY_ASISTENCIA, listAsistencias.get(ii).getAsistencia());
                valores_asistencia.put("IDALUM", idalumno);
                db.insert(Sqlite.TABLE_ASISTENCIAS, null, valores_asistencia);
            }
        }
    }

    private int buscarIdAlumno(int idAlumno, ArrayList<Alumnos> listAlumnos) {
        int idNuevoAlumno =0;
        for (int ii=0; ii < listAlumnos.size(); ii++) {
            if (idAlumno == listAlumnos.get(ii).getIdViejo())return listAlumnos.get(ii).getId();
        }
        return idNuevoAlumno;
    }

    private int buscarIdClase(int id_lista, ArrayList<Listas> listListas) {
        int idNuevoLista =0;
        for (int ii=0; ii < listListas.size(); ii++) {
            if (id_lista == listListas.get(ii).getIdViejo())return listListas.get(ii).getId();
        }
        return idNuevoLista;
    }

}
