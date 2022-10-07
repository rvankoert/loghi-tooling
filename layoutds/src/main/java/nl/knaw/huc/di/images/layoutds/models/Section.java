package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private List<Section> sections;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
