package lang;

import com.incontact.connectors.mongo.MongoConnector;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

@Service
public class CheckRecords {

    private RestTemplate restTemplate;
    private MongoConnector  mongoConnector;

    public CheckRecords()
    {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(requestFactory);
    }

    public void getRecordDetail(String contactId) {

        String fooResourceUrl
                = "https://localhost/metrics/records/" + contactId;
//        ResponseEntity<String> response
//                = restTemplate.getForEntity(fooResourceUrl, String.class);
        RecordResponse recordResponse = restTemplate
                .getForObject(fooResourceUrl, RecordResponse.class);

        getFromRecordsCollection(recordResponse.collectionName, contactId);
//        System.out.println(response.getStatusCode());
        System.out.println(recordResponse.accountId);
    }

    private void getFromRecordsCollection(String collectionName, String contactId)
    {
        try {
            final MongoCredential credential = MongoCredential.createCredential("icamongo", "admin", "phu6vjvBAaQWKGb2ebKLMNSBdpgYHDBc".toCharArray());
            credential.withMechanism(AuthenticationMechanism.SCRAM_SHA_1);
            List<Document>  recordDocuments = new ArrayList<>();
            List<MongoCredential> mc = List.of(credential);
            ServerAddress sa = new ServerAddress("localhost", 27018);
            MongoClient mongoClient = new MongoClient(sa, mc, MongoClientOptions.builder().serverSelectionTimeout(100000).connectTimeout(500000).sslEnabled(false).build());

            final MongoDatabase records = mongoClient.getDatabase("records");
            final MongoCollection<Document> collection = records.getCollection(collectionName);
            Bson filter = eq("external_id", contactId);
            FindIterable<Document> result = collection.find(filter);

            result.limit(10);
            result.maxAwaitTime(10, TimeUnit.SECONDS);
            final Document document = result.cursor().tryNext();

            while (result.cursor().hasNext())
            {
                recordDocuments.add(result.iterator().next());
            }
            //while (result.)

            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("id", "3656d399-74ac-4f22-8025-a09bbf51e857");
            final FindIterable<Document> documents = collection.find(searchQuery);

            while (documents.cursor().hasNext()) {
                System.out.println(documents.cursor().next());
            }


            mongoConnector = new MongoConnector("mongodb://localhost", "records");
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

    }

    private void getFromRecordsCollection2(String collectionName, String contactId)
    {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        final MongoDatabase records = mongoClient.getDatabase("records");
        final MongoCollection<Document> collection = records.getCollection(collectionName);
    }

    public void getRecordDetailsHttpClient() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://localhost/metrics/records/77666108")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }
}
