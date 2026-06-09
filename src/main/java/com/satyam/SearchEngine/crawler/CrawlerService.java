package com.satyam.SearchEngine.crawler;

import com.satyam.SearchEngine.Repo.PageRepo;
import com.satyam.SearchEngine.model.CrawlStatus;
import com.satyam.SearchEngine.model.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CrawlerService {
    private static final int MAX_PAGES = 10000;
    private static final String SEED_URL = "https://en.wikipedia.org/wiki/Main_Page";
    private static final String WIKI_PREFIX = "https://en.wikipedia.org/wiki/";
    private static final int MAX_RETRY = 3;
    private static final Set<String> WEBSITE_DISALLOWED = Set.of("special",
            "talk",
            "user",
            "file",
            "template",
            "category",
            "wikipedia",
            "portal",
            "help",
            "draft",
            "module");

    @Autowired
    PageRepo pageRepo;

    public void startCrawling(){
        Set<String> visited = new HashSet<>();
        Queue<String> urlQueue = new LinkedList<>();
        Set<String> discovered = new HashSet<>();



        //fetching pending pages first
        List<Page> pageList = pageRepo.findByStatus(CrawlStatus.PENDING,PageRequest.of(0,100));
        if(pageList.isEmpty()){
            urlQueue.add(SEED_URL);
            discovered.add(SEED_URL);
        }else{
            pageList.forEach(p -> {
                urlQueue.add(p.getUrl());
                discovered.add(p.getUrl());
            });
        }

        int pageCrawled = 0;
        while(!urlQueue.isEmpty() && pageCrawled < MAX_PAGES) {
             crawl(visited, urlQueue,discovered);
//            System.out.println("END: "+LocalDateTime.now());
             pageCrawled ++;
        }
        System.out.println("CRAWLING REACHED MAX_PAGES !");
    }

    private void crawl( Set<String> visited, Queue<String> urlQueue,Set<String> discovered) {
        String queueUrl = urlQueue.poll();
        if(queueUrl == null || visited.contains(queueUrl)) return;   //double guarding

        Page page = checkCrawlStatus(queueUrl);
        if(page == null) return;

        Document doc = request(queueUrl, visited, page);
        if(doc == null){
            pageRepo.save(page);
            return;
        }

        handleParserAndSave(doc,queueUrl,page);

        for(Element ele:doc.select("a[href]")){
            String sub_url = normalize(ele.absUrl("href"));
            if (sub_url == null) continue;

            if(!discovered.contains(sub_url) && sub_url.startsWith(WIKI_PREFIX)){
                discovered.add(sub_url);
                urlQueue.add(sub_url);

                //TODO:  n+1 problem here
                if(pageRepo.findByUrl(sub_url).isEmpty())
                    pageRepo.save(new Page(sub_url,CrawlStatus.PENDING));
            }
        }

    }


    private Document request(String url, Set<String> visited,Page page) {
        Connection con = Jsoup.connect(url)
                .userAgent("SatyamBot/1.0 ((https://github.com/satyam-721/); satyamsagar305@gmail.com)")
//                .userAgent("Mozilla/5.0 (compatible; SatyamBot/1.0; +https://github.com/satyam-721/)")
                .timeout(8000)              // not to hangover at slow response
                .header("Accept-Language", "en-US,en;q=0.9") // get English content
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .ignoreHttpErrors(true);    // handle 404/500 yourself instead of exceptions
        try {

            //delay between each request
            Thread.sleep(10);   /** need to change if there is multiple functions calling request()*/
            Document doc  = con.get();
//            System.out.println("Start: "+LocalDateTime.now());

            if(con.response().statusCode()==200){
                page.setHttpStatusCode(200);
                visited.add(url);
                return doc;

            }else{

                page.setHttpStatusCode( con.response().statusCode() );

                page.setRetryCount( page.getRetryCount()+1 );
                page.setStatus(CrawlStatus.FAILED);
                System.out.println(page.getHttpStatusCode()+": "+url);

                if(page.getHttpStatusCode() == 404){
                    page.setStatus(CrawlStatus.SKIPPED);
                }

                //if rate limit is reached backing of by 15 sec
                if(page.getHttpStatusCode() == 429){
                    System.out.println("Rate Limited, BACKING OFF: "+ url);
                    try {
                        Thread.sleep(15000); // back off 15 seconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                return null;
            }



        } catch (IOException e) {
            System.out.println("Failed: "+url + e.getMessage());
            page.setRetryCount( page.getRetryCount()+1 );
            page.setStatus(CrawlStatus.FAILED);
            page.setFailureReason(e.getMessage());
            return null;

        } catch (InterruptedException e) {
            System.out.println("Waiting interrupted");
            throw new RuntimeException(e);
        }
    }



    private String normalize(String url){
        if(url == null || url.isBlank())
            return null;

        int hash = url.indexOf('#');
        if(hash != -1){
            url = url.substring(0, hash);
        }

        int queryIndex = url.indexOf('?');
        if(queryIndex != -1){
            url = url.substring(0, queryIndex);
        }

        url = url.strip();
        if(url.isBlank()) return null;

        int columnIndex = url.indexOf(":",7);
        if(columnIndex == -1)
            columnIndex = url.toLowerCase().indexOf("%3a",7);

        if(columnIndex != -1) {
            String nameSpaces = url.substring(url.lastIndexOf('/', columnIndex) + 1,columnIndex).toLowerCase();

//        if (pageName.isEmpty()) return null;

            // filter useless namespaces, keep legitimate content
//                String decoded = pageName.replace("%3a", ":");
//                String namespace = decoded.substring(0, decoded.indexOf(':')).toLowerCase();


            if (WEBSITE_DISALLOWED.contains(nameSpaces)) return null;
            if (url.contains("Category:Noindexed_pages") || url.contains("Category%3ANoindexed_pages")
                    || url.contains("Category%3aNoindexed_pages")) return null;


        }

        if (url.length() > 2048) return null;

        return url;

    }

    private void handleParserAndSave(Document doc, String url, Page page) {

        String title = doc.title();
        if(title.isEmpty()) title=url;
        Element updatedLine = doc.selectFirst("#footer-info-lastmod");    //contains last updated date of wikipedia
        doc.select("script, style, nav, header, footer, aside").remove();

        if (updatedLine != null){
            String updatedDateTime = updatedLine.text()
                    .strip()
                    .replaceFirst("This page was last edited on ","");
            String lastUpdatedDate = updatedDateTime.substring(0,updatedDateTime.indexOf(","));

            try {
                 page.setLastUpdated( LocalDate.parse(
                        lastUpdatedDate,
                        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
                ));
            } catch (Exception e) {
                System.out.println("Not a valid date format, retrying short format insertion");
                page.setLastUpdated( LocalDate.parse(
                        lastUpdatedDate,
                        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
                ));
            }
        }

        page.setTitle(title);
        page.setContent( doc.body().text().replaceFirst("Jump to content From Wikipedia, the free encyclopedia","") );
        page.setSnippet(page.getContent().substring(0, Math.min(245, page.getContent().length())) + "...");
        page.setStatus(CrawlStatus.CRAWLED);

        pageRepo.save(page);
    }

    private Page checkCrawlStatus(String url) {
        System.out.println(url);
        Page page = pageRepo.findByUrl(url)
                .orElse(new Page(url,CrawlStatus.PENDING));

        if(page.getStatus() == CrawlStatus.CRAWLED || page.getStatus() == CrawlStatus.SKIPPED)
            return null;

        if(page.getStatus() == CrawlStatus.FAILED && page.getRetryCount() >= MAX_RETRY)
            return null;

        page.setStatus(CrawlStatus.CRAWLING);
        page.setCrawledAt(LocalDateTime.now());
        page.setUrl(url);

        return page;



    }
}
