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

    public ExecutorServiceConfig getExtractBaseLinesExecutorServiceConfig() {
        return extractBaseLinesExecutorServiceConfig;
    }

    public String getP2alaConfigFile() {
        return p2palaConfigFile;
    }

    public ExecutorService getCutFromImageBasedOnPageXmlExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return cutFromImageBasedOnPageXmlExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService getLoghiHTRMergePageXMLResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return loghiHTRMergePageXMLResourceExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService getRecalculateReadingOrderNewResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return recalculateReadingOrderNewResourceExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService getSplitPageXMLTextLineIntoWordsResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return splitPageXMLTextLineIntoWordsResourceExecutorServiceConfig.createExecutorService(environment, metricRegistry);
    }

    public ExecutorService getDetectLanguageOfPageXmlResourceExecutorService(Environment environment, MetricRegistry metricRegistry) {
        return detectLanguageOfPageXmlResourceExecutorService.createExecutorService(environment, metricRegistry);
    }
}
