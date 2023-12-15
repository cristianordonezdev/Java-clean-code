package utils;
import java.io.*;
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.Date;

public class Records {
	
	public boolean main(String line) {
		boolean exists = true; 
        try {
        	File file = new File("records.txt");
        	Date date = new Date(file.lastModified());
        	System.out.println(date);

        	if (file.exists()) {
        		exists = write(read(file), line);
        	} else {
    			FileWriter file_writer = new FileWriter("records.txt");
    			file_writer.write(line);
    			file_writer.close();
    			exists = false;
        	}
        } catch (Exception e) {
        	System.out.println("hubo un error iniciando");
        }
        return exists;
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
