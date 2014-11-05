package Crawl;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class geturl {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter writer = null;
		
		writer = new BufferedWriter(new FileWriter(new File("url.txt"), false));
		
		int startPage = Integer.valueOf(args[0]);
		int endPage = Integer.valueOf(args[1]);
		
		SimpleCrawler si = new SimpleCrawler();
		String s = new String("http://www.court.gov.cn/extension/search.htm?keyword=%E5%85%AC%E5%8F%B8&caseCode=&wenshuanyou=&anjianleixing=&docsourcename=&court=&beginDate=2004-01-01&endDate=2014-10-22&adv=1&orderby=&order=&page=");
		int page=1;
		for (page=startPage;page<=endPage;page++){
			String html = null;
			
			html = si.methodPa(s+page);
			
			System.out.println("-->  page"+page+'\n');
			String url = SimpleCrawler.getUrl(html);
			try {
				writer.write(url,0,url.length());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int time = (int) Math.rint(Math.random()
					* (Paremeters.ENDTIME - Paremeters.STARTTIME)
					+ Paremeters.STARTTIME);
			
			try {
				Thread.sleep(time * 100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writer.flush();
			
		}
		writer.close();
		
		
	}

}
