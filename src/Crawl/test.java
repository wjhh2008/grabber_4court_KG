package Crawl;



public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SimpleCrawler si = new SimpleCrawler();
		String html = si.methodPa("http://czsrdgj.cn.china.cn/company-information.html");
		//System.out.println(html);
		System.out.println("--------match begin--------\n"+SimpleCrawler.matchstr(html, 
"法定代表人/负责人</th>[\\s\\S]*公司成立时间", 9, 6
		).replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim());
		System.out.println("--------match end  --------");
		
	}

}
