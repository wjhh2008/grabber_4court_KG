package Crawl;



public class test {

	public static void main(String[] args) {
		
		SimpleCrawler si = new SimpleCrawler();
		String html = si.methodPa("http://szhuimeikj.cn.china.cn/supply/3187105953.html");
		String productpage = html;
		System.out.println("--------match begin--------");
		
		String paragraph = SimpleCrawler.matchstr(productpage, "<!-- mod-detail end -->[\\s\\S]*<div class=\"text-detail\">", 0, 0);
		//System.out.println(paragraph);
		String[] infoa = SimpleCrawler.matchstr(paragraph,"<th>.[^<]*</th>", 0, 0).replaceAll("&nbsp;", "").replaceAll("<([^a][^\\s<>]*?)[^><]*>|</>|<.*? />|", "").trim().replaceAll("：", "").split("\\n+");
		String[] infob = SimpleCrawler.matchstr(paragraph,"<td>.[^<]*</td>", 0, 0).replaceAll("&nbsp;", "").replaceAll("<([^a][^\\s<>]*?)[^><]*>|</>|<.*? />|", "").trim().replaceAll("：", "").split("\\n+");
		
		for (int k=0;k<infoa.length;k++){
			System.out.print(infoa[k]+" | ");
		}
		System.out.println();
		for (int k=0;k<infob.length;k++){
			System.out.print(infob[k]+" | ");
		}
		System.out.println();
		System.out.println("--------match end  --------");
		
	}

}
