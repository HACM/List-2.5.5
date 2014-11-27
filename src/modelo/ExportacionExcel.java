package modelo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import android.content.Context;
import android.os.Environment;
import au.com.bytecode.opencsv.CSVWriter;
import setget.AsistenciaAlumno;

public class ExportacionExcel {
    Sqlite modelo;
    Context contexto;
    Activity activity;

    public ExportacionExcel(Context contexto) {
        modelo = new Sqlite(contexto);
        this.contexto = contexto;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static int generarExcel(Context context, String fileName,
                                   List<String> datos, String nombreClase, String descripcion,
                                   String cantidadAl, String limite) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            // Log.w("FileUtils", "Storage not available or read only");
            return 1;
        }

        int respuesta = 2;

        // Nuevo excel
        Workbook wb = new HSSFWorkbook();

        Cell celda = null;

        // estilo para las celdas
        CellStyle celEstiloEncabezado = wb.createCellStyle();
        celEstiloEncabezado.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
        celEstiloEncabezado.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle celEstiloPresente = wb.createCellStyle();
        celEstiloPresente.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        celEstiloPresente.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle celEstiloAusente = wb.createCellStyle();
        celEstiloAusente.setFillForegroundColor(HSSFColor.RED.index);
        celEstiloAusente.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        CellStyle celEstiloTarde = wb.createCellStyle();
        celEstiloTarde.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
        celEstiloTarde.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // nueva hoja de excel
        Sheet hoja = null;
        hoja = wb.createSheet(nombreClase);


        Row nuestroCredito = hoja.createRow(0);
        CellRangeAddress unionCeldas = new CellRangeAddress(0,0,0,7);
        hoja.addMergedRegion(unionCeldas);

        celda = nuestroCredito.createCell(0);
        celda.setCellValue("Generado por ListIn - Disponible en Play Store");

        hoja.createRow(1);

        // generar las columnos
        for (int i = 0; i < datos.size(); i++) {
            Row fila = hoja.createRow((i+2));

            String[] celdasFilas = datos.get(i).split(",");

            for (int ii = 0; ii < celdasFilas.length; ii++) {
                //establecer ancho a todas las columnas
                switch (ii) {
                    case 0:
                        hoja.setColumnWidth(ii, (15 * 100));
                        break;
                    case 1:
                        hoja.setColumnWidth(ii, (15 * 300));
                        break;
                    case 2:
                        hoja.setColumnWidth(ii, (15 * 300));
                        break;
                    default:
                        hoja.setColumnWidth(ii, (15 * 200));
                        break;
                }

                celda = fila.createCell(ii);
                celda.setCellValue(celdasFilas[ii].toString());

                //estilo solo al encabezado
                if (i == 0)
                    celda.setCellStyle(celEstiloEncabezado);
                //
                if (ii >= 3) {
                    if (" P".equals(celdasFilas[ii].toString())) {
                        celda.setCellStyle(celEstiloPresente);
                    }
                    if (" A".equals(celdasFilas[ii].toString())) {
                        celda.setCellStyle(celEstiloAusente);
                    }
                    if (" T".equals(celdasFilas[ii].toString())) {
                        celda.setCellStyle(celEstiloTarde);
                    }
                }
            }
        }

        FileOutputStream os = null;

        try {
            os = new FileOutputStream(fileName);
            wb.write(os);
            // Log.e("FileUtils", "Writing file" + fileName);
            respuesta = 3;
        } catch (IOException e) {
            respuesta = 4;
            // Log.e("FileUtils", "Error writing " + fileName, e);
        } catch (Exception e) {
            // Log.e("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
                respuesta = 5;
                // Log.e("FileUtils", "Failed to save file", ex);
            }
        }

        return respuesta;
    }


    public List<String> datosExportacion(int id_CLASE) {
        List<String> filasCSV =  new ArrayList<String>();
        try {
            ModeloClase modelclases = new ModeloClase(contexto);
            ModeloLista modellistas = new ModeloLista(contexto);
            modelclases.alumnos = modelclases.alumnosClaseId(id_CLASE);
            modellistas.listas = modellistas.cargarTodasListasExportacion(id_CLASE);

            ArrayList<String> encabezados = new ArrayList<String>();
            encabezados.add("No.");
            encabezados.add("Apellidos");
            encabezados.add("Nombres");

            //recorrer las fechas y agregar cada una al encabezado
            for (int i=0;i<modellistas.listas.size();i++){
                encabezados.add(modellistas.listas.get(i).getFecha());
            }

            //quitar [] que da el arreglo a conversir en string, y quitar "
            String encabezadosListos = encabezados.toString().replace("\"","");
            encabezadosListos = encabezadosListos.replace("[", "");
            encabezadosListos = encabezadosListos.replace("]", "");
            filasCSV.add(encabezadosListos);


            ModeloAsistencia modeloAsistencia = new ModeloAsistencia(contexto);

            //recorrer cada alumno y obtener su asistencia de todas las fechas
            for (int i=0;i<modelclases.alumnos.size();i++){

                ArrayList<String> registroAlumnoTodasFechas = new ArrayList<String>();
                registroAlumnoTodasFechas.add(""+(i+1));
                registroAlumnoTodasFechas.add(modelclases.alumnos.get(i).getApellido());
                registroAlumnoTodasFechas.add(modelclases.alumnos.get(i).getNombre());

                //buscar la asistencia para cada fecha
                ArrayList<AsistenciaAlumno> dameAsistenciasAlumno = modeloAsistencia.alumnoDetalleAsistenciaExportacion(modelclases.alumnos.get(i).getId(), id_CLASE);

                for (int bucle=0;bucle<dameAsistenciasAlumno.size();bucle++){
                    switch (dameAsistenciasAlumno.get(bucle).getAsistencia()){
                        case 1:
                            registroAlumnoTodasFechas.add("P");
                            break;
                        case 2:
                            registroAlumnoTodasFechas.add("T");
                            break;
                        case 3:
                            registroAlumnoTodasFechas.add("A");
                            break;
                        default:
                            registroAlumnoTodasFechas.add("-");
                            break;
                    }
                }
                String reg = registroAlumnoTodasFechas.toString().replace("\"","");
                reg = reg.replace("[", "");
                reg = reg.replace("]", "");
                filasCSV.add(reg);

            }
        } catch (Exception ex) {
        }
        return filasCSV;
    }









    //////// prueba guardando en csv - sin uso
    public ExportacionExcel(final String direccion_archivo) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(direccion_archivo));
            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] { "No.", "Apellido", "Nombre," });
            data.add(new String[] { "1", "Ayerdiz", "Mario," });
            data.add(new String[] { "2", "Baltodano", "Perez," });
            data.add(new String[] { "3", "Castillo", "Ruben," });
            data.add(new String[] { "4", "Gonzalez", "Henry," });
            data.add(new String[] { "5", "Hernandez", "Luigi," });
            data.add(new String[] { "6", "Martinez", "Linda," });
            data.add(new String[] { "7", "Vilchez", "Steven," });

            writer.writeAll(data);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}