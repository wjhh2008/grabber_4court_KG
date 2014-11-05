package Crawl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class genxml {
	public static void main(String[] args) throws IOException, DocumentException{   

		int filei = 1;
		Document outdocument = DocumentHelper.createDocument();
        Element gonggaoselem = outdocument.addElement("gonggaos");
        while(filei <= 10000){
        	String filename1="D:\\法院\\法院公告\\"+ filei +".xml";  
	       try{
        	SAXReader reader = new SAXReader();  
        	Document  document = reader.read(new File(filename1));      
        	

        	Element root=document.getRootElement();              	
            Element e = root.element("date"); 
            Element name = root.element("party");
            Element aff = root.element("affiliation");
            Element con = root.element("content");
            
            System.out.println(e.getData());
            System.out.println(name.getData());  
            System.out.println(aff.getData());
            System.out.println(con.getData());
            
            
            Element gong = gonggaoselem.addElement("gonggao");
            String index = ""+filei;
            
            gong.addAttribute("id",index);
            
            Element date = gong.addElement("date");
            Element party = gong.addElement("party");
            Element affiliation = gong.addElement("affiliation");
            Element content = gong.addElement("content");
           
           
            
            date.setText(e.getData().toString());
            party.setText(name.getData().toString());
            affiliation.setText(aff.getData().toString());
            content.setText(con.getData().toString());
            
	       }
	       catch (Exception ex) {
	    	   ex.printStackTrace();

	       }
        	filei++;
        
        }
        
        try{
	         XMLWriter output = new XMLWriter(new FileWriter( new File("d:\\法院\\法院公告\\result.xml") ));
	         output.write( outdocument );
	         output.close();
	         }
	      catch(IOException e)
	          {
	          System.out.println(e.getMessage());
	          }
	   }
        
    
	  public static boolean writeInFile(String path, String str)
	    {
	    	String filepath=path;
	    	
	        OutputStreamWriter osw = null;
	        FileOutputStream fileos = null;
	        BufferedWriter bw = null;
	        try
	        {
	            fileos = new FileOutputStream(filepath, true);
	            osw = new OutputStreamWriter(fileos,"utf-8");
	            bw = new BufferedWriter(osw);
	            if(!str.equals(""))
	            {
	                bw.append(str);
	                bw.newLine();
	            }
	            bw.close();
	            osw.close();
	            fileos.close();
	            return true;
	        }
	        catch (IOException e)
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            return false;
	        } 
	    }
	    
     
}
