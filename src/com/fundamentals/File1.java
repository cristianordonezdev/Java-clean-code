package com.fundamentals;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import org.json.*;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import utils.Records;

public class File1 {
	private final Logger LOGGER = Logger.getLogger(Main.class.getName());
	private ArrayList<Json> formats = new ArrayList<>();
	private Properties properties = new Properties();
	private String file;
	
	public File1(String file) {
		this.file = file;
	}
	
	public JSONObject main() {
		InputStream input = null;

		try {
			input = new FileInputStream("config.properties");
			properties.load(input);

			FileHandler fileHandler = new FileHandler("status.log");
			fileHandler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(fileHandler);
			LOGGER.info("INICIANDO PROGRAMA");


			try {
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line;
				int index = -1;

				while ((line = bufferedReader.readLine()) != null) {

					if (headerMatcher(line).find()) {
						Json json_format = new Json();
						formats.add(json_format);
						getHeaderTransaction(clearData(line), json_format);
						index += 1;
					} else if (formats.size() > 0){
						formats.get(index).DATA += clearData(line) + "\n";
					}
				}
				exceptionToClean();

				bufferedReader.close();
				return getToJSON();
			} catch (IOException e) {
				System.out.println("main error");
				LOGGER.severe(e.toString());
			}
		} catch (Exception e) {
			System.out.println("main error1" + e);

			LOGGER.severe("Error al configurar el registro: " + e.getMessage());
		}
		return new JSONObject();
	}
	
	private String clearData(String line) {
		String regex = "[a-zA-Záéíóúñ._0-9\\/V=T=O=:\s]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);

		String cleared_data = "";
		while (matcher.find()) {
			cleared_data += matcher.group();
		}

		return cleared_data;
	}

	private void getHeaderTransaction(String line, Json json_format) {
		json_format.HEADER = line;
		getFormatDate(line, json_format);
		
		Pattern pattern_rest_options = Pattern.compile("V=\\d{8}(\\s?)T=\\d{3}(\\s?)O=\\d{4}");
        Matcher matcher = pattern_rest_options.matcher(line);
        
        if (matcher.find()) {
        	String[] parts = matcher.group().split("\\s+");
        	for (int i = 0; i < parts.length; i++) {
        		if (parts[i].startsWith("V=")) {
        			json_format.VENTA = parts[i].substring(2);
        		} if (parts[i].startsWith("T=")) {
        			
        			json_format.TERMINAL = parts[i].substring(2);
        		} if (parts[i].startsWith("O=")) {
        			json_format.OPERACION = parts[i].substring(2);
        		}
        	}
        }

	}

	private Matcher headerMatcher(String line) {
		Pattern pattern_regex_date = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{2}(\\s)?\\d{2}\\:\\d{2}");
		Matcher matcher_date = pattern_regex_date.matcher(line);

		return matcher_date;
	}

	private void getFormatDate(String cleared_data, Json json_format) {
		Matcher matcher_date = headerMatcher(cleared_data);

		String date = "";
		while (matcher_date.find()) {
			date += matcher_date.group();
		}
		if (date.isEmpty()) {
			LOGGER.severe("NO SE ENCONTRO UN PATRON PARA LA FECHA");
		} else {
			json_format.FECHA = date;
		}
	}

	private JSONObject getToJSON() {
		JSONObject json_object = new JSONObject();
		JSONArray json_array = new JSONArray();
		
		Records records = new Records();

		for (Json format : formats) {
			if (!records.main(format.HEADER)) {				
				JSONObject json = new JSONObject();
				json.put("fecha", format.FECHA);
				json.put("venta", format.VENTA);
				json.put("terminal", format.TERMINAL);
				json.put("operacion", format.OPERACION);
				json.put("data", format.DATA);
				json_array.put(json);
			}
		}
		String date = new SimpleDateFormat("ddMMyy").format(new Date());
		json_object.put("id", properties.getProperty("ip") + "_" + date);
		json_object.put("codigo", "01");
		json_object.put("body", json_array);
		json_object.put("fecha", date);

		return json_object;
	}

	private void exceptionToClean() {
		ArrayList<Json> temporal_formats = new ArrayList<>();

		String[] exceptions = { "CERRADO", "APLICACION PALACIO", "ABIERTO", "TERMINAL SETUP" };
		for (int i = 0; i < formats.size(); i++) {
			for (String exception_word : exceptions) {
				if (formats.get(i).DATA.contains(exception_word)) {
					temporal_formats.add(formats.get(i));
				}
			}
		}
		
		ArrayList<Json> copy = new ArrayList<Json>(formats);
		copy.removeAll(temporal_formats);
		formats = copy;
	}
}