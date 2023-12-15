package utils;
import java.io.*;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.Calendar;

public class Records {
	
	public boolean main(String line) {
		boolean exists = true; 
        try {
        	File file = new File("records.txt");
        	Calendar calendar = Calendar.getInstance();
        	if (file.exists()) {
        		if (checkDay(file) != calendar.get(Calendar.DAY_OF_MONTH)) {
        			file.delete();
        			exists = createFile(line, calendar);
        		} else {
        			exists = write(read(file), line);
        		}
        	} else {
        		exists = createFile(line, calendar);
        	}
        } catch (Exception e) {
        	System.out.println("hubo un error iniciando");
        }
        return exists;
	}
	private boolean createFile(String line, Calendar calendar) {
		try {        	
			FileWriter file_writer = new FileWriter("records.txt");
			file_writer.write("day:" + calendar.get(Calendar.DAY_OF_MONTH) +"\n"+ line);
			file_writer.close();
        } catch (Exception e) {
        	System.out.println("hubo un error iniciando");
        }
		return false;
	}
	private int checkDay(File file) {
		String text = "";
		String day = "";
		try {
			Scanner myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
				text += myReader.nextLine() + "\n";
				if (text.contains("day:")) {
					Pattern pattern = Pattern.compile("\\d{1,2}");
					Matcher matcher = pattern.matcher(text);
					while (matcher.find()) {
						day += matcher.group();
					}
					break;
				}
			} 
			myReader.close();
		} catch (IOException e) {
        	System.out.println("hubo un error leyendo");
            e.printStackTrace();
        }
		return Integer.parseInt(day);
	}
	public boolean exists(String text, String line) {
		boolean exists = false;
		if (text.contains(line)) {
			exists = true;
		}
		return exists;
 	}
	
	private String read(File file) {
		String text = "";
		try {
			Scanner myReader = new Scanner(file);
			while (myReader.hasNextLine()) {
				text += myReader.nextLine() + "\n";
			} 
			myReader.close();
		} catch (IOException e) {
        	System.out.println("hubo un error leyendo");
            e.printStackTrace();
        }    
		return text;
	}
	
	private boolean write(String previus_text, String line) {
		boolean exists = true;
		if (!exists(previus_text, line)) {
	    	try {
	    		FileWriter file_writer = new FileWriter("records.txt");
	    		file_writer.write(previus_text + "\n" + line);
	    		file_writer.close();
	        } catch (IOException e) {
	        	System.out.println("hubo un error escribiendo");
	            e.printStackTrace();
	        }
	    	exists = false;
		}
		return exists;
	}
}
