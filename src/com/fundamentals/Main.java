package com.fundamentals;
//import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.json.*;


public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	private static Properties properties = new Properties();

	public static void main(String[] args) {
		InputStream input = null;

		try {
			input = new FileInputStream("config.properties");
			properties.load(input);
			
			FileHandler fileHandler = new FileHandler("status.log");
			fileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(fileHandler);
			LOGGER.info("INICIANDO PROGRAMA");
			
	        String folder = "files"; 

			String archivoMasReciente_2 = obtenerArchivoMasReciente(folder, "EJALL");
	        String archivoMasReciente_1 = obtenerArchivoMasReciente(folder, "ejbackup");
	        
	        if (archivoMasReciente_1 != null || archivoMasReciente_2 != null) {
//	        	------JSON FILE 1----------
	        	File1 file1 = new File1(folder + "\\" + archivoMasReciente_1);
	        	JSONObject json_1 = file1.main();
	        	System.out.println("JSON DE CHINO: ");
	        	System.out.println(json_1);
//	        	----------------------------	        	
//	        	------JSON FILE 2----------
	        	File2 file2 = new File2(folder + "\\" + archivoMasReciente_2);
	        	JSONObject json_2 = file2.main();
	        	System.out.println("JSON DE YALI: ");
	        	System.out.println(json_2);
//	        	----------------------------
	        } else {
	        	System.out.println("No se encontraron archivos en la carpeta especificada.");
	        }
			
		} catch (Exception e) {
			LOGGER.severe("Error al configurar el registro: " + e.getMessage());
		}
	}

	private static String obtenerArchivoMasReciente(String carpeta, String type_name) {
        File directorio = new File(carpeta);
        File[] archivos = directorio.listFiles((dir, nombre) -> nombre.contains(type_name));
      

        if (archivos != null && archivos.length > 0) {
            File archivoMasReciente = archivos[0];

            for (File archivo : archivos) {
            
                if (archivo.isFile()) {
                   
                    if (archivo.lastModified() > archivoMasReciente.lastModified()) {
                        archivoMasReciente = archivo;
                    }
                }
            }
            return archivoMasReciente.getName();
        }
        return null;
    }
	
}