package com.uca.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.*;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.widget.*;
import modelo.*;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import setget.Clases;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class DashboardPrincipal extends Activity {
	// variable del txt para poner nombre de la persona
	TextView logeado;
	ArrayList<Clases> clase_data;
	ListView Clases_listview;
	AdaptadorClasesMostrar cAdapter;
	// iniciar sqlite
	ModeloClase modeloClase;
	ModeloUsuario modeloUsuario;
	ExportacionExcel instanciaExportacionExcel;
	SwipeListView swipelistview;
	Activity principal;
	String nombre_validado, descripcion_validada, Toast_msg;
	int id_actualizar, ausencias_validada = 0;

	int idUsuario;
	String nombre, apellido, contra, correo;

    final int[] respuesta = {0};

    Sincronizacion sincro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		idUsuario = Integer.parseInt(getIntent().getStringExtra("id"));
		super.onCreate(savedInstanceState);
		clase_data = new ArrayList<Clases>();
		modeloClase = new ModeloClase(getApplicationContext());
		instanciaExportacionExcel = new ExportacionExcel(getApplicationContext());
		setContentView(R.layout.dashboard_principal);
		iniciar();
	}

	//eventos de deslizar izquierda o derecha de cada item en el listview
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
			}

			@Override
			public void onStartClose(int position, boolean right) {
			}

			@Override
			public void onClickFrontView(int position) {
				swipelistview.openAnimate(position);
			}

			@Override
			public void onClickBackView(int position) {
				swipelistview.closeAnimate(position);
			}

			@Override
			public void onDismiss(int[] reverseSortedPositions) {
			}

		});

		swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);  
		swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE);  
		swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_CHOICE);
		swipelistview.setOffsetLeft(convertDpToPixel(150f));
		swipelistview.setOffsetRight(convertDpToPixel(300f));
		swipelistview.setAnimationTime(400);
		swipelistview.setSwipeOpenOnLongPress(true); 

	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

	@SuppressLint("CutPasteId")
	private void iniciar() {
		Clases_listview = (ListView) findViewById(R.id.listdashboarclases);
		swipelistview = (SwipeListView) findViewById(R.id.listdashboarclases);
		Clases_listview.setItemsCanFocus(false);
		refrescarDatos();
		iniciarEventoSwipe();
		cargarInfoUsuario();
	}

	public void refrescarDatos() {
		clase_data.clear();
		ArrayList<Clases> clases_areglo_desde_db = modeloClase.cargarTodasClases(idUsuario); 
		cAdapter = new AdaptadorClasesMostrar(DashboardPrincipal.this,
				R.layout.listview_clases, clases_areglo_desde_db);
		Clases_listview.setAdapter(cAdapter);
		swipelistview.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() { 
		super.onResume();
		refrescarDatos();
	}

	class AdaptadorClasesMostrar extends ArrayAdapter<Clases> {
		Activity activity;
		int layoutResourceId;
		Clases user;
		ArrayList<Clases> data = new ArrayList<Clases>();

		public AdaptadorClasesMostrar(Activity act, int layoutResourceId,
				ArrayList<Clases> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			UserHolder holder = null;
			if (row == null) {
				LayoutInflater inflater = LayoutInflater.from(activity);
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new UserHolder();
				holder.nombre = (TextView) row
						.findViewById(R.id.txt_nombre_clase);
				holder.asistencias = (TextView) row
						.findViewById(R.id.txt_cantidad_clase);
				holder.descripcion = (TextView) row
						.findViewById(R.id.txt_descripcion_clase);
				holder.exportar = (Button) row.findViewById(R.id.btn_exportar);
				holder.listar = (Button) row.findViewById(R.id.btn_listar);
				holder.alumnos = (Button) row.findViewById(R.id.btn_alumnos);
				holder.edit = (Button) row.findViewById(R.id.btn_update);
                holder.delete = (Button) row.findViewById(R.id.btn_eliminar);
                holder.trabajos = (Button) row.findViewById(R.id.btn_respaldo);

				row.setTag(holder);
			} else {
				holder = (UserHolder) row.getTag();
			}
			user = data.get(position);
			holder.exportar.setTag(user.get_id() + "--" + user.get_name()
					+ "--" + user.get_descripcion() + "--"
					+ user.get_cantidad_al() + "--" + user.get_asencias());
			holder.listar.setTag(user.get_id()+"--"+user.get_asencias()+"--"+user.get_name());
			holder.alumnos
					.setTag(user.get_id() + "--" + user.get_cantidad_al());
			holder.edit.setTag(user.get_id() + "--" + user.get_name()+ "--" + user.get_descripcion()+ "--" + user.get_asencias());
            holder.delete.setTag(user.get_id() + "--" + user.get_name());

            holder.nombre.setText(user.get_name());
			holder.asistencias.setText("Limite de Ausencias: "
					+ user.get_asencias() + "  |  Alumnos: "
					+ user.get_cantidad_al());
			holder.descripcion.setText(user.get_descripcion());

			holder.exportar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					exportacionExcel(v.getTag().toString());
				}
			});
			holder.listar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent dashboard_clase = new Intent(activity,
							DashboardClase.class);
					final String[] dividir = v.getTag().toString().split("--");
					dashboard_clase.putExtra("ID_CLASE", dividir[0]);
                    dashboard_clase.putExtra("LIMITE", dividir[1]);
                    dashboard_clase.putExtra("NOMBRECLASE", dividir[2]);
					activity.startActivity(dashboard_clase);
				}
			});
			holder.alumnos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String[] dividir = v.getTag().toString().split("--");
					Intent verAlumnos = new Intent(activity,
							DashboardAlumnos.class);
					verAlumnos.putExtra("CLASE_ID", "" + dividir[0]);
					verAlumnos.putExtra("CANTIDAD_ALUMNOS", "" + dividir[1]);
					activity.startActivity(verAlumnos);
				}
			});
			holder.edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final String[] dividir = v.getTag().toString().split("--");

					final int id = Integer.parseInt(dividir[0].toString());

					AlertDialog.Builder confirmacionmensaje = new AlertDialog.Builder(
							activity);
					confirmacionmensaje.setTitle("Actualizar: "
							+ dividir[1].toString());
					confirmacionmensaje.setIcon(R.drawable.editaricono);

					LinearLayout layout = new LinearLayout(activity);
					final EditText editnombre = new EditText(activity);
					final EditText editDescripcion = new EditText(activity);
					final EditText editlimite = new EditText(activity);

					layout.setOrientation(LinearLayout.VERTICAL);

					editnombre.setHint("Nuevo Nombre");
					editDescripcion.setHint("Nueva Descripción ");
					editlimite.setHint("Nuevo Limite Ausencias");
					editlimite.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editnombre.setText(dividir[1]);
                    editDescripcion.setText(dividir[2]);
                    editlimite.setText(dividir[3]);

                    txtEventos(editnombre, editDescripcion, editlimite);

					layout.addView(editnombre);
					layout.addView(editDescripcion);
					layout.addView(editlimite);
					confirmacionmensaje.setView(layout);

					confirmacionmensaje.setPositiveButton("Actualizar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (nombre_validado != null
											&& descripcion_validada != null
											&& ausencias_validada != 0) {
										int respuesta = modeloClase.actualizarClase(new Clases(id,
												nombre_validado,
												descripcion_validada,
												ausencias_validada));
										if (respuesta ==0) {
											mensaje("Problemas al actualizar Clase. Error MC-14", 3);
										}
										else if (respuesta!=0){
											refrescarDatos();
											mensaje("Clase actualizada.", 2);
										}
									} else {
										Toast_msg = "Verifique los dados";
										mensaje(Toast_msg, 3);
									}
								}
							});

					confirmacionmensaje.setNegativeButton("Cancelar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});
					confirmacionmensaje.show();
				}
			});
            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] dividir = v.getTag().toString().split("--");
                    AlertDialog.Builder confirmacionmensaje = new AlertDialog.Builder(
                            activity);
                    confirmacionmensaje.setIcon(R.drawable.eliminaricono);
                    confirmacionmensaje.setTitle("¿Eliminar " + dividir[1]
                            + "?");
                    confirmacionmensaje
                            .setMessage("Eliminará todas las asistencias y sus alumnos.");
                    confirmacionmensaje.setPositiveButton("Eliminar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    int respuesta = modeloClase.eliminarClase(Integer
                                            .parseInt(dividir[0].toString()));
                                    switch (respuesta) {
                                        case 0:
                                            refrescarDatos();
                                            mensaje("Problemas antes de eliminar. Error MC-71", 3);
                                            break;
                                        case 1:
                                            refrescarDatos();
                                            mensaje("Clase eliminada.", 2);
                                            break;
                                        case 2:
                                            refrescarDatos();
                                            mensaje("Problemas al eliminar. Error MC-86", 3);
                                            break;
                                    }
                                }
                            });

                    confirmacionmensaje.setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                }
                            });
                    confirmacionmensaje.show();
                }
            });
            holder.trabajos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mensaje("Proximamente...",1);
                }
            });

			return row;
		}
		class UserHolder {
			TextView nombre;
			TextView asistencias;
			TextView descripcion;
			Button listar, edit, delete, alumnos, exportar, trabajos;
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

    private void respaldar(final String a, final String b, final String c, final String d, final String e) {
        try{
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Enviando respaldo a www.listin.hol.es");
            progress.setMessage("Ingrese con la misma cuenta que tiene actualmente en este dispositivo.");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.show();
            new Thread(){
                public void run() {
                    try {
                        respuesta[0] = modeloClase.mandarRespaldo( a,b,c,d,e );
                        Thread.sleep(1000);
                        progress.dismiss();
                        switch (respuesta[0]) {
                            case 0:
                                mensaje("Problemas antes de enviar. Error MC-182", 3);
                                break;
                            case 1:
                                mensaje("Sus datos han sido enviados a www.listin.hol.es", 2);
                                break;
                            case 2:
                                mensaje("Problemas al eviar. Error MC-182", 3);
                                break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        progress.dismiss();
                    }
                }
            }.start();
        } catch (Exception ex) {
            Log.e("------", "" + ex.toString());
        }
    }

    private void exportacionExcel(final String datos) {
		final String[] dividir = datos.split("--");;
		SimpleFileDialog FileSaveDialog = new SimpleFileDialog(
				DashboardPrincipal.this, "GuardarComo",
				new SimpleFileDialog.SimpleFileDialogListener() {
					@Override
					public void onChosenDir(String chosenDir) {
						if (chosenDir.endsWith(".xls")) {
							int idClase = Integer.parseInt(dividir[0]);
							List<String> datos = instanciaExportacionExcel.datosExportacion(idClase);
							//GuardarCSV generarDoc = new GuardarCSV(datos, chosenDir);
							//GuardarCSV asdf = new GuardarCSV(chosenDir);
							int respuesta = instanciaExportacionExcel.generarExcel(DashboardPrincipal.this, chosenDir, datos, dividir[1], dividir[2], dividir[3], dividir[4]);
							switch (respuesta){
							case 1:
								mensaje("Upps error verifica la carpeta o memoria, Error-65",3);
								break;
							case 2:
								mensaje("Upps no se pudo procesar correctamente, Error-68",3);
								break;
							case 3:
								mensaje("Archivo Excel guardado",2);
								break;
							case 4:
								mensaje("Upps error al crear Archivo, Selecciona otra carpeta. Error-146",3);
								break;
							case 5:
								mensaje("Upps error al crear Archivo, Error-154",3);
								break;
							}
						}
						else{
							mensaje("La extensión del archivo debe ser .xls",3);
						}
					}
				});
		// le damos por defecto el nombre de la clase mas el formato xls
		FileSaveDialog.Default_File_Name = dividir[1]+".xls";
		FileSaveDialog.chooseFile_or_Dir();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.inicio, menu);
		if (idUsuario == 0) {
			MenuItem perfil = menu.findItem(R.id.btn_perfil);
			MenuItem cerrar = menu.findItem(R.id.btn_cerrar_sesion);
			perfil.setVisible(false);
			cerrar.setTitle("Salir");
		}
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.btn_tutorial:
			ayudaTutorial();
			return true;
		case R.id.btn_cerrar_sesion:
			cerrarSesion();
			return true;
		case R.id.btn_agregar_clase:
			Intent nueva_clase = new Intent(DashboardPrincipal.this,
					AgregarClase.class);
			nueva_clase.putExtra("idUsuario", "" + idUsuario);
			startActivity(nueva_clase);
			return true;
        case R.id.btn_perfil:
            verPerfil();
           return true;
        case R.id.btn_info:
           Intent info = new Intent(DashboardPrincipal.this, Informacion.class);
           info.putExtra("correo", correo);
           startActivity(info);
           return true;
        case R.id.btn_licencia:
            plic();
           return true;
        case R.id.btn_descargar:

            AlertDialog.Builder confirmacionmensaje = new AlertDialog.Builder(this);
            confirmacionmensaje.setTitle("¿Descargar respaldo de nuestros servidores?");
            confirmacionmensaje.setMessage("Si anteriormente realizó algún respaldo, puede descargarlo para continuar en este dispositivo.");
            confirmacionmensaje.setIcon(R.drawable.descargar);
            confirmacionmensaje.setPositiveButton("Sincronizar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            if (verificarConexion()==true) {
                                try {
                                    sincro = new Sincronizacion(getApplicationContext(), idUsuario, correo);
                                    descargarAlumnos(idUsuario);
                                } catch (Exception e) {
                                    refrescarDatos();
                                    mensaje("Problemas al enviar. Error MC-180", 3);
                                }
                            }
                            else{
                                mensaje("Verifique la conexión a Internet", 3);
                            }
                        }
                    });
            confirmacionmensaje.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                        }
                    });
            confirmacionmensaje.show();

            return true;
            case R.id.btn_respaldar:
                AlertDialog.Builder confirRespaldomnj = new AlertDialog.Builder( this);
                confirRespaldomnj.setIcon(R.drawable.respaldo);
                confirRespaldomnj.setTitle("¿Enviar todos los datos a nuestros servidores?");
                confirRespaldomnj.setMessage("Se enviará un respaldo de TODAS sus clases, alumnos y asistencias a www.listin.hol.es donde puede ingresar con la misma cuenta actual de este dispotivido.");
                confirRespaldomnj.setPositiveButton("Respaldar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                if (verificarConexion()==true) {
                                    respaldar(correo, contra, ""+idUsuario, nombre,apellido);
                                    try {
                                        Thread.sleep(1500);
                                        refrescarDatos();
                                        mensaje("Respaldos enviados.",1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        refrescarDatos();
                                        mensaje("Problemas al enviar. Error MC-180", 3);
                                    }
                                }
                                else{
                                    mensaje("Verifique la conexión a Internet", 3);
                                }
                            }
                        });
                confirRespaldomnj.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
                confirRespaldomnj.show();
                return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    private void plic() {
        final Dialog mnj = new Dialog(this);
        mnj.setTitle("Términos de Licencia:");
        mnj.setContentView(R.layout.licencia);
        Button btnaceptar = (Button) mnj.findViewById(R.id.btnaceptar);
        Button btncancelar = (Button) mnj.findViewById(R.id.btncancelar);
        btnaceptar.setVisibility(View.GONE);
        btncancelar.setVisibility(View.GONE);
        mnj.show();
    }

    private void ayudaTutorial() {
		Intent tutorial = new Intent(DashboardPrincipal.this, Tutorial.class);
		DashboardPrincipal.this.startActivity(tutorial);
	}
	
	private void verPerfil() {
		Intent panelPrincipal = new Intent(DashboardPrincipal.this,
				CrearCuenta.class);
		panelPrincipal.putExtra("perfil", "1");
		panelPrincipal.putExtra("id", "" + idUsuario);
		panelPrincipal.putExtra("nombre", nombre);
		panelPrincipal.putExtra("apellido", apellido);
		panelPrincipal.putExtra("correo", correo);
		panelPrincipal.putExtra("contra", contra);
		panelPrincipal.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(panelPrincipal);
	}

	private void cerrarSesion() {
		if (idUsuario != 0) {
			modeloUsuario = new ModeloUsuario(getApplicationContext());
			switch (modeloUsuario.cerrarSesion(idUsuario)) {
			case 0:
				mensaje("Upps, algo vergonsoso ha sucedido. Error: M-213", 1);
				break;
			case 1:
				mensaje("Sessión cerrada exitosamente", 2);
                conteoCerrar();
				break;
			case 2:
				mensaje("Upps, Al parecer no puedes cerrar sesión", 3);
				break;
			}
		} else {
			conteoCerrar();
		}
	}

	@Override
	protected void onDestroy() {
		Crouton.cancelAllCroutons();
		super.onDestroy();
	}

	private void terminar() {
		Intent regresarInicio = new Intent(DashboardPrincipal.this,Inicio.class);
        regresarInicio.putExtra("correoCreado", correo);
		startActivity(regresarInicio);
        this.finish();
	}

	private void conteoCerrar() {
		new CountDownTimer(1000, 1000) {
			public void onTick(long millisUntilFinished) {
			}
			public void onFinish() {
				terminar();
			}
		}.start();
	}

	private void cargarInfoUsuario() {
		//logeado = (TextView) findViewById(R.id.txt_logeado);
		//logeado.setTextColor(Color.rgb(96, 161, 226));

        nombre = getIntent().getStringExtra("nombre");
		apellido = getIntent().getStringExtra("apellido");
		contra = getIntent().getStringExtra("contra");
		correo = getIntent().getStringExtra("correo");
		if (idUsuario != 0) {
			new CountDownTimer(2000, 2000) {
				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					mensaje("Bienvenido " + nombre + " " + apellido
							+ ", que tenga un buen día", 1);
				}
			}.start();
            getActionBar().setTitle("Clases de "+ nombre + " " + apellido);
            modeloClase.re(correo, nombre, apellido);
            //logeado.setText("Logeado: " + nombre + " " + apellido);
		} else {
			new CountDownTimer(1200, 1200) {
				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					mensaje("No has iniciado sesión", 3);
				}
			}.start();
			//logeado.setText("NO LOGEADO");
            getActionBar().setTitle("Clases -- NO LOGEADO");
		}
	}

	public void esValidoNombre(EditText edt) throws NumberFormatException {
		if (edt.getText().toString().length() <= 2) {
			nombre_validado = null;
		} else if ( (edt.getText().toString().length() >= 3) && (edt.getText().toString().length() <= 5) ) {
			edt.setError("Mínimo 5 letras");
			nombre_validado = null;
		} else if ( (edt.getText().toString().length() >= 15) ) {
			edt.setError("Máximo 15 letras");
			nombre_validado = null;
		} else if (!edt.getText().toString().matches("[a-zA-Z ]+")) {
			edt.setError("Solo letras");
			nombre_validado = null;
		} else {
			nombre_validado = edt.getText().toString();
		}
	}

	public void esValidoDescripcion(EditText edt) throws NumberFormatException {
		if (edt.getText().toString().length() <= 2) {
			descripcion_validada = null;
		} else if ( (edt.getText().toString().length() >= 3) && (edt.getText().toString().length() <= 5) ) {
			edt.setError("Mínimo 5 letras");
			descripcion_validada = null;
		} else if ( (edt.getText().toString().length() >= 11) ) {
			edt.setError("Máximo 10 letras");
			descripcion_validada = null;
		} else if (!edt.getText().toString().matches("[a-zA-Z0-9 ]+")) {
			edt.setError("Solo letras y números");
			descripcion_validada = null;
		} else {
			descripcion_validada = edt.getText().toString();
		}
	}

	public void esValidoAusencia(EditText edt) {
		if (edt.getText().toString().length() == 0) {
			ausencias_validada = 0;
		} else if (edt.getText().toString().length() >= 3) {
			ausencias_validada = 0;
			edt.setError("Máximo 2 números");
		} else {
			Integer.parseInt(edt.getText().toString());
			ausencias_validada = Integer.parseInt(edt.getText().toString());
		}
	}

	private void txtEventos(final EditText txt_nombre,
			final EditText txt_descripcion, final EditText txt_ausencias) {
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
	}
    @Override
    public void onBackPressed() {
    }

    private void descargarAlumnos(int idUsuario) {
        final Handler myHandler = new Handler();
        final Runnable myRunnable = new Runnable() {
            public void run() {
                refrescarDatos();
            }
        };
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Sincronizando desde www.listin.hol.es");
        progress.setMessage("Descargando alumnos, clases, listas y asistencias...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
        try{
            sincro.empezar();
            final Timer myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    myHandler.post(myRunnable);
                    progress.dismiss();
                    mensaje("Sincronización terminada.",1);
                    myTimer.cancel();
                }
            }, 4000, 4000);
        }catch (Exception ex) {
                Log.e("------", "" + ex.toString());
                refrescarDatos();
                mensaje("Problemas con la Sincronización.", 3);
        }
    }
}

