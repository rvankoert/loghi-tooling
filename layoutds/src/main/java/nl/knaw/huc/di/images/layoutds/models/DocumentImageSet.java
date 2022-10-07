package nl.knaw.huc.di.images.layoutds.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import nl.knaw.huc.di.images.layoutds.models.pim.IPimObject;
import nl.knaw.huc.di.images.layoutds.models.pim.PimFile;
import nl.knaw.huc.di.images.layoutds.models.pim.PimUser;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Entity
@XmlRootElement
@Table(indexes = {
        @Index(columnList = "id", name = "documentImageset_id_hidx", unique = true),
        @Index(columnList = "uuid", name = "documentImageset_uuid_hidx", unique = true),
        @Index(columnList = "imageset", name = "documentImageset_imageset_hidx"),
        @Index(columnList = "prettyName", name = "documentImageset_prettyName_hidx")
},
        uniqueConstraints = @UniqueConstraint(columnNames = {"pimuserid", "imageset"})
)

public class DocumentImageSet implements IPimObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Type(type = "text")
    private String imageset;
    @Type(type = "text")
    private String prettyName;
    @Type(type = "text")
    private String longName;
    private boolean publish;
    @Type(type = "text")
    private String license;
    @Type(type = "text")
    private String dcTitle;
    private String dcCreator;
    private String dcContributor;
    private String dcSubject;
    @Type(type = "text")
    private String dcDescription;
    private String dcPublisher;
    private String dcDate;
    private String dcType;
    private String dcFormat;
    private String dcIdentifier;
    private String dcSource;
    private String dcLanguage;
    private String dcRelation;
    private String dcCoverage;
    private String dcRights;

    @Type(type = "text")
    private String attribution;
    @Type(type = "text")
    private String logo;
    @Type(type = "text")
    @Column(unique = true, nullable = false)
    private String uri;
    @Type(type = "text")
    private String tag;
    @Type(type = "text")
    private String remoteUri;

//    @OneToMany(targetEntity = DocumentImage.class, mappedBy = "documentImageSet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @ElementCollection(targetClass = DocumentImage.class)

//    INSERT into documentimagedataset ( SELECT id, documentImagesetId from documentImage where documentImagesetId is not null)

    @ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST
    }, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name = "documentimagedataset",
            joinColumns = {@JoinColumn(name = "documentImageSetId")},
            inverseJoinColumns = {@JoinColumn(name = "documentImageId")},
            indexes = {
                    @Index(name = "idx_documentimagedataset_documentImageSetId", columnList = "documentImageSetId"),
                    @Index(name = "idx_documentimagedataset_documentImageId", columnList = "documentImageId")
            }
    )
    @OrderBy("pageorder ASC, uri ASC")
    private Set<DocumentImage> documentImages = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name = "pdfdocumentdataset",
            joinColumns = {@JoinColumn(name = "documentImageSetId")},
            inverseJoinColumns = {@JoinColumn(name = "pdfDocumentId")}
    )
    private Set<PdfDocument> pdfDocuments = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name = "xmldocumentdataset",
            joinColumns = {@JoinColumn(name = "documentImageSetId")},
            inverseJoinColumns = {@JoinColumn(name = "xmlDocumentId")}
    )
    private Set<XmlDocument> xmlDocuments = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(
            name = "pimfiledataset",
            joinColumns = {@JoinColumn(name = "documentImageSetId")},
            inverseJoinColumns = {@JoinColumn(name = "pimFileId")}
    )
    private Set<PimFile> pimFiles = new HashSet<>();


//    @ManyToOne(targetEntity = DocumentSeries.class, fetch = FetchType.EAGER)
//    @JoinColumn(name = "documentSeriesId")
//    private DocumentSeries documentSeries;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "documentImageSetCollection",
            joinColumns = {@JoinColumn(name = "documentImageSetId")},
            inverseJoinColumns = {@JoinColumn(name = "collectionId")}
    )
    @JsonIgnore
    private List<Collection> collections = Lists.newArrayList();

    private Date created;
    private Date updated;
    private Date deleted;

    @ManyToOne(targetEntity = PimUser.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "pimUserId")
    @JsonIgnore
    private PimUser owner;

    // Leave Boolean database value can be null.
    // The code will crash, trying to convert null to a boolean.
    private Boolean publicDocumentImageSet;

    @ManyToOne(targetEntity = ElasticSearchIndex.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "elasticSearchIndexId")
    private ElasticSearchIndex elasticSearchIndex;

    @ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST
    }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "documentImageSubSets",
            joinColumns = {@JoinColumn(name = "parentId")},
            inverseJoinColumns = {@JoinColumn(name = "childId")}
    )
    private Set<DocumentImageSet> subSets;

    @ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST
    }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "documentImageSubSets",
            joinColumns = {@JoinColumn(name = "childId")},
            inverseJoinColumns = {@JoinColumn(name = "parentId")}
    )
    private Set<DocumentImageSet> superSets;

    @Type(type = "text")
    private String description;

    public DocumentImageSet() {
        this.uuid = UUID.randomUUID();
        this.created = new Date();
        this.publish = false;
        subSets = new HashSet<>();
        superSets = new HashSet<>();
    }

    public void addDataSet(Collection collection) {
        this.collections.add(collection);
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    public String getLongName() {
        if (Strings.isNullOrEmpty(longName)) {
            return getPrettyName();
        }

        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getImageset() {
        return imageset;
    }

    public void setImageset(String imageset) {
        this.imageset = imageset;
    }

    public boolean getPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getDcTitle() {
        return dcTitle;
    }

    public void setDcTitle(String dcTitle) {
        this.dcTitle = dcTitle;
    }

    public String getDcCreator() {
        return dcCreator;
    }

    public void setDcCreator(String dcCreator) {
        this.dcCreator = dcCreator;
    }

    public String getDcContributor() {
        return dcContributor;
    }

    public void setDcContributor(String dcContributor) {
        this.dcContributor = dcContributor;
    }

    public String getDcSubject() {
        return dcSubject;
    }

    public void setDcSubject(String dcSubject) {
        this.dcSubject = dcSubject;
    }

    public String getDcDescription() {
        return dcDescription;
    }

    public void setDcDescription(String dcDescription) {
        this.dcDescription = dcDescription;
    }

    public String getDcPublisher() {
        return dcPublisher;
    }

    public void setDcPublisher(String dcPublisher) {
        this.dcPublisher = dcPublisher;
    }

    public String getDcDate() {
        return dcDate;
    }

    public void setDcDate(String dcDate) {
        this.dcDate = dcDate;
    }

    public String getDcType() {
        return dcType;
    }

    public void setDcType(String dcType) {
        this.dcType = dcType;
    }

    public String getDcFormat() {
        return dcFormat;
    }

    public void setDcFormat(String dcFormat) {
        this.dcFormat = dcFormat;
    }

    public String getDcIdentifier() {
        return dcIdentifier;
    }

    public void setDcIdentifier(String dcIdentifier) {
        this.dcIdentifier = dcIdentifier;
    }

    public String getDcSource() {
        return dcSource;
    }

    public void setDcSource(String dcSource) {
        this.dcSource = dcSource;
    }

    public String getDcLanguage() {
        return dcLanguage;
    }

    public void setDcLanguage(String dcLanguage) {
        this.dcLanguage = dcLanguage;
    }

    public String getDcRelation() {
        return dcRelation;
    }

    public void setDcRelation(String dcRelation) {
        this.dcRelation = dcRelation;
    }

    public String getDcCoverage() {
        return dcCoverage;
    }

    public void setDcCoverage(String dcCoverage) {
        this.dcCoverage = dcCoverage;
    }

    public String getDcRights() {
        return dcRights;
    }

    public void setDcRights(String dcRights) {
        this.dcRights = dcRights;
    }

    /**
     * Retrieves the images directly coupled to the image set and the images coupled to the subsets.
     * Uses lazy loaded documentImages and subSets
     *
     * @return
     */
    @JsonIgnore
    public Stream<DocumentImage> getAllImages() {
        final Stream<DocumentImage> subSetImages = subSets.stream()
                .flatMap(DocumentImageSet::getAllImages);
        return Stream.concat(documentImages.stream(), subSetImages);
    }

    /**
     * Retrieves the images directly linked to this DocumentImageSet.
     * To retrieve the from the sub sets as well use getAllImages.
     *
     * @return
     */
    public Set<DocumentImage> getDocumentImages() {
        return documentImages;
    }

    public void setDocumentImages(Set<DocumentImage> documentImages) {
        this.documentImages = documentImages;
    }

    public void addDocumentImage(DocumentImage image) {
        documentImages.add(image);
    }

    public void removeDocumentImage(DocumentImage documentImage) {
        documentImages.remove(documentImage);
    }

//    public DocumentSeries getDocumentSeries() {
//        return this.documentSeries;
//    }
//
//    public void setDocumentSeries(DocumentSeries documentSeries) {
//        this.documentSeries = documentSeries;
//    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public PimUser getOwner() {
        return owner;
    }

    public void setOwner(PimUser owner) {
        this.owner = owner;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isPublicDocumentImageSet() {
        return publicDocumentImageSet == null || publicDocumentImageSet;
    }

    public void setPublicDocumentImageSet(boolean publicDocumentImageSet) {
        this.publicDocumentImageSet = publicDocumentImageSet;
    }

    public boolean validate() {

        return getImageset() != null && getImageset().trim().length() > 0 && uri != null && uri.trim().length() > 0;
    }


    public Set<XmlDocument> getXmlDocuments() {
        return xmlDocuments;
    }

    public void setXmlDocuments(Set<XmlDocument> xmlDocuments) {
        this.xmlDocuments = xmlDocuments;
    }

    public void addXmlDocument(XmlDocument xmlDocument) {
        xmlDocuments.add(xmlDocument);
    }


    public Set<PdfDocument> getPdfDocuments() {
        return pdfDocuments;
    }

    public void setPdfDocuments(Set<PdfDocument> pdfDocuments) {
        this.pdfDocuments = pdfDocuments;
    }

    public void addPdfDocument(PdfDocument pdfDocument) {
        pdfDocuments.add(pdfDocument);
    }


    public Set<PimFile> getPimFiles() {
        return pimFiles;
    }

    public void setPimFiles(Set<PimFile> pimFiles) {
        this.pimFiles = pimFiles;
    }

    public void addPimFile(PimFile pimFile) {
        pimFiles.add(pimFile);
    }

    public ElasticSearchIndex getElasticSearchIndex() {
        return elasticSearchIndex;
    }

    public void setElasticSearchIndex(ElasticSearchIndex elasticSearchIndex) {
        this.elasticSearchIndex = elasticSearchIndex;
    }

    public void removeSubset(DocumentImageSet documentImageSubSet) {
        subSets.remove(documentImageSubSet);
    }

    public void addSubSet(DocumentImageSet subSet) {
        final int subSetToAddDepth = subSet.countDepthSubsets(0);
        if (subSetToAddDepth > 3) {
            throw new IllegalArgumentException("Sub set to has too many layers of sub sets");
        }

        final int superSetDepth = this.countDepthSuperSets(0);
        if (superSetDepth > 3) {
            throw new IllegalArgumentException("This set to has too many layers of super sets");
        }

        if ((subSetToAddDepth + superSetDepth) > 3) {
            throw new IllegalArgumentException("There will be too many layers when sub set is added");
        }

        HashSet<DocumentImageSet> checkedImageSets = new HashSet<>();
        if (isSomewhereInTheFamilyTree(superSets, subSets, subSet, checkedImageSets)) {
            throw new IllegalArgumentException("The sub set is already in the tree.");
        }

        subSets.add(subSet);
    }

    private static boolean isSomewhereInTheFamilyTree(Set<DocumentImageSet> superSets, Set<DocumentImageSet> subSets, DocumentImageSet subSetToAdd, final Set<DocumentImageSet> checkedImageSets) {
        for (DocumentImageSet superSet : superSets.stream().filter(imageSet -> !checkedImageSets.contains(imageSet)).collect(Collectors.toSet())) {
            if (superSet.uuid.equals(subSetToAdd.uuid)) {
                return true;
            }

            checkedImageSets.add(superSet);
            if (isSomewhereInTheFamilyTree(superSet.superSets, superSet.subSets, subSetToAdd, checkedImageSets)) {
                return true;
            }
        }

        for (DocumentImageSet subSet : subSets.stream().filter(imageSet -> !checkedImageSets.contains(imageSet)).collect(Collectors.toSet())) {
            if (subSet.uuid.equals(subSetToAdd.uuid)) {
                return true;
            }

            checkedImageSets.add(subSet);
            if (isSomewhereInTheFamilyTree(subSet.superSets, subSet.subSets, subSetToAdd, checkedImageSets)) {
                return true;
            }
        }

        return false;
    }

    private int countDepthSuperSets(int depth) {
        if (depth >= 3) {
            return depth;
        }
        final Optional<Integer> highestDepth = superSets.stream()
                .map(superset -> superset.countDepthSuperSets(depth + 1))
                .max(Comparator.naturalOrder());
        return highestDepth.orElse(depth + 1);
    }

    /**
     * Only use for testing
     */
    void addSuperSet(DocumentImageSet superSet) {
        superSets.add(superSet);
    }

    private int countDepthSubsets(int depth) {
        if (depth >= 3) {
            return depth;
        }
        final Optional<Integer> highestDepth = getSubSets().stream()
                .map(subSet -> subSet.countDepthSubsets(depth + 1))
                .max(Comparator.naturalOrder());
        return highestDepth.orElse(depth + 1);
    }

    @JsonIgnore
    public Set<DocumentImageSet> getSubSets() {
        return subSets;
    }

    public void setSubSets(Set<DocumentImageSet> subSets) {
        this.subSets = subSets;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentImageSet that = (DocumentImageSet) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "DocumentImageSet{" +
                "uuid=" + uuid +
                '}';
    }


    public String getRemoteUri() {
        return remoteUri;
    }

    public void setRemoteUri(String remoteUri) {
        this.remoteUri = remoteUri;
    }

    @JsonIgnore
    public Set<DocumentImageSet> getSuperSets() {
        return superSets;
    }
}