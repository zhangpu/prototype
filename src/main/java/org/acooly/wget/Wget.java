package org.acooly.wget;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;

public class Wget {

	private String localPath = "D:\\temp\\wangyou";
	
	private volatile String host;
	private volatile String root;
	
	HttpClient client = new HttpClient();
	
	public static void main(String[] args) throws Exception{
		String url = "http://www.92zw.com/files/article/html/47/47718/index.html";
		Wget wget = new Wget();
		//System.out.println(wget.getPage(url));
		//wget.savePageClear("http://www.92zw.com/files/article/html/47/47718/4638264.html");
		
		PrintWriter out = new PrintWriter(new File("D:\\temp\\wangyou\\wangyou.txt"));
		List<String> files = wget.getPage(url);
		for(String file:files){
			wget.savePageClear("http://www.92zw.com/files/article/html/47/47718/"+file,out);
		}
		out.close();
		//System.out.println(files.size());
		//System.out.println(files);
	}
	
	List<String> getPage(String url){
		List<String> files = new LinkedList<String>();
		String content = "";
		HttpMethod method = new GetMethod(url);
		try {
			int status = client.executeMethod(method);
			if(status == HttpStatus.SC_OK){
				BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
				String buf = null;
				while((buf = reader.readLine()) != null){
					buf = StringUtils.trimToEmpty(buf);
					if(buf.startsWith("<a")){
						String fileName = buf.substring(buf.indexOf("\"")+1);
						fileName = fileName.substring(0,fileName.indexOf("\""));
						files.add(fileName);
					}
				}
				reader.close();
			}
			
		} catch (Exception e) {
			System.out.println("ÍøÂç´íÎó:"+e.getMessage());
		}
		return files;
	}
	
	
	void savePageClear(String url,PrintWriter out){
		String name = URIUtil.getName(url);
		HttpMethod method = new GetMethod(url);
		BufferedReader reader = null;
		try {
			int status = client.executeMethod(method);
			if(status == HttpStatus.SC_OK){
				reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
				
				String buf = null;
				String content = "";
				while((buf = reader.readLine()) != null){
					if(buf.indexOf("newstitle>") != -1){
						content = buf.substring(buf.indexOf("newstitle>") + 10,buf.indexOf("</SPAN>")) + "\r\n";
					}
					int start = buf.indexOf("<DIV id=\"content\">");
					if(start != -1){
						content += buf;
						content += reader.readLine();
						break;
					}

				}
				content = StringUtils.replace(content, "<br />", "\n");
				content = StringUtils.replace(content, "<td valign=\"top\">", "");
				content = StringUtils.replace(content, "</div></DIV></td>", "");
				content = StringUtils.replace(content, "&nbsp;", " ");
				content = StringUtils.replace(content, "<DIV id=\"content\">", "");
				content = StringUtils.replace(content, "</tr>", "");
				content = content.substring(0,content.indexOf("<br>"));
				out.println(content);
			}
			System.out.println("saved: "+url);
		} catch (Exception e) {
			System.out.println("error: "+url);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}			
		}
	}
	
	
	void savePage(String url){
		String name = URIUtil.getName(url);
		HttpMethod method = new GetMethod(url);
		BufferedReader reader = null;
		PrintWriter out = null;
		try {
			int status = client.executeMethod(method);
			if(status == HttpStatus.SC_OK){
				reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
				out = new PrintWriter(new File(localPath,name));
				String buf = null;
				while((buf = reader.readLine()) != null){
					out.println(buf);
				}
			}
			System.out.println("saved: "+url);
		} catch (Exception e) {
			System.out.println("error: "+url);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			if(out != null){
				out.close();
			}
			
		}
	}
	
	void init(String url){
		
	}
	
	String getFolder(String url){
		String[] s = url.split("/");
		return s[s.length-2];
	}
	
}
