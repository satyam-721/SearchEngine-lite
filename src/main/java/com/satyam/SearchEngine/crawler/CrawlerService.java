package com.satyam.SearchEngine.crawler;

import com.satyam.SearchEngine.model.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Service
public class CrawlerService {

    public void startCrawling(){
        String seedUrl = "https://en.wikipedia.org/wiki/Main_Page";
        Set<String> visited = new HashSet<>();
        Queue<String> urlQueue = new LinkedList<>();
        Set<String> discovered = new HashSet<>();

        urlQueue.add(seedUrl);
        discovered.add(seedUrl);

        int pageCrawled = 0;
        while(!urlQueue.isEmpty() && pageCrawled <10) {
             crawl(pageCrawled,visited, urlQueue,discovered);
             pageCrawled ++;
        }
    }

    private void crawl(int count, Set<String> visited, Queue<String> urlQueue,Set<String> discovered) {
        String queueUrl = normalize(urlQueue.poll());
        if(queueUrl == null) return;
        Document doc = request(queueUrl, visited);


        if (doc!=null){
            for(Element ele:doc.select("a[href]")){
                String sub_url = normalize(ele.absUrl("href"));

                if(!discovered.contains(sub_url) && sub_url.startsWith("https://en.wikipedia.org/wiki/")){
                    discovered.add(sub_url);
                    urlQueue.add(sub_url);
                }
            }
        }

    }

    private Document request(String url, Set<String> visited) {
        Connection con = Jsoup.connect(url);
        try {
            Document doc  = con.get();
            if(con.response().statusCode()==200){
                Page page = documentParser(doc,url);
                System.out.println(page);

                visited.add(url);
                return doc;
            }
            return null;
        } catch (IOException e) {
            System.out.println("Failed: "+url);
            return null;
        }
    }



    private String normalize(String url){
        if(url == null || url.isBlank())
            return null;

//        String pageName = url.substring(url.lastIndexOf('/') + 1);
//        if (pageName.contains(":")){
//            return null;
//        }


        int hash = url.indexOf('#');
        if(hash != -1){
            url = url.substring(0, hash);
        }

        int queryIndex = url.indexOf('?');
        if(queryIndex != -1){
            url = url.substring(0, queryIndex);
        }

        return url;
    }

    private Page documentParser(Document doc,String url) {

        String title = doc.title();
        if(title.isEmpty()) title=url;

        doc.select("script, style, nav, header, footer, aside").remove();
        String content = doc.body().text();

        return new Page(url,title,content);
    }

}
