package modelo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import setget.Alumnos;

public class Leercsv {

	public int pruebaLecturaCsv(String direccionArchivo) {
		int respuesta = 1;
		ArrayList<Alumnos> alumnos_data = new ArrayList<Alumnos>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Boolean encabezado_listo = false;
		try {
			br = new BufferedReader(new FileReader(direccionArchivo));
			while ((line = br.readLine()) != null) {
				String[] separacion = line.split(cvsSplitBy);
				if (encabezado_listo == true) {
					Alumnos datos = new Alumnos();
                    datos.setApellido(separacion[0]);
                    datos.setNombre(separacion[1]);
					datos.setCorreo("sinCorreo");
					alumnos_data.add(datos);
					respuesta = 4;
				}
				if (encabezado_listo == false) {
					encabezado_listo = true;
				}
			}
		} catch (FileNotFoundException e) {
			respuesta = 2;
		} catch (IOException e) {
			respuesta = 3;
		} finally {
			if (br != null) {
				try {
					br.close();
					respuesta = 4;
				} catch (IOException e) {
					respuesta = 3;
				}
			}
		}
		return respuesta;
	}

	public ArrayList<Alumnos> leer_los_datos(String direccionArchivo) {
		ArrayList<Alumnos> alumnos_data = new ArrayList<Alumnos>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		Boolean encabezadoListo = false;

		try {
			br = new BufferedReader(new FileReader(direccionArchivo));
			while ((line = br.readLine()) != null) {
				String[] separacion = line.split(cvsSplitBy);
				if (encabezadoListo == true) {
					Alumnos datos = new Alumnos();
					datos.setApellido(separacion[0]);
					datos.setNombre(separacion[1]);
					datos.setCorreo("sinCorreo");
					alumnos_data.add(datos);
				}
				if (encabezadoListo == false) {
                    encabezadoListo = true;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return alumnos_data;
	}
}