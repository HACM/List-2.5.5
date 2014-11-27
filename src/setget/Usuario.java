package setget;

public class Usuario {

	// private variables
	public int _id, respuestaModelo;
	public String _name;
	public String _lastname;
	public String _email;
	public String _password;

	public Usuario() {
	}
	
	// constructor
	public Usuario(int id, String name, String _lastname, String _email, String _password) {
		this._id = id;
		this._name = name;
		this._lastname = _lastname;
		this._email = _email;
		this._password = _password;
	}

	// constructor para crear cuenta
	public Usuario(String name, String _lastname, String _email, String _password) {
		this._name = name;
		this._lastname = _lastname;
		this._email = _email;
		this._password = _password;
	}

	//contructor para login
	public Usuario(String correo_validado, String contra_validada) {
		this._email = correo_validado;
		this._password = contra_validada;
	}

	// getting ID
	public int getID() {
		return this._id;
	}

	// setting id
	public void setID(int id) {
		this._id = id;
	}

	// getting name
	public String getName() {
		return this._name;
	}

	// setting name
	public void setName(String name) {
		this._name = name;
	}

	// getting phone number
	public String getLastName() {
		return this._lastname;
	}

	// setting phone number
	public void setLastName(String lastname) {
		this._lastname = lastname;
	}

	// getting email
	public String getEmail() {
		return this._email;
	}

	// setting email
	public void setEmail(String email) {
		this._email = email;
	}
	
	public String get_password() {
		return _password;
	}

	public void set_password(String _password) {
		this._password = _password;
	}

	public int getRespuestaModelo() {
		return respuestaModelo;
	}

	public void setRespuestaModelo(int respuestaModelo) {
		this.respuestaModelo = respuestaModelo;
	}
	
	
}