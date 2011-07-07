package org.acooly.note;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Contact {

	public static void main(String[] args) throws Exception{
		
		String file = "D:\\temp\\bbbb.TXT";
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		FileWriter out = new FileWriter("D:\\temp\\newContact.txt",true);
		String buf = "";
		
		while((buf = reader.readLine()) != null){
			String bufs[] = buf.split(",");
			if(bufs.length > 14){
				//3ºÍ13
				out.write(bufs[3]+","+bufs[13]+"\n");				
			}

		}
		out.flush();
		out.close();
		reader.close();
		
	}
	
}
