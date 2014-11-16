package Crawl;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class PushState implements Runnable {
	
	String id;
	static String url = "https://api.xively.com/v2/feeds/628656427.xml?timezone=+8";
	MultiThreadedHttpConnectionManager connectionManager;
	
	PushState (){
		super();
		id = "test";
		this.connectionManager = SimpleCrawler.connectionManager;
	}
	PushState (String id){
		super();
		this.id = id; 
		this.connectionManager = SimpleCrawler.connectionManager;
	}
	
	@Override
	public void run(){
		double state = SimpleCrawler.state;
		String 	value =  String.format("%.1f", state);
		Document upxml = DocumentHelper.createDocument();
		Element eeml = upxml.addElement("eeml");
		Element environment = eeml.addElement("environment");
		Element data = environment.addElement("data").addAttribute("id", id);
		data.addElement("current_value").addText(value);
		
		for(int i=0;i<3;i++){
			HttpClient client = new HttpClient(connectionManager);
			PutMethod myPut = new PutMethod(url);
			List<Header> headers = new ArrayList<Header>();
			headers.add(new Header("Content-Type","text/xml"));
			headers.add(new Header("charset","utf-8"));
			headers.add(new Header("X-ApiKey","Wn6Cs6OuTACpKGlssPnOajl1sgsx0byLhgheUzl1MrI0tRbD"));
			client.getHostConfiguration().getParams().setParameter("http.default-headers", headers);
			
			try {
				String xml = upxml.asXML();
				//myPut.setRequestBody(upxml.toString());
				myPut.setRequestEntity((RequestEntity) new StringRequestEntity(xml,"text/xml","utf-8"));
				int statusCode = client.executeMethod(myPut);
				//System.out.println(myPut.getResponseBodyAsString());
				if(statusCode == HttpStatus.SC_OK){
					break;
				}else{
					System.err.println(statusCode);
					Thread.sleep(300);
					continue;
				}
			} catch (HttpException e) {
				System.err.print("UpNetErr");
			} catch (IOException e) {
				System.err.print("UpNetIOErr");
			} catch (InterruptedException e) {
				System.err.print("UpNetINTErr");

			} finally{
				myPut.releaseConnection();
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		(new Thread(new PushState())).start();  
        System.out.println("main thread run ");
	}
	
	

}
