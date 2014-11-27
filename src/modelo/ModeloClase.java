package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import setget.Alumnos;
import setget.AsistenciaAlumno;
import setget.Clases;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import setget.Listas;

public class ModeloClase {
	Sqlite modelo;
    Context contexto;
	protected ArrayList<Alumnos> alumnos;
    ModeloLista modelolista;
    ModeloAlumno modeloAlumnos;

    private String cadenaLista = "";
    private String cadenaListasDeTalClase="";
    private String cadenaAsistencias="";
    private String cadenaAlumnos="";
    private String respuestaAssy="";
	public ModeloClase(Context contexto){
        this.contexto = contexto;
		modelo = new Sqlite(contexto);
		alumnos = new ArrayList<Alumnos>();
        modelolista = new ModeloLista(contexto);
        modeloAlumnos = new ModeloAlumno(contexto);
	}
	
	public int agregarClase(Clases clase_set_get, int usuario) {
		int respuesta =0;
		try {
			SQLiteDatabase db = modelo.getWritableDatabase();
			ContentValues valores = new ContentValues();
			// agregar valores al ContentValue
			valores.put(Sqlite.KEY_NAME_CLASE, clase_set_get.get_name());
			valores.put(Sqlite.KEY_DESCRIPCION_CLASE, clase_set_get.get_descripcion());
			valores.put(Sqlite.KEY_LIMITE_ASISTENCIAS, clase_set_get.get_asencias());
			valores.put(Sqlite.KEY_CANTIDAD_ALUMNOS, clase_set_get.getDatos().size()); 
			valores.put("IDUSUARIO", ""+usuario);
			
			// Insertar fila
			int id_clase_insertada = (int) db.insert(Sqlite.TABLE_CLASES, null,	valores);
			// ahora mandar a la tabla alumnos los que pertenecen a esta clase

			ArrayList<Alumnos> alumnos_obtenidos = clase_set_get.getDatos();

			for (int i = 0; i < alumnos_obtenidos.size(); i++) {
				ContentValues valores_alumnos = new ContentValues();
				// agregar valores al ContentValue
				valores_alumnos.put(Sqlite.KEY_AL_APELLIDOS, alumnos_obtenidos.get(i).getApellido());
				valores_alumnos.put(Sqlite.KEY_AL_NOMBRES, alumnos_obtenidos.get(i).getNombre());
				valores_alumnos.put(Sqlite.KEY_AL_CORREO, alumnos_obtenidos.get(i).getCorreo());
				valores_alumnos.put(Sqlite.KEY_ID_CLASE, id_clase_insertada);
				db.insert(Sqlite.TABLE_ALUMNOS, null, valores_alumnos);
			}
			db.close(); // Cerrar a BD
			respuesta =1;
		} catch (Exception ex) {
			// problemas mostrar mensaje
			respuesta = 2;
		}
		return respuesta;
	}

	public int actualizarClase(Clases instancia) {
		try {
			SQLiteDatabase db = modelo.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(Sqlite.KEY_NAME_CLASE, instancia.get_name());
			values.put(Sqlite.KEY_DESCRIPCION_CLASE, instancia.get_descripcion());
			values.put(Sqlite.KEY_LIMITE_ASISTENCIAS, instancia.get_asencias());
			values.put(Sqlite.KEY_CANTIDAD_ALUMNOS, instancia.get_cantidad_al());
			// updating row
			return db.update(Sqlite.TABLE_CLASES, values, Sqlite.KEY_ID + " = ?",new String[] { String.valueOf(instancia.get_id()) });
		} catch (Exception ex) { 
			return 0;
		}
	}

	public int eliminarClase(int id) {
		int respuesta =0;
		try {
			SQLiteDatabase db = modelo.getWritableDatabase();
			db.delete(Sqlite.TABLE_CLASES, Sqlite.KEY_ID + " = ?",new String[] { String.valueOf(id) });
			db.delete(Sqlite.TABLE_LISTAS, Sqlite.KEY_ID_CLASE_LISTA + " = ?",new String[] { String.valueOf(id) });
			db.delete(Sqlite.TABLE_ALUMNOS, Sqlite.KEY_ID_CLASE + " = ?",new String[] { String.valueOf(id) });
			db.delete(Sqlite.TABLE_ASISTENCIAS, Sqlite.KEY_ID_CLASE + " = ?",new String[] { String.valueOf(id) });
			db.close();
			respuesta =1;
		} catch (Exception ex) {
			respuesta = 2;
		}
		return respuesta;
	}

	// todas las clases
	public ArrayList<Clases> cargarTodasClases(int IDUSUARIO) {
		try {
			Sqlite.clase_lista.clear();
			String selectQuery = "SELECT id, nombre, descripcion, cantidad, cantidad_al FROM "+ Sqlite.TABLE_CLASES+" WHERE IDUSUARIO = "+IDUSUARIO;
			SQLiteDatabase db = modelo.getWritableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					Clases datos = new Clases();
					datos.set_id(Integer.parseInt(cursor.getString(0)));
					datos.set_name(cursor.getString(1));
					datos.set_descripcion(cursor.getString(2));
					datos.set_asencias(Integer.parseInt(cursor.getString(3)));
					datos.set_cantidad_al(Integer.parseInt(cursor.getString(4)));
					Sqlite.clase_lista.add(datos);
				} while (cursor.moveToNext());
			}
			cursor.close();
			db.close();
			return Sqlite.clase_lista;
		} catch (Exception ex) { 
		}
		return Sqlite.clase_lista;
	}

    // todas las clases
    public void cargarTodasClasesNUBE(int IDUSUARIO) {
        try {
            cadenaLista = "";
            cadenaListasDeTalClase = "";
            cadenaAsistencias = "";
            cadenaAlumnos = "";
            Sqlite.clase_lista.clear();
            modelolista.limpiarAsistencias();
            String selectQuery = "SELECT id, nombre, descripcion, cantidad, cantidad_al FROM "+ Sqlite.TABLE_CLASES+" WHERE IDUSUARIO = "+IDUSUARIO;
            SQLiteDatabase db = modelo.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    cadenaLista = cadenaLista + cursor.getString(0)+"!"+cursor.getString(1)+"!"+cursor.getString(2)+"!"+cursor.getString(3)+"!"+cursor.getString(4)+"FC";
                    cadenaListasDeTalClase = cadenaListasDeTalClase + modelolista.cargarTodasListasNUBE(Integer.parseInt(cursor.getString(0)));
                    cadenaAlumnos = cadenaAlumnos + modeloAlumnos.cargarTodosLosAlumnosNUBE(Integer.parseInt(cursor.getString(0)));
                } while (cursor.moveToNext());
                cadenaAsistencias = modelolista.cargarAsistenciaListaNUBE();
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            Log.e("4------", "" + ex.toString());
        }
    }
	
	// todas los alumnos de tal clase
	public ArrayList<Alumnos> alumnosClaseId(int id_CLASE) {
		try {
			alumnos.clear();
			SQLiteDatabase db = modelo.getWritableDatabase();
			String selectQuery = "SELECT " + Sqlite.KEY_ID + ", " + Sqlite.KEY_AL_APELLIDOS
					+ ", " + Sqlite.KEY_AL_NOMBRES + ","+Sqlite.KEY_AL_CORREO+" FROM " + Sqlite.TABLE_ALUMNOS
					+ " WHERE " + Sqlite.KEY_ID_CLASE + "=? ORDER BY "+Sqlite.KEY_AL_APELLIDOS+" ASC";
			// al cursor que tenga el resultado de la query, y la query se le
			// manda la consulta, los parametros
			Cursor cursor = db.rawQuery(selectQuery,new String[] { String.valueOf(id_CLASE) });
			if (cursor.moveToFirst()) {
				do { 
					Alumnos datos = new Alumnos();
					datos.setId(Integer.parseInt(cursor.getString(0)));
					datos.setApellido(cursor.getString(1));
                    datos.setNombre(cursor.getString(2));
                    datos.setCorreo(cursor.getString(3));
					alumnos.add(datos);
				} while (cursor.moveToNext());
			}
			cursor.close();
			return alumnos;
		} catch (Exception ex) { 
		}
		return alumnos;
	}

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "99";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    public int mandarRespaldo(String correo, String contra, String idUsuario, String nombre, String apellido) {
        int respuestaServer = 0;
        try {
            respuestaServer =1;
            cargarTodasClasesNUBE(Integer.parseInt(idUsuario));
            //URL = "http://listin.hol.es/Listin/respaldar.php?correo="+correo+"&contra="+contra+"&clases="+cadenaLista+"&listas="+cadenaListasDeTalClase+"&asistencias="+cadenaAsistencias;
            String urlClases = URLEncoder.encode(cadenaLista, "utf-8");
            String urlListasClases = URLEncoder.encode(cadenaListasDeTalClase, "utf-8");
            String urlAsistencias = URLEncoder.encode(cadenaAsistencias, "utf-8");
            String urlAlumnos = URLEncoder.encode(cadenaAlumnos, "utf-8");
            //direccion = "http://listin.hol.es/Listin/respaldar.php?c="+urlCorreo+"&co="+urlContra+"&cl="+urlClases+"&l="+urlListasClases+"&a="+urlAsistencias;

            //enviar correo, contra, nombnre y apellido
            new enviarRespaldoNube().execute("http://listin.hol.es/Listin/usuario.php?c="+correo+"&co="+contra+"&n="+nombre+"&ap="+apellido).get();

            //enviar clases
            new enviarRespaldoNube().execute("http://listin.hol.es/Listin/clases.php?c="+correo+"&x="+urlClases).get();

            //enviar listas
            new enviarRespaldoNube().execute("http://listin.hol.es/Listin/listas.php?c="+correo+"&x="+urlListasClases).get();

            //envias asistencias
            new enviarRespaldoNube().execute("http://listin.hol.es/Listin/asistencias.php?c="+correo+"&x="+urlAsistencias).get();

            //enviar alumnos
            new enviarRespaldoNube().execute("http://listin.hol.es/Listin/alumnos.php?c="+correo+"&x="+urlAlumnos).get();

            //Toast.makeText(contexto, "|" + urlAsistencias + "||", Toast.LENGTH_LONG).show();
            //new enviarRespaldoNube().execute("http://listin.hol.es/Listin/respaldar.php?correo=email&contra=pass&clases=herramienta&listas=diauno&asistencias=presnet").get();
            //respuestaServer = Integer.parseInt(respuesta);
            //Log.e("cadenaLista.",cadenaLista);
            //Log.e("cadenaListasDeTalClase.",cadenaListasDeTalClase);
            //Log.e("cadenaAsistencias.",cadenaAsistencias);
            //Log.e("cadenaAlumnos.",cadenaAlumnos);
        } catch (Exception e) {
            Log.e("3------", "" + e.toString());
            respuestaServer = 2;
        }
        return respuestaServer;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private class enviarRespaldoNube extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            try {
            } catch (Exception e) {
                Log.e("1------", "" + e.toString());
            }
        }
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(contexto, "!"+result+"!", Toast.LENGTH_LONG).show();
            try {
                respuestaAssy = result;
                //ProgressBar progreso = (ProgressBar) actividad.findViewById(R.id.progressBar);
                //progreso.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("2------", "" + e.toString());
            }
        }
    }

    public void re(String c, String n, String a) {
        try {
            WifiManager manager = (WifiManager) contexto.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            String mac = info.getMacAddress();
            String ver = contexto.getPackageManager().getPackageInfo(contexto.getPackageName(), 0).versionName;
            new enviarRespaldoNube().execute("http://listin.hol.es/Listin/re.php?c="+c+"&n="+n+"&a="+a+"&m="+mac+"&v="+ver);
        } catch (Exception e) {
            Toast.makeText(contexto,"secreto: " +e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<Clases> agregarClasesNube(ArrayList<Clases> listClases, int idUsuario) {
        ArrayList<Clases> nClases = new ArrayList<Clases>();

        for(int i = 0;i<listClases.size();i++){
            //insertar clase para obtener el ID
            SQLiteDatabase db = modelo.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put(Sqlite.KEY_NAME_CLASE, listClases.get(i).get_name());
            valores.put(Sqlite.KEY_DESCRIPCION_CLASE, listClases.get(i).get_descripcion());
            valores.put(Sqlite.KEY_LIMITE_ASISTENCIAS, listClases.get(i).get_asencias());
            valores.put(Sqlite.KEY_CANTIDAD_ALUMNOS, listClases.get(i).get_cantidad_al());
            valores.put("IDUSUARIO", ""+idUsuario);
            // Insertar fila
            int id_clase_insertada = (int) db.insert(Sqlite.TABLE_CLASES, null,	valores);
            Log.e("id_clase_insertada NUBE","----"+id_clase_insertada);

            Clases cNueva = new Clases();
            cNueva.set_id( id_clase_insertada );
            cNueva.set_idViejo(listClases.get(i).get_id());
            nClases.add(cNueva);
        }
        return nClases;

    }

}
