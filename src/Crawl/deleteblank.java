package Crawl;
	    
import java.io.BufferedInputStream;  
import java.io.BufferedReader;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileWriter;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
	  
	public class deleteblank {  
	    private static String filename1;  
	    private static String filename2;  
	    public static void main(String[] args) {  
	        filename1="D:\\eclipse projects\\legal\\src\\Crawl\\1.txt";  
	        filename2="2.txt";  
	        File file=new File(filename1);  
	        InputStream is=null;  
	        BufferedReader br = null;  
	        String tmp;  
	        FileWriter writer=null;  
	        int i=0;  
	        try {  
	            is=new BufferedInputStream(new FileInputStream(file));  
	            br = new BufferedReader(new InputStreamReader(is, "utf-8"));  
	            writer = new FileWriter(filename2, true);  
	            while((tmp=br.readLine())!=null){  
	                if(tmp.equals(""));  
	                else{  
	                    writer.write(tmp+"\n");  
	                    i++;  
	                    System.out.println(i);  
	                }  
	            }  
	            writer.close();  
	            is.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	}  

