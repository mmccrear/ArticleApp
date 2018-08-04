package com.codebind;

import java.util.ArrayList;
import java.util.List;

public class Kicker {
    private String m_key;
    private List<KickerPair> m_RelatedArticles;
    private String m_videoLink;

    public Kicker(List<Object> related){
        if(related.size()%2==1){
            return;
        }
        m_key = related.get(0).toString();

        m_videoLink = related.get(1).toString();

        m_RelatedArticles = new ArrayList();
        for(int i=2;i<related.size();i=i+2){
            String link = related.get(i).toString();
            String title = related.get(i+1).toString();
            KickerPair kp = new KickerPair(link,title);
            m_RelatedArticles.add(kp);
        }
    }

    public String get_key() {
        return m_key;
    }

    public boolean has_video() { return m_videoLink != ""; }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(KickerPair kp : m_RelatedArticles){
            sb.append(kp.toString());
        }
        return sb.toString();
    }

    public String videoToString(){
        String vidHTML = "<p><iframe width=\"560\" height=\"315\" src=\"";
        vidHTML += m_videoLink;
        vidHTML += "\" frameborder=\"0\" allowfullscreen></iframe></p>";
        return vidHTML;
    }
}

