package com.satyam.SearchEngine.testing;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.satyam.SearchEngine.model.dto.PageContentDto;
import serpapi.SerpApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FallBackApi {

        public static List<PageContentDto> generate(String query) {

            String apiKey = System.getenv("SERP_API_KEY");

            Map<String, String> auth = new HashMap<>();
            auth.put("api_key", apiKey);

            SerpApi client = new SerpApi(auth);

            Map<String, String> parameters = new HashMap<>();
            parameters.put("engine", "google");
            parameters.put("q", query);
            parameters.put("location", "jamshedpur");
            parameters.put("gl", "in");
            parameters.put("num", "20");
            parameters.put("safe", "active");

            try {
                JsonObject results = client.search(parameters);


                return fetchRequiredResult(results);
//                System.out.println(results);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    private static List<PageContentDto> fetchRequiredResult(JsonObject results) {

        JsonArray organicResults =
                results.getAsJsonArray("organic_results");

        List<PageContentDto> pageContentList= new ArrayList<>();

        for(JsonElement  element: organicResults){
                JsonObject result = element.getAsJsonObject();

            String title = result.get("title").getAsString();
            String link = result.get("link").getAsString();
            String snippet = result.get("snippet").getAsString();

            pageContentList.add(
                    new PageContentDto(
                            (long) (Math.random() * 100),
                            link,
                            title,
                            snippet
                    )
            );
        }
        return pageContentList;
    }

}
