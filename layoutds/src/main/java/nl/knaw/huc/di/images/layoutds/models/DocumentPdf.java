package nl.knaw.huc.di.images.layoutds.models;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(indexes = {
        @Index(columnList = "id", name = "documentPdf_id_hidx")
})

public class DocumentPdf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Integer pageCount;
    @Type(type = "text")
    private String text;



}