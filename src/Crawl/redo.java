package Crawl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class redo{  
	
	HttpClient httpClient;
		
    public static void main(String[] args) throws IOException{   
        String all = "";
      
        
        
        String filename1="D:\\法院\\法院公告\\78086开始.xml";  
      
        int i=78086;  
        while(i<78346){
        try {  
        	
 	       
            File file=new File(filename1);  
            InputStream is=null;  
            BufferedReader br = null;  
            String tmp;  
            all = "";
            is=new BufferedInputStream(new FileInputStream(file));  
            br = new BufferedReader(new InputStreamReader(is, "utf-8"));  
            Document document = DocumentHelper.createDocument();
       //     Element gonggaoselem = document.addElement("gonggaos");
            while((tmp=br.readLine())!=null){  
           
            	if(tmp.equals("") );  
                	else{ all = all + tmp; }
            }
     
            System.out.println(all);
        	Element bigelem = document.addElement("gonggao");
        	bigelem.addAttribute("id", ""+i);
        	Element partyelem = bigelem.addElement("party");
        	Element dateelem = bigelem.addElement("date");
        	Element affiliationelem = bigelem.addElement("affiliation");
        	Element contentelem = bigelem.addElement("content");
        	String origin = all;
        	all = all.substring(all.indexOf("<!--当事人和内容一行-->"), all.indexOf(" <!--裁判文书和内容一行-->"));
        	Pattern p =Pattern.compile("<div class=\"dsrnr\">(.*?)</div>",Pattern.CASE_INSENSITIVE);
    	    Matcher m = p.matcher(all);
    	    while (m.find()) 
    	    {
    	    	String party = m.group(1).trim();
    	    	partyelem.setText(party);
    	    	//System.out.println(party);
    	    }
    	    
    	    all = origin;
    	    Pattern p1 =Pattern.compile("刊登日期：(.*?)<br />",Pattern.CASE_INSENSITIVE);
    	    Matcher m1 = p1.matcher(all);
    	    while (m1.find()) 
    	    {
    	    	String date = m1.group(1).trim();
    	    	dateelem.setText(date);
//     	    	System.out.println(date);
    	    }
    	    
    	    all = origin;
    	    all = all.substring(all.indexOf("<!--所属法院一行-->"), all.indexOf("刊登版面："));
    	    all = all.trim();
        	//System.out.println(all);
    	    Pattern p2 =Pattern.compile("(.*?) <br />",Pattern.CASE_INSENSITIVE);
    	    Matcher m2 = p2.matcher(all);
    	    while (m2.find()) 
    	    {
    	    	String affi = m2.group(1).trim();
    	    	affiliationelem.setText(affi);
  // 	    	System.out.println(affi);
    	    }
  
    	    all = origin;
    	    all = all.substring(all.indexOf("<!--裁判文书和内容一行-->"),all.indexOf("<!--所属法院一行-->"));
    	//    System.out.println(all);
    	    Pattern p3 =Pattern.compile(">(.*?)</div>",Pattern.CASE_INSENSITIVE);
    	    Matcher m3 = p3.matcher(all);
    	    String content = "";
    	    int index = 1;
    	    while (m3.find()) 
    	    {
    	    	if( index==1)	{content = content +  m3.group(1).trim()+"."; index++;}
    	    	else content = content +m3.group(1);
    	    	            	    	
    	    }
  //  	    System.out.println(content);
    	    contentelem.setText(content);
    	    		try{
    	    	         XMLWriter output = new XMLWriter(new FileWriter( new File("d:\\法院\\法院公告\\"+i+".xml") ));
    	    	         output.write( document );
    	    	         output.close();
    	    	         }
    	    	      catch(IOException e)
    	    	          {
    	    	          System.out.println(e.getMessage());
    	    	          }
            
            	is.close();  
          
        	} catch (IOException e) {  
        		e.printStackTrace();  
        	}
        	i++;
        }
        
                	  
               /// 	writeInFile("d:\\法院\\法院公告源代码\\"+ i +".txt",all);
                	                	
              //  	Element bigelem = gonggaoselem.addElement("gonggao");
                
            	    	   
               
                	
              
             //       System.out.println(i+tmp);  
               
      

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
