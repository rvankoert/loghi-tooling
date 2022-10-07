package nl.knaw.huc.di.images.layoutds.models.pim;

import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class ManuscriptMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "text")
    private String title;

    @Type(type = "text")
    private String dateProduction; // can be multiple
    //provenance;
    @Type(type = "text")
    private String placeOfProduction;
    @Type(type = "text")
    private String possesors;
    @Type(type = "text")
    private String possessingInstitutions;
    @Type(type = "text")
    private String aqcuisition;
    // identifiers
    @Type(type = "text")
    private String settlement;
    @Type(type = "text")
    private String repository;
    @Type(type = "text")
    private String shelfMark;
    @Type(type = "text")
    private String collection;
    @Type(type = "text")
    private String manuscriptName;

    @Type(type = "text")
    private String scriptClassification;
    @Type(type = "text")
    private String descriptionHand;
    @Type(type = "text")
    private String descriptionScript;

    //    content
    @Type(type = "text")
    private String manuscriptParts;
    @Type(type = "text")
    private String structureBook;

    @Type(type = "text")
    private String languages;

    //content
    @Type(type = "text")
    private String category; //law, philosophy, bibles, liturgical ms, book of hours, religious commentarie
    //writingSupport
    @Type(type = "text")
    private String writingSupport; // parchment, paper, mixed
    @Type(type = "text")
    private String illuminations;
    @Type(type = "text")
    private String bookworms;
    @Type(type = "text")
    private String paws;

    @Type(type = "text")
    private String location;
    @Type(type = "text")
    private String collectionName;
    @Type(type = "text")
    private String DOI;
    @Type(type = "text")
    private String titleEnglish;
    @Type(type = "text")
    private String material;
    @Type(type = "text")
    private String placeOfOriginEnglish;
    @Type(type = "text")
    private String dateOfOriginEnglish;
    @Type(type = "text")
    private String numberOfPages;
    @Type(type = "text")
    private String dimensions;
    @Type(type = "text")
    private String onlineSince;
    @Type(type = "text")
    private String summaryEnglish;
    @Type(type = "text")
    private String persons;
    @Type(type = "text")
    private String century;
    @Type(type = "text")
    private String textLanguage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDateProduction() {
        return dateProduction;
    }

    public void setDateProduction(String dateProduction) {
        this.dateProduction = dateProduction;
    }

    public String getPlaceOfProduction() {
        return placeOfProduction;
    }

    public void setPlaceOfProduction(String placeOfProduction) {
        this.placeOfProduction = placeOfProduction;
    }

    public String getPossesors() {
        return possesors;
    }

    public void setPossesors(String possesors) {
        this.possesors = possesors;
    }

    public String getPossessingInstitutions() {
        return possessingInstitutions;
    }

    public void setPossessingInstitutions(String possessingInstitutions) {
        this.possessingInstitutions = possessingInstitutions;
    }

    public String getAqcuisition() {
        return aqcuisition;
    }

    public void setAqcuisition(String aqcuisition) {
        this.aqcuisition = aqcuisition;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getShelfMark() {
        return shelfMark;
    }

    public void setShelfMark(String shelfMark) {
        this.shelfMark = shelfMark;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getManuscriptName() {
        return manuscriptName;
    }

    public void setManuscriptName(String manuscriptName) {
        this.manuscriptName = manuscriptName;
    }

    public String getScriptClassification() {
        return scriptClassification;
    }

    public void setScriptClassification(String scriptClassification) {
        this.scriptClassification = scriptClassification;
    }

    public String getDescriptionHand() {
        return descriptionHand;
    }

    public void setDescriptionHand(String descriptionHand) {
        this.descriptionHand = descriptionHand;
    }

    public String getDescriptionScript() {
        return descriptionScript;
    }

    public void setDescriptionScript(String descriptionScript) {
        this.descriptionScript = descriptionScript;
    }

    public String getManuscriptParts() {
        return manuscriptParts;
    }

    public void setManuscriptParts(String manuscriptParts) {
        this.manuscriptParts = manuscriptParts;
    }

    public String getStructureBook() {
        return structureBook;
    }

    public void setStructureBook(String structureBook) {
        this.structureBook = structureBook;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWritingSupport() {
        return writingSupport;
    }

    public void setWritingSupport(String writingSupport) {
        this.writingSupport = writingSupport;
    }

    public String getIlluminations() {
        return illuminations;
    }

    public void setIlluminations(String illuminations) {
        this.illuminations = illuminations;
    }

    public String getBookworms() {
        return bookworms;
    }

    public void setBookworms(String bookworms) {
        this.bookworms = bookworms;
    }

    public String getPaws() {
        return paws;
    }

    public void setPaws(String paws) {
        this.paws = paws;
    }
}
