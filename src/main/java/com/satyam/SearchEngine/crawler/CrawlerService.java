package com.satyam.SearchEngine.crawler;

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

        int count = 1;
        while(!urlQueue.isEmpty() && count <10) {
            count = crawl( count,visited, urlQueue,discovered);
        }
    }

    private int crawl(int count, Set<String> visited, Queue<String> urlQueue,Set<String> discovered) {
        String queueUrl = normalize(urlQueue.poll());
        if(queueUrl == null) return count+1;
        Document doc = request(queueUrl, visited);


        if (doc!=null){
            for(Element ele:doc.select("a[href]")){
                String sub_url = normalize(ele.absUrl("href"));

                if(!discovered.contains(sub_url)){
                    discovered.add(sub_url);
                    urlQueue.add(sub_url);
                }
            }
        }

        return count+1;
    }

    private Document request(String url, Set<String> visited) {
        Connection con = Jsoup.connect(url);
        try {
            Document doc  = con.get();
            if(con.response().statusCode()==200){
                System.out.println(url);
                System.out.println(doc.title());
                System.out.println();

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

        int hash = url.indexOf('#');

        if(hash != -1){
            url = url.substring(0, hash);
        }

        return url;
    }
}
