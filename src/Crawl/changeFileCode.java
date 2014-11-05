package Crawl;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class changeFileCode {
	// ��ȡ���ļ�  
    private String fileIn;  
  
    // ��ȡ�r�ļ��õı���  
    private String fileInEn;  
    
    // д�����ļ�  
    private String fileOut;  
  
    // д���r�ļ��õı���  
    private String fileOutEn;  
  
    /**
     * ��ȡԴ�ļ��ı���
     * @param filePath Դ�ļ����ڵľ���·��
     * @return
     */
    public  String getFileEnCode(String filePath) {
		InputStream inputStream = null;
		String code = ""; 
		try {
			inputStream = new FileInputStream(filePath);  
	        byte[] head = new byte[3];  
	        inputStream.read(head);   
	         
	   
	            code = "gb2312";  
	        if (head[0] == -1 && head[1] == -2 )  
	            code = "UTF-16";  
	        if (head[0] == -2 && head[1] == -1 )  
	            code = "Unicode";  
	        if(head[0]==-17 && head[1]==-69 && head[2] ==-65)  
	            code = "UTF-8";  
	          
	        System.out.println(code);
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return code;
	}
  
    public void setFileIn(String fileInPath, String fileInEncoding) {  
        this.setFileIn(fileInPath);  
        this.setFileInEn(fileInEncoding);  
    }  
  
    public void setFileOut(String fileOutPath, String fileOutEncoding) {  
        this.setFileOut(fileOutPath);  
        this.setFileOutEn(fileOutEncoding);  
    }  
      
    public void start() {     
        String str = this.read(fileIn,fileInEn);     
        this.write(fileOut, fileOutEn, str);     
    }     
      
    /** 
     * ���ļ� 
     *  
     * @param fileName 
     * @param encoding 
     */  
    private String read(String fileName, String encoding) {  
        try {  
            BufferedReader in = new BufferedReader(new InputStreamReader(  
                    new FileInputStream(fileName), encoding));  
  
            String string = "";  
            String str = "";  
            while ((str = in.readLine()) != null) {  
                string += str + "\n";  
            }  
            in.close();  
            return string;  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        return "";  
    }  
  
    /** 
     * д�ļ� 
     *  
     * @param fileName 
     *            �µ��ļ��� 
     * @param encoding 
     *            д�����ļ��ı��뷽ʽ 
     * @param str 
     */  
    private void write(String fileName, String encoding, String str) {  
        try {  
            Writer out = new BufferedWriter(new OutputStreamWriter(  
                    new FileOutputStream(fileName), encoding));  
            out.write(str);  
            out.close();  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
    }  
  
    public String getFileIn() {  
        return fileIn;  
    }  
  
    public void setFileIn(String fileIn) {  
        this.fileIn = fileIn;  
    }  
  
    public String getFileInEn() {  
        return fileInEn;  
    }  
  
    public void setFileInEn(String fileInEn) {  
        this.fileInEn = fileInEn;  
    }  
  
    public String getFileOut() {  
        return fileOut;  
    }  
  
    public void setFileOut(String fileOut) {  
        this.fileOut = fileOut;  
    }  
  
    public String getFileOutEn() {  
        return fileOutEn;  
    }  
  
    public void setFileOutEn(String fileOutEn) {  
        this.fileOutEn = fileOutEn;  
    }  
  
    
    public static void main(String[] args) { 
    	String InputFilePath = "D:\\��Ժ\\��Ժ����\\";
    	int i = 1;
    	while(i <= 1){
    	String FileName = "101173-106704.xml";
    	
    	changeFileCode changeFileCode1 = new changeFileCode();  
        String path = InputFilePath+FileName;  
        File file = new File(path);  
        String fileCode = changeFileCode1.getFileEnCode(path); 
        
        if(fileCode!=null && !"".equals(fileCode)) {
	        changeFileCode1.setFileIn(file.getPath(), fileCode);//����ļ�����ΪANSI��GBK�����������UTF-8��UTF-8����  
	        changeFileCode1.setFileOut(file.getPath(), "UTF-8");//UTF-8���ļ�����ΪUTF-8�� ���ΪGBK������ΪANSI  
	        changeFileCode1.start();  
        }
        i++;
    } 
    }
}  
