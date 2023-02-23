package nl.knaw.huc.di.images.loghiwebservice;

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

    public ExecutorService getCutFromImageBasedOnPageXmlExecutorService(Environment environment) {
        return cutFromImageBasedOnPageXmlExecutorServiceConfig.createExecutorService((environment));
    }

    public ExecutorService getLoghiHTRMergePageXMLResourceExecutorService(Environment environment) {
        return loghiHTRMergePageXMLResourceExecutorServiceConfig.createExecutorService(environment);
    }

    public ExecutorService getRecalculateReadingOrderNewResourceExecutorService(Environment environment) {
        return recalculateReadingOrderNewResourceExecutorServiceConfig.createExecutorService(environment);
    }

    public ExecutorService getSplitPageXMLTextLineIntoWordsResourceExecutorService(Environment environment) {
        return splitPageXMLTextLineIntoWordsResourceExecutorServiceConfig.createExecutorService(environment);
    }

    public ExecutorService getDetectLanguageOfPageXmlResourceExecutorService(Environment environment) {
        return detectLanguageOfPageXmlResourceExecutorService.createExecutorService(environment);
    }
}
