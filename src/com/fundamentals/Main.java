package com.fundamentals;
//import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import org.json.*;

public class Main {

	public static void main(String[] args) {
		
		
		
		String rutaArchivo = "what.txt";

        try {
            FileReader fileReader = new FileReader(rutaArchivo);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int index = -1;
            ArrayList<Json> formats = new ArrayList<>();
    		

            while ((line = bufferedReader.readLine()) != null) {
            	if (containDate(line)) {
            		Json json_format = new Json();
            		formats.add(json_format);
            		getHeaderTransaction(clearData(line), json_format);
            		index += 1;
            	} else {
            		formats.get(index).DATA += clearData(line) + "\n";
            	}
            }
            JSONArray json_array = getToJSON(formats);
            System.out.println(json_array.toList());
    		int i = 0;
//    		for (Json format : formats) {
//    			
//        		System.out.println("=============TRANSACCION "+ i +"=================");
//                System.out.println("HEADER: " + format.HEADER);
//                System.out.println("FECHA: " + format.FECHA);
//                System.out.println("VENTA: " + format.VENTA);
//                System.out.println("TERMINAL: " + format.TERMINAL);
//                System.out.println("OPERACION: " + format.OPERACION);
//                System.out.println("DATA: " + format.DATA);
//                i += 1;
//            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

  
	}
	private static void service() {
        String apiUrl = "https://jsonplaceholder.typicode.com/posts";

        // Crear un cliente HTTP
        HttpClient httpClient = HttpClient.newHttpClient();

        // Crear datos a enviar en el cuerpo de la solicitud POST (en formato JSON)
        Map<Object, Object> postData = new HashMap<>();
        postData.put("title", "Nuevo título");
        postData.put("body", "Contenido del nuevo post");
        postData.put("userId", 1);

        // Crear una solicitud HTTP POST con el cuerpo JSON
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(buildRequestBodyFromMap(postData))
                .build();

        try {
            // Enviar la solicitud y recibir la respuesta
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Imprimir el código de respuesta y el cuerpo de la respuesta
//            System.out.println("Código de respuesta: " + response.statusCode());
//            System.out.println("Cuerpo de la respuesta: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private static HttpRequest.BodyPublisher buildRequestBodyFromMap(Map<Object, Object> data) {
        // Convertir el mapa de datos a una cadena JSON
        String json = data.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                .reduce((s1, s2) -> s1 + "," + s2)
                .map(s -> "{" + s + "}")
                .orElse("{}");

        // Crear un cuerpo de solicitud a partir de la cadena JSON
        return HttpRequest.BodyPublishers.ofString(json);
    }
	
	private static boolean containDate(String line) {

        Pattern pattern = Pattern.compile("\\b\\d{1,2}/\\d{1,2}/\\d{2,4}\\b");
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
	}
	private static String clearData(String line) {
		String regex = "[a-zA-Záéíóúñ._0-9\\/V=T=O=:\s]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);
		
		String cleared_data = "";
		while (matcher.find()) {
			cleared_data += matcher.group();
		}
		
		return cleared_data;
	}
	private static void getHeaderTransaction(String line, Json json_format) {
//		System.out.println(line);

		
		String[] parts = line.split("\\s+");
		json_format.HEADER = line;
		getFormatDate(line, json_format);


        for (int i = 0; i < parts.length; i++) {
        	if (parts[i].startsWith("V=")) {
                json_format.VENTA = parts[i].substring(2);
            } else if (parts[i].startsWith("T=")) {

                json_format.TERMINAL = parts[i].substring(2);
            } else if (parts[i].startsWith("O=")) {
                json_format.OPERACION = parts[i].substring(2);
            }
        }
	}
	private static void getFormatDate(String cleared_data, Json json_format) {
		Pattern pattern_regex_date = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{2}(\\s)?\\d{2}\\:\\d{2}");
		Matcher matcher_date = pattern_regex_date.matcher(cleared_data);
		
		String date = "";
		while (matcher_date.find()) {
			date += matcher_date.group();
		}
		if (date.isEmpty()) {
//		    System.out.println("NO EXISTE FECHA");
		} else {
			json_format.FECHA = date;
		}
		
	}
	
	private static JSONArray getToJSON(ArrayList<Json> formats) {
		
		JSONArray json_array = new JSONArray();
		for(Json format: formats){
			JSONObject json = new JSONObject();
			json.put("FECHA", format.FECHA);
			json.put("VENTA", format.VENTA);
			json.put("TERMINAL", format.TERMINAL);
			json.put("OPERACION", format.OPERACION);
			json.put("DATA", format.DATA);
			
			json_array.put(json);
			
		}
		
		return json_array;
	}
	

}
