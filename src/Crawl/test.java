package Crawl;



public class test {

	public static void main(String[] args) {
		
		SimpleCrawler si = new SimpleCrawler();
		String productpage = si.methodPa("http://904741689.cn.china.cn/supply/2322353662.html");
		System.out.println("--------match begin--------");
		
		
		String paragraph = SimpleCrawler.matchstr(productpage, "<!-- mod-detail end -->[\\s\\S]*<div class=\"text-detail\">", 0, 0);
		String[] infoa = SimpleCrawler.matchstr(paragraph,"<th>.[^<]*</th>", 0, 0).replaceAll("&nbsp;", "").replaceAll("<([^a][^\\s<>]*?)[^><]*>|</>|<.*? />|", "").trim().replaceAll("：", "").split("\\n+");
		String[] infob = SimpleCrawler.matchstr(paragraph,"<td>.[^<]*</td>", 0, 0).replaceAll("&nbsp;", "").replaceAll("<([^a][^\\s<>]*?)[^><]*>|</>|<.*? />|", "").trim().replaceAll("：", "").split("\\n+");
		
		for (int k=0;k<Math.min(infoa.length,infob.length);k++){
			System.out.println("line--> "+infoa[k].replaceAll("[\\x00-\\xff]", "")+" "+infob[k]);
		}
		
		
		System.out.println("--------match end  --------");
		
	}

}
