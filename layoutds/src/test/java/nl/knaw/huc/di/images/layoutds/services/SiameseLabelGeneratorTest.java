package nl.knaw.huc.di.images.layoutds.services;

import nl.knaw.huc.di.images.layoutds.models.pim.PimFieldDefinition;
import nl.knaw.huc.di.images.layoutds.services.SiameseLabelGenerator.FieldValueRetriever;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SiameseLabelGeneratorTest {

    public static final String IMAGE_REMOTE_URI = "http://images.org/image.jpg";
    private SiameseLabelGenerator siameseLabelGenerator;
    private FieldValueRetriever fieldValueRetriever;

    @Before
    public void setUp() throws Exception {
        fieldValueRetriever = mock(FieldValueRetriever.class);
        siameseLabelGenerator = new SiameseLabelGenerator(fieldValueRetriever);
    }

    @Test
    public void generateLabelReplacesNonAlphaNumericCharactersWithUnderscores() {
        final PimFieldDefinition pimField = fieldWithLabel("pimField1", "label 1,2!'");

        final String label = siameseLabelGenerator.generateLabel(IMAGE_REMOTE_URI, List.of(pimField));

        assertThat(label, is("label_1_2__"));
    }

    @Test
    public void generateLabelConcatenatesTheFieldValuesWithUnderscores() {
        final PimFieldDefinition pimField1 = fieldWithLabel("pimField1", "label1");
        final PimFieldDefinition pimField2 = fieldWithLabel("pimField2", "label2");
        final PimFieldDefinition pimField3 = fieldWithLabel("pimField3", "label3");
        final List<PimFieldDefinition> fields = List.of(pimField1, pimField2, pimField3);

        final String label = siameseLabelGenerator.generateLabel(IMAGE_REMOTE_URI, fields);

        assertThat(label, is("label1_label2_label3"));
    }

    private PimFieldDefinition fieldWithLabel(String fieldName, String label) {
        final PimFieldDefinition pimFieldDefinition = new PimFieldDefinition();
        pimFieldDefinition.setName(fieldName);
        when(fieldValueRetriever.getFieldValueFor(eq(IMAGE_REMOTE_URI), ArgumentMatchers.refEq(pimFieldDefinition)))
                .thenReturn(label);
        return pimFieldDefinition;
    }

    @Test
    public void generateLabelReturnsAnEmptyStringWhenNoValuesExistForImageAndPimFieldDefinitions() {
        final PimFieldDefinition pimField1 = fieldWithLabel("pimField1", null);
        final PimFieldDefinition pimField2 = fieldWithLabel("pimField2", null);
        final PimFieldDefinition pimField3 = fieldWithLabel("pimField3", null);
        final List<PimFieldDefinition> fields = List.of(pimField1, pimField2, pimField3);

        final String label = siameseLabelGenerator.generateLabel(IMAGE_REMOTE_URI, fields);

        assertThat(label, isEmptyString());
    }

}