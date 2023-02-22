package nl.knaw.huc.di.images.loghiwebservice;

import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huc.di.images.loghiwebservice.resources.CutFromImageBasedOnPageXMLNewResource;
import nl.knaw.huc.di.images.loghiwebservice.resources.ExtractBaselinesResource;
import nl.knaw.huc.di.images.loghiwebservice.resources.LoghiHTRMergePageXMLResource;
import nl.knaw.huc.di.images.loghiwebservice.resources.RecalculateReadingOrderNewResource;

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
    }

    @Override
    public void run(LoghiWebserviceConfiguration configuration, Environment environment) {
        final ExecutorService extractBaselinesExecutor = configuration.getExtractBaseLinesExecutorServiceConfig().createExecutorService(environment);
        final String uploadLocation = configuration.getUploadLocation();
        final ExtractBaselinesResource resource = new ExtractBaselinesResource(extractBaselinesExecutor, uploadLocation, configuration.getP2alaConfigFile());
        environment.jersey().register(resource);

        final ExecutorService cutFromImageExecutorService = configuration.getCutFromImageBasedOnPageXmlExecutorServiceConfig().createExecutorService(environment);
        final CutFromImageBasedOnPageXMLNewResource cutFromImageBasedOnPageXMLNewResource = new CutFromImageBasedOnPageXMLNewResource(cutFromImageExecutorService, uploadLocation);
        environment.jersey().register(cutFromImageBasedOnPageXMLNewResource);

        final ExecutorService executorService = configuration.getLoghiHTRMergePageXMLResourceExecutorServiceConfig().createExecutorService(environment);
        final LoghiHTRMergePageXMLResource loghiHTRMergePageXMLResource = new LoghiHTRMergePageXMLResource(uploadLocation, executorService);
        environment.jersey().register(loghiHTRMergePageXMLResource);

        final ExecutorService recalculateReadingOrderNewResourceExecutorService = configuration.getRecalculateReadingOrderNewResourceExecutorService(environment);
        final RecalculateReadingOrderNewResource recalculateReadingOrderNewResource = new RecalculateReadingOrderNewResource(recalculateReadingOrderNewResourceExecutorService, uploadLocation);
        environment.jersey().register(recalculateReadingOrderNewResource);

    }

}