package modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.uca.list.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import setget.Alumnos;
import setget.AsistenciaAlumno;
import setget.Clases;
import setget.Listas;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ModeloAsistencia {
    Sqlite modelo;
    Context contexto;
    Activity actividad;
    private ArrayList<AsistenciaAlumno> asistenciaAlumnos;
    private ArrayList<Alumnos> alumnos;
    private Integer respuestaInternet = 0;

    public ModeloAsistencia(Context contexto) {
        modelo = new Sqlite(contexto);
        this.contexto = contexto;
        asistenciaAlumnos = new ArrayList<AsistenciaAlumno>();
        alumnos = new ArrayList<Alumnos>();
    }

    public ModeloAsistencia(Context contexto, Activity actividad) {
        modelo = new Sqlite(contexto);
        this.contexto = contexto;
        asistenciaAlumnos = new ArrayList<AsistenciaAlumno>();
        alumnos = new ArrayList<Alumnos>();
        this.actividad = actividad;
    }



    public ArrayList<AsistenciaAlumno> alumnoDetalleAsistencia(int id_alumno,
                                                               int id_CLASE) {
        try {
            asistenciaAlumnos.clear();
            SQLiteDatabase db = modelo.getWritableDatabase();

            String selectQuery = "SELECT " + Sqlite.KEY_ID_LISTA + ", "
                    + Sqlite.KEY_ASISTENCIA + ", " + "(SELECT "
                    + Sqlite.KEY_FECHA + " FROM " + Sqlite.TABLE_LISTAS
                    + " WHERE Asis." + Sqlite.KEY_ID_LISTA + "="
                    + Sqlite.TABLE_LISTAS + "." + Sqlite.KEY_ID + ") as Fecha"
                    + " FROM " + Sqlite.TABLE_ASISTENCIAS + " Asis "
                    + "WHERE IDALUM =? and " + Sqlite.KEY_ID_CLASE + "=? and "
                    + Sqlite.KEY_ASISTENCIA + "!=0 ORDER BY Fecha ASC";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{"" + id_alumno, "" + id_CLASE});
            if (cursor.moveToFirst()) {
                do {
                    // Log.e(null,
                    // "entro"+cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
                    AsistenciaAlumno datos = new AsistenciaAlumno();
                    datos.setId_lista(Integer.parseInt(cursor.getString(0)));
                    datos.setAsistencia(Integer.parseInt(cursor.getString(1)));
                    datos.setFecha(cursor.getString(2));
                    asistenciaAlumnos.add(datos);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return asistenciaAlumnos;
        } catch (Exception ex) {
        }
        return asistenciaAlumnos;
    }

    public int mandarCorreoDeAsistenciasAlumno(int idAlumno, int idClase, String correo, String nom, String ape) {
        int respuesta = 0;
        try {
            SQLiteDatabase db = modelo.getWritableDatabase();
            String selectQuery = "SELECT " + Sqlite.KEY_ID_LISTA + ", "
                    + Sqlite.KEY_ASISTENCIA + ", " + "(SELECT "
                    + Sqlite.KEY_FECHA + " FROM " + Sqlite.TABLE_LISTAS
                    + " WHERE Asis." + Sqlite.KEY_ID_LISTA + "="
                    + Sqlite.TABLE_LISTAS + "." + Sqlite.KEY_ID + ") as Fecha"
                    + " FROM " + Sqlite.TABLE_ASISTENCIAS + " Asis "
                    + "WHERE IDALUM =? and " + Sqlite.KEY_ID_CLASE + "=? and "
                    + Sqlite.KEY_ASISTENCIA + "!=0 ORDER BY Fecha ASC";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{"" + idAlumno, "" + idClase});
            String detalles = "";
            if (cursor.moveToFirst()) {
                do {
                    String asistencia = "";
                    switch (Integer.parseInt(cursor.getString(1))) {
                        case 1:
                            asistencia = "1";
                            break;
                        case 2:
                            asistencia = "2";
                            break;
                        case 3:
                            asistencia = "3";
                            break;
                    }
                    detalles = cursor.getString(2) + "--" + asistencia + "!" + detalles;
                } while (cursor.moveToNext());
            }
            String urldetalles = URLEncoder.encode(detalles, "utf-8");

            String direccion = "c="+correo+"&n="+nom+"&a="+ape+"&d="+urldetalles;
            new enviarAsistenciaAlumno().execute("http://asadoslacarnada.freeiz.com/detalleAsistencia.php?"+direccion);
            new enviarAsistenciaAlumno().execute("http://bumzz.hol.es/detalleAsistencia.php?"+direccion);
            Log.e("DIRECCION---", direccion);

            //String str_result = new HttpAsyncTask().execute("http://cafi.hol.es/CAFI/ListIn.php").get();
            //Toast.makeText(contexto, "-----" , Toast.LENGTH_LONG).show();
            cursor.close();
            respuesta = 1;
        } catch (Exception ex) {
            respuesta = 3;
            Log.e("Problemas correo: ", ex.toString());
        }
        return respuesta;
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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }


    private class enviarAsistenciaAlumno extends AsyncTask<String, Void, String> {
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
                //respuestaInternet = Integer.parseInt(result);
                //Toast.makeText(contexto, "--" + respuestaInternet + "--", Toast.LENGTH_LONG).show();
                //ProgressBar progreso = (ProgressBar) actividad.findViewById(R.id.progressBar);
                //progreso.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e("2------", "" + e.toString());
            }
        }
    }



        public ArrayList<AsistenciaAlumno> alumnoDetalleAsistenciaExportacion(int id_alumno, int id_CLASE) {
            try {
                asistenciaAlumnos.clear();
                SQLiteDatabase db = modelo.getWritableDatabase();
                String selectQuery = "SELECT " + Sqlite.KEY_ID_LISTA + ", "
                        + Sqlite.KEY_ASISTENCIA + ", " + "(SELECT " + Sqlite.KEY_FECHA + " FROM "
                        + Sqlite.TABLE_LISTAS + " WHERE Asis." + Sqlite.KEY_ID_LISTA + "="
                        + Sqlite.TABLE_LISTAS + "." + Sqlite.KEY_ID + ") as Fecha" + " FROM "
                        + Sqlite.TABLE_ASISTENCIAS + " Asis " + "WHERE IDALUM =? and "
                        + Sqlite.KEY_ID_CLASE + "=? ORDER BY Fecha ASC";
                Cursor cursor = db.rawQuery(selectQuery, new String[]{"" + id_alumno, "" + id_CLASE});
                if (cursor.moveToFirst()) {
                    do {
                        // Log.e(null,
                        // "entro"+cursor.getString(0)+cursor.getString(1)+cursor.getString(2));
                        AsistenciaAlumno datos = new AsistenciaAlumno();
                        datos.setId_lista(Integer.parseInt(cursor.getString(0)));
                        datos.setAsistencia(Integer.parseInt(cursor.getString(1)));
                        datos.setFecha(cursor.getString(2));
                        asistenciaAlumnos.add(datos);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                return asistenciaAlumnos;
            } catch (Exception ex) {
            }
            return asistenciaAlumnos;
        }

        public void actualizarAsistenciaAlumnos(Alumnos instancia, int id_lista, int estadoAnterior) {
            try {
                SQLiteDatabase db = modelo.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Sqlite.KEY_ID_LISTA, id_lista);
                values.put(Sqlite.KEY_ID_CLASE, instancia.getId_su_clase());
                // Presente=1 Tarde=2 Ausente=3
                values.put(Sqlite.KEY_ASISTENCIA, instancia.getAsistencia());
                // updating row
                db.update(Sqlite.TABLE_ASISTENCIAS, values, Sqlite.KEY_ID + " = ?", new String[]{String.valueOf(instancia.getId())});
                actualizarListado(id_lista, instancia.getAsistencia(), estadoAnterior);
            } catch (Exception ex) {
                Log.e("Problemas para actualizar la asistencia de alumnos",ex.toString());
            }
        }

        public Listas buscarListas(int id) {
            SQLiteDatabase db = modelo.getReadableDatabase();
            Cursor cursor = db.query(Sqlite.TABLE_LISTAS, new String[]{Sqlite.KEY_FECHA,
                            Sqlite.KEY_PRESENTES, Sqlite.KEY_AUSENTES, Sqlite.KEY_TARDES, Sqlite.KEY_ID_CLASE_LISTA},
                    Sqlite.KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor != null)
                cursor.moveToFirst();
            Listas encontrado = new Listas(id, cursor.getString(0),
                    Integer.parseInt(cursor.getString(1)), Integer.parseInt(cursor
                    .getString(2)), Integer.parseInt(cursor.getString(3)),
                    Integer.parseInt(cursor.getString(4)));
            cursor.close();
            db.close();
            return encontrado;
        }

        //actualizar la cantidad de Presentes, Ausentes o Tardes
        public void actualizarListado(int id_lista, int pres_tard_o_ausen, int estadoAnterior) {
            Listas encontrado = buscarListas(id_lista);
            switch (pres_tard_o_ausen) {
                case 1:
                    if (estadoAnterior == 2) {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), (encontrado.getPresentes() + 1), (encontrado.getTardes() - 1), encontrado.getAusentes(), encontrado.getId_clase());
                    } else if (estadoAnterior == 3) {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), (encontrado.getPresentes() + 1), encontrado.getTardes(), (encontrado.getAusentes() - 1), encontrado.getId_clase());
                    } else {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), (encontrado.getPresentes() + 1), encontrado.getTardes(), encontrado.getAusentes(), encontrado.getId_clase());
                    }
                    break;
                case 2:
                    if (estadoAnterior == 1) {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), (encontrado.getPresentes() - 1), (encontrado.getTardes() + 1), encontrado.getAusentes(), encontrado.getId_clase());
                    } else if (estadoAnterior == 3) {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), encontrado.getPresentes(), (encontrado.getTardes() + 1), (encontrado.getAusentes() - 1), encontrado.getId_clase());
                    } else {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), encontrado.getPresentes(), (encontrado.getTardes() + 1), encontrado.getAusentes(), encontrado.getId_clase());
                    }
                    break;
                case 3:
                    if (estadoAnterior == 1) {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), (encontrado.getPresentes() - 1), encontrado.getTardes(), (encontrado.getAusentes() + 1), encontrado.getId_clase());
                    } else if (estadoAnterior == 2) {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), encontrado.getPresentes(), (encontrado.getTardes() - 1), (encontrado.getAusentes() + 1), encontrado.getId_clase());
                    } else {
                        actualizarPorAsistencia(id_lista, encontrado.getFecha(), encontrado.getPresentes(), encontrado.getTardes(), (encontrado.getAusentes() + 1), encontrado.getId_clase());
                    }
                    break;
            }
        }


        public int actualizarPorAsistencia(int id_lista, String fecha, int pre, int tarde, int ausen, int id_clase) {
            try {
                SQLiteDatabase db = modelo.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Sqlite.KEY_FECHA, fecha);
                values.put(Sqlite.KEY_PRESENTES, pre);
                values.put(Sqlite.KEY_TARDES, tarde);
                values.put(Sqlite.KEY_AUSENTES, ausen);
                values.put(Sqlite.KEY_ID_CLASE_LISTA, id_clase);
                return db.update(Sqlite.TABLE_LISTAS, values, Sqlite.KEY_ID + " = ?", new String[]{String.valueOf(id_lista)});
            } catch (Exception ex) {
                return 0;
            }
        }


        public ArrayList<Alumnos> alumnosIdClasePresentes(int id_CLASE, int id_LISTA) {
            try {
                alumnos.clear();
                SQLiteDatabase db = modelo.getWritableDatabase();

                String selectQuery = "SELECT " + Sqlite.KEY_ID +","+Sqlite.IDALUM+ ", (Select "+Sqlite.KEY_AL_APELLIDOS+" FROM " + Sqlite.TABLE_ALUMNOS + " WHERE "+Sqlite.TABLE_ALUMNOS+"."+Sqlite.KEY_ID +"="+Sqlite.TABLE_ASISTENCIAS+"."+Sqlite.IDALUM+") AS "+Sqlite.KEY_AL_APELLIDOS
                        + ", (Select "+Sqlite.KEY_AL_NOMBRES+" FROM " + Sqlite.TABLE_ALUMNOS + " WHERE "+Sqlite.TABLE_ALUMNOS+"."+Sqlite.KEY_ID +"="+Sqlite.TABLE_ASISTENCIAS+"."+Sqlite.IDALUM+") AS "+Sqlite.KEY_AL_NOMBRES + " FROM " + Sqlite.TABLE_ASISTENCIAS
                        + " WHERE " + Sqlite.KEY_ASISTENCIA + " =? AND " + Sqlite.KEY_ID_CLASE + " =? AND " + Sqlite.KEY_ID_LISTA + " =?";
                Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf("1"), String.valueOf(id_CLASE), String.valueOf(id_LISTA)});

                if (cursor.moveToFirst()) {
                    do {
                        Alumnos datos = new Alumnos();
                        datos.setId(Integer.parseInt(cursor.getString(0)));
                        datos.setId_su_clase(Integer.parseInt(cursor.getString(1)));
                        datos.setApellido(cursor.getString(2));
                        datos.setNombre(cursor.getString(3));
                        alumnos.add(datos);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                return alumnos;
            } catch (Exception ex) {
                Log.e("Problemas Alumnos Presentes: ", ex.toString());
            }
            return alumnos;
        }

        public ArrayList<Alumnos> alumnosIdClaseAusentes(int id_CLASE, int id_LISTA) {
            try {
                alumnos.clear();
                SQLiteDatabase db = modelo.getWritableDatabase();
                String selectQuery = "SELECT " + Sqlite.KEY_ID +","+Sqlite.IDALUM+ ", (Select "+Sqlite.KEY_AL_APELLIDOS+" FROM " + Sqlite.TABLE_ALUMNOS + " WHERE "+Sqlite.TABLE_ALUMNOS+"."+Sqlite.KEY_ID +"="+Sqlite.TABLE_ASISTENCIAS+"."+Sqlite.IDALUM+") AS "+Sqlite.KEY_AL_APELLIDOS
                        + ", (Select "+Sqlite.KEY_AL_NOMBRES+" FROM " + Sqlite.TABLE_ALUMNOS + " WHERE "+Sqlite.TABLE_ALUMNOS+"."+Sqlite.KEY_ID +"="+Sqlite.TABLE_ASISTENCIAS+"."+Sqlite.IDALUM+") AS "+Sqlite.KEY_AL_NOMBRES + " FROM " + Sqlite.TABLE_ASISTENCIAS
                        + " WHERE " + Sqlite.KEY_ASISTENCIA + " =? AND " + Sqlite.KEY_ID_CLASE + " =? AND " + Sqlite.KEY_ID_LISTA + " =?";
                Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf("3"), String.valueOf(id_CLASE), String.valueOf(id_LISTA)});

                if (cursor.moveToFirst()) {
                    do {
                        Alumnos datos = new Alumnos();
                        datos.setId(Integer.parseInt(cursor.getString(0)));
                        datos.setId_su_clase(Integer.parseInt(cursor.getString(1)));
                        datos.setApellido(cursor.getString(2));
                        datos.setNombre(cursor.getString(3));
                        alumnos.add(datos);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                return alumnos;
            } catch (Exception ex) {
                Log.e("Problemas Alumnos Ausentes: ", ex.toString());
            }
            return alumnos;
        }

        public ArrayList<Alumnos> alumnosIdClaseTardes(int id_CLASE, int id_LISTA) {
            try {
                alumnos.clear();
                SQLiteDatabase db = modelo.getWritableDatabase();
                String selectQuery = "SELECT " + Sqlite.KEY_ID +","+Sqlite.IDALUM+ ", (Select "+Sqlite.KEY_AL_APELLIDOS+" FROM " + Sqlite.TABLE_ALUMNOS + " WHERE "+Sqlite.TABLE_ALUMNOS+"."+Sqlite.KEY_ID +"="+Sqlite.TABLE_ASISTENCIAS+"."+Sqlite.IDALUM+") AS "+Sqlite.KEY_AL_APELLIDOS
                        + ", (Select "+Sqlite.KEY_AL_NOMBRES+" FROM " + Sqlite.TABLE_ALUMNOS + " WHERE "+Sqlite.TABLE_ALUMNOS+"."+Sqlite.KEY_ID +"="+Sqlite.TABLE_ASISTENCIAS+"."+Sqlite.IDALUM+") AS "+Sqlite.KEY_AL_NOMBRES + " FROM " + Sqlite.TABLE_ASISTENCIAS
                        + " WHERE " + Sqlite.KEY_ASISTENCIA + " =? AND " + Sqlite.KEY_ID_CLASE + " =? AND " + Sqlite.KEY_ID_LISTA + " =?";
                Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf("2"), String.valueOf(id_CLASE), String.valueOf(id_LISTA)});

                if (cursor.moveToFirst()) {
                    do {
                        Alumnos datos = new Alumnos();
                        datos.setId(Integer.parseInt(cursor.getString(0)));
                        datos.setId_su_clase(Integer.parseInt(cursor.getString(1)));
                        datos.setApellido(cursor.getString(2));
                        datos.setNombre(cursor.getString(3));
                        alumnos.add(datos);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                return alumnos;
            } catch (Exception ex) {
                Log.e("Problemas Alumnos Tardes: ", ex.toString());
            }
            return alumnos;
        }
}
