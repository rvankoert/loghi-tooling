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

    public String getUploadLocation() {
        return uploadLocation;
    }

    public ExecutorServiceConfig getExtractBaseLinesExecutorServiceConfig() {
        return extractBaseLinesExecutorServiceConfig;
    }

    public String getP2alaConfigFile() {
        return p2palaConfigFile;
    }

    public ExecutorServiceConfig getCutFromImageBasedOnPageXmlExecutorServiceConfig() {
        return cutFromImageBasedOnPageXmlExecutorServiceConfig;
    }

    public ExecutorServiceConfig getLoghiHTRMergePageXMLResourceExecutorServiceConfig() {
        return loghiHTRMergePageXMLResourceExecutorServiceConfig;
    }

    public ExecutorService getRecalculateReadingOrderNewResourceExecutorService(Environment environment) {
        return recalculateReadingOrderNewResourceExecutorServiceConfig.createExecutorService(environment);
    }
}
