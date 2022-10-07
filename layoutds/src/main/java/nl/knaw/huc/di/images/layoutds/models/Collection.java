package nl.knaw.huc.di.images.layoutds.models;

import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.List;


@Entity
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // TODO RUTGERCHECK: add recursive collections
    //    private List<Collection> collections; // A collection can contain zero or more collections

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "documentImageSetCollection",
            joinColumns = {@JoinColumn(name = "collectionId")},
            inverseJoinColumns = {@JoinColumn(name = "documentImageSetId")}
    )
    private List<DocumentImageSet> documentImageSets = Lists.newArrayList(); // A collection can contain zero or more books

    public void addDocumentImageSet(DocumentImageSet documentImageSet) {
        documentImageSets.add(documentImageSet);
    }

    public List<DocumentImageSet> getImages() {
        return documentImageSets;
    }

    public void setImages(List<DocumentImageSet> documentImageSets) {
        this.documentImageSets = documentImageSets;
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

    public List<DocumentImageSet> getDocumentImageSets() {
        return documentImageSets;
    }

    public void setDocumentImageSets(List<DocumentImageSet> documentImageSets) {
        this.documentImageSets = documentImageSets;
    }
}
