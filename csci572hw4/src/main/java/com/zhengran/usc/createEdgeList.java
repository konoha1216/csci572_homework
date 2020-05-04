package com.zhengran.usc;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;

public class createEdgeList{
    public static void main(String[] args)throws Exception {
        String srcPath = "/Users/konoha/Desktop/csci-572/hw4/shared/aaa/NYTIMES/URLtoHTML_nytimes_news.csv";
        String charset = "utf-8";

        String dirPath = "/Users/konoha/Desktop/csci-572/hw4/shared/aaa/NYTIMES/nytimes";

        HashMap<String, String> fileUrlMap = new HashMap<String, String>();
        HashMap<String, String> urlFileMap = new HashMap<String, String>();

        CSVReader reader = new CSVReader(new FileReader(srcPath), ',', '"', 1);
        String[] nextLine;
        int cnt = 0;
        while((nextLine = reader.readNext()) != null){
            if(nextLine != null){
                cnt+=1;
                fileUrlMap.put(nextLine[0], nextLine[1]);
                urlFileMap.put(nextLine[1], nextLine[0]);
            }
        }
        System.out.println(cnt);

//        try(CSVReader csvReader = new CSVReaderBuilder(new BufferedReader(new InputStreamReader(new FileInputStream(new File(srcPath)), charset))).build())
//        {
//            Iterator<String[]> iterator = csvReader.iterator();
//
//            while (iterator.hasNext()) {
//                fileUrlMap.put(iterator.next()[0], iterator.next()[1]);
//                urlFileMap.put(iterator.next()[1], iterator.next()[0]);
//                Arrays.stream(iterator.next()).forEach(System.out::print);
//                System.out.println();
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }

        File dir = new File(dirPath);
        Set<String> edges = new HashSet<String>();
        for (File file: dir.listFiles()){
            Document doc = Jsoup.parse(file, "UTF-8", fileUrlMap.get(file.getName()));
            Elements links = doc.select("a[href]");
//            Elements pngs = doc.select("[src]");

            for(Element link:links){
                String url = link.attr("abs:href").trim();
                if(urlFileMap.containsKey(url)){
                    edges.add(file.getName() + " " + urlFileMap.get(url));
                }
            }
        }

        File file = new File("edgeLists.txt");
        file.createNewFile();
        PrintWriter writer = new PrintWriter(file);
        for(String s:edges){
            writer.println(s);
        }

        writer.flush();
        writer.close();



    }

}
