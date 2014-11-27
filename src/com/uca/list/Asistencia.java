package com.uca.list;

import java.util.ArrayList;

import android.os.CountDownTimer;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.*;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import modelo.ModeloAsistencia;
import modelo.ModeloLista; 
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView; 
import setget.Alumnos;
import android.annotation.SuppressLint;
import android.app.Activity;   
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle; 
import android.util.DisplayMetrics; 
import android.view.LayoutInflater; 
import android.view.View;
import android.view.ViewGroup;

public class Asistencia extends Activity { 
	//variable del txt para poner nombre de la persona 
	ArrayList<Alumnos> alumos_data;
	ListView Clases_listview;
	AdaptadorAsistencias cAdapter;
	//iniciar sqlite
	ModeloLista modeloLista;
	ModeloAsistencia modeloAsistencia;
	int CLASE_ID, LISTA_ID, LIMITE;
    String FECHA;
	SwipeListView swipelistview;		
	int posicionClick=0, preseOausen=0; 
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.asistencia);   
		alumos_data = new ArrayList<Alumnos>();
		modeloLista = new ModeloLista(getApplicationContext());
		
		CLASE_ID = Integer.parseInt(getIntent().getStringExtra("CLASE_ID")); 
		LISTA_ID = Integer.parseInt(getIntent().getStringExtra("LISTA_ID"));
        LIMITE = Integer.parseInt(getIntent().getStringExtra("LIMITE"));
        FECHA = getIntent().getStringExtra("FECHA");

        modeloAsistencia = new ModeloAsistencia(getApplicationContext());

        getActionBar().setTitle("Asistencia "+FECHA);
        iniciar_lista_clases();
		iniciar_evento_swipe(); 
	}

	@SuppressLint("CutPasteId")
	private void iniciar_lista_clases(){
		Clases_listview = (ListView) findViewById(R.id.listaasistencia);
		swipelistview = (SwipeListView) findViewById(R.id.listaasistencia);
		Clases_listview.setItemsCanFocus(false);
		refrescarDatos();		
	}
	
	public void refrescarDatos() {
		alumos_data.clear();
		alumos_data = modeloLista.asistenciasAlumnos(CLASE_ID, LISTA_ID);
		cAdapter = new AdaptadorAsistencias(Asistencia.this, R.layout.listview_alumnos, alumos_data);
		Clases_listview.setAdapter(cAdapter);
		swipelistview.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
	}

	

	@Override
	public void onResume() {
		super.onResume();
		refrescarDatos();
	}
	
	class AdaptadorAsistencias extends ArrayAdapter<Alumnos> {
		Activity activity;
		int layoutResourceId;
		Alumnos user;
		ArrayList<Alumnos> data = new ArrayList<Alumnos>();
		
		public AdaptadorAsistencias(Activity act, int layoutResourceId, ArrayList<Alumnos> data) {
			super(act, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.activity = act;
			this.data = data;
			notifyDataSetChanged();
		}
		
		@Override
		public int getPosition(Alumnos item) { 
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
				holder.apellido = (TextView) row.findViewById(R.id.txt_apellido); 
				holder.nombre = (TextView) row.findViewById(R.id.txt_nombre); 
				
				holder.presente = (Button) row.findViewById(R.id.btn_presente);
				holder.tarde = (Button) row.findViewById(R.id.btn_tarde);
				holder.ausente = (Button) row.findViewById(R.id.btn_ausente);
				row.setTag(holder);
			} else {
				holder = (UserHolder) row.getTag();
			}
			user = data.get(position);			 
			holder.num.setText(this.getItemId(position+1) + " - ");
			holder.apellido.setText(user.getApellido());
			holder.nombre.setText(user.getNombre());			 
			return row;
		}

		class UserHolder {
			TextView num, apellido, nombre; 
			Button presente, tarde, ausente;
		}

	}

	private void iniciar_evento_swipe() {

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
				asignarPosicion(position);
				preseOausen  = (right==true)? 3 : 1 ;
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
				asistencia();
			}

		});

		swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); 
		swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);  
		swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);
		swipelistview.setOffsetLeft(convertDpToPixel(250f)); // left side offset
		swipelistview.setOffsetRight(convertDpToPixel(250f)); // right side 
		swipelistview.setAnimationTime(500); // Animation time
		swipelistview.setSwipeOpenOnLongPress(true); // enable or disable 

		swipelistview.setAdapter(cAdapter);
	}
	private void asignarPosicion(int posicion){
		posicionClick =	posicion;
	}
	private void asistencia(){ 
		Alumnos instancia= new Alumnos();
		instancia.setId( alumos_data.get(posicionClick).getId() );
		instancia.setApellido( alumos_data.get(posicionClick).getApellido() );
		instancia.setNombre( alumos_data.get(posicionClick).getNombre() );
		instancia.setId_su_clase(CLASE_ID);
		instancia.setAsistencia(preseOausen); 
		if ( (preseOausen==3) && (alumos_data.get(posicionClick).getCantAusensias()!=0) ){			
			if ((instancia.getCantAusensias()+2) == LIMITE){
				iniciarVentanaInformacionAusencia(alumos_data.get(posicionClick).getNombre(),alumos_data.get(posicionClick).getApellido(), "Ha estado ausente "+(alumos_data.get(posicionClick).getCantAusensias()+1)+" veces. El máximo permitido es " + LIMITE);
			}
			else if ((alumos_data.get(posicionClick).getCantAusensias()+1) == LIMITE){
				iniciarVentanaInformacionAusencia(alumos_data.get(posicionClick).getNombre(),alumos_data.get(posicionClick).getApellido(), "Ha estado ausente "+(alumos_data.get(posicionClick).getCantAusensias()+1)+" veces. El máximo permitido es " + LIMITE);
			}
			else if ( alumos_data.get(posicionClick).getCantAusensias() >= LIMITE){
				iniciarVentanaInformacionAusencia(alumos_data.get(posicionClick).getNombre(),alumos_data.get(posicionClick).getApellido(), "Ha estado ausente "+(alumos_data.get(posicionClick).getCantAusensias()+1)+" veces. El máximo permitido es " + LIMITE);
			}			
		}
        else if  (cAdapter.getCount()==0){
            Crouton.makeText(this, "Asistencia terminada", Style.CONFIRM).show();
            conteoCerrar();
        }
        modeloAsistencia.actualizarAsistenciaAlumnos(instancia, LISTA_ID,0);
        cAdapter.remove( alumos_data.get(posicionClick) );
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
    private void terminar() {
        this.finish();
    }
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
	
	void iniciarVentanaInformacionAusencia(String nombre, String apellido, String mensaje){		
		AlertDialog.Builder ventanaDetalles = new AlertDialog.Builder(Asistencia.this); 		
		ventanaDetalles.setTitle(nombre + " " + apellido);
		ventanaDetalles.setIcon(R.drawable.estados);			
		LinearLayout layout = new LinearLayout(Asistencia.this); 	
		layout.setOrientation(LinearLayout.VERTICAL); 
		TextView inforamcion = new TextView(Asistencia.this); 
		inforamcion.setText(mensaje);
		layout.addView(inforamcion); 
		ventanaDetalles.setView(layout);
		ventanaDetalles.setPositiveButton("Listo",
				new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,	int whichButton) {
                        if  (cAdapter.getCount()==0){
                            Crouton.makeText(Asistencia.this, "Asistencia terminada", Style.CONFIRM).show();
                            conteoCerrar();
                        }
                }
				}); 
		ventanaDetalles.show(); 
	}
	
	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}
	
	

}
