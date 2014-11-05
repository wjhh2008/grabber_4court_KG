package Crawl;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class sc3{  
	
	HttpClient httpClient;
		
    public static void main(String[] args) throws IOException, InterruptedException{   
        sc3 si=new sc3();
        String html = "";
        int pagenum = 1;
        
        while(pagenum <= 1){
        	
        	String urlpath = "http://www.hshfy.sh.cn/shfy/gweb/ktgg_search.jsp?pagesnum="+pagenum;
        	html = si.methodPa(urlpath);
        	String filepath = "D:\\法院\\上海法院\\网页源代码\\"+pagenum+".txt";
        	
        	List<String> affi = new ArrayList<String>();

    //    	String affi = "";
        	String court = "";
        	String date = "";
        	String casenumber = "";
        	String reason = "";
        	String department = "";
        	String judge = "";
        	String appellant = "";
        	String defandent = "";
        	
            Pattern p =Pattern.compile("<TD class=10dpi width=48 align='center'><FONT color=#000000>(.*?)<span class=\"word7\">",Pattern.CASE_INSENSITIVE);
    	    Matcher m = p.matcher(html);
    	    while (m.find()) 
    	    {
    	    	String temp = m.group(1).trim();
    	    	affi.add(temp);
    	  //  	affiliationelem.setText(affi);
    	    	System.out.println(affi);
    	    }
        	
//    	    while(affi.){
//    			System.out.println("next Index="+affi.nextIndex()+",Object="+affi.next());
//    		}	
            		
        	writeInFile(filepath, html);
    
        pagenum++;
        Thread.sleep(3000);
        }
        
       
    }   
     
    
  
    public String methodPa(String strURL) throws IOException{  
    	System.getProperties().setProperty("proxySet", "true");//设置代理IP，防止ip被封
    	httpClient = new HttpClient();
		List <Header> headers = new ArrayList <Header>();  
        headers.add(new Header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"));  
        httpClient.getHostConfiguration().getParams().setParameter("http.default-headers", headers); //尽量添加headers，模拟浏览器
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));  
         
        GetMethod getMethod = new GetMethod(strURL);//使用Get或post根据网页而定
        int statusCode=0;//返回状态 200 404 500这种
		try{
			statusCode= httpClient.executeMethod(getMethod);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		String html="";
		if (statusCode == HttpStatus.SC_OK) { 
			byte[] dataResponseBody = getMethod.getResponseBody();
			html = new String(dataResponseBody);
			
		}else{
			System.out.println("有问题");
		}
		getMethod.releaseConnection();
	//	System.out.println(html);
		return html;
    }   
    
    //获得页面的所有链接
  
  

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
