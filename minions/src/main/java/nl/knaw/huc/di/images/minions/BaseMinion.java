package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import jakarta.ws.rs.core.UriBuilder;
import nl.knaw.huc.di.images.layoutds.models.DocumentImage;
import nl.knaw.huc.di.images.layoutds.models.OCRJob;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;


public class BaseMinion {


    private static String serverUri = "http://localhost:9006/";
    private static String apiKey = "471c6c6c-aee9-485b-9d64-a0a82a2936ba";
    private static int sleeplength = 1;
    public static int maxRandom = 1000;

    public static String getServerUri() {
        return serverUri;
    }


    public static String processArgs(String[] args) {
        if (args.length > 0) {
            setServerUri(args[0]);
        }
        if (args.length >= 2) {
            setApiKey(args[1]);
        }
        return null;
    }



    private static void setServerUri(String serverUri) {
        BaseMinion.serverUri = serverUri;
    }

    public static DocumentImage getDocumentImage(String serverPath) throws IOException, InterruptedException {
        URL url;
        String urlString = serverUri + serverPath;
        url = new URL(urlString);
        while (true) {
            URLConnection conn = url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            DocumentImage documentImage;
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charsets.UTF_8));
                sleeplength = 100;

                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                bufferedReader.close();

                ObjectMapper mapper = new ObjectMapper();
                documentImage = mapper.readValue(stringBuilder.toString(), DocumentImage.class);
                return documentImage;
            } catch (IOException ex) {
                sleeplength += 500;
                ex.printStackTrace();
                Thread.sleep(sleeplength);
            }
        }
    }

    public static OCRJob getOCRJob(String serverPath, String apiKey, OCRJob.OcrSystem ocrSystem) throws InterruptedException {
        String urlString = serverUri + serverPath + "?ocr=" + ocrSystem;
        while (true) {
            OCRJob ocrJob = null;
            try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
                final String sessionId = logIn(client, serverUri, apiKey);
                final HttpGet httpGet = new HttpGet(urlString);
                httpGet.addHeader("Authorization", sessionId);

                try (final CloseableHttpResponse response = client.execute(httpGet)) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        ocrJob = new ObjectMapper().readValue(response.getEntity().getContent(), OCRJob.class);
                    } else {
                        System.err.println("Could not retrieve OCRJob: " + response.getStatusLine().getReasonPhrase());
                    }
                }
                return ocrJob;
            } catch (IOException ex) {
                sleeplength += 500;
                ex.printStackTrace();
                Thread.sleep(sleeplength);
            }
        }
    }

    public static String logIn(CloseableHttpClient client, String serverUri, String apiKey) throws IOException {
        final URI uri = UriBuilder.fromUri(serverUri).path("api/apikeylogin").build();
        final HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", apiKey);
        try (final CloseableHttpResponse execute = client.execute(httpPost)) {
            if (execute.getStatusLine().getStatusCode() == 204) {
                final Header x_auth_token = execute.getHeaders("X_AUTH_TOKEN")[0];
                return x_auth_token.getValue();
            } else {
                System.err.println("Could not login: " + execute.getStatusLine().getReasonPhrase());
                return null;
            }
        }
    }

    public static void setApiKey(String apiKey) {
        BaseMinion.apiKey = apiKey;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static void printHelp(Options options, String callName) {
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(callName, options, true);
    }
}
