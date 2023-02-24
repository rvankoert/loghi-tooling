package nl.knaw.huc.di.images.loghiwebservice;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
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
        final MetricRegistry metricRegistry = new MetricRegistry();
        final CollectorRegistry collectorRegistry = new CollectorRegistry();
        collectorRegistry.register(new DropwizardExports(metricRegistry));
        environment.admin().addServlet("prometheus", new MetricsServlet(collectorRegistry)).addMapping("/prometheus");

        final ExecutorService extractBaselinesExecutor = configuration.createExtractBaseLinesExecutorService(environment, metricRegistry);
        final String uploadLocation = configuration.getUploadLocation();
        System.out.println("Storage location: " + uploadLocation);
        final ExtractBaselinesResource resource = new ExtractBaselinesResource(extractBaselinesExecutor, uploadLocation, configuration.getP2alaConfigFile());
        environment.jersey().register(resource);

        final ExecutorService cutFromImageExecutorService = configuration.createCutFromImageBasedOnPageXmlExecutorService(environment, metricRegistry);
        final CutFromImageBasedOnPageXMLNewResource cutFromImageBasedOnPageXMLNewResource = new CutFromImageBasedOnPageXMLNewResource(cutFromImageExecutorService, uploadLocation);
        environment.jersey().register(cutFromImageBasedOnPageXMLNewResource);

        final ExecutorService executorService = configuration.createLoghiHTRMergePageXMLResourceExecutorService(environment, metricRegistry);
        final LoghiHTRMergePageXMLResource loghiHTRMergePageXMLResource = new LoghiHTRMergePageXMLResource(uploadLocation, executorService);
        environment.jersey().register(loghiHTRMergePageXMLResource);

        final ExecutorService recalculateReadingOrderNewResourceExecutorService = configuration.createRecalculateReadingOrderNewResourceExecutorService(environment, metricRegistry);
        final RecalculateReadingOrderNewResource recalculateReadingOrderNewResource = new RecalculateReadingOrderNewResource(recalculateReadingOrderNewResourceExecutorService, uploadLocation);
        environment.jersey().register(recalculateReadingOrderNewResource);

        final ExecutorService splitPageXMLTextLineIntoWordsResourceExecutorService = configuration.createSplitPageXMLTextLineIntoWordsResourceExecutorService(environment, metricRegistry);
        final SplitPageXMLTextLineIntoWordsResource splitPageXMLTextLineIntoWordsResource = new SplitPageXMLTextLineIntoWordsResource(splitPageXMLTextLineIntoWordsResourceExecutorService, uploadLocation);
        environment.jersey().register(splitPageXMLTextLineIntoWordsResource);

        ExecutorService detectLanguageOfPageXmlResourceExecutorService = configuration.createDetectLanguageOfPageXmlResourceExecutorService(environment, metricRegistry);
        final DetectLanguageOfPageXmlResource detectLanguageOfPageXmlResource = new DetectLanguageOfPageXmlResource(uploadLocation, detectLanguageOfPageXmlResourceExecutorService);
        environment.jersey().register(detectLanguageOfPageXmlResource);

    }

}