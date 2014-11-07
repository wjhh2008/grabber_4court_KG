package Crawl;



public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SimpleCrawler si = new SimpleCrawler();
		String html = si.methodPa("http://czsrdgj.cn.china.cn/supply");
		//System.out.println(html);
		System.out.println("--------match begin--------\n");
		String[] productgrp = SimpleCrawler.matchstr(html, 
				
		
"<ul class=\"fl-clr\">[\\s\\S]*<div class=\"pros-line\">", 0, 0


		).replaceAll("<([^a]\\S*?)[^>]*>|</>|<.*? />|", "").trim().split("\\s\\s+");
		for (int i=0;i<productgrp.length;i++){
			System.out.println("-->productgrp "+i);
			//System.out.println("type = "+SimpleCrawler.matchstr(productgrp[i],">.*\\(",1,1));
			//System.out.println("type = "+SimpleCrawler.matchstr(productgrp[i],"href=.*rel",6,5));
			String pdurl = SimpleCrawler.matchstr(productgrp[i],"href=.*rel",6,5);
			//int num = 0;
			while(true){
				String subtypehtml = si.methodPa(pdurl);
				String[] producturl = SimpleCrawler.matchstr(subtypehtml,"class=\"desc\">\\s*<a.*?>",13,0).trim().split("\\s\\s+");
				for (int j=0;j<producturl.length;j++){
					String productpage = si.methodPa(SimpleCrawler.matchstr(producturl[j],"href=.*title=",6,8));
					//num++;
				}
				
				pdurl = SimpleCrawler.matchstr(SimpleCrawler.matchstr(subtypehtml,"href\\S*\\s*\\S*rollPage.*>下一页",6,0),"^.*html",0,0);
				
				if (pdurl.equals("")) break;
			}
			//System.out.println(num);
			
		}
		
		System.out.println("--------match end  --------");
		
	}

}
