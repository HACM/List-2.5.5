package setget;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;


public class Clases {
	private int _id,_idViejo, _cantidad_al,_asencias;
	private  String _name, _descripcion;
	private ArrayList<Alumnos> datos; 
	
	public Clases(){}
	//constructor para datos al buscar con id
	public Clases(int _id, String _name, String _descripcion, int _asencias ) { 
		this._id = _id;
		this._name = _name;
		this._descripcion = _descripcion;
		this._asencias = _asencias;  
	} 
	//constructor para insertar una nueva clase ya con los alumnos desde cvs
	public Clases(  String _name, String _descripcion, int  _asencias, ArrayList<Alumnos> datos) {  
		this._name = _name;
		this._descripcion = _descripcion;
		this._asencias = _asencias; 
		this.datos = datos;
	}
	  
	
	public int get_cantidad_al() {
		return _cantidad_al;
	}
	public void set_cantidad_al(int _cantidad_al) {
		this._cantidad_al = _cantidad_al;
	}
	public ArrayList<Alumnos> getDatos() {
		return datos;
	}
	public void setDatos(ArrayList<Alumnos> datos) {
		this.datos = datos;
	}
	public int get_asencias() {
		return _asencias;
	}

	public void set_asencias(int _asencias) {
		this._asencias = _asencias;
	}

	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String get_name() {
		return _name;
	}
	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_descripcion() {
		return _descripcion;
	}
	public void set_descripcion(String _descripcion) {
		this._descripcion = _descripcion;
	}
	//contructor para login
	public Clases(String nombre, String descripcion, int _asencias) {
		this._name = nombre;
		this._descripcion = descripcion;
		this._asencias = _asencias; 
	}

    public int get_idViejo() {
        return _idViejo;
    }

    public void set_idViejo(int _idViejo) {
        this._idViejo = _idViejo;
    }
}
