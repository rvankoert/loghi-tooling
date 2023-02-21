package nl.knaw.huc.di.images.loghiwebservice.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.setup.Environment;

import javax.validation.constraints.NotEmpty;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;

public class ExtractBaseLinesExecutorServiceConfig {

    @NotEmpty
    @JsonProperty
    private int maxThreads;

    @NotEmpty
    @JsonProperty
    private int queueLength;


    public ExecutorService createExectorService(Environment environment) {
        return environment.lifecycle()
                .executorService("ExtractBaselines")
                .maxThreads(maxThreads)
                .workQueue(new ArrayBlockingQueue<Runnable>(queueLength, true))
                .build();
    }
}
