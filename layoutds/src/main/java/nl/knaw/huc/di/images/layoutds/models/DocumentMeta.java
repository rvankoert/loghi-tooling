package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DocumentMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String content;
    private String equiv;

    public DocumentMeta(){
    }

    public DocumentMeta(String name, String content, String equiv){
        this.name = name;
        this.content = content;
        this.equiv = equiv;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEquiv() {
        return equiv;
    }

    public void setEquiv(String equiv) {
        this.equiv = equiv;
    }
}
