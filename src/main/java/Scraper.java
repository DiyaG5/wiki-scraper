import com.microsoft.playwright.*;
import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.nio.file.*;
import com.opencsv.CSVWriter;

public class Scraper {

    public static void scrapePages(List<String> pages, String jsonPath, String csvPath) throws Exception {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch();
            Page page = browser.newPage();

            for (String title : pages) {
                String url = "https://en.wikipedia.org/wiki/" + title;
                page.navigate(url);

                Map<String, Object> pageData = new LinkedHashMap<>();
                pageData.put("url", url);
                pageData.put("title", page.title());
                pageData.put("summary", page.locator("div.mw-parser-output > p").first().textContent().trim());
                pageData.put("infobox", extractInfobox(page));
                pageData.put("tables", extractTables(page));

                results.add(pageData);
            }

            try (Writer writer = Files.newBufferedWriter(Paths.get(jsonPath))) {
                new GsonBuilder().setPrettyPrinting().create().toJson(results, writer);
            }

            try (CSVWriter writer = new CSVWriter(new FileWriter(csvPath))) {
                writer.writeNext(new String[] { "Title", "Summary", "URL" });
                for (Map<String, Object> data : results) {
                    writer.writeNext(new String[] {
                        data.get("title").toString(),
                        data.get("summary").toString(),
                        data.get("url").toString()
                    });
                }
            }

            browser.close();
        }
    }

    private static Map<String, String> extractInfobox(Page page) {
        Map<String, String> info = new LinkedHashMap<>();
        Locator rows = page.locator("table.infobox tr");

        for (int i = 0; i < rows.count(); i++) {
            Locator th = rows.nth(i).locator("th");
            Locator td = rows.nth(i).locator("td");

            if (th.count() > 0 && td.count() > 0) {
                info.put(th.textContent().trim(), td.textContent().trim());
            }
        }

        return info;
    }

    private static List<List<String>> extractTables(Page page) {
        return TableUtil.extractTables(page, "table.wikitable");
    }
}
