package Crawl;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;


public class SimpleCrawler{  
	
	String charset;
	static double state = 0;
	static MultiThreadedHttpConnectionManager connectionManager =
			new MultiThreadedHttpConnectionManager();
	private static long lastDate;
	private static String infilename;
	public SimpleCrawler(){
		this.charset = "gbk";
	}
	public SimpleCrawler(String charset){
		this.charset = charset;
	}
	private static void update(){
		long nowDate = System.currentTimeMillis();
		if ((nowDate-lastDate)>1000*60*10){
			lastDate = nowDate;
			(new Thread(new PushState(infilename))).start();  
		}
	}
	public static void main(String[] args){

		SimpleCrawler si = new SimpleCrawler();
		OutputFormat format = OutputFormat.createPrettyPrint();//缩减型格式
			  		 format.setEncoding("UTF-8");//设置文件内部文字的编码
			 		 format.setExpandEmptyElements(true);
			 		 format.setTrimText(false);
					 format.setIndent(true);      // 设置是否缩进
					 format.setIndent("   ");     // 以空格方式实现缩进
					 format.setNewlines(true);    // 设置是否换行
		infilename = args[0];
		File filedir = new File(args[0]);
		if (!filedir.exists()) {
			System.err.println("Can't Find dir : "+filedir);
			return;
		}
		int pageesp = 0;
		if (args.length>1){
			pageesp = Integer.parseInt(args[1]);
		}
		String [] filelist = filedir.list();
		//int txtdone = 0;
		//int txtdo = 0;
		int totlepages = 0;
		int pagedone = 0;
		for (int n=0;n<filelist.length;n++){
			if (matchstr(filelist[n],"\\.txt",0,0).equals("")){
				//txtdone++;
			}else{
				FileReader filerd = null;
				try {
					filerd = new FileReader(new File(filedir+File.separator+filelist[n]));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				LineNumberReader lnr = new LineNumberReader(filerd);
				try {
					while ((lnr.readLine()) != null) {
						totlepages++;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("Totle "+totlepages+" Pages.");
		//state = 100*(txtdo+txtdone)*1.00/filelist.length;
		//(new Thread(new PushState(args[0]))).start();  
        
		lastDate = System.currentTimeMillis();
		for (int n=0;n<filelist.length;n++){
			String filename = filelist[n];
			int startPage = 1;
			int page = 0;
			if (matchstr(filename,"\\.txt",0,0).equals("")){
				continue;
			}
			//txtdo++;
			startPage = startPage + pageesp;
			pageesp = 0;
			File file = new File(filedir+File.separator+filename);
			File outdir  = new File(filedir+File.separator+filename.replaceAll("\\.[\\S]*$", ""));
			if (!(outdir.exists()||outdir.mkdir())) {
				System.err.println("Can't Make dir : "+outdir);
				return;
			}
			
	
			InputStream is = null;
			BufferedReader br = null;
			String url;
			
			Document comp = null;
			Document sup = null;
			Element companys = null;
			Element shops = null;
			try {
				is = new BufferedInputStream(new FileInputStream(file));
				br = new BufferedReader(new InputStreamReader(is, "utf-8"));
				while ((url = br.readLine()) != null) {
					page++;
					pagedone++;
					state = 100.00*pagedone/totlepages;
					if (page<startPage){
						continue;
					}
					
					System.out.printf("[%.1f",state);
					System.out.print("%] ");
					System.out.println("Start \""+filename+"\"  Page "+page+ "  ...");
					
					url = matchstr(url,"[a-zA-z]+://[^\\s]*",0,0).trim();
					if (page==startPage){
						comp = DocumentHelper.createDocument();
						sup = DocumentHelper.createDocument();
						companys = comp.addElement("Companys");
						shops = sup.addElement("Shops");
					}
					//System.out.printf("   Page %4d",page);
			/*					
					int time = (int) Math.rint(Math.random()
							* (Paremeters.ENDTIME - Paremeters.STARTTIME)
							+ Paremeters.STARTTIME);
					Thread.sleep(time * 100);
					System.out.println("Thread sleeping 0."+time+" s ..");
			 */
					String mainhtml = si.methodPa(url);
					if(mainhtml.equals("")){
						System.out.println("(*)Page"+page+" Html is null...continue next...");
						continue;
					}
					
					Element company = companys.addElement("Company");
					company.addElement("Name").addText(matchstr(mainhtml, "class=\"pdmd\">\\s*\\S*\\s*<", 13, 1).trim());
					company.addElement("WebSite").addText(url);
					
					String compinfo = si.methodPa(url+"/company-information.html");
					company.addElement("Info").addText(matchstr(compinfo,"class=\"company-info\">\\s*<p>.[^<]*</p>", 24, 4).trim());
					company.addElement("Business").addText(matchstr(compinfo, "主营产品或服务</th>\\s*<td.*>\\s*<a.*>[\\s\\S]*</a.*>\\s*</td.*>\\s*<th.*>主营行业", 7, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("BusinessRange").addText(matchstr(compinfo, "主营行业</th>\\s*<td.*>\\s*<a.*>[\\s\\S]*</a.*>\\s*</td.*>", 4, 0).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("BusinessModel").addText(matchstr(compinfo, "经营模式[\\s\\S]*主要客户群", 4, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("RegisteredCapital").addText(matchstr(compinfo, "注册资本</th>[\\s\\S]*公司注册地", 4, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("FoundingTime").addText(matchstr(compinfo, "公司成立时间</th>[\\s\\S]*主营产品或服务", 6, 7).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("LocatedIn").addText(matchstr(compinfo,"公司注册地</th>[\\s\\S]*公司注册号", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("OrgType").addText(matchstr(compinfo,"企业类型</th>[\\s\\S]*注册资本", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("ArtificialPersion").addText(matchstr(compinfo,"法定代表人/负责人</th>[\\s\\S]*公司成立时间", 9, 6).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("RegisterNumber").addText(matchstr(compinfo,"公司注册号</th>[\\s\\S]*<th>经营范围", 5, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("BusinessRange").addText(matchstr(compinfo,"经营范围</th>[\\s\\S]*登记机关", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("RegisterAgency").addText(matchstr(compinfo,"登记机关</th>[\\s\\S]*最近年检时间", 4, 6).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("SalesArea").addText(matchstr(compinfo,"主要市场</th>[\\s\\S]*主要经营", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Customer").addText(matchstr(compinfo,"主要客户群</th>[\\s\\S]*主要市场", 5, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Turnover").addText(matchstr(compinfo,"年营业额</th>[\\s\\S]*年出口额", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("BrandName").addText(matchstr(compinfo,"品牌名称</th>[\\s\\S]*经营模式", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("WareHouse").addText(matchstr(compinfo,"主要经营地点</th>[\\s\\S]*年营业额", 6, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Staff").addText(matchstr(compinfo,"员工人数</th>[\\s\\S]*厂房面积", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Exports").addText(matchstr(compinfo,"年出口额</th>[\\s\\S]*年进口额", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Inports").addText(matchstr(compinfo,"年进口额</th>[\\s\\S]*质量控制", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("OEMAgent").addText(matchstr(compinfo,"是否提供OEM代加工</th>[^<]*<td>[^<]*", 10, 0).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Homepage").addText(matchstr(compinfo,"公司网址</th>[\\s\\S]*是否", 4, 2).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Qualityctl").addText(matchstr(compinfo,"质量控制</th>[\\s\\S]*管理体系认证", 4, 6).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("ManageCertification").addText(matchstr(compinfo,"管理体系认证</th>[\\s\\S]*员工人数", 6, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|&lt;t", "").trim());
					company.addElement("FactoryArea").addText(matchstr(compinfo,"厂房面积</th>[\\s\\S]*公司网址", 4, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("LastCheckTime").addText(matchstr(compinfo,"最近年检时间</th>[\\s\\S]*营业期限", 6, 4).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("ContactName").addText(matchstr(compinfo,"联系人:[\\s\\S]*职", 4, 1).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Tel").addText(matchstr(compinfo,"电 话 :[\\s\\S]*手 机 :", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Fax").addText(matchstr(compinfo,"传 真 :[\\s\\S]*地 址 :", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					company.addElement("Address").addText(matchstr(compinfo,"地 址 :[\\s\\S]*客 服 :", 5, 5).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					
					try{
						String credithtml = si.methodPa(url+"/credit-profile.html");
						org.jsoup.select.Elements content = Jsoup.parse(credithtml).getElementsByAttributeValue("class", "rig-bd fl-clr");
						if (!content.isEmpty()){
							org.jsoup.nodes.Element cat = content.first();
							company.addElement("CreditIndex").addText(cat.getElementsByAttributeValue("class", "red-mark").text().trim());
							org.jsoup.select.Elements tcats = cat.getElementsByAttributeValue("class", "sp-bd");
							if (!tcats.isEmpty()){
								org.jsoup.select.Elements cats = tcats.first().getElementsByTag("tr");
								Element tmp = null;
								org.jsoup.nodes.Element line = null;
								line = cats.get(0);
								tmp = company.addElement("VIPCertification");
								tmp.addElement("Score").addText(line.child(1).text().trim());
								tmp.addElement("Time").addText(line.child(2).text().trim());
								line = cats.get(1);
								tmp = company.addElement("VIPQualifications");
								tmp.addElement("Score").addText(line.child(1).text().trim());
								tmp.addElement("Years").addText(line.child(2).text().trim());
								line = cats.get(2);
								tmp = company.addElement("IntegrityManagerment");
								tmp.addElement("Score").addText(line.child(1).text().trim());
								tmp.addElement("Month").addText(line.child(2).text().trim());
								line = cats.get(3);
								tmp = company.addElement("BusinissLicense");
								tmp.addElement("Score").addText(line.child(1).text().trim());
								line = cats.get(4);
								tmp = company.addElement("HonorCertification");
								tmp.addElement("Score").addText(line.child(1).text().trim());
							}
						}
					}catch(NullPointerException|IndexOutOfBoundsException e){
						System.err.println("Credit-profile Get Err");
					}
					
					String supplyhtml = si.methodPa(url+"/supply");
					//company.addElement("").addText(matchstr(compinfo,).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					//company.addElement("").addText(matchstr(compinfo,).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
					
					Element shop = shops.addElement("Shop");
					String[] productgrp = matchstr(supplyhtml, "<ul class=\"fl-clr\">[\\s\\S]*<div class=\"pros-line\">", 0, 0).replaceAll("<([^a]\\S*?)[^>]*>|</>|<.*? />|", "").trim().split("\\s\\s+");
					int total = 0;
					//System.out.print("      ");
					
					for (int i=0;i<productgrp.length;i++){		
						//System.err.println("Product grp: "+i+" "+productgrp[i]);
						String ptypename = "";
						String pdurl = "";
						if (i==0&&productgrp[i].equals("")){
							pdurl = url+"/supply";
						}else{
							ptypename = matchstr(productgrp[i],">[^<]*\\(",1,1).trim();
							pdurl = matchstr(productgrp[i],"http[\\S]*html",0,0).trim();
						}
						Element producttype = shop.addAttribute("url", url.trim()).addElement("Producttype").addAttribute("name", ptypename);
						
						int num = 0;
						while(true){
							String subtypehtml = si.methodPa(pdurl);
							String[] producturl = SimpleCrawler.matchstr(subtypehtml,"class=\"desc\">\\s*<a.*?>",13,0).trim().split(">[\\s]*<");
							for (int j=0;j<producturl.length;j++){
								update();
								num++;
								System.out.printf("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
								System.out.printf("    Doing No.%-4d in Grp %-2d.",num,i+1);
								String purl = SimpleCrawler.matchstr(producturl[j],"href=\"http[\\S]*html",6,0).trim();
								if (purl.equals("")) System.err.println("Oh! empty URL from "+url+"/supply"+" @page_"+(i+1)+" num_"+(num+1)+" detail:"+producturl[j]);
								Element product = producttype.addElement("Product");
								product.addElement("Url").addText(purl);
								product.addElement("Shopurl").addText(url);
								product.addElement("Type").addText(ptypename);
								String productpage = si.methodPa(purl);
								
								product.addElement("Name").addText(matchstr(productpage,"detailBox-hd\">[\\s]*<[^<]*>[^<]*</[^<]*>", 14, 0).trim().replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", ""));
								product.addElement("Wholesale").addText(matchstr(productpage, "price-box[\\S\\s]*mro-main-attr", 11, 25).replaceAll("<([^a]\\S*?)[^>]*>|</>|<.*? />|", "").trim().replaceAll("\\s\\s+", " "));
								product.addElement("SendDate").addText(matchstr(productpage,"发&nbsp;货&nbsp;期[\\S\\s]*所&nbsp;在&nbsp;地", 16, 15).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
								product.addElement("Location").addText(matchstr(productpage,"所&nbsp;在&nbsp;地[^<]*</[^>]*>[^<]*<[^>]*>[^<]*</[^>]*>", 16, 0).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim().replaceAll("&nbsp;", " ").replaceAll("[\\s]*", ""));
								product.addElement("Telephone").addText(matchstr(productpage,"手&nbsp;&nbsp;&nbsp;&nbsp;机[\\S\\s]*order-btn", 27, 21).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
								Element disinfo = product.addElement("Disinfo");
								//String paragraph = SimpleCrawler.matchstr(productpage, "<!-- mod-detail end -->[\\s\\S]*<div class=\"text-detail\">", 0, 0);
								//String[] infoa = SimpleCrawler.matchstr(paragraph,"<th>.[^<]*</th>", 0, 0).replaceAll("&nbsp;", "").replaceAll("<([^a][^\\s<>]*?)[^><]*>|</>|<.*? />|", "").trim().replaceAll("：", "").split("\\n+");
								//String[] infob = SimpleCrawler.matchstr(paragraph,"<td>.[^<]*</td>", 0, 0).replaceAll("&nbsp;", "").replaceAll("<([^a][^\\s<>]*?)[^><]*>|</>|<.*? />|", "").trim().replaceAll("：", "").split("\\n+");
								org.jsoup.nodes.Document paged = Jsoup.parse(productpage);
								
								org.jsoup.select.Elements cinfo = paged.getElementsByAttributeValue("class", "attr-list");
								if (!cinfo.isEmpty()){
									org.jsoup.nodes.Element catinfo =cinfo.first();
									org.jsoup.select.Elements infosa = catinfo.getElementsByTag("th");
									org.jsoup.select.Elements infosb = catinfo.getElementsByTag("td");
									for (int k=0;k<Math.min(infosa.size(), infosb.size());k++){
										if (infosa.get(k).text().replaceAll("[\\x00-\\xff℃：]", "").trim().length()<1) continue;
										disinfo.addElement(infosa.get(k).text().replaceAll("[\\x00-\\xff℃：]", "").trim()).addText(infosb.get(k).text().trim());
									}
								}
								
								Element relates = product.addElement("Relates");
								org.jsoup.select.Elements recommend = paged.getElementsByAttributeValue("class", "recomened-detail");
								if (!recommend.isEmpty()){
									org.jsoup.select.Elements recommends = paged.getElementsByAttributeValue("class", "recomened-detail").first().getElementsByTag("li");
									for (org.jsoup.nodes.Element rec:recommends){
										relates.addElement("Relateurl").addText(rec.getElementsByTag("a").first().attr("href"));
										relates.addElement("Relatename").addText(rec.getElementsByTag("a").first().attr("title"));
									}
								}
								
								//console.printf("\b\b\b\b\b% 5d", num);
							}
							
							pdurl = SimpleCrawler.matchstr(SimpleCrawler.matchstr(subtypehtml,"<[^>]*>下一页",6,0),"http[\\S]*html",0,0).trim();
							
							if (pdurl.equals("")) break;
						}
						total = total + num;
						
					}
					System.out.printf("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
					System.out.printf("    Done total %d in Grp %d.\n",total,productgrp.length);
					//System.out.println("   shops info finish crawling.");
					
					if (page % Paremeters.MAX_ITEM == 0){
						String DIRR = outdir+File.separator;
						String PAGE = startPage+"-" + page;
				        startPage = page+1;
						try {
	
							XMLWriter XMLcompanys = new XMLWriter(new FileWriter(new File(DIRR+"companys"+PAGE+".xml")),format);
							XMLWriter XMLshops = new XMLWriter(new FileWriter(new File(DIRR+"shops"+PAGE+".xml")),format);
							XMLcompanys.write(comp);
							XMLcompanys.close();
							XMLshops.write(sup);
							XMLshops.close();

							System.out.println(filename+" "+PAGE + " XML done");
						} catch (IOException e) {
							System.out.println(e.getMessage());
						}
						
					}
					//page++;
				}
				
				if (page>=startPage){
					String DIRR = outdir+File.separator;
					String PAGE = startPage+"-" + page;
			        startPage = page+1;
					try {

						XMLWriter XMLcompanys = new XMLWriter(new FileWriter(new File(DIRR+"companys"+PAGE+".xml")),format);
						XMLWriter XMLshops = new XMLWriter(new FileWriter(new File(DIRR+"shops"+PAGE+".xml")),format);
						XMLcompanys.write(comp);
						XMLcompanys.close();
						XMLshops.write(sup);
						XMLshops.close();
						System.out.println(filename+" "+PAGE + " XML done.");
					} catch (IOException e) {
						System.out.println(e.getMessage());
					}
					
				}
				br.close();
				is.close();
	
			} catch (IOException e) {
				e.printStackTrace();

			}
			file.delete();
		}


	}
	
	public static String matchstr(String html, String regexp, int stoffs, int enoffs){
		//System.out.println("regexp: "+regexp);
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
		if (!strURL.matches("http[\\S]*")){
			//System.out.println("URL enpty!");
			return "";	
		}
		System.getProperties().setProperty("proxySet", "true");// 设置代理IP，防止ip被封
		//for cookies
		DefaultHttpParams.getDefaultParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		HttpClient httpClient =  new HttpClient(connectionManager);
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"));
		//headers.add(new Header("Cookie", "china_uv=66365e1884; Hm_lvt_3cfaa114cca90dbeb8cf6908074f92ef=1415327823; Hm_lpvt_3cfaa114cca90dbeb8cf6908074f92ef=1415328358; _ga=GA1.2.1863492324.1415327823; 9329132056=30020"));
		if (strURL.contains("company-information.html")) headers.add(new Header("Referer", strURL));
		
		httpClient.getHostConfiguration().getParams().setParameter("http.default-headers", headers);  // 尽量添加headers，模拟浏览器
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(0, false));
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000); 
		httpClient.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);

		//System.out.println(strURL);
		BufferedReader br = null;
		InputStream input = null;
		GetMethod getMethod = null;
		String html = "";
		int networkdowncount = 0;
		for (int i=0;i<3;){
			networkdowncount = networkdowncount+1-i;
			int wait = networkdowncount/3;
			if (wait >60) wait = 60;
			if (wait>0){
				try {
					System.err.println("waitting "+wait*0.5);
					Thread.sleep(wait*500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			getMethod = new GetMethod(strURL);
			//getMethod.setRequestHeader("Connection", "close"); 
			
			int statusCode = 0;
			try {
				statusCode = httpClient.executeMethod(getMethod);
				networkdowncount = 0;
				if (statusCode == HttpStatus.SC_OK) {
					input = getMethod.getResponseBodyAsStream();
					br = new BufferedReader(new InputStreamReader(input,charset));
					String s = "";
					while ((s = br.readLine()) != null) {
						html = html + s + "\r\n";
					}
					br.close();
					input.close();
					break;
				}else{
		
					if (statusCode!=503){
						System.err.println(statusCode);
						if (statusCode==404) i++;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (HttpException e) {
				System.err.println("NETerr");
			} catch (IOException e) {
				System.err.println("NETIOerr");
			} finally{
				getMethod.releaseConnection();
			}
		}
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
			e.printStackTrace();
			return false;
		}
	}
	
	
 }  
