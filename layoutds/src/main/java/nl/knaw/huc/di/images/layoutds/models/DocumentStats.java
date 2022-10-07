package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.DAO.DataItem;

import java.math.BigInteger;
import java.util.ArrayList;

public class DocumentStats {
    private BigInteger total;
    private long tesseract3Analyzed;
    private long tesseract4Analyzed;
    private long layoutAnalyzed;

    private ArrayList<DataItem> data;
    private ArrayList<DataItem> layoutDayData;
    private ArrayList<DataItem> tesseract3DayData;
    private ArrayList<DataItem> tesseract4DayData;
    private Long tesseract4HocrAnalyzed;
    private ArrayList<DataItem> tesseract4HocrDayData;
    private Long toBeSentToElasticSearch;
    private Long frogNerAnalyzed;
    private Long tesseract4BestHocrAnalyzed;
    private ArrayList<DataItem> tesseract4BestHocrDayData;
    private long frogNerBestAnalyzed;
    private ArrayList<DataItem> frogNerBestAnalyzedDayData;

    public BigInteger getTotal() {
        return total;
    }

    public void setTotal(BigInteger total) {
        this.total = total;
    }


    public long getLayoutAnalyzed() {
        return layoutAnalyzed;
    }

    public void setLayoutAnalyzed(long layoutAnalyzed) {
        this.layoutAnalyzed = layoutAnalyzed;
    }

    public ArrayList<DataItem> getData() {
        return data;
    }

    public void setData(ArrayList<DataItem> data) {
        this.data = data;
    }

    public ArrayList<DataItem> getLayoutDayData() {
        return layoutDayData;
    }

    public void setLayoutDayData(ArrayList<DataItem> layoutDayData) {
        this.layoutDayData = layoutDayData;
    }

    public Long getTesseract4HocrAnalyzed() {
        return tesseract4HocrAnalyzed;
    }

    public void setTesseract4HocrAnalyzed(Long tesseract4HocrAnalyzed) {
        this.tesseract4HocrAnalyzed = tesseract4HocrAnalyzed;
    }

    public ArrayList<DataItem> getTesseract4HocrDayData() {
        return tesseract4HocrDayData;
    }

    public void setTesseract4HocrDayData(ArrayList<DataItem> tesseract4HocrDayData) {
        this.tesseract4HocrDayData = tesseract4HocrDayData;
    }

    public Long getToBeSentToElasticSearch() {
        return toBeSentToElasticSearch;
    }

    public void setToBeSentToElasticSearch(Long toBeSentToElasticSearch) {
        this.toBeSentToElasticSearch = toBeSentToElasticSearch;
    }

    public ArrayList<DataItem> getFrogNerBestAnalyzedDayData() {
        return frogNerBestAnalyzedDayData;
    }

    public void setfrogNerBestAnalyzedDayData(ArrayList<DataItem> frogNerBestAnalyzedDayData) {
        this.frogNerBestAnalyzedDayData = frogNerBestAnalyzedDayData;
    }

    public Long getTesseract4BestHocrAnalyzed() {
        return tesseract4BestHocrAnalyzed;
    }

    public void setTesseract4BestHocrAnalyzed(Long tesseract4BestHocrAnalyzed) {
        this.tesseract4BestHocrAnalyzed = tesseract4BestHocrAnalyzed;
    }

    public ArrayList<DataItem> getTesseract4BestHocrDayData() {
        return tesseract4BestHocrDayData;
    }

    public void setTesseract4BestHocrDayData(ArrayList<DataItem> tesseract4BestHocrDayData) {
        this.tesseract4BestHocrDayData = tesseract4BestHocrDayData;
    }

    public long getFrogNerBestAnalyzed() {
        return frogNerBestAnalyzed;
    }

    public void setFrogNerBestAnalyzed(long frogNerBestAnalyzed) {
        this.frogNerBestAnalyzed = frogNerBestAnalyzed;
    }
}
