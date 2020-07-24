import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;

public class Page {
    public String url;
    public String title;
    public String Date;
    public String Html;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getPage() throws IOException {

        Document doc = null;
        Connection connect =  Jsoup.connect(this.url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Safari/537.36")
                .timeout(1000 * 10);
        try{
            doc = connect.get();
        }catch (SocketTimeoutException e){
            try{
                doc = connect.get();
            }catch (SocketTimeoutException ee){
                try{
                    doc = connect.get();
                }catch (SocketTimeoutException eee){
                    return null;
                }
            }
        }
        Elements New = doc.select("body > article > div.subPage_con.pr > section > div.pageArticle");

        String body = new String("");
        for (Element e : New.select("div.article > p")
        ) {
            if (e.select("img").size() != 0) {
                e.select("img").attr("onclick", "");

                downImages("./DownloadPages/Images/", e.select("img").attr("abs:src"));

                e.select("img").attr("src",
                        "./Images/" + e.select("img").attr("abs:src")
                                .substring(e.select("img").attr("abs:src").lastIndexOf('/') + 1,
                                        e.select("img").attr("abs:src").length()));
            }
            body = body + "\n" + e.toString();
        }
        return body;

    }

    public void setHtml(String Title, String Date, String Content) {
        String html = new String("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "  <meta http-equiv=\"Content-Style-Type\" content=\"text/css\">\n" +
                "  <title>" + Title + "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div class=\"pageArticle\" style=\"width: 853px; margin: auto;\" >\n" +
                " <div class=\"articleTitle02\">\n" +
                "  <h3 style=\"font-size: 1.5rem;text-align: center;line-height: 26px; color: #0858b1;\">" + Title + "</h3>\n" +
                " </div>\n" +
                " <div class=\"articleAuthor\" style=\"margin: 0px;color: #282828;-webkit-font-smoothing: subpixel-antialiased;text-align: center;line-height: 20px;font-size: 1.3rem;padding: 10px 0;border-top: 1px solid #dcdcdc;\">\n" +
                "  <span>发布日期：<strong>" + Date + "</strong> </span>\n" +
                " </div>\n" +
                " <div class=\"article\">" + Content + "</div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n");
        this.Html = html;
    }

    public void downImages(String filePath, String imgUrl) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String imgName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1, imgUrl.length());
        try {
            String urlTail = URLEncoder.encode(imgName, "UTF-8");
            imgUrl = imgUrl.substring(0, imgUrl.lastIndexOf('/') + 1) + urlTail.replaceAll("\\+", "\\%20");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(filePath + imgName);

        try {
            URL url = new URL(imgUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            InputStream in = connection.getInputStream();
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buf = new byte[1024];
            int size;
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String filePath, String fileName) throws IOException {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(filePath + fileName);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        writer.write(this.Html);
        writer.close();
    }

    public boolean start() throws IOException {
        try {
            setHtml(this.title, this.Date, this.getPage());
            writeToFile("./DownloadPages/", this.Date + "_"+ this.title + ".html");
            System.out.println(this.Date + "_"+ this.title + ".html");
            return true;
        } catch (HttpStatusException e) {
            return false;
        }
    }
}
