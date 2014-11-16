package Crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;





public class chinagov {
	
	private static BufferedReader urlfile;

	public static void main(String[] args) throws IOException {
		SimpleCrawler si = new SimpleCrawler("utf-8");
		OutputFormat format = OutputFormat.createCompactFormat();//缩减型格式
					 format.setEncoding("UTF-8");//设置文件内部文字的编码
					// format.setExpandEmptyElements(true);
					// format.setTrimText(false);
					// format.setIndent(true);      // 设置是否缩进
					// format.setIndent("   ");     // 以空格方式实现缩进
					// format.setNewlines(true);    // 设置是否换行
		File file = new File("govurl.txt");
		urlfile = new BufferedReader(new FileReader(file));
		String url;
		PrintStream p = new PrintStream(System.out);
		int line = 0,start = 1;
		Document doc = null;
		Element cs = null;
		while ((url = urlfile.readLine()) != null){
			line++;
			p.printf("Line %-7d ...", line); //16
			if (line==start){
				doc = DocumentHelper.createDocument();
				cs = doc.addElement("consultation");
			}
			String html = si.methodPa(url);
			Element lawcase = cs.addElement("lawcase");
			lawcase.addElement("title").addText(SimpleCrawler.matchstr(html, "wsTitle\">[^<]*</div>", 9, 6));
			String ws = SimpleCrawler.matchstr(html, "id=DocArea[\\S\\s]*border:1px solid #CCC", 11, 33);
			String [] wsarr = SimpleCrawler.matchstr(ws, "<div [^>]*>[^<]*</div>", 0, 0).split("</div>");
			String content = "";
			String finish = "";
			for (int i=0;i<wsarr.length-1;i++){
				String p1 = wsarr[i].replaceAll("<(\\S*?)[^>]*>|</>|<.*? />|", "").trim();
				switch (i){
				case 0:
					lawcase.addElement("court").addText(p1);
					break;
				case 1:
					lawcase.addElement("class").addText(p1);
					break;
				case 2:
					lawcase.addElement("num").addText(p1);
					break;
				default:
					if (wsarr[i].contains("text-align: right")){
						finish += p1;
					}else{
						content += p1;
					}
					break;
				}
			}
			lawcase.addElement("content").addText(content);
			lawcase.addElement("finish").addText(finish);
			p.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
			
			if (line % 1000==0){
				p.print("Writing XML page "+start+"->"+line);
				try {

					File outfile = new File("gov"+start+"->"+line+".xml");
					XMLWriter XMLdoc = new XMLWriter(new FileWriter(outfile),format);
					XMLdoc.write(doc);
					XMLdoc.close();
					start = line+1;
					p.println(" done.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		if (line>=start){
			p.print("Writing XML page "+start+"->"+line);
			try {

				File outfile = new File("gov"+start+"->"+line+".xml");
				XMLWriter XMLdoc = new XMLWriter(new FileWriter(outfile),format);
				XMLdoc.write(doc);
				XMLdoc.close();
				start = line+1;
				p.println(" done.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		urlfile.close();
		
		

	}

}
