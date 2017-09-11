
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;



public class Crawler {
	private static final int MAX_PAGES_TO_SEARCH = 1000;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();
    private List<String> cited = new LinkedList<String>();
    private List<String> refrenced = new LinkedList<String>();

   
    private String nextUrl(){
        String nextUrl;
        do
        {
            nextUrl = this.pagesToVisit.remove(0);
        } while(this.pagesVisited.contains(nextUrl));
        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }
    
    public void crawlWeb(String startUrl) throws IOException{
    	crawlFirstPage(startUrl);
    	System.out.println("done here");
    	crawlArticles();
    }
    
    private void crawlFirstPage(String startUrl) throws IOException{
    	List<String> startLinks = new LinkedList<String>();
    	PageHandler ph = new PageHandler();
    	startLinks.addAll(ph.getAllArticles(startUrl));
    	for (int i = 0; i < 11; i++) {
			pagesToVisit.add(startLinks.remove(0));
		}
    }
    
    private void crawlArticles() throws IOException{
    	int j = 1;
    	while(this.pagesVisited.size() < MAX_PAGES_TO_SEARCH)
        {
            String currentUrl;
            PageHandler ph = new PageHandler();
            JSONObject obj = new JSONObject();
            
            currentUrl = this.nextUrl();
            String id = getid(currentUrl);
            obj.put("id", id);
            cited.addAll(ph.handleCited(id, obj));
            refrenced.addAll(ph.handleRef(id, obj));
            String url = "https://www.researchgate.net/" + currentUrl;
            ph.downloader(obj , url);
            for (int i = 0; i < min(10, cited.size()); i++) {
                this.pagesToVisit.add(cited.get(i));
			}
            for (int i = 0; i < min(10, refrenced.size()); i++) {
                this.pagesToVisit.add(refrenced.get(i));
			}
            
            try {
            	String filename = String.valueOf(j);
        		FileWriter file = new FileWriter(filename);
        		file.write(obj.toJSONString());
        		file.flush();
        		file.close();

        	} catch (IOException e) {
        		e.printStackTrace();
        	}
System.out.println(obj);
        }
    }
	private String getid(String currentUrl) {
		String numberOnly= currentUrl.replaceAll("[^0-9]", "");
		return numberOnly;
	}

	private int min(int a, int b) {
		if(a > b)
			return b;
		return a;
	}
    
}

