package nl.knaw.huc.di.images.loghiwebservice;

import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.knaw.huc.di.images.loghiwebservice.resources.CutFromImageBasedOnPageXMLNewResource;
import nl.knaw.huc.di.images.loghiwebservice.resources.ExtractBaselinesResource;

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
        final ExtractBaselinesResource resource = new ExtractBaselinesResource(extractBaselinesExecutor, configuration.getUploadLocation(), configuration.getP2alaConfigFile());
        environment.jersey().register(resource);

        final ExecutorService cutFromImageExecutorService = configuration.getCutFromImageBasedOnPageXmlExecutorServiceConfig().createExecutorService(environment);
        final CutFromImageBasedOnPageXMLNewResource cutFromImageBasedOnPageXMLNewResource = new CutFromImageBasedOnPageXMLNewResource(cutFromImageExecutorService, configuration.getUploadLocation());
        environment.jersey().register(cutFromImageBasedOnPageXMLNewResource);

    }

}