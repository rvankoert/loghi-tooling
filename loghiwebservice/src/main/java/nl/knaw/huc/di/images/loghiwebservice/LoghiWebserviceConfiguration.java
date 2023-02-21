package nl.knaw.huc.di.images.loghiwebservice;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huc.di.images.loghiwebservice.configuration.ExtractBaseLinesExecutorServiceConfig;

import javax.validation.constraints.NotEmpty;

public class LoghiWebserviceConfiguration extends Configuration {
    @JsonProperty
    private String uploadLocation;

    @JsonProperty
    private ExtractBaseLinesExecutorServiceConfig extractBaseLinesExecutorServiceConfig;

    public String getUploadLocation() {
        return uploadLocation;
    }

    public ExtractBaseLinesExecutorServiceConfig getExtractBaseLinesExecutorServiceConfig() {
        return extractBaseLinesExecutorServiceConfig;
    }
}
