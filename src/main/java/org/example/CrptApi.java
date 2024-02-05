package org.example;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

class DocumentRequest
{
    private String description;
    private String doc_id;
    private String doc_status;
    private String doc_type;
    private boolean importRequest;
    private String owner_inn;
    private String participant_inn;
    private String producer_inn;
    private String production_date;
    private String production_type;
    private String reg_date;
    private String reg_number;

    public String getDescription()
    {
        return description;
    }
    public String getDoc_id()
    {
        return doc_id;
    }
    public String getDoc_status()
    {
        return doc_status;
    }
    public String getDoc_type()
    {
        return doc_type;
    }
    public boolean isImportRequest()
    {
        return importRequest;
    }
    public String getOwner_inn()
    {
        return owner_inn;
    }
    public String getParticipant_inn()
    {
        return participant_inn;
    }
    public String getProducer_inn()
    {
        return producer_inn;
    }
    public String getProduction_date()
    {
        return production_date;
    }
    public String getProduction_type()
    {
        return production_type;
    }
    public String getReg_date()
    {
        return reg_date;
    }
    public String getReg_number()
    {
        return reg_number;
    }
    public static DocumentRequest fromJson(String json) throws Exception
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, DocumentRequest.class);
    }
    public String toJson() throws Exception
    {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
public class CrptApi
{
    private final HttpClient httpClient;
    private final Semaphore requestApiLimit;
    private String token = "";
    public CrptApi(TimeUnit timeUnit, int requestLimit, String token)
    {
        this.httpClient = HttpClient.newHttpClient();
        this.requestApiLimit = new Semaphore(requestLimit);
        this.token = token;
    }
    public synchronized void documentJsontoServer(String apiUrl, DocumentRequest documentRequest, String signature)
    {
        try
        {
            requestApiLimit.acquire();
            String jsonStructure = documentRequest.toJson();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonStructure))
                    .build();


            HttpResponse<String> listCode = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (listCode.statusCode() == 200)
            {
                System.out.println("Документ создан успешно.");
            }
            else
            {
                System.out.println("Ошибка. Код: " + listCode.statusCode() + "\nСодержимое: " + listCode.body());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            requestApiLimit.release();
        }
    }
    public static void main(String[] args)
    {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 10, "");
        String url_API = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        String documentJson = "{\"description\": {\"participantInn\": \"string\"}, " +
                "\"doc_id\": \"string\", \"doc_status\": \"string\", \"doc_type\": \"LP_INTRODUCE_GOODS\", " +
                "\"importRequest\": true, \"owner_inn\": \"string\", \"participant_inn\": \"string\", " +
                "\"producer_inn\": \"string\", \"production_date\": \"2020-01-23\", \"production_type\": \"string\", " +
                "\"products\": [{\"certificate_document\": \"string\", \"certificate_document_date\": \"2020-01-23\", " +
                "\"certificate_document_number\": \"string\", \"owner_inn\": \"string\", \"producer_inn\": \"string\", " +
                "\"production_date\": \"2020-01-23\", \"tnved_code\": \"string\", \"uit_code\": \"string\", " +
                "\"uitu_code\": \"string\" }], \"reg_date\": \"2020-01-23\", \"reg_number\": \"string\"}";
        String string = "подпись";
        try
        {
            DocumentRequest documentRequest = DocumentRequest.fromJson(documentJson);
            crptApi.documentJsontoServer(url_API, documentRequest, string);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
