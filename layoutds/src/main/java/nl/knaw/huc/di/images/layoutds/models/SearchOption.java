package nl.knaw.huc.di.images.layoutds.models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

public class SearchOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String address;
    private String elasticSearchIndex;
    private String elasticSearchBaseUri;
    private String title;
    private String mainText;

    private List<SearchOption> searchOptions;


}
