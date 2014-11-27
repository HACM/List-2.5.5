package com.uca.list;

import modelo.ModeloAsistencia;
import modelo.ModeloLista;
import modelo.Sqlite;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import setget.Listas; 
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle; 
import android.os.CountDownTimer;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class AgregarActualizarLista extends Activity {
	// variables de la interfaz
	DatePicker fecha;
	TextView btn_crear;
	// variables para usar cuando se validen los campos
	String fecha_validado, Toast_msg, accion;
	int id_de_la_clase, id_actualizar, presente, ausente, tardes;
	// iniciar sqlite
	ModeloAsistencia modeloAsistencia;
	ModeloLista modeloLista;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mostrar layout crear_cuenta
		setContentView(R.layout.add_update_lista);
		modeloAsistencia = new ModeloAsistencia(getApplicationContext());
		modeloLista = new ModeloLista(getApplicationContext());
		

		accion = getIntent().getStringExtra("accion");
		id_de_la_clase = Integer.parseInt(getIntent()
				.getStringExtra("CLASE_ID"));

		// cargar variables de interfaz
				fecha = (DatePicker) findViewById(R.id.dt_fecha);
				btn_crear = (TextView) findViewById(R.id.btn_crear); 
				btn_crear.setTextColor(Color.rgb(0, 124, 250));
				// origen de los tiempos
				btnEventos();
				
		if (accion.equalsIgnoreCase("update")) {
			setTitle("Actualizar Lista");			
			btn_crear.setText("Actualizar");
			id_actualizar = Integer.parseInt(getIntent().getStringExtra(
					"ID_LISTA"));
			Listas datos = modeloAsistencia.buscarListas(id_actualizar);
			String[] fecha_dividida = datos.getFecha().split("-");
			//alistar por si actualizar
			presente = datos.getPresentes();
			tardes = datos.getTardes();
			ausente = datos.getAusentes();
			//alistar por si actualizar
			fecha.init(Integer.parseInt(fecha_dividida[2]),
					(Integer.parseInt(fecha_dividida[1])-1),
					Integer.parseInt(fecha_dividida[0]), null);
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}
	}
 

	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}
	private void terminar(){
		this.finish();
	}
	
	private void conteoCerrar(){
		new CountDownTimer(1000, 1000) {
			public void onTick(long millisUntilFinished) { 
				
			}
		     public void onFinish() {
		    	 terminar();
		     }
		  }.start();		
	}

	private void btnEventos() {
		// misma funcion que la INICIO para los eventos de los botones
		btn_crear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(accion.equals("insert")){
					String dia = ""+fecha.getDayOfMonth();
					String mes = ""+(fecha.getMonth()+1);
					if (dia.length()==1){
						dia="0"+dia;
					}
					if (mes.length()==1){
						mes="0"+mes;
					}
                    int respuesta =  modeloLista.agregarLista(new Listas(id_de_la_clase, dia
						+ "-"
						+ mes
						+ "-"
						+ fecha.getYear()), id_de_la_clase);
			    switch (respuesta){
                    case 0:
                        mensaje("Problemas con la Base de Datos",3);
                        break;
                    case 1:
                        mensaje("Lista Guardada",2);
                        break;
                    case 2:
                        mensaje("Problemas para Guardada",3);
                        break;
                    case 3:
                        mensaje("Lista con la misma fecha ya existe",1);
                        break;
                    case 4:
                        mensaje("Problemas de verificación",3);
                        break;
                }
				conteoCerrar();
				}
				else if(accion.equals("update")){
					String dia = ""+fecha.getDayOfMonth();
					String mes = ""+(fecha.getMonth()+1);
					if (dia.length()==1){
						dia="0"+dia;
					}
					if (mes.length()==1){
						mes="0"+mes;
					}
					modeloLista.actualizarLista(new Listas(id_actualizar,  dia
							+ "-"
							+ mes
							+ "-"
							+ fecha.getYear(), presente, tardes, ausente, id_de_la_clase ));
					Toast_msg = "Lista Actualizada";
					mensaje(Toast_msg,2);
					conteoCerrar();
				}
			}
		});
	}
	public void mensaje(String msg, int estilo) {
		//1 Style.ALERT
		//2 Style.CONFIRM
		//3 Style.INFO
		switch(estilo){
		case 1:
			Crouton.makeText(this,msg, Style.INFO).show();  
			break;
		case 2:
			Crouton.makeText(this,msg, Style.CONFIRM).show();  
			break;
		case 3:
			Crouton.makeText(this,msg, Style.ALERT).show();  
			break;
				
		}
	}
 

}
