package org.example.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.example.model.Result;
import org.example.model.Weight;
import org.example.util.index;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(value = "/search",loadOnStartup = 0)//表示是否在启动时初始化
public class SearchServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        index.buildForwardIndex();
        index.buildInvertedIndex();
        System.out.println("init ok");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        Map<String,Object> map = new HashMap<>();
        String query = req.getParameter("query");
        List<Result> results = new ArrayList<>();
        try{
            if(query == null && query.trim().length() == 0){
                map.put("ok",false);
                map.put("msg","搜索内容为空");
            }else{
                //根据搜索内容进行分词 遍历每个分词
                for(Term t: ToAnalysis.parse(query).getTerms()){
                    String participle = t.getName();
                    //每个分词 在倒排中查找对应的文档（一个文档对应多个文档）
                    List<Weight> weights = index.get(participle);
                    for(Weight weight:weights){
                        //一个文档转换为一个Result（不同分词可能存在相同文档 需要合并）
                        Result r = new Result();
                        r.setId(weight.getDoc().getId());
                        r.setTitle(weight.getDoc().getTitle());
                        r.setUrl(weight.getDoc().getUrl());
                        r.setWeight(weight.getWeight());
                        String content = weight.getDoc().getContent();
                        r.setDes(content.length()<=60?content:content.substring(0,60)+"...");
                        results.add(r);
                    }
                }
                results.sort(new Comparator<Result>() {
                    @Override
                    public int compare(Result o1, Result o2) {
                        return Integer.compare(o2.getWeight(),o1.getWeight());
                    }
                });

                //一个文档转换为一个Result（不同分词可能存在相同文档 需要合并）
                //合并完成后，对List<Result>排序：权重降序排序
                map.put("ok",true);
                map.put("data",results);
            }
        }catch (Exception e ){
            e.printStackTrace();
            map.put("ok",false);
            map.put("msg","未知错误");
        }
        PrintWriter pw = resp.getWriter();
        //设置响应体的内容：map对象序列化为json字符串
        pw.println(new ObjectMapper().writeValueAsString(map));
    }


}
