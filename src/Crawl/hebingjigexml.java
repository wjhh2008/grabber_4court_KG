package Crawl;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class hebingjigexml {
	public static void main(String[] args) throws IOException, DocumentException{   

		int filei = 101173;
		Document outdocument = DocumentHelper.createDocument();
        Element gonggaoselem = outdocument.addElement("gonggaos");
        int filenum = 1;
        while(filenum <5){
        	
            String filename1="D:\\法院\\法院公告\\"+ filenum +".xml";  
	       try{
        	SAXReader reader = new SAXReader();  
        	Document  document = reader.read(new File(filename1));      
        	

        	Element root=document.getRootElement();    
        	List list = root.elements(); 
        	
        	 for (int i = 0, size = list.size(); i < size; i++) {  
        		 
                 Element e = (Element) list.get(i);  
                 //获取属性id  
                 Element gong = gonggaoselem.addElement("gonggao");
                 gong.addAttribute("id",filei+"");
                 filei++;
                 System.out.println(filei);
                 List ce = e.elements();  
                 for (int j = 0, csize = ce.size(); j < csize; j++) {  
                     Element tempE = (Element) ce.get(j);  
                     String qName = tempE.getName();  
                  
                     switch (qName) {  
                     case "date":  
                         String datestr = tempE.getText();  
                         Element date = gong.addElement("date");
                         date.setText(datestr);
                         break;  
                     case "party":  
                         String partystr = tempE.getText();  
                         Element party = gong.addElement("party");
                         party.setText(partystr);
                         break;  
                     case "affiliation":
                    	 String affstr = tempE.getText();
                    	 Element affiliation = gong.addElement("affiliation");
                    	 affiliation.setText(affstr);
                    	 break;
                     case "content":
                    	 String contentstr = tempE.getText();
                    	 Element content = gong.addElement("content");
                    	 content.setText(contentstr);
                    	 break;
                     }  
                   
              /*     Element e = root.element("date"); 
                     Element name = root.element("party");
                     Element aff = root.element("affiliation");
                     Element con = root.element("content");
                     
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
                */ }  
                 //结束一轮<person>结点的遍历，将person对象存入集合，并清空。  
               
             }  
        	
          

	       }
	       catch (Exception ex) {
	    	   ex.printStackTrace();

	       }
        	
	       filenum++;
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
