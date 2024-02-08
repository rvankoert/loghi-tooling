package nl.knaw.huc.di.images.loghiwebservice.resources;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class LoghiHTRMergePageXMLResourceTest {

    // OLD STYLE TESTS LoghiHTRMergePageXMLResourceTest

    @Test
    public void getResourceOldStyleResultLineTest() {
        LoghiHTRMergePageXMLResource.ResultLine result = LoghiHTRMergePageXMLResource.getResultLine("nl-hanatest.png\t0.10\tDit is een testtranscriptie");
        assertThat(result.getFilename(), is("nl-hanatest"));
        assertThat(result.getMetadata(), is("[]")); // should be [] if not provided
        assertThat(result.getConfidence(), is(0.10)); // confidence is a double
        assertThat(result.getText().toString(), is("Dit is een testtranscriptie"));
    }

    @Test
    public void getOldStyleResultLineTestRealistic() {
        LoghiHTRMergePageXMLResource.ResultLine result = LoghiHTRMergePageXMLResource.getResultLine("NL-HaNA_2.09.09_28_0791-line_34324324322.png\t0.7871581315994263\tdit is een andere teststring");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_28_0791-line_34324324322"));
        assertThat(result.getMetadata(), is("[]")); // should be [] if not provided
        assertThat(result.getConfidence(), is(0.7871581315994263)); // confidence is a double
        assertThat(result.getText().toString(), is("dit is een andere teststring"));
    }

    @Test
    public void getOldStyleResultLineTestRealisticEmptyPred() {
        LoghiHTRMergePageXMLResource.ResultLine result = LoghiHTRMergePageXMLResource.getResultLine("NL-HaNA_2.09.09_28_0791-line_34324324322.png\t0.7871581315994263\t");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_28_0791-line_34324324322"));
        assertThat(result.getMetadata(), is("[]")); // should be [] if not provided
        assertThat(result.getConfidence(), is(0.7871581315994263)); // confidence is a double
        assertThat(result.getText().toString(), is(""));
    }

    // NEW STYLE TESTS LoghiHTRMergePageXMLResourceTest

    @Test
    public void getNewStyleResultLineTest() {
        LoghiHTRMergePageXMLResource.ResultLine result = LoghiHTRMergePageXMLResource.getResultLine("nl-hanatest.png\t{hier was json}\t0.10\tDit is een testtranscriptie");
        assertThat(result.getFilename(), is("nl-hanatest"));
        assertThat(result.getMetadata(), is("{hier was json}"));
        assertThat(result.getConfidence(), is(0.10)); // confidence is a double
        assertThat(result.getText().toString(), is("Dit is een testtranscriptie"));
    }

    @Test
    public void getNewStyleResultLineTestRealistic() {
        LoghiHTRMergePageXMLResource.ResultLine result = LoghiHTRMergePageXMLResource.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\tnog een andere *3 variatie");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(result.getText().toString(), is("nog een andere *3 variatie"));
    }

    @Test
    public void getNewStyleResultLineTestRealisticEmptyPred() {
        LoghiHTRMergePageXMLResource.ResultLine result = LoghiHTRMergePageXMLResource.getResultLine("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses.png\t{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}\t0.5223081350326538\t");
        assertThat(result.getFilename(), is("NL-HaNA_2.09.09_593_0418-line_9seafaw-341s-ewa-adfzxf-ffses"));
        assertThat(result.getMetadata(), is("{'model_name': 'generic-finetuned', 'test_invalid': 'NOT_FOUND'}"));
        assertThat(result.getConfidence(), is(0.5223081350326538)); // confidence is a double
        assertThat(result.getText().toString(), is(""));
    }

}