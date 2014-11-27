package com.uca.list;

import java.util.ArrayList;

import android.view.WindowManager;
import modelo.DialogoArchivo;
import modelo.Leercsv;
import modelo.ModeloClase;
import modelo.Sqlite;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import setget.Clases;
import setget.Alumnos;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AgregarClase extends Activity {

	// variables de la interfaz
	EditText txt_nombre, txt_descripcion, txt_ausencias;
	TextView btn_crear, txt_archivo, txt_ayuda;
	Button btn_archivo;
	// variables para usar cuando se validen los campos
	String nombre_validado, descripcion_validada, Toast_msg,
			direccion_archivo_validada=null;
	int id_actualizar, ausencias_validada = 0, idUsuario;
	ArrayList<Alumnos> arregloListadoCsv;
	// iniciar sqlite
	ModeloClase modeloClase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agregarclase);
		iniciarVariables();

		// origen de los tiempos
		eventos();
	}

	private void iniciarVariables() {
		modeloClase = new ModeloClase(getApplicationContext());
		arregloListadoCsv = new ArrayList<Alumnos>();

		// cargar variables de interfaz
		btn_archivo = (Button) findViewById(R.id.btn_archivo);
		txt_nombre = (EditText) findViewById(R.id.txt_nombre);
		txt_descripcion = (EditText) findViewById(R.id.txt_descripcion);
		txt_ayuda = (TextView) findViewById(R.id.txt_ayuda);

		txt_ausencias = (EditText) findViewById(R.id.txt_ausencias);
		txt_archivo = (TextView) findViewById(R.id.txt_nombre_archivo);
		btn_crear = (TextView) findViewById(R.id.btn_crear);
		btn_crear.setTextColor(Color.rgb(82, 82, 82));

		idUsuario = Integer.parseInt(getIntent().getStringExtra("idUsuario"));
	}

	private void eventos() {
		txtEventos();

		btnEventos();
	}

	private void terminar() {
		this.finish();
	}

	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

	private void conteoCerrar() {
		new CountDownTimer(2000, 1000) {
			public void onTick(long millisUntilFinished) {

			}

			public void onFinish() {
				terminar();
			}
		}.start();
	}

	private void txtEventos() {
		// MISMA FUNCION QUE EL INICIO PARA VERIFICAR
		txt_nombre.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				esValidoNombre(txt_nombre);
			}
		});
		txt_descripcion.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				esValidoDescripcion(txt_descripcion);
			}
		});
		txt_ausencias.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				esValidoAusencia(txt_ausencias);
			}
		});
	}

	private void btnEventos() {
		// misma funcion que la INICIO para los eventos de los botones
		btn_crear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// si todas las varaibles no son nulas (OSEA YA VALIDADAS)
				if (nombre_validado != null && descripcion_validada != null
						&& ausencias_validada != 0) {
					// AGREGAR usuario, pero antes se manda a
					// usuario_set_get los datos y esta misma se manda la
					// instancia a sqlite
					int respuesta = modeloClase.agregarClase(new Clases(
							nombre_validado, descripcion_validada,
							ausencias_validada, arregloListadoCsv), idUsuario);
					switch (respuesta) {
					case 0:
						mensaje("Problemas al crear Clase. Error MC-14", 3);
						conteoCerrar();
						break;
					case 1:
                        if ( arregloListadoCsv.size()==0 ){
                            mensaje("Clase creada sin alumnos", 2);
                        }
                        else{
                            mensaje("Clase creada con alumnos", 2);
                        }
						conteoCerrar();
						break;
					case 2:
						mensaje("Problemas al guardar Clase. Error MC-48", 3);
						conteoCerrar();
						break;
					}
				} else if (nombre_validado == null || descripcion_validada == null || ausencias_validada == 0 ) {
					mensaje("Verifique los Datos", 3); 
				} else if (direccion_archivo_validada == null) {
					mensaje("Seleccioner el Archivo CVS", 1);
				} else {
					mensaje("Verifique los Datos", 3); 
				}
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			}
		});
		btn_archivo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogoArchivo FileOpenDialog = new DialogoArchivo(
						AgregarClase.this, "FileOpen",
						new DialogoArchivo.SimpleFileDialogListener() {

							@Override
							public void onChosenDir(String archivo_direccion) {
								if (archivo_direccion.endsWith(".csv")) {
									direccion_archivo_validada = archivo_direccion;
									String[] parts = direccion_archivo_validada
											.split("/");
									txt_archivo.setText(parts[parts.length - 1]
											.toString());
									Leercsv instancia = new Leercsv();
									switch (instancia
											.pruebaLecturaCsv(archivo_direccion)) {
									case 1:
										mensaje("Problemas al leer el archivo",1);
										direccion_archivo_validada = null;
										break;
									case 2:
										mensaje("Problemas al cargar el archivo. Error L-36",1);
										direccion_archivo_validada = null;
										break;
									case 3:
										mensaje("Error inesperado del archivo. Error L-38",1);
										direccion_archivo_validada = null;
										break;
									case 4:
										arregloListadoCsv = instancia
												.leer_los_datos(archivo_direccion);
										mensaje("Listado cargado exitosamente",
												2);
										direccion_archivo_validada = "listo";
										break;
									}
								}
								else{
									mensaje("La extensión del archivo debe ser .csv",
											1);
									direccion_archivo_validada = null;
								}
							}
						});
				FileOpenDialog.chooseFile_or_Dir("../");
			}
		});
		txt_ayuda.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tutorial = new Intent(AgregarClase.this, PasosCSV.class);
				AgregarClase.this.startActivity(tutorial);
			}
		});

	}

	public void esValidoNombre(EditText edt) throws NumberFormatException {
		if (edt.getText().toString().length() <= 2) {
			nombre_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if ((edt.getText().toString().length() >= 3)
				&& (edt.getText().toString().length() <= 4)) {
			edt.setError("Mínimo 5 letras");
			nombre_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if ((edt.getText().toString().length() >= 15)) {
			edt.setError("Máximo 15 letras");
			nombre_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (!edt.getText().toString().matches("[a-zA-Z ]+")) {
			edt.setError("Solo letras");
			nombre_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else {
			nombre_validado = edt.getText().toString();
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}
	}

	public void esValidoDescripcion(EditText edt) throws NumberFormatException {
		if (edt.getText().toString().length() <= 2) {
			descripcion_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if ((edt.getText().toString().length() >= 3)
				&& (edt.getText().toString().length() <= 4)) {
			edt.setError("Mínimo 5 letras");
			descripcion_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if ((edt.getText().toString().length() >= 40)) {
			edt.setError("Máximo 40 letras");
			descripcion_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (!edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {
			edt.setError("Solo letras y números");
			descripcion_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else {
			descripcion_validada = edt.getText().toString();
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}
	}

	public void esValidoAusencia(EditText edt) {
		if (edt.getText().toString().length() == 0) {
			ausencias_validada = 0;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (edt.getText().toString().length() >= 2) {
			ausencias_validada = 0;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
			edt.setError("Máximo 1 dígito");
		} else {
			Integer.parseInt(edt.getText().toString());
			ausencias_validada = Integer.parseInt(edt.getText().toString());
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}
	}

	public void mensaje(String msg, int estilo) {
		// 1 Style.INFO
		// 2 Style.CONFIRM
		// 3 Style.ALERT
		switch (estilo) {
		case 1:
			Crouton.makeText(this, msg, Style.ALERT).show();
			break;
		case 2:
			Crouton.makeText(this, msg, Style.CONFIRM).show();
			break;
		case 3:
			Crouton.makeText(this, msg, Style.INFO).show();
			break;
		}
	}

	public void Limpiar() {
		txt_nombre.setText("");
		txt_descripcion.setText("");
		txt_ausencias.setText("");
	}

}
