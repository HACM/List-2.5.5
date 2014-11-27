package com.uca.list;
 
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import modelo.ModeloUsuario;
import modelo.Sqlite;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import setget.Alumnos;
import setget.Usuario;
import android.os.Bundle;
import android.app.Activity; 
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;

//EL ORIGEN DE LOS TIEMPOS
public class Inicio extends Activity {

	//CAPA DE LA BD
	ModeloUsuario modeloUsuario;
    boolean recuperacion=false;
	//variables que tiene el formulario del inicio LOGIN
	private EditText txt_email, txt_contra;
	private TextView btn_continuar, btn_crear, btn_saltar, btn_tutorial, btnlicencia;
	//variables que se usan ya cuando esten validados los campos
	private String correo_validado, contra_validada, Toast_msg;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		modeloUsuario = new ModeloUsuario(getApplicationContext());
		//verificar si ha iniciado sesion
		verificiarActivaSesion();		
	}

	private void verificiarActivaSesion() {
        if (modeloUsuario.cil()==1){
            Usuario instancia = modeloUsuario.buscarActivo();
            switch (instancia.getRespuestaModelo()){
                case 0:
                    iniciarNormal();
                    //mensaje("Upps, algo vergonzoso ha sucedido. Error: MU-31",1);
                    break;
                case 1:
                    iniciarUsuarioActivo(instancia);
                    break;
                case 2:
                    iniciarNormal();
                    break;
            }
        }
        else{
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.inicio);
            cargarVariables();
            desaparecer();
            plic();
        }
	}

    private void desaparecer() {
        txt_email.setVisibility(View.GONE);
        txt_contra.setVisibility(View.GONE);
        btn_continuar.setVisibility(View.GONE);
        btn_crear.setVisibility(View.GONE);
        btn_tutorial.setVisibility(View.GONE);
        btnlicencia.setVisibility(View.GONE);
    }

    private void plic() {
        final Dialog mnj = new Dialog(this);
        mnj.setTitle("Términos de Licencia:");
        mnj.setCanceledOnTouchOutside(false);
        mnj.setContentView(R.layout.licencia);
        Button btnaceptar = (Button) mnj.findViewById(R.id.btnaceptar);
        Button btncancelar = (Button) mnj.findViewById(R.id.btncancelar);

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeloUsuario.licok();
                mnj.dismiss();
                iniciar();
                mostrar();
            }
        });
        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mnj.dismiss();
                terminar();
            }
        });
        mnj.show();
        ScrollView scro = (ScrollView) mnj.findViewById(R.id.scrollView);
        scro.pageScroll(View.FOCUS_DOWN);
        scro.fullScroll(View.FOCUS_DOWN);
        scro.scrollTo(5, 10);
    }

    private void mostrar() {
        txt_email.setVisibility(View.VISIBLE);
        txt_contra.setVisibility(View.VISIBLE);
        btn_continuar.setVisibility(View.VISIBLE);
        btn_crear.setVisibility(View.VISIBLE);
        btn_tutorial.setVisibility(View.VISIBLE);
        btnlicencia.setVisibility(View.VISIBLE);
    }

    private void terminar() {
        finish();
    }

    private void iniciarUsuarioActivo(Usuario instancia){
		Intent panelPrincipal = new Intent(Inicio.this,	DashboardPrincipal.class); 
		panelPrincipal.putExtra("id", ""+instancia.getID());
		panelPrincipal.putExtra("nombre", instancia.getName());
		panelPrincipal.putExtra("apellido", instancia.getLastName());
		panelPrincipal.putExtra("correo", instancia.getEmail());
		panelPrincipal.putExtra("contra", instancia.get_password());
		panelPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(panelPrincipal);
		finish();
	}
	
	private void iniciarNormal(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inicio);
        cargarVariables();
		iniciar();
	}

	private void cargarVariables() { 
		txt_email = (EditText) findViewById(R.id.txt_correo);
		txt_contra = (EditText) findViewById(R.id.txt_contra);
		btn_continuar = (TextView) findViewById(R.id.btn_continuar);
		btn_crear = (TextView) findViewById(R.id.btn_crear);
		btn_tutorial = (TextView) findViewById(R.id.btn_tutorial);
        btnlicencia = (TextView) findViewById(R.id.btn_licencia);
	}

	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

	private void iniciar() {
		//color al boton continuar
		btn_continuar.setTextColor(Color.rgb(0, 124, 250));
        try{
            correo_validado = getIntent().getStringExtra("correoCreado").toString();
            txt_email.setText(correo_validado);
            txt_contra.requestFocus();
        }
        catch(Exception ex){
        }

		//eventos como de verificar correo, contra
		Txt_eventos();

		//eventos de click a los botones
		Btn_eventos();	

	}

	private void Btn_eventos() {
		//boton del continuar para LOGIN
		btn_continuar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//una vez q no sean NULL las variables ya verificadas
				if (correo_validado != null && contra_validada != null) {
					Usuario instancia = new Usuario(correo_validado, contra_validada);
					instancia = modeloUsuario.buscar_login(instancia);
					switch (instancia.getRespuestaModelo()){
					case 0:
						//mensaje("Upps, algo vergonsoso ha sucedido. Error: M-140",1);
                        mensaje("Cuenta no encontrada", 1);
						break;
					case 1:
						iniciarUsuarioActivo(instancia);
						break;
					case 2:
						btn_continuar.setTextColor(Color.rgb(82, 82, 82)); 
						mensaje("Cuenta no encontrada", 1);
                        if (recuperacion==false)mostrarTemporalmenteRecuperarContra();
						break;		
					}
				} 
				else { 
					mensaje("Verifique los Datos",3);
				}
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txt_email.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(txt_contra.getWindowToken(), 0);
            }
		});
/*
		//si da click en SALTAR
		btn_saltar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//iniciar intet desde inicio.this para q inicie panelprincipal.class
				Intent panelPrincipal = new Intent(Inicio.this,	DashboardPrincipal.class);
				//variables a mandar
				panelPrincipal.putExtra("id", ""+0);
				panelPrincipal.putExtra("nombre", "-");
				panelPrincipal.putExtra("apellido", "-");
				panelPrincipal.putExtra("contra", "-");
				panelPrincipal.putExtra("correo", "-");  				
				panelPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP 	| Intent.FLAG_ACTIVITY_NEW_TASK);
				//iniciar clase
				startActivity(panelPrincipal);
			}
		});
*/
		btn_crear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent add_user = new Intent(Inicio.this, CrearCuenta.class); 
				add_user.putExtra("perfil", "0");
				startActivity(add_user);
                terminar();
			}
		});
		btn_tutorial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                if (recuperacion==false) {
                    Intent tutorial = new Intent(Inicio.this, Tutorial.class);
                    Inicio.this.startActivity(tutorial);
                }
                else{
                    if (correo_validado!=null){
                        iniciarRecuperacion();
                    }
                    else{
                        mensaje("Por favor, ingrese el correo para recuperar su contraseña",1);
                    }
                }
			}
		});

        btnlicencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mnj = new Dialog(Inicio.this);
                mnj.setTitle("Términos de Licencia:");
                mnj.setContentView(R.layout.licencia);

                Button btnaceptar = (Button) mnj.findViewById(R.id.btnaceptar);
                Button btncancelar = (Button) mnj.findViewById(R.id.btncancelar);
                btnaceptar.setVisibility(View.GONE);
                btncancelar.setVisibility(View.GONE);
                mnj.show();
            }
        });
	}

    private void iniciarRecuperacion() {
        AlertDialog.Builder mnjconfirmacion = new AlertDialog.Builder(
                this);
        mnjconfirmacion.setTitle("Recuperación de contraseña");
        mnjconfirmacion.setIcon(R.drawable.recuperaricon);
        mnjconfirmacion.setMessage("¿Enviar contraseña al correo " + txt_email.getText()+"?");
        mnjconfirmacion.setNegativeButton("Cancelar", null);
        mnjconfirmacion.setPositiveButton("Enviar",
                new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        int respuesta = modeloUsuario.recumeracionContrasena(""+txt_email.getText());
                        switch (respuesta){
                            case 0:
                                mensaje("Problemas para enviar contraseña. Error MU-166",1);
                                break;
                            case 1:
                                mensaje("Contraseña enviada a su correo",2);
                                break;
                            case 2:
                                mensaje("Ese correo no pertenece a ninguna cuenta, por favor verifique",1);
                                break;
                            case 3:
                                mensaje("Problemas para enviar contraseña, por favor verifique la conexión a internet",1);
                                break;
                        }
                    }
                });
        mnjconfirmacion.show();
    }

    private void mostrarTemporalmenteRecuperarContra() {
        btn_tutorial.setText(R.string.recuperar);
        recuperacion=true;
        final ObjectAnimator animator = ObjectAnimator.ofInt(btn_tutorial, "textColor", Color.rgb(255, 255, 255));
        new CountDownTimer(16000, 4000) {
            public void onTick(long millisUntilFinished) {
                animator.setDuration(2000L);
                animator.setEvaluator(new ArgbEvaluator());
                animator.setInterpolator(new DecelerateInterpolator(2));
                animator.start();
            }

            public void onFinish() {
                btn_tutorial.setText(R.string.btn_tutorial);
                recuperacion=false;
                btn_tutorial.setTextColor(Color.rgb(82, 82, 82));
            }
        }.start();
    }

    private void Txt_eventos(){
		//eventos de los txt
		txt_email.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void afterTextChanged(Editable s) {
				//mandar a validar el correo
				esValidoCorreo(txt_email);
			}
		});

		txt_contra.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void afterTextChanged(Editable s) { 
				esValidaContra(txt_contra);
			}
		});
	}

    private void esValidoCorreo(EditText edt) {
		if (edt.getText().toString().length() <= 2) { 
			correo_validado = null;
			btn_continuar.setTextColor(Color.rgb(82, 82, 82));
		} else if ( (edt.getText().toString().length() >= 3) && (edt.getText().toString().length() <= 5) ) {  
			edt.setError("Correo inválido");
			correo_validado = null; 
			btn_continuar.setTextColor(Color.rgb(82, 82, 82));
		} else if (isEmailValid(edt.getText().toString()) == false) { 
			edt.setError("Correo inválido");
			correo_validado = null; 
			btn_continuar.setTextColor(Color.rgb(82, 82, 82));
		} else {
			correo_validado = edt.getText().toString(); 
			btn_continuar.setTextColor(Color.rgb(0, 124, 250));
		}
	}

	private void esValidaContra(EditText edt) {
		if (edt.getText().toString().length() <= 2) { 
			contra_validada = null;
			btn_continuar.setTextColor(Color.rgb(82, 82, 82));
		} else if ( (edt.getText().toString().length() >= 3) && (edt.getText().toString().length() <= 4) ) {
			edt.setError("Contraseña muy corta");
			contra_validada = null; 
			btn_continuar.setTextColor(Color.rgb(82, 82, 82));
		} else if (edt.getText().toString().length() <= 4) {
			edt.setError("Contraseña muy corta");
			contra_validada = null;
			btn_continuar.setTextColor(Color.rgb(82, 82, 82));
		} else if (!edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {
			edt.setError("Solo letras y números");
			contra_validada = null; 
			btn_continuar.setTextColor(Color.rgb(82, 82, 82));
		} else { 
			btn_continuar.setTextColor(Color.rgb(0, 124, 250));
			contra_validada = edt.getText().toString();
		}
	}

	boolean isEmailValid(CharSequence email) { 
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private void mensaje(String msg, int estilo) {
		//1 Style.ALERT
		//2 Style.CONFIRM
		//3 Style.INFO
		switch(estilo){
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
    @Override
    public void onBackPressed() {
    }
 
}
