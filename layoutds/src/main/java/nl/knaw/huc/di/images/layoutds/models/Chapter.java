package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Chapter implements IHasChapters, IBookSection{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer page;
    private String title;
    private String summary;
    private String from;
    private String to;
    private Date date;
    private ArrayList<Author> authors;
    private ArrayList<DocumentImage> documentImages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String name;

    private ArrayList<Chapter> chapters;

    public ArrayList<Chapter> getChapters(){
        if (chapters==null){
            chapters = new ArrayList<>();
        }
        if (chapters.size()>1) {
            chapters.sort(Comparator.comparing(Chapter::getPage));
        }
        return chapters;
    }

    @Override
    public void addChapter(Chapter chapter) {
        if (chapters== null){
            chapters = new ArrayList<>();
        }
        chapters.add(chapter);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPage() {
        if (page==null){
            return 0;
        }
        return page;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
