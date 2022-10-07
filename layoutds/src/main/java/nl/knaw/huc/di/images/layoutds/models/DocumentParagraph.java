package nl.knaw.huc.di.images.layoutds.models;

import java.util.ArrayList;

public class DocumentParagraph {
    private ArrayList<DocumentTextLine> documentTextLines;
    private String id;
    private String title;
    private String lang;

    public ArrayList<DocumentTextLine> getDocumentTextLines() {

        if (documentTextLines==null){
            documentTextLines = new ArrayList<>();
        }
        return documentTextLines;
    }

    public void setDocumentTextLines(ArrayList<DocumentTextLine> documentTextLines) {
        this.documentTextLines = documentTextLines;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }
}
