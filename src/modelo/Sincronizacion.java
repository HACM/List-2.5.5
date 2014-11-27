package modelo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import setget.Alumnos;
import setget.AsistenciaAlumno;
import setget.Clases;
import setget.Listas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.IDN;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Sincronizacion {
    ModeloAlumno alummo;
    ModeloClase clase;
    ModeloLista lista;
    ModeloAsistencia asistencia;
    ArrayList<Alumnos> listAlumnos;
    ArrayList<Clases> listClases;
    ArrayList<Listas> listListas;
    ArrayList<AsistenciaAlumno> listAsistencia;
    int idUsuario;
    String correo;

    public Sincronizacion(Context contexto, int idUsuario, String correo){
        alummo = new ModeloAlumno(contexto);
        clase = new ModeloClase(contexto);
        lista = new ModeloLista(contexto);
        asistencia = new ModeloAsistencia(contexto);
        this.idUsuario = idUsuario;
        this.correo = correo;
    }

    public void empezar() throws ExecutionException, InterruptedException {
        listAlumnos = new ArrayList<Alumnos>();
        listClases = new ArrayList<Clases>();
        listListas = new ArrayList<Listas>();
        listAsistencia = new ArrayList<AsistenciaAlumno>();
        new sincronizarAlumnos().execute("http://listin.hol.es/Listin/sincroAlumnos.php?c="+correo).get();
     }

    public class sincronizarAlumnos extends AsyncTask<String, Void, String> {
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
            try {
                JSONObject json = new JSONObject(result);
                JSONArray articles = json.getJSONArray("alumnos");
                for (int i=0; (i<json.getJSONArray("alumnos").length()); i++){
                    Alumnos alum = new Alumnos();
                    alum.setId(Integer.parseInt(articles.getJSONObject(i).getString("idAlumno")));
                    alum.setApellido(articles.getJSONObject(i).getString("apellidos"));
                    alum.setNombre(articles.getJSONObject(i).getString("nombres"));
                    alum.setCorreo(articles.getJSONObject(i).getString("correoAlumno"));
                    alum.setId_su_clase(Integer.parseInt(articles.getJSONObject(i).getString("idClase")));
                    listAlumnos.add(alum) ;
                }
                //alummo.agregarAlumnosNube(lisAlumnos);
                new sincronizarClases().execute("http://listin.hol.es/Listin/sincroClases.php?c="+correo).get();
            } catch (JSONException e) {
                Log.e("PROBLEMAS JSON",e.toString());
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public class sincronizarClases extends AsyncTask<String, Void, String> {
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
            try {
                JSONObject json = new JSONObject(result);
                JSONArray articles = json.getJSONArray("clases");
                for (int i=0; (i<json.getJSONArray("clases").length()); i++){
                    Clases clase = new Clases();
                    clase.set_id(Integer.parseInt(articles.getJSONObject(i).getString("idClase")));
                    clase.set_name(articles.getJSONObject(i).getString("nombre"));
                    clase.set_descripcion(articles.getJSONObject(i).getString("descripcion"));
                    clase.set_asencias(Integer.parseInt(articles.getJSONObject(i).getString("limite")));
                    clase.set_cantidad_al(Integer.parseInt(articles.getJSONObject(i).getString("cantidadAl")));
                    listClases.add(clase) ;
                }
                new sincronizarListas().execute("http://listin.hol.es/Listin/sincroListas.php?c="+correo).get();
                //clase.agregarAlumnosNube(lisAlumnos);
            } catch (JSONException e) {
                Log.e("PROBLEMAS JSON",e.toString());
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public class sincronizarListas extends AsyncTask<String, Void, String> {
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
            try {
                JSONObject json = new JSONObject(result);
                JSONArray articles = json.getJSONArray("listas");
                for (int i=0; (i<json.getJSONArray("listas").length()); i++){
                    Listas lista = new Listas();
                    lista.setId(Integer.parseInt(articles.getJSONObject(i).getString("idLista")));
                    lista.setId_clase(Integer.parseInt(articles.getJSONObject(i).getString("idClase")));
                    lista.setFecha(articles.getJSONObject(i).getString("fecha"));
                    lista.setPresentes(Integer.parseInt(articles.getJSONObject(i).getString("presentes")));
                    lista.setTardes(Integer.parseInt(articles.getJSONObject(i).getString("tardes")));
                    lista.setAusentes(Integer.parseInt(articles.getJSONObject(i).getString("ausentes")));
                    listListas.add(lista) ;
                }
                //alummo.agregarAlumnosNube(lisAlumnos);
                new sincronizarAsistencias().execute("http://listin.hol.es/Listin/sincroAsistencias.php?c="+correo).get();
            } catch (JSONException e) {
                Log.e("PROBLEMAS JSON",e.toString());
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public class sincronizarAsistencias extends AsyncTask<String, Void, String> {
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
            try {
                JSONObject json = new JSONObject(result);
                JSONArray articles = json.getJSONArray("asistencias");
                for (int i=0; (i<json.getJSONArray("asistencias").length()); i++){
                    AsistenciaAlumno asistencia = new AsistenciaAlumno();
                    asistencia.setId(Integer.parseInt(articles.getJSONObject(i).getString("idAsistencia")));
                    asistencia.setId_lista(Integer.parseInt(articles.getJSONObject(i).getString("idLista")));
                    asistencia.setIdAlumno(Integer.parseInt(articles.getJSONObject(i).getString("idAlumno")));
                    asistencia.setAsistencia(Integer.parseInt(articles.getJSONObject(i).getString("asistencia")));
                    asistencia.setAusencias(Integer.parseInt(articles.getJSONObject(i).getString("ausensias")));
                    listAsistencia.add(asistencia) ;
                }
                sincronizacionTerminada();
            } catch (JSONException e) {
                Log.e("PROBLEMAS JSON",e.toString());
                e.printStackTrace();
            }
        }
    }

    private void sincronizacionTerminada() {
        //guardar clases y obtener sus nuevos ID
        listClases = clase.agregarClasesNube(listClases, idUsuario);
        listAlumnos = alummo.agregarAlumnosNube(listAlumnos,listClases, idUsuario);
        listListas = lista.agregarListaNube(listClases,listListas);
        lista.agregarAsistencias(listClases,listListas,listAlumnos,listAsistencia);
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

}
