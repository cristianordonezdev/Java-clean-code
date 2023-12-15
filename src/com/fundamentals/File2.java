package com.fundamentals;
import org.json.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import utils.Records;

public class File2 {
	private String file;
	
	public File2(String file) {
		this.file = file;
	}
	 public JSONObject main() {

            try {
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
                      
                            miJson.put("registros", registros);
                           
                            bloques.put(miJson);
                        }

                     
                        miJson = new JSONObject();
                        registros = new JSONArray();

                        Records records = new Records();

                        String[] partes = obtenerPartesRelevantes(matcher.group()).split(",");
                        
                        if (! records.main(partes[0] + partes[1] + partes[2] + partes[3] + partes[4])) {

                        	miJson.put("pago", partes[0] + partes[1]);
                        	miJson.put("operacion", partes[2]);
                        	miJson.put("vendedor", partes[3]);
                        	miJson.put("codigo", partes[4]);
                        }
                        

                    } else if (!miJson.isEmpty()) {
                        JSONObject registro = new JSONObject();
                        if (linea.startsWith("1")) {
                           
                            registro.put("articulo", linea);
                            registros.put(registro);
                        } else if (linea.startsWith("2")) {
                           
                            registro.put("formatopago", linea);
                            registros.put(registro);
                        }
                    }
                }

               
                miJson.put("registros", registros);
                
                bloques.put(miJson);

                
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
                String fechaActual = dateFormat.format(new Date());

                
                JSONObject cuerpoJson = new JSONObject();
                cuerpoJson.put("tienda", "1002");
                cuerpoJson.put("fecha", fechaActual);
                cuerpoJson.put("codigo", "02");

                
                String direccionIP = obtenerDireccionIP();
                String id = direccionIP + "_" + fechaActual;
                cuerpoJson.put("id", id);

                
                cuerpoJson.put("body", bloques);
          
                bufferedReader.close();
                return cuerpoJson;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al abrir el archivo: " + e.getMessage());
            }
            return new JSONObject();
	         
	    }
	    private String obtenerPartesRelevantes(String linea) {
	        String[] partes = linea.split(",");
	        StringBuilder resultado = new StringBuilder();

	        for (String parte : partes) {
	            if (parte.trim().length() > 0) {
	                if (resultado.length() > 0) {
	                    resultado.append(",");
	                }
	                resultado.append(parte);
	            }
	        }

	        return resultado.toString();
	    }
	    private String obtenerDireccionIP() {
	        return "192.168.1.3";
	    }
}