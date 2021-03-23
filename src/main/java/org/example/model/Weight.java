package org.example.model;

public class Weight {
    private DocInfo doc;
    private int weight;
    private String keyword;

    public DocInfo getDoc() {
        return doc;
    }

    public void setDoc(DocInfo doc) {
        this.doc = doc;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "doc=" + doc +
                ", weight=" + weight +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
