package nl.knaw.huc.di.images.loghiwebservice;

import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huc.di.images.loghiwebservice.resources.*;

import java.util.concurrent.ExecutorService;

public class LoghiWebserviceApplication extends Application<LoghiWebserviceConfiguration> {
    public static void main(String[] args) throws Exception {
        new LoghiWebserviceApplication().run(args);
    }

    @Override
    public String getName() {
        return "loghi-webservice";
    }

    @Override
    public void initialize(Bootstrap<LoghiWebserviceConfiguration> bootstrap) {
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    }

    @Override
    public void run(LoghiWebserviceConfiguration configuration, Environment environment) {
        final ExecutorService extractBaselinesExecutor = configuration.getExtractBaseLinesExecutorServiceConfig().createExecutorService(environment);
        final String uploadLocation = configuration.getUploadLocation();
        System.out.println("Storage location: " + uploadLocation);
        final ExtractBaselinesResource resource = new ExtractBaselinesResource(extractBaselinesExecutor, uploadLocation, configuration.getP2alaConfigFile());
        environment.jersey().register(resource);

        final ExecutorService cutFromImageExecutorService = configuration.getCutFromImageBasedOnPageXmlExecutorService(environment);
        final CutFromImageBasedOnPageXMLNewResource cutFromImageBasedOnPageXMLNewResource = new CutFromImageBasedOnPageXMLNewResource(cutFromImageExecutorService, uploadLocation);
        environment.jersey().register(cutFromImageBasedOnPageXMLNewResource);

        final ExecutorService executorService = configuration.getLoghiHTRMergePageXMLResourceExecutorService(environment);
        final LoghiHTRMergePageXMLResource loghiHTRMergePageXMLResource = new LoghiHTRMergePageXMLResource(uploadLocation, executorService);
        environment.jersey().register(loghiHTRMergePageXMLResource);

        final ExecutorService recalculateReadingOrderNewResourceExecutorService = configuration.getRecalculateReadingOrderNewResourceExecutorService(environment);
        final RecalculateReadingOrderNewResource recalculateReadingOrderNewResource = new RecalculateReadingOrderNewResource(recalculateReadingOrderNewResourceExecutorService, uploadLocation);
        environment.jersey().register(recalculateReadingOrderNewResource);

        final ExecutorService splitPageXMLTextLineIntoWordsResourceExecutorService = configuration.getSplitPageXMLTextLineIntoWordsResourceExecutorService(environment);
        final SplitPageXMLTextLineIntoWordsResource splitPageXMLTextLineIntoWordsResource = new SplitPageXMLTextLineIntoWordsResource(splitPageXMLTextLineIntoWordsResourceExecutorService, uploadLocation);
        environment.jersey().register(splitPageXMLTextLineIntoWordsResource);

    }

}