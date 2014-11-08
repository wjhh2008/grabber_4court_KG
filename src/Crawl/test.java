package Crawl;

import java.io.PrintStream;



public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PrintStream p = System.out;
		SimpleCrawler si = new SimpleCrawler();
		String html = si.methodPa("http://puluody.cn.china.cn/supply");
		String subtypehtml = html;
		System.out.println("--------match begin--------");
		String tmp = SimpleCrawler.matchstr(subtypehtml,"<ul class=\"fl-clr\">[\\s\\S]*<div class=\"pros-line\">", 0, 0).replaceAll("<([^a]\\S*?)[^>]*>|</>|<.*? />|", "").trim();
		p.println(tmp);
		
		System.out.println("--------match end  --------");
		
	}

}
