package nl.knaw.huc.di.images.loghiwebservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import nl.knaw.huc.di.images.loghiwebservice.configuration.ExecutorServiceConfig;

public class LoghiWebserviceConfiguration extends Configuration {
    @JsonProperty
    private String uploadLocation;

    @JsonProperty
    private String p2palaConfigFile;

    @JsonProperty
    private ExecutorServiceConfig extractBaseLinesExecutorServiceConfig;

    @JsonProperty
    private ExecutorServiceConfig cutFromImageBasedOnPageXmlExecutorServiceConfig;

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
}
