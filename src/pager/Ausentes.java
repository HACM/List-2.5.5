package pager;

import java.util.ArrayList;
import modelo.ModeloAsistencia;
import setget.Alumnos;
import com.fortysevendeg.swipelistview.SwipeListView; 
import com.uca.list.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics; 
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button; 
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import setget.AsistenciaAlumno;

public final class Ausentes extends Fragment {

	static ArrayList<Alumnos> alumos_data;
	static ModeloAsistencia modeloAsistencia;
	static SwipeListView swipelistview;
	static Activity actividad;
	static int layoutalumnos, CLASE_ID, LISTA_ID;
	static ListView Clases_listview;
	AdaptadorAlumnosAusentes cAdapter;

	public static Ausentes newInstance(Activity activi, int layout,	int classID, int listaID) {
		Ausentes fragment = new Ausentes();
		alumos_data = new ArrayList<Alumnos>();
		modeloAsistencia = new ModeloAsistencia(activi);
		layoutalumnos = layout;
		actividad = activi;
		LISTA_ID = listaID;
		CLASE_ID = classID;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		layout.setGravity(Gravity.TOP);
		layout.addView(Set_Referash_Data());
		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public ListView Set_Referash_Data() {
		swipelistview = new SwipeListView(actividad, layoutalumnos,layoutalumnos);
		Clases_listview = new ListView(actividad);
		alumos_data.clear();
		alumos_data = modeloAsistencia.alumnosIdClaseAusentes(CLASE_ID,LISTA_ID);
		cAdapter = new AdaptadorAlumnosAusentes(actividad, layoutalumnos, alumos_data);
		Clases_listview.setAdapter(cAdapter);
		swipelistview.setAdapter(cAdapter);
		cAdapter.notifyDataSetChanged();

		return Clases_listview;
	}

	public class AdaptadorAlumnosAusentes extends ArrayAdapter<Alumnos> {
		Activity activity;
		int layoutResourceId;
		Alumnos user;
		ArrayList<Alumnos> data = new ArrayList<Alumnos>();

		public AdaptadorAlumnosAusentes(Activity act, int layoutResourceId,ArrayList<Alumnos> data) {
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
				holder.nombre = (TextView) row.findViewById(R.id.txt_nombre);
				holder.apellido = (TextView) row.findViewById(R.id.txt_apellido);
				holder.presente = (Button) row.findViewById(R.id.btn_presente);
				holder.ausente = (Button) row.findViewById(R.id.btn_ausente);
				holder.tarde = (Button) row.findViewById(R.id.btn_tarde);
				row.setTag(holder);
			} else {
				holder = (UserHolder) row.getTag();
			}
			user = data.get(position);

			holder.num.setText(this.getItemId(position + 1) + "- ");
			holder.apellido.setText(user.getApellido());
			holder.nombre.setText(user.getNombre());

			holder.ausente.setVisibility(View.GONE);
			holder.tarde.setTag(user.getId() + "--" + user.getApellido() + "--"+ user.getNombre() + "--" + this.getItemId(position));
			holder.presente.setTag(user.getId() + "--" + user.getApellido() + "--"+ user.getNombre() + "--"+ this.getItemId(position));
            holder.apellido.setTag(user.getId_su_clase() + "--" + user.getApellido() + "--" + user.getNombre());

            holder.apellido.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] dividir = v.getTag().toString().split("--");
                    ListView detallesList = new ListView(activity);

                    AlumnosDetallesAdaptador detaAdapter;
                    ArrayList<AsistenciaAlumno> detalles = new ArrayList<AsistenciaAlumno>();
                    detalles=
                            modeloAsistencia.alumnoDetalleAsistencia(Integer.parseInt(dividir[0].toString()), CLASE_ID);
                    detaAdapter = new AlumnosDetallesAdaptador(activity,R.layout.listview_asistenciadetalles, detalles);

                    detallesList.setAdapter(detaAdapter);
                    detaAdapter.notifyDataSetChanged();

                    AlertDialog.Builder ventanaDetalles = new AlertDialog.Builder(activity);
                    ventanaDetalles.setTitle("Detalles de " + dividir[1] + " " + dividir[2] + ":");
                    ventanaDetalles.setIcon(R.drawable.estados);
                    ventanaDetalles.setView(detallesList);
                    ventanaDetalles.setPositiveButton("Listo",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });
                    ventanaDetalles.show();
                }
            });

            holder.tarde.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

                    String[] dividir = v.getTag().toString().split("--");
                    Alumnos instancia = new Alumnos();
                    instancia.setId(Integer.parseInt(dividir[0].toString()));
                    instancia.setApellido(dividir[1].toString());
                    instancia.setNombre(dividir[2].toString());
                    instancia.setId_su_clase(CLASE_ID);
                    instancia.setAsistencia(2);
                    modeloAsistencia.actualizarAsistenciaAlumnos( instancia, LISTA_ID,3);
                    user = data.get(Integer.parseInt(dividir[3].toString()));
                    cAdapter.remove(user);
                    Crouton.makeText(actividad, dividir[1]+ " " + dividir[2] +" cambiado a Tarde",Style.CONFIRM).show();

                    /*
					final String[] dividir = v.getTag().toString().split("--");
					AlertDialog.Builder mnj_confirmacion = new AlertDialog.Builder(actividad);
					mnj_confirmacion.setTitle("¿Cambiar a llegada Tarde?");
					mnj_confirmacion.setIcon(R.drawable.tarde);
					mnj_confirmacion.setMessage("Estudiante: " + dividir[1]	+ " " + dividir[2]);
					mnj_confirmacion.setNegativeButton("Cancelar", null);
					mnj_confirmacion.setPositiveButton("Cambiar",
							new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,	int which) {
								}
							});
					mnj_confirmacion.show();
					*/
				}
			});
			holder.presente.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {

                    String[] dividir = v.getTag().toString().split("--");
                    Alumnos instancia = new Alumnos();
                    instancia.setId(Integer.parseInt(dividir[0].toString()));
                    instancia.setApellido(dividir[1].toString());
                    instancia.setNombre(dividir[2].toString());
                    instancia.setId_su_clase(CLASE_ID);
                    instancia.setAsistencia(1);
                    modeloAsistencia.actualizarAsistenciaAlumnos( instancia, LISTA_ID,3);
                    user = data.get(Integer.parseInt(dividir[3].toString()));
                    cAdapter.remove(user);
                    Crouton.makeText(actividad, dividir[1]+ " " + dividir[2] +" cambiado a Presente",Style.CONFIRM).show();

                    /*
					final String[] dividir = v.getTag().toString().split("--");
					AlertDialog.Builder mnj_confirmacion = new AlertDialog.Builder(actividad);
					mnj_confirmacion.setTitle("¿Cambiar a estado Presente?");
					mnj_confirmacion.setIcon(R.drawable.presnete);
					mnj_confirmacion.setMessage("Estudiante: " + dividir[1]	+ " " + dividir[2]);
					mnj_confirmacion.setNegativeButton("Cancelar", null);
					mnj_confirmacion.setPositiveButton("Cambiar",new AlertDialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,	int which) {
								}
							});
					mnj_confirmacion.show();
					*/
				}
            });
			return row;

		}

		class UserHolder {
			TextView num, nombre, apellido;
			Button presente, ausente, tarde;
		}

	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
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

}

 