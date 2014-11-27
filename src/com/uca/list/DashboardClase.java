package com.uca.list;

import java.util.ArrayList;  

import modelo.ModeloLista;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import setget.Listas;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;  
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup; 
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DashboardClase extends Activity { 
	//variable del txt para poner nombre de la persona 
	ArrayList<Listas> clase_data = new ArrayList<Listas>();
	ListView Clases_listview;
	AdaptadorClase cAdapter;
	//iniciar sqlite 
	ModeloLista modeloLista;
	int CLASE_ID, LIMITE;
    String NOMBRECLASE;
	SwipeListView swipelistview;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.dashboard_clase);   
		modeloLista = new ModeloLista(getApplicationContext());
		
		iniciar_lista_clases();
		iniciar_evento_swipe();
		CLASE_ID = Integer.parseInt(getIntent().getStringExtra("ID_CLASE"));
        LIMITE = Integer.parseInt(getIntent().getStringExtra("LIMITE"));
        NOMBRECLASE = getIntent().getStringExtra("NOMBRECLASE");
        getActionBar().setTitle("Asistencias de "+NOMBRECLASE);
	}
	
	@SuppressLint("CutPasteId")
	private void iniciar_lista_clases(){
		Clases_listview = (ListView) findViewById(R.id.listas_asistencias);
        swipelistview=(SwipeListView)findViewById(R.id.listas_asistencias); 
		Clases_listview.setItemsCanFocus(false);
		resfrecarDatos();		
	}
	 

	public void resfrecarDatos() {
		clase_data.clear();
        clase_data = modeloLista.cargarTodasListas(CLASE_ID);
		cAdapter = new AdaptadorClase(DashboardClase.this, R.layout.listview_clases_listados,clase_data);
		Clases_listview.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();
	}
	

	@Override
	public void onResume() {
		super.onResume();
		resfrecarDatos();
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
                //Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
               // Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                //Log.d("swipe", String.format("onClickFrontView %d", position)); 
                swipelistview.openAnimate(position); //when you touch front view it will openc 
            }

            @Override
            public void onClickBackView(int position) {
                //Log.d("swipe", String.format("onClickBackView %d", position));                
                swipelistview.closeAnimate(position);//when you touch back view it will close
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {            	
            }

        });
         
        swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH); // there are five swiping modes
        swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_NONE); //there are four swipe actions 
        swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_CHOICE);
        swipelistview.setOffsetLeft(convertDpToPixel(230f)); // left side offset
        swipelistview.setOffsetRight(convertDpToPixel(230f)); // right side offset
        swipelistview.setAnimationTime(500); // Animation time
        swipelistview.setSwipeOpenOnLongPress(true); // enable or disable SwipeOpenOnLongPress
	
        swipelistview.setAdapter(cAdapter);        
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
	
	public class AdaptadorClase extends ArrayAdapter<Listas> {
		Activity activity;
		int layoutResourceId;
		Listas user;
		ArrayList<Listas> data = new ArrayList<Listas>();

		public AdaptadorClase(Activity act, int layoutResourceId,ArrayList<Listas> data) {
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
				holder.dia = (TextView) row.findViewById(R.id.txt_dia);
				holder.mes = (TextView) row.findViewById(R.id.txt_mes);
				holder.ano = (TextView) row.findViewById(R.id.txt_ano);
                holder.detallesP = (TextView) row.findViewById(R.id.txt_detalles_p);
                holder.detallesT = (TextView) row.findViewById(R.id.txt_detalles_t);
                holder.detallesA = (TextView) row.findViewById(R.id.txt_detalles_a);
                holder.listar = (Button) row.findViewById(R.id.btn_listar);
				holder.edit = (Button) row.findViewById(R.id.btn_update);
				holder.delete = (Button) row.findViewById(R.id.btn_eliminar);
				holder.btn_alumnos = (Button) row.findViewById(R.id.btn_alumnos);
				row.setTag(holder);
			} else {
				holder = (UserHolder) row.getTag();
			}
			user = data.get(position);
            holder.listar.setTag(user.getId()+"--"+user.getFecha());
			holder.edit.setTag(user.getId());
			holder.btn_alumnos.setTag(user.getId());


            holder.detallesP.setTag(user.getId());
            holder.detallesT.setTag(user.getId());
            holder.detallesA.setTag(user.getId());

			String separar[] = user.getFecha().split("-");	
			holder.delete.setTag(user.getId()+"--"+user.getFecha());		
			holder.dia.setText(separar[0]);
			holder.mes.setText(separar[1]);
			holder.ano.setText(separar[2]);
            holder.detallesP.setText("P: " + user.getPresentes());
            holder.detallesT.setText("T: " + user.getTardes());
            holder.detallesA.setText("A: "+user.getAusentes());

            holder.detallesP.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent verDetallesLista = new Intent(activity, ListaDetalles.class);
                    verDetallesLista.putExtra("CLASE_ID", ""+CLASE_ID);
                    verDetallesLista.putExtra("LISTA_ID", ""+v.getTag().toString());
                    verDetallesLista.putExtra("TIPO", "1");
                    activity.startActivity(verDetallesLista);
                }
            });
            holder.detallesT.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent verDetallesLista = new Intent(activity, ListaDetalles.class);
                    verDetallesLista.putExtra("CLASE_ID", ""+CLASE_ID);
                    verDetallesLista.putExtra("LISTA_ID", ""+v.getTag().toString());
                    verDetallesLista.putExtra("TIPO", "2");
                    activity.startActivity(verDetallesLista);
                }
            });
            holder.detallesA.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent verDetallesLista = new Intent(activity, ListaDetalles.class);
                    verDetallesLista.putExtra("CLASE_ID", ""+CLASE_ID);
                    verDetallesLista.putExtra("LISTA_ID", ""+v.getTag().toString());
                    verDetallesLista.putExtra("TIPO", "3");
                    activity.startActivity(verDetallesLista);
                }
            });
            holder.btn_alumnos.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent verDetallesLista = new Intent(activity, ListaDetalles.class);
                    verDetallesLista.putExtra("CLASE_ID", ""+CLASE_ID);
                    verDetallesLista.putExtra("LISTA_ID", ""+v.getTag().toString());
                    verDetallesLista.putExtra("TIPO", "1");
                    activity.startActivity(verDetallesLista);
                }
            });
			holder.listar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
                    String separar[] = v.getTag().toString().split("--");
					Intent pasar_asistencia = new Intent(activity, Asistencia.class); 
					pasar_asistencia.putExtra("CLASE_ID", ""+CLASE_ID);
					pasar_asistencia.putExtra("LIMITE", ""+LIMITE);
                    pasar_asistencia.putExtra("LISTA_ID", separar[0]);
                    pasar_asistencia.putExtra("FECHA", separar[1]);
					activity.startActivity(pasar_asistencia); 
				}
			});
			holder.edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent actualizar_lista = new Intent(activity,AgregarActualizarLista.class);
					actualizar_lista.putExtra("accion", "update");
					actualizar_lista.putExtra("CLASE_ID", ""+CLASE_ID);
					actualizar_lista.putExtra("ID_LISTA", v.getTag().toString());
					activity.startActivity(actualizar_lista); 
				}
			});
			holder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					final String[] dividir = v.getTag().toString().split("--");
					AlertDialog.Builder mnj_confirmacion = new AlertDialog.Builder(activity);
					mnj_confirmacion.setTitle("¿Eliminar este día de asistencia?");
					mnj_confirmacion.setIcon(R.drawable.eliminaricono);
					mnj_confirmacion.setMessage("Asistencia del " + dividir[1]);
					mnj_confirmacion.setNegativeButton("Cancelar", null);
					mnj_confirmacion.setPositiveButton("Eliminar",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) { 				 
									modeloLista.eliminarLista(Integer.parseInt(dividir[0]));
									resfrecarDatos();
									Mensaje("Lista eliminada",2);
								}
							});
					mnj_confirmacion.show();	 
				}
			});
			return row;

		}

		class UserHolder {
			TextView dia, mes, ano, detallesP, detallesT, detallesA;
			Button listar, edit, delete, btn_alumnos;
		}

	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_class, menu); 
		menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_new)); 
	    return true;
	}
	 
	public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) { 
		    case R.id.btn_agregar_lista:
		    	Intent nueva_clase = new Intent(DashboardClase.this, AgregarActualizarLista.class);  
		    	nueva_clase.putExtra("accion", "insert");
		    	nueva_clase.putExtra("CLASE_ID", ""+CLASE_ID);
				startActivity(nueva_clase);  
		      return true; 
		    case android.R.id.home:
		    	this.finish(); 
		    default:
		      return super.onOptionsItemSelected(item);
		  }
		}
 
		 
	public void Mensaje(String msg, int estilo) {
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
