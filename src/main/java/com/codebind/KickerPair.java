package com.codebind;

public class KickerPair {
    String m_link;
    String m_title;

    public KickerPair(String link, String title){
        m_link = link;
        m_title = title;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("<p><b>Related: ");
        sb.append("<a href= ");
        sb.append(m_link);
        sb.append(">");
        sb.append(m_title);
        sb.append("</a></b></p>");
        return sb.toString();
    }
}
