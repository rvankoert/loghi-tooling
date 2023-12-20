package nl.knaw.huc.di.images.loghiwebservice;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import nl.knaw.huc.di.images.loghiwebservice.authentication.LoggedInAuthorizer;
import nl.knaw.huc.di.images.loghiwebservice.authentication.SessionManager;
import nl.knaw.huc.di.images.loghiwebservice.authentication.User;
import nl.knaw.huc.di.images.loghiwebservice.authentication.apikey.ApiKeyAuthenticator;
import nl.knaw.huc.di.images.loghiwebservice.authentication.apikey.ApiKeyFilter;
import nl.knaw.huc.di.images.loghiwebservice.authentication.apikey.JsonApiKeyUserNameManager;
import nl.knaw.huc.di.images.loghiwebservice.resources.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

        configuration.registerExtractBaseLinesResource(environment, metricRegistry);
        configuration.registerCutFromImageBasedOnPageXMLNewResource(environment, metricRegistry);
        configuration.registerLoghiHTRMergePageXMLResource(environment, metricRegistry);
        configuration.registerRecalculateReadingOrderNewResource(environment, metricRegistry);
        configuration.registerSplitPageXMLTextLineIntoWordsResource(environment, metricRegistry);
        configuration.registerDetectLanguageOfPageXmlResource(environment, metricRegistry);

        configuration.registerSecurity(environment);

        final File uploadLocation = new File(configuration.getUploadLocation());
        if (!uploadLocation.exists()) {
            try {
                Files.createDirectories(uploadLocation.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload location, please set the environment variable 'STORAGE_LOCATION' to a writable path.");
            }
        }
    }

}