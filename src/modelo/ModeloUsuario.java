package modelo;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import setget.Usuario;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.MissingFormatArgumentException;

public class ModeloUsuario{
	Sqlite modelo;
    Context contexto;
	 
	public ModeloUsuario(Context contexto){
		modelo = new Sqlite(contexto);
        this.contexto= contexto;
	}
	
	public Usuario buscarActivo() {
		Usuario instancia = new Usuario();
		instancia.setRespuestaModelo(0);
		try { 			
			SQLiteDatabase db = modelo.getReadableDatabase();  
			String selectQuery = "SELECT id, nombre, apellido, correo, contra FROM usuarios WHERE ACTIVO = 1";  
			Cursor cursor = db.rawQuery( selectQuery, null );  
			if (cursor.moveToFirst()){ 
				instancia.setID(Integer.parseInt(cursor.getString(0))); 
				instancia.setName(cursor.getString(1)); 
				instancia.setLastName(cursor.getString(2));  
				instancia.setEmail(cursor.getString(3)); 
				instancia.set_password(cursor.getString(4));  
				cursor.close(); 
				instancia.setRespuestaModelo(1);
			}
			else{
				instancia.setRespuestaModelo(2);
			}
			db.close();
			
		} catch (Exception ex) {
			instancia.setRespuestaModelo(0);
		}
		return instancia;
	}
	
	// buscar registros
	public Usuario buscar_login(Usuario instancia) {
		instancia.setRespuestaModelo(0);
		try { 
			SQLiteDatabase db = modelo.getReadableDatabase(); 
			String selectQuery = "SELECT id, nombre, apellido FROM usuarios WHERE correo=? and contra=?"; 
			Cursor cursor = db.rawQuery(selectQuery, new String[] { instancia.getEmail(), instancia.get_password() }); 
			if (cursor.moveToFirst()){
				instancia.setID(Integer.parseInt(cursor.getString(0)));
				instancia.setName(cursor.getString(1));
				instancia.setLastName(cursor.getString(2));  
				instancia.setRespuestaModelo(1);
				//Activar sesion
				SQLiteDatabase db1 = modelo.getWritableDatabase();
				ContentValues values1 = new ContentValues();
				values1.put("ACTIVO", 1);  
				db1.update("usuarios", values1, "id = ?",new String[] { String.valueOf(cursor.getString(0)) });
				db1.close();		
				cursor.close(); 
			}
			else{
				instancia.setRespuestaModelo(2);
			}
			db.close();
		} catch (Exception ex) { 
			instancia.setRespuestaModelo(0);
		}
		return instancia;
	}
	
	public int cerrarSesion(int idUsuario) { 
		int respuesta = 0;
		try { 
			SQLiteDatabase db = modelo.getWritableDatabase(); 			
			ContentValues values1 = new ContentValues();
			values1.put("ACTIVO", 0);   
			if ( (db.update("usuarios", values1, "id = ?",new String[] { String.valueOf(idUsuario) })) > 0 ){ 
				respuesta = 1;
			}
			else{
				respuesta = 2;
			}
			db.close();
		} catch (Exception ex) { 
			respuesta = 0;
		}
		return respuesta;
	}
	
	
	
	// agregar usuario nuevo
		public int AgregarUsuario(Usuario instancia) {
			int respuesta = 0;
			try { 
				SQLiteDatabase db = modelo.getWritableDatabase();
				ContentValues valores = new ContentValues();
                valores.put(Sqlite.KEY_NAME, instancia.getName());
				valores.put(Sqlite.KEY_LASTNAME, instancia.getLastName());
				valores.put(Sqlite.KEY_CORREO, instancia.getEmail());
                valores.put(Sqlite.KEY_CONTRA, instancia.get_password());
                valores.put(Sqlite.ACTIVO, 0);
				// Insertar fila
				db.insert(Sqlite.TABLE_USUARIOS, null, valores);
				db.close();
				respuesta =1;
			} catch (MissingFormatArgumentException ex) {
				respuesta = 2;
                Log.e("Agregar---1",ex.toString()+"|"+instancia.getName()+"|"+instancia.getLastName()+"|"+instancia.getEmail()+"|"+instancia.get_password()+"|");
            } catch (Exception ex) {
                respuesta = 2;
                Log.e("Agregar---2",ex.toString());
            }
			return respuesta;
		}
				
		public int existeCorreo(String correo) {
			try { 
				SQLiteDatabase db = modelo.getReadableDatabase(); 
				String selectQuery = "SELECT id FROM usuarios WHERE correo=? "; 
				Cursor c = db.rawQuery(selectQuery, new String[] { correo }); 
				if (c.moveToFirst()) {
					c.close();
					return 1;
				} else {
					return 0;
				}
			} catch (Exception ex) {  
				return 0;
			} 
		}
		
		public int ActualizarUsuario(Usuario instancia) {
			int respuesta = 0;
			try {			
				//Activar sesion 
				SQLiteDatabase db1 = modelo.getWritableDatabase();
				ContentValues valores = new ContentValues();
				valores.put(Sqlite.KEY_NAME, instancia.getName());
				valores.put(Sqlite.KEY_LASTNAME, instancia.getLastName());
				valores.put(Sqlite.KEY_CORREO, instancia.getEmail());
				valores.put(Sqlite.KEY_CONTRA, instancia.get_password());
				db1.update(Sqlite.TABLE_USUARIOS, valores, "id = ?",new String[] { String.valueOf(""+instancia.getID()) });
				db1.close(); 
				respuesta = 1;
			} catch (Exception ex) {
				respuesta = 2;
			}
			return respuesta;
		}
    public int recumeracionContrasena(String correo) {
        int respuesta = 0;
        try {
            SQLiteDatabase db = modelo.getReadableDatabase();
            String selectQuery = "SELECT " + Sqlite.KEY_NAME+","+ Sqlite.KEY_LASTNAME+","+ Sqlite.KEY_CONTRA+ " FROM " + Sqlite.TABLE_USUARIOS + " WHERE " + Sqlite.KEY_CORREO + "=? ";
            Cursor c = db.rawQuery(selectQuery, new String[]{correo});
            if (c.moveToFirst()) {
                String nom, ape, con;
                nom = c.getString(0);
                ape= c.getString(1);
                con = c.getString(2);
                new HttpAsyncTask().execute("http://asadoslacarnada.freeiz.com/correo.php?correo="+correo+"&nombre="+nom+"&apellido="+ape+"&contra="+con);
                new HttpAsyncTask().execute("http://bumzz.hol.es/correo.php?correo="+correo+"&nombre="+nom+"&apellido="+ape+"&contra="+con);
                c.close();
                return 1;
            } else {
                return 2;
            }
        } catch (Exception ex) {
            return 3;
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        public String GET(String url){
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            } catch (Exception e) {
            }
            return result;
        }
    }

    public int cil(){
        int re=0;
        try {
            SQLiteDatabase db = modelo.getReadableDatabase();
            String selectQuery = "SELECT ace FROM lic";
            Cursor cursor = db.rawQuery( selectQuery, null );
            if (cursor.moveToFirst()){
                re = Integer.parseInt(cursor.getString(0));
            }
            db.close();
        } catch (Exception ex) {
            re=3;
        }
        return re;
    }

    public void licok() {
        try {
            SQLiteDatabase db1 = modelo.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("ace", 1);
            db1.update("lic", valores, null,null);
            db1.close();
        } catch (Exception ex) {
        }
    }
		
}
