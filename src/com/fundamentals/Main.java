package com.fundamentals;
//import com.github.cliftonlabs.json_simple.JsonObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
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

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	private static ArrayList<Json> formats = new ArrayList<>();
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

			String file_route = "ticket1.txt";

			try {
				if ("01" == identifyFile(file_route)) {
					File1 file_instance = new File1(file_route);
					file_instance.main();
				} else if ("02" == identifyFile(file_route)) {
					File2 file_instance = new File2(file_route);
					file_instance.main();
				}
				
;
			} catch (Exception e) {
				LOGGER.severe(e.toString());
			}
		} catch (Exception e) {
			LOGGER.severe("Error al configurar el registro: " + e.getMessage());
		}
	}

	private static String identifyFile(String file) {
		String result_code = "02";
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				if (headerMatcher(line).find()) {
					result_code = "01";
				}
			}
		} catch (Exception e) {
			LOGGER.severe("NO SE PUDO LEER EL ARCHIVO" + e.getMessage());
		}
		
		return result_code;
	}
	
	private static Matcher headerMatcher(String line) {
		Pattern pattern_regex_date = Pattern.compile("\\d{2}\\/\\d{2}\\/\\d{2}(\\s)?\\d{2}\\:\\d{2}");
		Matcher matcher_date = pattern_regex_date.matcher(line);

		return matcher_date;
	}
}
