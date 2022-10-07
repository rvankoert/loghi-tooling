package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Deprecated
public class DocumentTextLineImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(targetEntity=DocumentImage.class, fetch= FetchType.LAZY)
    @JoinColumn(name="documentImageId")
    @JsonIgnore
    private DocumentImage parent;
    private String imageLocation;
    private String text;
    private TextLineImageType textLineImageType;
    private TranscriptionStatus transcriptionStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DocumentImage getParent() {
        return parent;
    }

    public void setParent(DocumentImage parent) {
        this.parent = parent;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextLineImageType getTextLineImageType() {
        return textLineImageType;
    }

    public void setTextLineImageType(TextLineImageType textLineImageType) {
        this.textLineImageType = textLineImageType;
    }

    public TranscriptionStatus getTranscriptionStatus() {
        return transcriptionStatus;
    }

    public void setTranscriptionStatus(TranscriptionStatus transcriptionStatus) {
        this.transcriptionStatus = transcriptionStatus;
    }
}
