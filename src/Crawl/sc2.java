package Crawl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class sc2{  
	
	HttpClient httpClient;
		
    public static void main(String[] args) throws IOException{   
        sc2 si=new sc2();
        String html = "";
        String urlpath = "http://www.court.gov.cn/extension/search.htm?keyword=%E4%BC%81%E4%B8%9A&caseCode=&wenshuanyou=&anjianleixing=&docsourcename=&court=&beginDate=2004-01-01&endDate=2014-10-22&adv=1&orderby=&order=&page=1";
        html = si.methodPa(urlpath);
        int i = 1;
        String filename1= i + ".txt";  
 
        writeInFile(filename1, html);
        
        File file=new File(filename1);  
        InputStream is=null;  
        BufferedReader br = null;  
        String tmp;  
        String allcontent = "";
        try {  
            is=new BufferedInputStream(new FileInputStream(file));  
            br = new BufferedReader(new InputStreamReader(is, "utf-8"));  
            while((tmp=br.readLine())!=null){  
            
            	if(tmp.equals(""));  
            	else{
            		allcontent = allcontent + tmp.trim();
            	}
          	
            }
            	is.close();  
            } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        System.out.println(allcontent);
    	Pattern p =Pattern.compile("<td align=\"left\">(.*?)��Ժ</td>",Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(allcontent);
	    while (m.find()) 
	    {
	    	String aff = m.group(1).trim();
	    	System.out.println(aff);
	    }
        
        
    }   
     
    
    
    public static String getUrl(String html){
    	String url = "";
    	String allurl = "";
    	Pattern p =Pattern.compile("class=\"fygg_contents\".*?<a href='(.*?)'>",Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(html);
	    while (m.find()) 
	    {
	    	url="http://www.live.chinacourt.org/"+m.group(1).trim();
	    	allurl = allurl + url + "\n";
	     //   System.out.println(url);	      
	    }
    	return allurl;
    }
    

    public String methodPa(String strURL) throws IOException{  
    	System.getProperties().setProperty("proxySet", "true");//���ô���IP����ֹip����
    	httpClient = new HttpClient();
		List <Header> headers = new ArrayList <Header>();  
        headers.add(new Header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"));  
        httpClient.getHostConfiguration().getParams().setParameter("http.default-headers", headers); //�������headers��ģ�������
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));  
         
        GetMethod getMethod = new GetMethod(strURL);//ʹ��Get��post������ҳ����
        int statusCode=0;//����״̬ 200 404 500����
		try{
			statusCode= httpClient.executeMethod(getMethod);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		String html="";
		if (statusCode == HttpStatus.SC_OK) { 
			InputStream input = getMethod.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(input, "utf-8")); 
			String s="";
			while (( s = br.readLine()) != null) { 
				html=html+s+"\r\n"; 
				//System.out.println(s);
			}
	
			
			if(br!=null){
				br.close();
			}
			if(input!=null){
				input.close();
			} 
			
		}else{
			System.out.println("������");
		}
		getMethod.releaseConnection();
		return html;
    }   
    
    //���ҳ�����������
  
  

    public static boolean writeInFile(String path, String str)
    {
    	String filepath=path;
    	
        OutputStreamWriter osw = null;
        FileOutputStream fileos = null;
        BufferedWriter bw = null;
        try
        {
            fileos = new FileOutputStream(filepath, false);
            osw = new OutputStreamWriter(fileos,"UTF-8");
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
