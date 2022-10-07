package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Book implements IHasChapters{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date publicationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    private ArrayList<DocumentImage> documentImages;

    private ArrayList<Author> authors;

    private String series;
    private String imageset;
    private String title;
    private String shortTitle;
    private String subTitle;
    private int year;
    private String summary;
    private String uri;
    private String OCLCNumber;
    private String ISBN;


    private ArrayList<Chapter> chapters;
    private ArrayList<Section> sections;

    public ArrayList<DocumentImage> getDocumentImages() {
        return documentImages;
    }

    public void setDocumentImages(ArrayList<DocumentImage> documentImages) {
        this.documentImages = documentImages;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getImageset() {
        return imageset;
    }

    public void setImageset(String imageset) {
        this.imageset = imageset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public ArrayList<Chapter> getChapters() {
        if (chapters==null){
            chapters = new ArrayList<>();
        }
        if (chapters.size()>1) {
            chapters.sort(Comparator.comparing(Chapter::getPage));
        }
        return chapters;
    }

    public void setChapters(ArrayList<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public void addChapter(Chapter chapter) {
        if (chapters== null){
            chapters = new ArrayList<>();
        }
        chapters.add(chapter);
    }

    public void addAuthor(String authorString) {
        if (authors == null){
            authors = new ArrayList<>();
        }
        Author author = new Author();
        author.setName(authorString);
        authors.add(author);
    }

    private ArrayList<String> metaData;
    public void addMeta(String text) {

        if (metaData == null){
            metaData = new ArrayList<>();
        }
        metaData.add(text);
    }

    private ArrayList<String> keywords;
    public void addKeyword(String text) {

        if (keywords == null){
            keywords = new ArrayList<>();
        }
        keywords.add(text);
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate= publicationDate;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getOCLCNumber() {
        return OCLCNumber;
    }

    public void setOCLCNumber(String OCLCNumber) {
        this.OCLCNumber = OCLCNumber;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String howToCite(){
        StringBuilder citation = new StringBuilder();
        for (Author author: getAuthors()){
            citation.append(author.toString()).append(", ");
        }
        citation.append(getTitle());
        citation.append(", ");
        citation.append("Huygens Instituut voor Nederlandse Geschiedenis");
        citation.append(", ");
        citation.append(getUri());
        citation.append(", ");
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd");
        citation.append(dt1.format(new Date()));

        return citation.toString();
    }

}
