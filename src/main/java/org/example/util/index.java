package org.example.util;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.example.model.DocInfo;
import org.example.model.Weight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class index {
    private static final List<DocInfo> FORWARD_INDEX = new ArrayList<>();
    private static final Map<String, List<Weight>> INVERTED_INDEX = new HashMap<>();

    public static List<Weight> get(String keyword){

        return INVERTED_INDEX.get(keyword);
    }

    public static void main(String[] args) {
        index.buildForwardIndex();
        //FORWARD_INDEX.stream().forEach(System.out::println);
        index.buildInvertedIndex();
        //for (Map.Entry<String, List<Weight>> e : INVERTED_INDEX.entrySet()) {
            //String keyword = e.getKey();
           // System.out.print(keyword + ": ");
            //List<Weight> weights = e.getValue();
            //weights.stream().map(w -> {
               // return "（" + w.getDoc().getId() + "，" + w.getWeight() + "）";
           // }).forEach(System.out::print);
            //System.out.println();
        //}
    }

        public static void buildInvertedIndex(){
            for (DocInfo doc : FORWARD_INDEX) {
                Map<String, Weight> cache = new HashMap<>();
                List<Term> titleParticiples = ToAnalysis.parse(doc.getTitle()).getTerms();
                for (Term titleParticiple : titleParticiples) {
                    Weight w = cache.get(titleParticiple.getName());
                    if (w == null) {
                        w = new Weight();
                        w.setDoc(doc);
                        w.setKeyword(titleParticiple.getName());
                        cache.put(titleParticiple.getName(), w);

                    }
                    w.setWeight(w.getWeight() + 10);
                }
                List<Term> contentParticiples = ToAnalysis.parse(doc.getContent()).getTerms();
                for (Term contentParticiple : contentParticiples) {
                    Weight w = cache.get(contentParticiple.getName());
                    if (w == null) {
                        w = new Weight();
                        w.setDoc(doc);
                        w.setKeyword(contentParticiple.getName());
                        cache.put(contentParticiple.getName(), w);
                    }
                    w.setWeight(w.getWeight() + 1);
                }
                for (Map.Entry<String, Weight> e : cache.entrySet()) {
                    String keyword = e.getKey();
                    Weight w = e.getValue();
                    List<Weight> weights = INVERTED_INDEX.get(keyword);
                    if (weights == null) {
                        weights = new ArrayList<>();
                        INVERTED_INDEX.put(keyword, weights);
                    }
                    weights.add(w);
                }

            }

        }
        public static void buildForwardIndex(){
            try {
                FileReader fr = new FileReader(Parser.RAW_DATA);
                BufferedReader br = new BufferedReader(fr);
                int id = 0;
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().equals("")) {
                        continue;
                    }
                    DocInfo doc = new DocInfo();
                    doc.setId(++id);
                    String[] parts = line.split("\3");
                    doc.setTitle(parts[0]);
                    doc.setUrl(parts[1]);
                    doc.setContent(parts[2]);
                    FORWARD_INDEX.add(doc);

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
}

