package com.uca.list;

import java.util.ArrayList;

import android.net.ConnectivityManager;
import modelo.ModeloAlumno;
import modelo.ModeloAsistencia;
import modelo.ModeloClase;
import modelo.Sqlite;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import setget.Alumnos;
import setget.AsistenciaAlumno;
import setget.Clases;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardAlumnos extends Activity {
	// variable del txt para poner nombre de la persona
	ArrayList<Alumnos> alumos_data;
	ListView Clases_listview;
	ClasesAdaptador cAdapter;
	// iniciar sqlite
	ModeloClase modeloClase; 
	ModeloAsistencia modeloAsistencia;
	ModeloAlumno modeloAlumno; 
	
	int CLASE_ID, LISTA_ID, CANTIDAD_ALUMNOS;
	SwipeListView swipelistview;
	String nombre_validado, apellido_validado, correo_validado, Toast_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard_alumnos);
		modeloClase = new ModeloClase(getApplicationContext());
		modeloAlumno = new ModeloAlumno(getApplicationContext());
				
		CLASE_ID = Integer.parseInt(getIntent().getStringExtra("CLASE_ID"));
		CANTIDAD_ALUMNOS = Integer.parseInt(getIntent().getStringExtra(
				"CANTIDAD_ALUMNOS"));
		
		iniciarInterfaz();
		iniciarEventoSwipe();
	}

	@SuppressLint("CutPasteId")
	private void iniciarInterfaz() {
		alumos_data = new ArrayList<Alumnos>();
		Clases_listview = (ListView) findViewById(R.id.list_alumnos);
		swipelistview = (SwipeListView) findViewById(R.id.list_alumnos);
		Clases_listview.setItemsCanFocus(false);
		resfrecarDatos();
	}

	@Override
	public void onResume() {
		super.onResume();
		resfrecarDatos();
	}

	public void resfrecarDatos() {
		alumos_data.clear();
		alumos_data = modeloClase.alumnosClaseId(CLASE_ID);
		cAdapter = new ClasesAdaptador(DashboardAlumnos.this,R.layout.listview_clasealumnos, alumos_data);
		Clases_listview.setAdapter(cAdapter);
		swipelistview.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
	}
 
	public class ClasesAdaptador extends ArrayAdapter<Alumnos> {
		Activity activity;
		int layoutResourceId;
		Alumnos user;
		ArrayList<Alumnos> data = new ArrayList<Alumnos>();

		public ClasesAdaptador(Activity act, int layoutResourceId,ArrayList<Alumnos> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			notifyDataSetChanged();
		}
		

		@Override
		public int getPosition(Alumnos item) {
			// TODO Auto-generated method stub
			return super.getPosition(item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			UserHolder holder = null;

			if (row == null) {
				LayoutInflater inflater = LayoutInflater.from(activity);
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new UserHolder();
				holder.num = (TextView) row.findViewById(R.id.txt_num);
				holder.nombre = (TextView) row.findViewById(R.id.txt_nombre);
				holder.apellido = (TextView) row
						.findViewById(R.id.txt_apellido);
                holder.detalles = (Button) row.findViewById(R.id.btn_detalles);
                holder.btn_enviarDetalles = (Button) row.findViewById(R.id.btn_enviarDetalles);
				holder.editar = (Button) row.findViewById(R.id.btn_update);
				holder.eliminar = (Button) row.findViewById(R.id.btn_eliminar);
				row.setTag(holder);
			} else {
				holder = (UserHolder) row.getTag();
			}
			user = data.get(position);

			holder.detalles.setTag(user.getId() + "--" + user.getApellido()
					+ "--" + user.getNombre());
            holder.editar.setTag(user.getId() + "--" + user.getApellido()
                    + "--" + user.getNombre()+"--"+user.getCorreo());
            holder.btn_enviarDetalles.setTag(user.getId() + "--" + user.getApellido()
                    + "--" + user.getNombre()+"--"+user.getCorreo());
			holder.eliminar.setTag(user.getId() + "--" + user.getApellido()
					+ "--" + user.getNombre());

			holder.num.setText(this.getItemId(position + 1) + "- ");
			holder.apellido.setText(user.getApellido());
			holder.nombre.setText(user.getNombre());

            holder.btn_enviarDetalles.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] dividir = v.getTag().toString().split("--");
                    final int idAl = Integer.parseInt(dividir[0]);
                    final String apellido = dividir[1];
                    final String nombre = dividir[2];
                    final String correo = dividir[3];
                    if (!correo.equals("sinCorreo") ) {
                        AlertDialog.Builder mnjconfirmacion = new AlertDialog.Builder(activity);
                        mnjconfirmacion.setTitle("Enviar detalles del Alumno " + apellido + " " + nombre);
                        mnjconfirmacion.setIcon(R.drawable.correodetalles);
                        mnjconfirmacion.setMessage("¿Enviar al correo " + correo + "?");
                        mnjconfirmacion.setNegativeButton("Cancelar", null);
                        mnjconfirmacion.setPositiveButton("Enviar",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (verificarConexion()==true){
                                        modeloAsistencia = new ModeloAsistencia(getApplicationContext(), activity);
                                        int respuesta = modeloAsistencia.mandarCorreoDeAsistenciasAlumno(idAl, CLASE_ID, correo, nombre, apellido);
                                        switch (respuesta) {
                                            case 0:
                                                mensaje("Problemas para enviar correo. Error MA-65", 1);
                                                break;
                                            case 1:
                                                mensaje("Detalles de asistencia enviada al correo", 2);
                                                break;
                                            case 2:
                                                mensaje("Ese correo no pertenece a ningun alumno, por favor verifique", 1);
                                                break;
                                            case 3:
                                                mensaje("Problemas para enviar correo, por favor verifique la conexión a internet", 1);
                                                break;
                                        }
                                    }else{
                                          mensaje("Verifique la conexión a Internet", 3);
                                        }
                                    }
                                });
                        mnjconfirmacion.show();
                    }
                    else{
                        mensaje("Agrege el correo a este estudiante", 1);
                    }
                }
            });


			holder.detalles.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String[] dividir = v.getTag().toString().split("--");  
					ListView detallesList = new ListView(activity);

					AlumnosDetallesAdaptador detaAdapter;
					modeloAsistencia = new ModeloAsistencia(getApplicationContext());
					ArrayList<AsistenciaAlumno> detalles = modeloAsistencia.alumnoDetalleAsistencia(Integer.parseInt(dividir[0].toString()), CLASE_ID); 
					detaAdapter = new AlumnosDetallesAdaptador(activity,R.layout.listview_asistenciadetalles, detalles);

					detallesList.setAdapter(detaAdapter);
					detaAdapter.notifyDataSetChanged();

					AlertDialog.Builder ventanaDetalles = new AlertDialog.Builder(activity);
					ventanaDetalles.setTitle("Detalles de " + dividir[1] + " " + dividir[2] + ":");
					ventanaDetalles.setIcon(R.drawable.estados);
					ventanaDetalles.setView(detallesList);
					ventanaDetalles.setPositiveButton("Listo",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					ventanaDetalles.show();
				}
			});
			holder.editar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					String[] dividir = v.getTag().toString().split("--");

					final int id = Integer.parseInt(dividir[0].toString());

					AlertDialog.Builder alert = new AlertDialog.Builder(
							activity);
					alert.setTitle(dividir[1].toString() + " "
							+ dividir[2].toString());
					alert.setIcon(R.drawable.editaricono);
					LinearLayout layout = new LinearLayout(activity);
					final EditText editapellido = new EditText(activity);
                    final EditText editnombre = new EditText(activity);
                    final EditText editcorreo = new EditText(activity);

					layout.setOrientation(LinearLayout.VERTICAL);

                    editapellido.setHint("Nuevo Apellido");
                    editnombre.setHint("Nuevo Nombre");
                    editcorreo.setHint("Nuevo Correo");

                    editapellido.setText(dividir[1]);
                    editnombre.setText(dividir[2]);
                    editcorreo.setText(dividir[3]);

                    txtEventos(editapellido, editnombre);
                    txtEventosCorreo(editcorreo);

					layout.addView(editapellido);
                    layout.addView(editnombre);
                    layout.addView(editcorreo);
					alert.setView(layout);

					alert.setPositiveButton("Actualizar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

                                    txtEventosCorreo(editcorreo);
                                    esValidoNombre(editnombre);
                                    esValidoApellido(editapellido);

									if (nombre_validado != null
											&& apellido_validado != null ) {
										String apellido = editapellido.getText().toString();
                                        String nombre = editnombre.getText().toString();
                                        String correo = "sinCorreo";
                                        if (correo_validado != null){
                                            correo= editcorreo.getText().toString();
                                        }
										Alumnos instancia = new Alumnos();
										instancia.setApellido(apellido);
                                        instancia.setNombre(nombre);
                                        instancia.setCorreo(correo);
										instancia.setId(id);
										instancia.setId_su_clase(CLASE_ID);
										modeloAlumno.actualizarAlumno(instancia);
										resfrecarDatos();
										resfrecarDatos();
										mensaje("Su Alumno ha sido actualizado exitosamente", 2);
									} else {
										mensaje("Verifique los dados", 3);
									}

								}
							});

					alert.setNegativeButton("Cancelar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					alert.show();
				}
			});
			holder.eliminar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String[] dividir = v.getTag().toString().split("--");

					final int id = Integer.parseInt(dividir[0].toString());

					AlertDialog.Builder alert = new AlertDialog.Builder(
							activity);
					alert.setTitle("¿Eliminar a " + dividir[1] + " "
							+ dividir[2] + "?");
					alert.setIcon(R.drawable.eliminaricono);

					alert.setPositiveButton("Eliminar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									CANTIDAD_ALUMNOS = (CANTIDAD_ALUMNOS - 1);
									modeloAlumno.eliminarAlumno(id, CLASE_ID, CANTIDAD_ALUMNOS);
									resfrecarDatos();
								}
							});

					alert.setNegativeButton("Cancelar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					alert.show();
				}
			});
			return row;

		}

		class UserHolder {
			TextView num, nombre, apellido;
			Button detalles, editar, eliminar, btn_enviarDetalles;
		}

	}

        private boolean verificarConexion() {
            ConnectivityManager con_manager = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
            if (con_manager.getActiveNetworkInfo() != null
                    && con_manager.getActiveNetworkInfo().isAvailable()
                    && con_manager.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                return false;
            }
        }

    private void iniciarEventoSwipe() {
		swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
			public void onOpened(int position, boolean toRight) {
			}

			@Override
			public void onClosed(int position, boolean fromRight) {
			}

			@Override
			public void onListChanged() {
			}

			@Override
			public void onMove(int position, float x) {
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				Log.d("swipe", String.format("onStartOpen %d - action %d",
						position, action));
			}

			@Override
			public void onStartClose(int position, boolean right) {
				Log.d("swipe", String.format("onStartClose %d", position));
			}

			@Override
			public void onClickFrontView(int position) {
				Log.d("swipe", String.format("onClickFrontView %d", position));
				swipelistview.openAnimate(position); 
			}

			@Override
			public void onClickBackView(int position) {
				Log.d("swipe", String.format("onClickBackView %d", position));
				swipelistview.closeAnimate(position); 
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
			}

		});

		swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
		swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE);
		swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_CHOICE);
		swipelistview.setOffsetLeft(convertDpToPixel(230f)); // left side offset
		swipelistview.setOffsetRight(convertDpToPixel(230f)); // right side
		swipelistview.setAnimationTime(500); // Animation time
		swipelistview.setSwipeOpenOnLongPress(true); // enable or disable
	}

    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_class, menu);
		menu.getItem(0).setIcon(
				getResources().getDrawable(R.drawable.ic_action_add_person));
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_agregar_lista:
			nuevoAlumno();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void nuevoAlumno() {
        nombre_validado = null;
        apellido_validado = null;
        correo_validado =null;
		AlertDialog.Builder ventanitaagregar = new AlertDialog.Builder(this);
		ventanitaagregar.setTitle("Nuevo Alumno:");
		ventanitaagregar.setIcon(R.drawable.nuevoestudiante);

		LinearLayout layout = new LinearLayout(this);
		final EditText editapellido = new EditText(this);
		final EditText editnombre = new EditText(this);
		final EditText editcorreo = new EditText(this);

		layout.setOrientation(LinearLayout.VERTICAL);

		editapellido.setHint("Apellido");
		editnombre.setHint("Nombre");
		editcorreo.setHint("Correo");
		txtEventos(editapellido, editnombre);
		txtEventosCorreo(editcorreo);

		layout.addView(editapellido);
		layout.addView(editnombre);
		layout.addView(editcorreo);

		ventanitaagregar.setView(layout);

		ventanitaagregar.setPositiveButton("Agregar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
                        txtEventosCorreo(editcorreo);
                        esValidoNombre(editnombre);
                        esValidoApellido(editapellido);
						if (nombre_validado != null
								&& apellido_validado != null ) {
							String apellido = editapellido.getText().toString();
							String nombre = editnombre.getText().toString();
							String correo = "sinCorreo";
                            if (correo_validado != null){
                                correo= editcorreo.getText().toString();
                            }
							Alumnos instanciaAl = new Alumnos();
							instanciaAl.setApellido(apellido);
							instanciaAl.setNombre(nombre);
							instanciaAl.setCorreo(correo);
							instanciaAl.setId_su_clase(CLASE_ID);
							CANTIDAD_ALUMNOS = (CANTIDAD_ALUMNOS + 1);
							modeloAlumno.agregarAlumno(instanciaAl,CANTIDAD_ALUMNOS);
							resfrecarDatos();
							Toast_msg = "Su Alumno ha sido agregado";
							resfrecarDatos();
							mensaje(Toast_msg, 2);
						} else {
							Toast_msg = "Verifique los datos";
							mensaje(Toast_msg, 3);
						}
					}
				});
		ventanitaagregar.setNegativeButton("Cancelar",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		ventanitaagregar.show();
	}
	
	public class AlumnosDetallesAdaptador extends ArrayAdapter<AsistenciaAlumno> {
		Activity activity;
		int layoutResourceId;
		AsistenciaAlumno user;
		ArrayList<AsistenciaAlumno> data = new ArrayList<AsistenciaAlumno>();

		public AlumnosDetallesAdaptador(Activity act, int layoutResourceId,
				ArrayList<AsistenciaAlumno> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			notifyDataSetChanged();
		}		

		@Override
		public int getPosition(AsistenciaAlumno item) {
			return super.getPosition(item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			UserHolder holder = null;
			if (row == null) {
				LayoutInflater inflater = LayoutInflater.from(activity);
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new UserHolder();
				holder.fecha = (TextView) row.findViewById(R.id.txt_fecha); 
				holder.presente = (Button) row.findViewById(R.id.btn_presente);
				holder.ausente = (Button) row.findViewById(R.id.btn_ausente);
				holder.tarde = (Button) row.findViewById(R.id.btn_tarde);
				row.setTag(holder);
			} else {
				holder = (UserHolder) row.getTag();
			}
			user = data.get(position);
            //mostar el icono conforme la asistencia P, A, T  y el resto ocultarlo
			switch (user.getAsistencia()){
			case 1:
				holder.presente.setVisibility(View.VISIBLE);
				holder.tarde.setVisibility(View.GONE);
				holder.ausente.setVisibility(View.GONE);
				break;
			case 2:
				holder.tarde.setVisibility(View.VISIBLE);
				holder.presente.setVisibility(View.GONE);
				holder.ausente.setVisibility(View.GONE);
				break;
			case 3:
				holder.ausente.setVisibility(View.VISIBLE);
				holder.presente.setVisibility(View.GONE);
				holder.tarde.setVisibility(View.GONE);
				break;				
			}  
			holder.fecha.setText(user.getFecha());  
			return row;
		}
		class UserHolder {
			TextView fecha;
			Button presente, ausente, tarde;
		}

	}

	private void txtEventos(final EditText txt_nombre,
			final EditText txt_apellido) {
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
	}

	private void txtEventosCorreo(final EditText txtcorreo) {
		// MISMA FUNCION QUE EL INICIO PARA VERIFICAR
		txtcorreo.addTextChangedListener(new TextWatcher() {
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
				esValidoCorreo(txtcorreo);
			}
		});
	}

	public void esValidoNombre(EditText edt) throws NumberFormatException {		
		if (edt.getText().toString().length() <= 2) { 
			nombre_validado = null;			
		} else if ( (edt.getText().toString().length() >= 3) && (edt.getText().toString().length() <= 4) ) {
			edt.setError("Mínimo 5 letras");
			nombre_validado = null;			
		} else if ( (edt.getText().toString().length() >= 10) ) {
			edt.setError("Máximo 10 letras");
			nombre_validado = null;			
		} else if (!edt.getText().toString().matches("[\\p{L}\\s]+")) {
			edt.setError("Solo letras");
			nombre_validado = null;			
		} else {
			nombre_validado = edt.getText().toString(); 
		}

	}

	public void esValidoApellido(EditText edt) throws NumberFormatException {
		if (edt.getText().toString().length() <= 2) { 
			apellido_validado = null;			
		} else if ( (edt.getText().toString().length() >= 3) && (edt.getText().toString().length() <= 4) ) {
			edt.setError("Mínimo 5 letras");
			apellido_validado = null;			
		} else if ( (edt.getText().toString().length() >= 10) ) {
			edt.setError("Máximo 10 letras");
			apellido_validado = null;			
		} else if (!edt.getText().toString().matches("[\\p{L}\\s]+")) {
			edt.setError("Solo letras");
			apellido_validado = null;			
		} else {
			apellido_validado = edt.getText().toString();			
		}

	}

	boolean esValidoCorreoPattern(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private void esValidoCorreo(EditText edt) {
		if ((edt.getText().toString().length() >= 5) && (edt.getText().toString().length() <= 6)) {
			edt.setError("Correo inválido");
			correo_validado = null;
		} else if (esValidoCorreoPattern(edt.getText().toString()) == false) {
			edt.setError("Correo inválido");
			correo_validado = null;
		} else {
			correo_validado = edt.getText().toString();
		}
	}

	public void mensaje(String msg, int estilo) {
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
		nombre_validado = null;
		apellido_validado = null;
		correo_validado = null;
	}

}
