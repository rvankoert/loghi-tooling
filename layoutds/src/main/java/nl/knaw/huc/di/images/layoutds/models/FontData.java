package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;

@Entity
public class FontData implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    private Blob data;
    private String originalFileName;

    public FontData() {

    }

    public FontData(Blob data, String originalFileName) {
        this.data = data;
        this.originalFileName = originalFileName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Blob getData() {
        return data;
    }

    public void setData(Blob data) {
        this.data = data;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
