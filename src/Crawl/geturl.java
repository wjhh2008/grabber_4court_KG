package Crawl;





public class geturl {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int startPage = Integer.valueOf(args[0]);
		int endPage = Integer.valueOf(args[1]);
		
		SimpleCrawler si = new SimpleCrawler();
		String s = new String("http://www.court.gov.cn/extension/search.htm?keyword=%E5%85%AC%E5%8F%B8&caseCode=&wenshuanyou=&anjianleixing=&docsourcename=&court=&beginDate=2004-01-01&endDate=2014-10-22&adv=1&orderby=&order=&page=");
		int page=1;
		for (page=startPage;page<=endPage;page++){
			String html = null;
			System.out.println("-->  page"+page);
			html = si.methodPa(s+page);
			
			String url = SimpleCrawler.getUrl(html);
			SimpleCrawler.writeInFile("url.txt", url);
			if (page%100 == 0) {
				SimpleCrawler.writeInFile("log.txt","page "+(page-100)+" - page "+page+" writed\n");
			}
			
			
		}
		SimpleCrawler.writeInFile("log.txt","page "+startPage+" - page "+endPage+" writed and job finished\n");
		
		
	}

}
