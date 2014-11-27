package setget;

public class Listas {
	private int id,idViejo, presentes, ausentes, tardes, id_clase;
	private String fecha;
	
	public Listas(){ 
	}

	//contructor para agregar lista
	public Listas(int id_clase, String fecha){
		this.id_clase = id_clase;
		this.fecha = fecha;		
	}
	//contructor para buscar lista
	public Listas(int id_lista, String fecha, int pre, int au, int tar, int clase_id ){ 
		this.id = id_lista;
		this.fecha = fecha;		
		this.presentes = pre;
		this.ausentes = au;
		this.tardes = tar;
		this.id_clase = clase_id;
	}
	
	
	public int getId_clase() {
		return id_clase;
	}

	public void setId_clase(int id_clase) {
		this.id_clase = id_clase;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public int getPresentes() {
		return presentes;
	}
	public void setPresentes(int presentes) {
		this.presentes = presentes;
	}
	public int getAusentes() {
		return ausentes;
	}
	public void setAusentes(int ausentes) {
		this.ausentes = ausentes;
	}
	public int getTardes() {
		return tardes;
	}
	public void setTardes(int tardes) {
		this.tardes = tardes;
	}

    public int getIdViejo() {
        return idViejo;
    }

    public void setIdViejo(int idViejo) {
        this.idViejo = idViejo;
    }
}
