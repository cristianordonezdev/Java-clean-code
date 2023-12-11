package com.fundamentals;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;


public class File2 {
	private Properties properties = new Properties();
	private String file;
	
	public File2(String file) {
		this.file = file;
	}
	
	public void main() {

		InputStream input = null;
        try {
        	input = new FileInputStream("config.properties");
			properties.load(input);

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linea;

            String patron = "0,\\d{1,2},\\d{4},\\d{5,8},[\\w\\w]+";
            Pattern pattern = Pattern.compile(patron);

            JSONArray bloques = new JSONArray();
            JSONObject miJson = new JSONObject();
            JSONArray registros = new JSONArray();

            while ((linea = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(linea);

                if (matcher.find()) {
                    if (!miJson.isEmpty()) {
                        // Agregar el array de registros al objeto JSON
                        miJson.put("Registros", registros);
                        // Agregar el objeto JSON al array de bloques
                        bloques.put(miJson);
                    }

                    // Crear un nuevo objeto JSON para el siguiente bloque
                    miJson = new JSONObject();
                    registros = new JSONArray();

                    // Asignar valores a variables
                    String[] partes = obtenerPartesRelevantes(matcher.group()).split(",");
                    miJson.put("PAGO", partes[0] + partes[1]);
                    miJson.put("OPERACION", partes[2]);
                    miJson.put("ID_CAJERO", partes[3]);
                    miJson.put("VENDEDOR", partes[4]);
                } else if (!miJson.isEmpty()) {
                    JSONObject registro = new JSONObject();
                    if (linea.startsWith("1")) {
                        // Registros que comienzan con "1" son artículos
                        registro.put("ARTICULO", linea);
                        registros.put(registro);
                    } else if (linea.startsWith("2")) {
                        // Registros que comienzan con "2" son formas de pago
                        registro.put("FORMATO_DE_PAGO", linea);
                        registros.put(registro);
                    }
                }
            }

            // Agregar el último array de registros al último objeto JSON
            miJson.put("Registros", registros);
            // Agregar el último objeto JSON al array de bloques
            bloques.put(miJson);

            // Obtener la fecha actual en formato de 6 dígitos (ddMMyy)
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
            String fechaActual = dateFormat.format(new Date());

            // Crear el objeto JSON principal con los campos adicionales
            JSONObject cuerpoJson = new JSONObject();
            cuerpoJson.put("fecha", fechaActual);
            cuerpoJson.put("codigo", "02");

            // Generar el ID usando la dirección IP y la fecha
            String direccionIP = properties.getProperty("ip");
            String id = direccionIP + "_" + fechaActual;
            cuerpoJson.put("id", id);

            // Agregar el array de bloques al objeto JSON principal
            cuerpoJson.put("body", bloques);

            System.out.println(cuerpoJson.toString(2));

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static String obtenerPartesRelevantes(String linea) {
      String[] partes = linea.split(",");
      StringBuilder resultado = new StringBuilder();

      for (String parte : partes) {
          if (!parte.isEmpty()) {
              if (resultado.length() > 0) {
                  resultado.append(",");
              }
              resultado.append(parte);
          }
      }

      return resultado.toString();
  }


}