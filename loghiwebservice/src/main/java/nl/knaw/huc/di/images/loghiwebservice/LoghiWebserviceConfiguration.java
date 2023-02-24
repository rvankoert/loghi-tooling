package nl.knaw.huc.di.images.loghiwebservice;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import nl.knaw.huc.di.images.loghiwebservice.configuration.ExecutorServiceConfig;

import java.util.concurrent.ExecutorService;

public class LoghiWebserviceConfiguration extends Configuration {
    @JsonProperty
    private String uploadLocation;

    @JsonProperty
    private String p2palaConfigFile;

    @JsonProperty
    private ExecutorServiceConfig extractBaseLinesExecutorServiceConfig;

    @JsonProperty
    private ExecutorServiceConfig cutFromImageBasedOnPageXmlExecutorServiceConfig;

    @JsonProperty
    private ExecutorServiceConfig loghiHTRMergePageXMLResourceExecutorServiceConfig;

    @JsonProperty
    private ExecutorServiceConfig recalculateReadingOrderNewResourceExecutorServiceConfig;

    @JsonProperty
    private ExecutorServiceConfig splitPageXMLTextLineIntoWordsResourceExecutorServiceConfig;

    @JsonProperty
    private ExecutorServiceConfig detectLanguageOfPageXmlResourceExecutorService;

    public String getUploadLocation() {
        return uploadLocation;
    }

   
    public String getP2alaConfigFile() {
        return p2palaConfigFile;
    }

    public ExecutorService createExtractBaseLinesExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return extractBaseLinesExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService createCutFromImageBasedOnPageXmlExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return cutFromImageBasedOnPageXmlExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService createLoghiHTRMergePageXMLResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return loghiHTRMergePageXMLResourceExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService createRecalculateReadingOrderNewResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return recalculateReadingOrderNewResourceExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService createSplitPageXMLTextLineIntoWordsResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return splitPageXMLTextLineIntoWordsResourceExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService createDetectLanguageOfPageXmlResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return detectLanguageOfPageXmlResourceExecutorService.createExecutorService(environment, metricRegistry);
    }
}
