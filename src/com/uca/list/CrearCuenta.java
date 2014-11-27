package com.uca.list;

import android.view.*;
import android.view.inputmethod.InputMethodManager;
import modelo.ModeloUsuario;
import modelo.Sqlite;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import setget.Usuario;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CrearCuenta extends Activity {

	// variables de la interfaz
	EditText txt_nombre, txt_apellido, txt_correo, txt_contra,
			txt_confir_contra;
	TextView btn_crear;
	Button btn_regresar;
	// variables para usar cuando se validen los campos
	String nombre_validado, apellido_validado, correo_validado,
			contra_validada, Toast_msg;
	// para perfilint
	int idUsuario, esPerfil;
	String nombre, apellido, contra, correo;

	// iniciar sqlite
	ModeloUsuario modeloUsuario;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crear_cuenta);
		modeloUsuario = new ModeloUsuario(getApplicationContext());
		esPerfil = Integer.parseInt(getIntent().getStringExtra("perfil"));
		iniciar();
	}

	private void iniciar() {
		// cargar variables de interfaz
		txt_nombre = (EditText) findViewById(R.id.txt_nombre);
		txt_apellido = (EditText) findViewById(R.id.txt_apellidocrearcuenta);
		txt_correo = (EditText) findViewById(R.id.txt_correo);
		txt_contra = (EditText) findViewById(R.id.txt_contra);
		txt_confir_contra = (EditText) findViewById(R.id.txt_confir_contra);
		btn_crear = (TextView) findViewById(R.id.btn_crear);

		// origen de los tiempos
		eventos();
		if (esPerfil == 1) {
			this.setTitle("Perfil:");
			idUsuario = Integer.parseInt(getIntent().getStringExtra("id"));
			nombre = getIntent().getStringExtra("nombre");
			apellido = getIntent().getStringExtra("apellido");
			contra = getIntent().getStringExtra("contra");
			correo = getIntent().getStringExtra("correo");

			txt_nombre.setText(nombre);
			txt_apellido.setText(apellido);
			txt_correo.setText(correo);
			txt_contra.setText(contra);
			btn_crear.setText("Actualizar");
		}
	}

	private void eventos() {
		Txt_Eventos();
		Btn_Eventos();
	}

	private void Txt_Eventos() {
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

		txt_apellido.addTextChangedListener(new TextWatcher() {
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
				esValidoApellido(txt_apellido);
			}
		});

		txt_correo.addTextChangedListener(new TextWatcher() {
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
				esValidoCorreo(txt_correo);
			}
		});

		txt_contra.addTextChangedListener(new TextWatcher() {
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
				esValidaContra(txt_contra);
			}
		});
		txt_confir_contra.addTextChangedListener(new TextWatcher() {
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
				esValidaContra_Coinciden(txt_contra, txt_confir_contra);
			}
		});
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
    private void terminar() {
        if (esPerfil == 0) {
            Intent regresar = new Intent(CrearCuenta.this, Inicio.class);
            regresar.putExtra("correoCreado", correo_validado);
            startActivity(regresar);
            this.finish();
        } else if (esPerfil == 1) {
            this.finish();
        }
    }

	private void Btn_Eventos() {
		btn_crear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// si todas las varaibles no son nulas (OSEA YA VALIDADAS)
                esValidoNombre(txt_nombre);
                esValidoApellido(txt_apellido);
                esValidoCorreo(txt_correo);
                esValidaContra(txt_contra);
                esValidaContra_Coinciden(txt_contra, txt_confir_contra);

				if (nombre_validado != null && apellido_validado != null
						&& correo_validado != null && contra_validada != null) {
					if (modeloUsuario.existeCorreo(correo_validado) != 1) {
						int respuesta = modeloUsuario
								.AgregarUsuario(new Usuario(nombre_validado,
										apellido_validado, correo_validado,
										contra_validada));
						switch (respuesta) {
						case 0:
							Mensaje("No se pudo guardar el usuario. Error MU-13",
									3);
							conteoCerrar();
							break;
						case 1:
							Mensaje("Su cuenta ha sido creada exitosamente, por favor inicie sesión",
									2);
							conteoCerrar();
							break;
						case 2:
							Mensaje("Problemas para guardar. Error MU-30", 3);
							conteoCerrar();
							break;
						}
					} else {
						if (esPerfil == 0) {
							Mensaje("Ese correo ya tiene una cuenta", 3);
						} else if (esPerfil == 1) {
							int respuesta = modeloUsuario
									.ActualizarUsuario(new Usuario(idUsuario,
											nombre_validado, apellido_validado,
											correo_validado, contra_validada));
							switch (respuesta) {
							case 0:
								Mensaje("No se pudo actualizar el usuario. Error MU-13",
										3);
								conteoCerrar();
								break;
							case 1:
								Mensaje("Cuenta actualizada, por favor inicie sesión nuevamente",
										2);
								conteoCerrar();
								break;
							case 2:
								Mensaje("Problemas para actualizar. Error MU-30",
										3);
								conteoCerrar();
								break;
							}
						}
					}
				} else {
					Mensaje("Verifique los Datos", 1);
				}
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			}
		});
	}

	public void esValidoCorreo(EditText edt) {
		if (edt.getText().toString() == null) {
			edt.setError("Correo inválido");
			correo_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (isEmailValid(edt.getText().toString()) == false) {
			edt.setError("Correo inválido");
			correo_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else {
			correo_validado = edt.getText().toString();
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}
	}

	public void esValidaContra(EditText edt) {
		if (edt.getText().toString() == null) {
			edt.setError("Invalida Contraseña");
			contra_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (!edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {
			edt.setError("Obligatorio letras y números");
			contra_validada = null;
		} else if ( (edt.getText().toString().length() >= 3) && (edt.getText().toString().length() <= 4) ) {
			edt.setError("Contraseña muy corta");
			contra_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		}
	}

	public void esValidaContra_Coinciden(EditText edt, EditText edt2) {
		if (edt2.getText().toString() == null) {
			edt2.setError("Invalida Contraseña");
			contra_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (!edt2.getText().toString().matches("[a-zA-Z0-9 ]+")) {
			edt2.setError("Obligatorio letras y números");
			contra_validada = null;
		} else if ( (edt2.getText().toString().length() >= 3) && (edt2.getText().toString().length() <= 4)) {
			edt2.setError("Contraseña muy corta");
			contra_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if ( ((edt2.getText().toString().length() >= 3) && (edt2.getText().toString().length() <= 4)) || (edt.getText().toString().equals(edt2.getText().toString()) == false)) {
			edt2.setError("Contraseñas no coinciden");
			contra_validada = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else {
			contra_validada = edt.getText().toString();
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}
	}

	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
		} else if ((edt.getText().toString().length() >= 11)) {
			edt.setError("Máximo 10 letras");
			nombre_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (!edt.getText().toString().matches("[\\p{L}\\s]+")) {
			edt.setError("Solo letras");
			nombre_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else {
			nombre_validado = edt.getText().toString();
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}

	}

	public void esValidoApellido(EditText edt) throws NumberFormatException {
		if (edt.getText().toString().length() <= 2) {
			apellido_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if ((edt.getText().toString().length() >= 3)
				&& (edt.getText().toString().length() <= 4)) {
			edt.setError("Mínimo 5 letras");
			apellido_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if ((edt.getText().toString().length() >= 11)) {
			edt.setError("Máximo 10 letras");
			apellido_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else if (!edt.getText().toString().matches("[\\p{L}\\s]+")) {
			edt.setError("Solo letras");
			apellido_validado = null;
			btn_crear.setTextColor(Color.rgb(82, 82, 82));
		} else {
			apellido_validado = edt.getText().toString();
			btn_crear.setTextColor(Color.rgb(0, 124, 250));
		}
	}

	private void Mensaje(String msg, int estilo) {
		// 1 Style.ALERT
		// 2 Style.CONFIRM
		// 3 Style.INFO
		switch (estilo) {
		case 1:
			Crouton.makeText(this, msg, Style.INFO).show();
			break;
		case 2:
			Crouton.makeText(this, msg, Style.CONFIRM).show();
			break;
		case 3:
			Crouton.makeText(this, msg, Style.ALERT).show();
			break;

		}
	}
    @Override
    public void onBackPressed() {
        terminar();
    }

}