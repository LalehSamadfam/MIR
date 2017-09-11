import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PageHandler {

	private String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36"; 
	
	private String REF_URL = "https://www.researchgate.net/publicliterature.PublicationCitationsList.html?dbw=true&publicationUid="+"&showContexts=true&showCitationsSorter=true&showAbstract=true&showType=true&showPublicationPreview=true&swapJournalAndAuthorPositions=false";
	public List<String> handleCited(String id, JSONObject obj ){
    	StringBuffer result = new StringBuffer();
    	List<String> links = null;
    	
		 try
	        {
	            HttpClient client = HttpClientBuilder.create().build();
	            HttpGet request = new HttpGet(citedUrl(id));
	        	request.addHeader("User-Agent", USER_AGENT);
	        	request.addHeader("authority", "www.researchgate.net");
	        	request.addHeader("x-requested-with","XMLHttpRequest");
	        	request.addHeader("accept", "application/json");
	        	
	        	HttpResponse response = client.execute(request);
	        
	        	BufferedReader rd = new BufferedReader(
	        			new InputStreamReader(response.getEntity().getContent()));
	        	String line = "";
	        	while ((line = rd.readLine()) != null) {
	        		result.append(line);
	        	}
	        	
	            links = getCitedLinks(result.toString());
	        	
	        }
	        catch(IOException ioe)
	        {
	            // We were not successful in our HTTP request
	            System.out.println("Error in out HTTP request " + ioe);
	        }
		return links;
	}
	
	
	public List<String> handleRef(String id, JSONObject obj){
		StringBuffer result = new StringBuffer();
    	
		 try
	        {
			 	System.out.println("here");
	            HttpClient client = HttpClientBuilder.create().build();
	            HttpGet request = new HttpGet(refUrl(id));
	        	request.addHeader("User-Agent", USER_AGENT);
	        	request.addHeader("authority", "www.researchgate.net");
	        	request.addHeader("x-requested-with","XMLHttpRequest");
	        	request.addHeader("accept", "application/json");
	        	
	        	HttpResponse response = client.execute(request);
	        
	        	BufferedReader rd = new BufferedReader(
	        			new InputStreamReader(response.getEntity().getContent()));
	        	String line = "";
	        	while ((line = rd.readLine()) != null) {
	        		result.append(line);
	        	}
	        }
	        catch(IOException ioe)
	        {
	            // We were not successful in our HTTP request
	            System.out.println("Error in out HTTP request " + ioe);
	        }
		return getRefLinks(result.toString());
	}


	public List<String> getAllArticles(String url) throws IOException{
		List<String> startArticles = new LinkedList<String>();
		 Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
		 Document doc = connection.get();
         Elements links = doc.select("h5");
         for (Element link : links) {
        	 if(link.child(0).text().equals("Article:")){
        		 startArticles.add(link.child(1).attr("href"));
        	 }
         }
		return startArticles;
	}


	public void downloader(JSONObject obj, String url) throws IOException {
		 Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
		 Document doc = connection.get();
		 String title = doc.select("h1").text();
		 String abs = null;
		 Elements pabs = doc.select("div");
		 for (Element pab: pabs) {
			if(pab.attr("class").equals("pub-abstract")){
				abs = pab.child(0).child(0).child(1).text();
				System.out.println(pab.child(0).child(0).child(1).attributes());
				System.out.println("inja");
				break;
			}
		}
		 obj.put("title", title);
		 obj.put("abstract", abs);
		 System.out.println("title");
	}


	
	private static ArrayList<String> getCitedLinks(String json) {
	    ArrayList<String> citations = new ArrayList<String>();
	    JSONParser parser = new JSONParser();
	    String base = "https://www.researchgate.net/";
	    
	    try {
	      JSONObject obj = (JSONObject)parser.parse(json);
	      JSONObject result = (JSONObject) obj.get("result");
	      JSONObject data  = (JSONObject) result.get("data");
	      JSONArray citationItems = (JSONArray) data.get("citationItems");
	      for (int i = 0; i < citationItems.size(); i++) {
	        JSONObject citationData = (JSONObject) ((JSONObject) citationItems.get(i)).get("data");
	        String url = (String) citationData.get("publicationUrl");
	        if(url != null)
	          citations.add(base + url);
	        System.out.println(base + url);
	      }
	      
	      
	    } catch (ParseException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    return citations;
	  }

	private static ArrayList<String> getRefLinks(String json) {
	    ArrayList<String> citations = new ArrayList<String>();
	    JSONParser parser = new JSONParser();
	    String base = "https://www.researchgate.net/";
	    
	    try {
	      JSONObject obj = (JSONObject)parser.parse(json);
	      JSONObject result = (JSONObject) obj.get("result");
	      JSONObject data  = (JSONObject) result.get("data");
	      JSONArray citationItems = (JSONArray) data.get("citationItems");
	      for (int i = 0; i < citationItems.size(); i++) {
	        JSONObject citationData = (JSONObject) ((JSONObject) citationItems.get(i)).get("data");
	        String url = (String) citationData.get("publicationUrl");
	        if(url != null)
	          citations.add(base + url);
	        System.out.println(base + url);
	      }
	      
	      
	    } catch (ParseException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    return citations;
	  }


	

	////////////////////////////////////////////////////////////////////
	private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
    private String citedUrl(String id) {	
		return "https://www.researchgate.net/publicliterature.PublicationIncomingCitationsList.html?publicationUid="+id+"&showCitationsSorter=true&showAbstract=true&showType=true&showPublicationPreview=true&swapJournalAndAuthorPositions=false";
	}

	private String refUrl(String id){
		return "https://www.researchgate.net/publicliterature.PublicationCitationsList.html?dbw=true&publicationUid="+id+"&showContexts=true&showCitationsSorter=true&showAbstract=true&showType=true&showPublicationPreview=true&swapJournalAndAuthorPositions=false";
	}

}
