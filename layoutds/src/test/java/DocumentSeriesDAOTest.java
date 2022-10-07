//import nl.knaw.huc.di.images.layoutds.DAO.DocumentSeriesDAO;
//import nl.knaw.huc.di.images.layoutds.StudentJpaConfig;
//import nl.knaw.huc.di.images.layoutds.models.DocumentSeries;
//import org.hamcrest.CoreMatchers;
//import org.hamcrest.Matchers;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.support.AnnotationConfigContextLoader;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {StudentJpaConfig.class}, loader = AnnotationConfigContextLoader.class)
//public class DocumentSeriesDAOTest {
//
//
//  @Test
//  public void getPrettySeriesReturnsTheSeriesOrderedBySeriesProperty() {
//    DocumentSeriesDAO documentSeriesDAO = new DocumentSeriesDAO();
//    saveSeries(documentSeriesDAO, "series1");
//    saveSeries(documentSeriesDAO, "series2");
//    saveSeries(documentSeriesDAO, "series3");
//
//    List<String> series = documentSeriesDAO.getPrettySeries().stream()
//                                           .map(DocumentSeries::getSeries)
//                                           .collect(Collectors.toList());
//
//    assertThat(series, Matchers.contains("series1","series2", "series3"));
//  }
//
//  private void saveSeries(DocumentSeriesDAO documentSeriesDAO, String name) {
//    DocumentSeries documentSeries2 = new DocumentSeries();
//    documentSeries2.setSeries(name);
//    documentSeriesDAO.save(documentSeries2);
//  }
//
//
//}
//
