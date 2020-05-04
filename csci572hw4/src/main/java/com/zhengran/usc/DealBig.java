package com.zhengran.usc;

import javax.swing.*;
import java.io.*;

public class DealBig {
    public static void main(String[] args)throws Exception {
        try {
            String big0 = "/Users/konoha/IdeaProjects/csci572hw4/big.txt";
            String big1 = "big1.txt";
//            File filename = new File(big0);
            FileInputStream inputStream = new FileInputStream(big0);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line=null;
            String result ="";

            int cnt=0;
            while((line = br.readLine()) != null){
//                line = br.readLine().replaceAll("[^a-z]+","");
                if(!line.equals("")) {
                    result += (line+"\n");
                }
                System.out.println(cnt++);
            }

            File writename = new File(big1);
            writename.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(result);
            out.flush();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
