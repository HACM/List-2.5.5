package setget;

public class Alumnos {
	private int id,idViejo, asistencia, id_su_clase, cantAusensias;
	private String apellido, nombre, correo;
	
	  
	public int getCantAusensias() {
		return cantAusensias;
	}
	public void setCantAusensias(int cantAusensias) {
		this.cantAusensias = cantAusensias;
	}
	public int getId_su_clase() {
		return id_su_clase;
	}
	public void setId_su_clase(int id_su_clase) {
		this.id_su_clase = id_su_clase;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAsistencia() {
		return asistencia;
	}
	public void setAsistencia(int asistencia) {
		this.asistencia = asistencia;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
    public int getIdViejo() {
        return idViejo;
    }
    public void setIdViejo(int idViejo) {
        this.idViejo = idViejo;
    }
}
