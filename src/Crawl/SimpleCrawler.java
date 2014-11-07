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
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.omg.IOP.CodecFactory;

public class SimpleCrawler{  
	
	HttpClient httpClient;

	public static void main(String[] args) throws IOException,
			InterruptedException {
		SimpleCrawler si = new SimpleCrawler();
		//String html = "";
		
		File file = new File(Paremeters.urlfile);
		
		String outPath = "Company.xml";
		InputStream is = null;
		BufferedReader br = null;
		String url;

		int startPage = Integer.valueOf(args[0]);
		int endPage = Integer.valueOf(args[1]);
		int i = 0;
		Document comp = null;
		Document sup = null;
		
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			br = new BufferedReader(new InputStreamReader(is, "utf-8"));
			while ((url = br.readLine()) != null && startPage <= endPage) {
				i++;
				if (i % Paremeters.MAX_ITEM == 1){
					comp = DocumentHelper.createDocument();
					sup = DocumentHelper.createDocument();
				}
				System.out.println("start to crawl the "+startPage+"th page..");
		/*					
				int time = (int) Math.rint(Math.random()
						* (Paremeters.ENDTIME - Paremeters.STARTTIME)
						+ Paremeters.STARTTIME);
				Thread.sleep(time * 100);
				System.out.println("Thread sleeping 0."+time+" s ..");
		 */
				String mainhtml = si.methodPa(url);
				String compinfo = si.methodPa(url+"/company-information.html");
				String supplyhtml = si.methodPa(url+"/supply");
				if(mainhtml.equals("")){
					System.out.println("content save fail...continue next...");
					continue;
				}
				
				//System.out.println("save the source content!");
				
				
				Element company = comp.addElement("company");
				company.addElement("id").addText(matchstr(mainhtml, "class=\"pdmd\">\\s*\\S*\\s*<", 13, 1).trim());
				company.addElement("url").addText(url);
				company.addElement("info").addText(matchstr(compinfo,"class=\"company-info\">\\s*<p>[^<]*</p>", 24, 4).trim());
				company.addElement("business").addText(matchstr(compinfo, "主营产品或服务</th>\\s*<td.*>\\s*<a.*>[\\s\\S]*</a.*>\\s*</td.*>\\s*<th.*>主营行业", 7, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("range").addText(matchstr(compinfo, "主营行业</th>\\s*<td.*>\\s*<a.*>[\\s\\S]*</a.*>\\s*</td.*>", 4, 0).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("model").addText(matchstr(compinfo, "经营模式[\\s\\S]*主要客户群", 4, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("registered").addText(matchstr(compinfo, "注册资本</th>[\\s\\S]*公司注册地", 4, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("founding_time").addText(matchstr(compinfo, "公司成立时间</th>[\\s\\S]*主营产品或服务", 6, 7).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("area").addText(matchstr(compinfo,"公司注册地</th>[\\s\\S]*公司注册号", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("organization").addText(matchstr(compinfo,"企业类型</th>[\\s\\S]*注册资本", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("artificialpersion").addText(matchstr(compinfo,"法定代表人/负责人</th>[\\s\\S]*公司成立时间", 9, 6).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("register_number").addText(matchstr(compinfo,"公司注册号</th>[\\s\\S]*经营范围", 5, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("sales_area").addText(matchstr(compinfo,"主要市场</th>[\\s\\S]*主要经营", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("customers").addText(matchstr(compinfo,"主要客户群</th>[\\s\\S]*主要市场", 5, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("turnover").addText(matchstr(compinfo,"年营业额</th>[\\s\\S]*年出口额", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("brandname").addText(matchstr(compinfo,"品牌名称</th>[\\s\\S]*经营模式", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("warehouse").addText(matchstr(compinfo,"主要经营地点</th>[\\s\\S]*年营业额", 6, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("services");
				company.addElement("staff").addText(matchstr(compinfo,"员工人数</th>[\\s\\S]*厂房面积", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("exports");
				company.addElement("agent");
				company.addElement("website").addText(matchstr(compinfo,"公司网址</th>[\\s\\S]*是否", 4, 2).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("alibabashop");
				company.addElement("contact-name").addText(matchstr(compinfo,"联系人:[\\s\\S]*职", 4, 1).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("Tel").addText(matchstr(compinfo,"电 话 :[\\s\\S]*手 机 :", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("fax").addText(matchstr(compinfo,"传 真 :[\\s\\S]*地 址 :", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("address").addText(matchstr(compinfo,"地 址 :[\\s\\S]*客 服 :", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				company.addElement("zip_code");
				
				//company.addElement("").addText(matchstr(compinfo,).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				//company.addElement("").addText(matchstr(compinfo,).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				
				//Element shops = sup.addElement("shops");
				//shops.addElement("shop").addAttribute("url", arg1)addText(matchstr(supplyhtml,).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				//shops.addElement("").addText(matchstr(compinfo,).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				//shops.addElement("").addText(matchstr(compinfo,).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
				
				
				
				
				try {
					XMLWriter output = new XMLWriter(new FileWriter(new File(outPath)));
					output.write(comp);
					output.close();
					System.out.println("save the XML content!");
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				
				if (i % Paremeters.MAX_ITEM == 0){
					
				}
				
				startPage++;
				// System.out.println(i+tmp);
			}

			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


	}
	
	public static String matchstr(String html, String regexp, int stoffs, int enoffs){
		System.out.println("regexp: "+regexp);
		Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(html);
		String str = "";
		String res = "";
		while (m.find()) {
			String htmlpart = m.group(0).trim();
			str = htmlpart.substring(stoffs,htmlpart.length()-enoffs);
			res = res + str +"\n";
		}
		return res;
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
		//for cookies
		DefaultHttpParams.getDefaultParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		
		httpClient = new HttpClient();
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"));
		//headers.add(new Header("Cookie", "china_uv=66365e1884; Hm_lvt_3cfaa114cca90dbeb8cf6908074f92ef=1415327823; Hm_lpvt_3cfaa114cca90dbeb8cf6908074f92ef=1415328358; _ga=GA1.2.1863492324.1415327823; 9329132056=30020"));
		headers.add(new Header("Referer", strURL));
		
		httpClient.getHostConfiguration().getParams().setParameter("http.default-headers", headers);  // 尽量添加headers，模拟浏览器
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(0, false));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000); 
		httpClient.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		
		//System.out.println(strURL);
		BufferedReader br = null;
		InputStream input = null;
		GetMethod getMethod = null;
		String html = "";
		for (int i=0;i<3;){
			
			
			getMethod = new GetMethod(strURL);// 使用Get或post根据网页而定
			int statusCode = 0;// 返回状态 200 404 500这种
			System.out.println("get..."+strURL);
			try {
				
				statusCode = httpClient.executeMethod(getMethod);
			} catch (HttpException e) {
				System.err.println("Connection HttpException");
				continue;
			} catch (IOException e) {
				System.err.println("Connection IOException");
				//e.printStackTrace();
				continue;
			}
			
			if (statusCode == HttpStatus.SC_OK) {
				try {
					//html = getMethod.getResponseBodyAsString();
					input = getMethod.getResponseBodyAsStream();
					br = new BufferedReader(new InputStreamReader(input,"gbk"));
					String s = "";
					while ((s = br.readLine()) != null) {
						html = html + s + "\r\n";
					}
				} catch (IOException e) {
					System.err.println("Get Data IOException");
					html = "";
					getMethod.releaseConnection();
					continue;
				}
				
	
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.err.println("Close IOException");
					}
				}
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.err.println("Close IOException");
					}
				}
				break;
			}else{
				System.err.println("HttpStatus: "+statusCode);
				i++;
			}
		}

		
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
			bw.append(str);
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
