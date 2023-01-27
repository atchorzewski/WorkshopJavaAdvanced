package pl.coderslab;


import com.github.slugify.Slugify;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class WebScrawler {
    private static final String MAIN_WEBSITE = "https://www.infoworld.com";
    private static final String SAVE_DIR = "infoword";
    final static Slugify slg = Slugify.builder().build();

    public static void main(String[] args) {

        Document doc = null;
        try {
            doc = Jsoup.connect(MAIN_WEBSITE.concat("/category/java/"))
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> map = doc.select("div.article h3 a")
                .stream()
                .collect(Collectors.toMap(e -> String.join("-", UUID.randomUUID().toString(), slg.slugify(e.text())),
                        e -> MAIN_WEBSITE.concat(e.attr("href"))));

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        map.forEach((fileName, url) -> executorService.execute(() -> extracted(fileName, url)));
        executorService.shutdown();
    }

    private static void extracted(String fileName, String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements select1 = document.select("div[id=drr-container]");
            FileUtils.writeStringToFile(new File(Paths.get(SAVE_DIR, fileName.concat(".txt")).toString()),
                    select1.text(), "utf-8");
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



