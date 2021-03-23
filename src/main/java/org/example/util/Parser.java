package org.example.util;

import javafx.util.Builder;
import org.example.model.DocInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static final String API_PATH ="E:\\jdk-8u261-docs-all\\docs\\api";
    public static final String API_BASE_PATH =" https://docs.oracle.com/javase/8/docs/api";
    public static final String RAW_DATA = "E:/raw_data.txt";

    public static void main(String[] args) throws IOException {
        List<File> htmls = listHtml(new File(API_PATH));
       //List<DocInfo> docs = new ArrayList<>();
        FileWriter fw = new FileWriter(RAW_DATA);
        PrintWriter pw = new PrintWriter(fw,true);

       for(File html:htmls){
           //System.out.println(html.getAbsolutePath());
           DocInfo doc = parserHtml(html);
           //System.out.println(doc);
           //docs.add(doc);
           pw.println(doc.getTitle()+"\3"+doc.getUrl()+"\3"+doc.getContent());
       }
    }

    private static DocInfo parserHtml(File html) {
        DocInfo doc = new DocInfo();
        doc.setTitle(html.getName().substring(0,html.getName().length()-".html".length()));
        String uri = html.getAbsolutePath().substring(API_PATH.length());
        doc.setUrl(API_BASE_PATH+uri);
        doc.setContent(parserContent(html));
        return doc;
    }
    //多个标签进行拼接
    private static String parserContent(File html) {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fr = new FileReader(html);
            int b;
            boolean isContent = false;

            while((b = fr.read()) != -1){
                char c  =(char)b;
                if(isContent){
                    if(c == '<'){
                        isContent = false;
                        continue;
                    }else if(c == '\n' || c == '\r'){
                        sb.append(" ");
                    }else{
                        sb.append(c);
                    }
                }else if(c == '>'){
                   isContent = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private static List<File> listHtml(File dir){
        List<File> list = new ArrayList<>();
        File[] children = dir.listFiles();
        for(File child:children){
            if(child.isDirectory()){
                list.addAll(listHtml(child));
            }else if(child.getName().endsWith(".html")){
                list.add(child);
            }
        }
        return list;
    }
}
