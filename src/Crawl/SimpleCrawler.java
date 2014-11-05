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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;  
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

public class SimpleCrawler{  
	
	HttpClient httpClient;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		SimpleCrawler si = new SimpleCrawler();
		String html = "";
		
		String urlfileName = Paremeters.urlfile;

		File file = new File(urlfileName);
		
		String outPath = "XMLFile" + File.separator + args[0] + "-" + args[1] + ".xml";
		InputStream is = null;
		BufferedReader br = null;
		String tmp;

		int startPage = Integer.valueOf(args[0]);
		int endPage = Integer.valueOf(args[1]);
		int i = 1;
		
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			Document document = DocumentHelper.createDocument();
			Element gonggaoselem = document.addElement("gonggaos");
			while ((tmp = br.readLine()) != null
					&& startPage < endPage) {
				if (tmp.equals("") || i < startPage) {
					i++;
					continue;
				} else {
					System.out.println("start to crawl the "+startPage+"th page..");
					int time = (int) Math.rint(Math.random()
							* (Paremeters.ENDTIME - Paremeters.STARTTIME)
							+ Paremeters.STARTTIME);
					Thread.sleep(time * 100);
					System.out.println("Thread sleeping 0."+time+" s ..");
					html = si.methodPa(tmp);
					if(html.equals("")){
						System.out.println("content save fail...continue next...");
						continue;
					}
					writeInFile(Paremeters.sourceFileDir + startPage + ".txt",
							html);
					System.out.println("save the source content!");
					Element bigelem = gonggaoselem.addElement("gonggao");
					bigelem.addAttribute("id", "" + startPage);
					Element partyelem = bigelem.addElement("party");
					Element dateelem = bigelem.addElement("date");
					Element affiliationelem = bigelem.addElement("affiliation");
					Element contentelem = bigelem.addElement("content");
					String origin = html;
					html = html.substring(html.indexOf("<!--当事人和内容一行-->"),
							html.indexOf(" <!--裁判文书和内容一行-->"));
					Pattern p = Pattern.compile(
							"<div class=\"dsrnr\">(.*?)</div>",
							Pattern.CASE_INSENSITIVE);
					Matcher m = p.matcher(html);
					while (m.find()) {
						String party = m.group(1).trim();
						partyelem.setText(party);
						// System.out.println(party);
					}
					
					html = origin;
					Pattern p1 = Pattern.compile("刊登日期：(.*?)<br />",
							Pattern.CASE_INSENSITIVE);
					Matcher m1 = p1.matcher(html);
					while (m1.find()) {
						String date = m1.group(1).trim();
						dateelem.setText(date);
						// System.out.println(date);
					}

					html = origin;
					html = html.substring(html.indexOf("<!--所属法院一行-->"),
							html.indexOf("刊登版面："));
					html = html.trim();
					// System.out.println(html);
					Pattern p2 = Pattern.compile("(.*?) <br />",
							Pattern.CASE_INSENSITIVE);
					Matcher m2 = p2.matcher(html);
					while (m2.find()) {
						String affi = m2.group(1).trim();
						affiliationelem.setText(affi);
						// System.out.println(affi);
					}

					html = origin;
					html = html.substring(html.indexOf("<!--裁判文书和内容一行-->"),
							html.indexOf("<!--所属法院一行-->"));
					// System.out.println(html);
					Pattern p3 = Pattern.compile(">(.*?)</div>",
							Pattern.CASE_INSENSITIVE);
					Matcher m3 = p3.matcher(html);
					String content = "";
					int index = 1;
					while (m3.find()) {
						if (index == 1) {
							content = content + m3.group(1).trim() + ".";
							index++;
						} else
							content = content + m3.group(1);

					}
					// System.out.println(content);
					contentelem.setText(content);
					try {
						XMLWriter output = new XMLWriter(new FileWriter(
								new File(outPath)));
						output.write(document);
						output.close();
						System.out.println("save the XML content!");
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
				}
				startPage++;
				// System.out.println(i+tmp);
			}

			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public static String getUrl(String html) {
		String url = "";
		String allurl = "";
		Pattern p = Pattern.compile(
				new String("\\<a href=\\\"http.*\\\"\\s*title=\\\".*\\\"\\s*target=\\\"_blank\\\"\\>"),
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(html);
		while (m.find()) {
			String htmlpart = m.group(0).trim();
			int end = htmlpart.indexOf("htm");
			url = htmlpart.substring(9,end+3);
			allurl = allurl + url + "\n";
			//System.out.println(url);
		}
		return allurl;
		//if (m.find()) System.out.println(m.group(0));
		//return m.toString();
	}
	
	public String methodPa(String strURL){
		System.getProperties().setProperty("proxySet", "true");// 设置代理IP，防止ip被封
		httpClient = new HttpClient();
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"));
		httpClient.getHostConfiguration().getParams().setParameter("http.default-headers", headers);  // 尽量添加headers，模拟浏览器
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(0, false));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(3000); 
		
		BufferedReader br = null;
		InputStream input = null;
		GetMethod getMethod = null;
		String html = "";
		do{
			getMethod = new GetMethod(strURL);// 使用Get或post根据网页而定
			int statusCode = 0;// 返回状态 200 404 500这种
			
			System.out.println("get...");
			try {
				statusCode = httpClient.executeMethod(getMethod);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
			
			if (statusCode == HttpStatus.SC_OK) {
				try {
					input = getMethod.getResponseBodyAsStream();
					br = new BufferedReader(new InputStreamReader(input,"utf-8"));
					String s = "";
					while ((s = br.readLine()) != null) {
						html = html + s + "\r\n";
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						br.close();
						input.close();
						continue;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					html = "";
					getMethod.releaseConnection();
					continue;
				}
				
	
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			}else{
				System.out.println("HttpStatus: "+statusCode);
			}
		}while(true);

		
		getMethod.releaseConnection();
		return html;
	}

	// 获得页面的所有链接

	public static boolean writeInFile(String path, String str) {
		String filepath = path;

		OutputStreamWriter osw = null;
		FileOutputStream fileos = null;
		BufferedWriter bw = null;
		try {
			fileos = new FileOutputStream(filepath, true);
			osw = new OutputStreamWriter(fileos, "utf-8");
			bw = new BufferedWriter(osw);
			if (!str.equals("")) {
				bw.append(str);
				bw.newLine();
			}
			bw.close();
			osw.close();
			fileos.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
 }  
