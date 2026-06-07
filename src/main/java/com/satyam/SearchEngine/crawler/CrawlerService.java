package com.satyam.SearchEngine.crawler;

import com.satyam.SearchEngine.Repo.PageRepo;
import com.satyam.SearchEngine.model.CrawlStatus;
import com.satyam.SearchEngine.model.Page;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CrawlerService {
    private static final int MAX_PAGES = 100;
    private static final String SEED_URL = "https://en.wikipedia.org/wiki/Main_Page";
    private static final String WIKI_PREFIX = "https://en.wikipedia.org/wiki/";
    private static final int MAX_RETRY = 3;

    @Autowired
    PageRepo pageRepo;

    public void startCrawling(){
        Set<String> visited = new HashSet<>();
        Queue<String> urlQueue = new LinkedList<>();
        Set<String> discovered = new HashSet<>();



        //fetching pending pages first
        List<Page> pageList = pageRepo.findByStatus(CrawlStatus.PENDING);
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

                if(pageRepo.findByUrl(sub_url).isEmpty())
                    pageRepo.save(new Page(sub_url,CrawlStatus.PENDING));
            }
        }

    }


    private Document request(String url, Set<String> visited,Page page) {
        Connection con = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (compatible; SatyamBot/1.0; +https://github.com/satyam-721/)")
                .timeout(8000)              // not to hangover at slow response
                .header("Accept-Language", "en-US,en;q=0.9") // get English content
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .ignoreHttpErrors(true);    // handle 404/500 yourself instead of exceptions
        try {
            Thread.sleep(1000);   /**need to change if there is multiple functions calling request()*/
            Document doc  = con.get();

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

                return null;
            }



        } catch (IOException e) {
            System.out.println("Failed: "+url);
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

        String pageName = url.substring(url.lastIndexOf('/') + 1);
        if (pageName.isEmpty()) return null;

        // filter useless namespaces, keep legitimate content
        if (pageName.contains(":")) {
            String namespace = pageName.substring(0, pageName.indexOf(':')).toLowerCase();
            Set<String> allowed = Set.of("category", "portal", "file", "help");
            if (!allowed.contains(namespace)) return null;
        }

        return url;

    }

    private void handleParserAndSave(Document doc, String url, Page page) {

        String title = doc.title();
        if(title.isEmpty()) title=url;
        doc.select("script, style, nav, header, footer, aside").remove();

        page.setTitle(title);
        page.setContent( doc.body().text() );
        page.setSnippet(page.getContent().substring(0, Math.min(245, page.getContent().length())) + "...");
        page.setStatus(CrawlStatus.CRAWLED);

        pageRepo.save(page);
    }

    private Page checkCrawlStatus(String url) {
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
