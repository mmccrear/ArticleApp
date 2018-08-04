package com.codebind;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;
import org.apache.commons.lang3.StringUtils;
import org.languagetool.MultiThreadedJLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyController implements Initializable {
    private static final String m_sheetsId = "";
    private static final String m_kickerRange = "Kickers!A2:P999";
    private static final String m_subsRange = "Standards!A2:C999";
    private static final String m_btmMessageRange = "Message!A1";
    private static String m_btmMessage;
    private static List<Kicker> m_kickers;
    private static StandardsMap m_standardsRepeated;
    private static StandardsMap m_standardsOneTime;
    private String m_Article;


    //GUI Fields
    @FXML
    private HTMLEditor finalArticle;
    @FXML
    private TextArea initArticle;

    @FXML
    private Button btnProcess;
    @FXML
    private Button btnCopy;

    @FXML
    private ComboBox cbKickers;
    @FXML
    private CheckBox cVideo;

    public void initialize(URL location, ResourceBundle resources) {
        setKickerList(cbKickers);
    }

    /* ---------------* Event Listeners *--------------- */
    @FXML
    protected void processArticleButtonAction(ActionEvent e) {
        StringBuilder sb = new StringBuilder();

        /* Kicker */
        int idx = cbKickers.getSelectionModel().getSelectedIndex();
        Kicker k = null;
        if (idx > 0) {
            //subtract 1 to account for blank option;
            k = m_kickers.get(idx - 1);
            sb.append(k);
            sb.append("\n\n");
        }

        /* Article */
        String article = process();
        sb.append(article);

        /* Video */
        if(idx > 0 && k.has_video() && cVideo.isSelected()){
            sb.append(k.videoToString());
        }

        /* Bottom Message */
        if( generateBottomMessage() ){
            sb.append(m_btmMessage);
        }

        m_Article = sb.toString();

        finalArticle.setHtmlText(m_Article);
        cVideo.setSelected(false);
    }

    @FXML
    protected void copyArticleButtonAction(ActionEvent e){
        StringSelection stringSelection = new StringSelection(finalArticle.getHtmlText());
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    /* ---------------* Kicker Methods *--------------- */
    private boolean setKickerList(ComboBox cb) {
        generateKickers();
        ObservableList<String> obList = FXCollections.observableArrayList();
        obList.add("");
        for(Kicker k : m_kickers){
            obList.add(k.get_key());
        }
        cb.getItems().addAll(obList);
        return true;
    }

    private boolean generateKickers() {
        m_kickers = new ArrayList();
        List<List<Object>> kickers = GoogleData.getSheetsData(m_sheetsId, m_kickerRange);
        for (List<Object> obList : kickers) {
            m_kickers.add(new Kicker(obList));
        }
        return true;
    }

    private boolean generateBottomMessage(){
        List<List<Object>> message = GoogleData.getSheetsData(m_sheetsId, m_btmMessageRange);
        if(message.isEmpty()){
            m_btmMessage = "";
            return false;
        }
        m_btmMessage = message.get(0).get(0).toString();
        return true;
    }

    /* ---------------* Standards Methods *--------------- */
    private boolean generateStandards() {
        m_standardsRepeated = new StandardsMap();
        m_standardsOneTime = new StandardsMap();
        List<List<Object>> standards = GoogleData.getSheetsData(m_sheetsId, m_subsRange);
        for (List<Object> obList : standards) {
            if(obList.size() < 1 || obList.size() > 3){
                // TODO better error checking
                continue;
            }
            String sKey = obList.get(0).toString();
            String sVal = obList.size() == 1 ? "" : obList.get(1).toString();
            String oldVal = obList.size() == 2 ?
                    m_standardsRepeated.put(sKey,sVal) : m_standardsOneTime.put(sKey,sVal);

            if(oldVal!=null){
                return false;
            }
        }
        return true;
    }

    /* ---------------* Article Methods *--------------- */
    private String process() {
        generateStandards();

        String article = initArticle.getText();
        article = StringUtils.replaceEach(article,
                m_standardsRepeated.getKeysArray(), m_standardsRepeated.getValsArray());

        ArrayList<String> oneTimeKeys = m_standardsOneTime.getKeys();
        ArrayList<String> oneTimeVals = m_standardsOneTime.getVals();
        for(int i=0;i<oneTimeKeys.size();i++) {
            article = StringUtils.replaceFirst(article, oneTimeKeys.get(i), oneTimeVals.get(i));
        }

        article = StringUtils.replacePattern(article,"\\s{2,}"," ");

        return spellCheck(article);
    }

    private String spellCheck(String str){
        MultiThreadedJLanguageTool langTool = new MultiThreadedJLanguageTool(new AmericanEnglish());
        List<RuleMatch> matches = null;
        try {
            matches = langTool.check(str);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return str;
        }

        int addedCharsCount = 0;
        String startSpan = "<span style=\"background: rgba(35 , 181 , 186 , 0.3); font-size: 16px; line-height: 24px; padding: 0 8px;\">";
        int startSpanLen = startSpan.length();
        String endSpan = "</span>";
        int endSpanLen = endSpan.length();

        StringBuilder s = new StringBuilder(str);
        for (RuleMatch match : matches){
            System.out.println(match);
            int startPos = match.getFromPos()+addedCharsCount;
            addedCharsCount+=startSpanLen;
            int endPos = match.getToPos()+addedCharsCount;

            s.insert(startPos,startSpan);
            s.insert(endPos,endSpan);

            addedCharsCount+=endSpanLen;
        }
        return s.toString();
    }
}
