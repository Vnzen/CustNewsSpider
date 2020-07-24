import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static String spider(String URL) throws IOException, InterruptedException {
        try{
            Document doc = Jsoup.connect(URL).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36").get();
            Elements newList = doc.select("body > article > div.subPage_con.pr > section > ul > li");
            ArrayList pageList = new ArrayList();
            for (Element e : newList
            ) {
                Page p = new Page();
                p.setUrl(e.getElementsByTag("a").attr("abs:href"));
                p.setTitle(e.getElementsByTag("a").text());
                p.setDate(e.getElementsByTag("span").text());
                if (! p.start() && !pageList.contains(e.getElementsByTag("a").attr("abs:href"))){
                    pageList.add(e.getElementsByTag("a").attr("abs:href"));
                    return null;
                }
                Thread.sleep(1000);
            }
            return (doc.select("body > article > div.subPage_con.pr > section > div.pages > a.end").attr("abs:href"));
        }catch (HttpStatusException e){
            System.out.println("HTTP error fetching URL. Status=404, URL="+URL);
            return null;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String URL = "https://www.cust.edu.cn/lgxw/";
        do {
            System.out.println(URL);
            URL = spider(URL);
            Thread.sleep(1000);
        }while (URL != null);
    }
}
