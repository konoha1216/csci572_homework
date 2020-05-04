package com.zhengran.usc;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class TikaExtraction {
    public static void main(String[] args)throws Exception {
        String path = "/Users/konoha/Desktop/csci-572/hw4/shared/aaa/NYTIMES/nytimes";
        File file = new File(path);
        File[] fs = file.listFiles();
        int cnt = 1;

        for(File f:fs){
//            System.out.println(f);
            System.out.println(cnt);
            BodyContentHandler handler = new BodyContentHandler(10*1024*1024);
            ParseContext pcontext = new ParseContext();
            Metadata metadata = new Metadata();
            FileInputStream inputStream = new FileInputStream(f);

            HtmlParser htmlParser = new HtmlParser();
            htmlParser.parse(inputStream, handler, metadata, pcontext);

            try{
                String x = handler.toString().replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");
                FileWriter writer = new FileWriter("big2.txt",true);
                writer.write(x);
                writer.close();
                cnt+=1;
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

