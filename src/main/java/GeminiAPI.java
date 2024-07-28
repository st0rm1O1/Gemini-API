import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


/**
 * This class demonstrates the usage of the GeminiAPI for generating text responses based on a given prompt.
 *
 * <h2>Dependencies</h2>
 * To use this class, ensure you have the following dependency in your Maven project:
 * <pre>{@code
 * <dependency>
 *     <groupId>org.json</groupId>
 *     <artifactId>json</artifactId>
 *     <version>20240303</version>
 * </dependency>
 * }</pre>
 *
 * <h2>Usage</h2>
 * The following example demonstrates how to use the `GeminiAPI` to get a response for a given text prompt.
 * <pre>{@code
 * public class Main {
 *     public static void main(String[] args) {
 *         // Fetch the API key from the environment variables
 *         String apiKey = System.getenv("GOOGLE_API_KEY");
 *
 *         // Define the text prompt
 *         String textPrompt = "Hello Gemini.";
 *
 *         // Get the response from GeminiAPI
 *         String response = GeminiAPI.textPrompt(apiKey, textPrompt);
 *
 *         // Print the response
 *         System.out.println(response);
 *     }
 * }
 * }</pre>
 *
 * <h2>Sample Response</h2>
 * Below is an example of a response returned by the Gemini API:
 * <pre>{@code
 * {
 *   "candidates": [
 *     {
 *       "content": {
 *         "parts": [
 *           {
 *             "text": "The old backpack sat tucked away in the attic, a forgotten relic of a time long past. ..."
 *           }
 *         ],
 *         "role": "model"
 *       },
 *       "finishReason": "STOP",
 *       "index": 0,
 *       "safetyRatings": [
 *         {"category": "HARM_CATEGORY_SEXUALLY_EXPLICIT", "probability": "NEGLIGIBLE"},
 *         {"category": "HARM_CATEGORY_HATE_SPEECH", "probability": "NEGLIGIBLE"},
 *         {"category": "HARM_CATEGORY_HARASSMENT", "probability": "NEGLIGIBLE"},
 *         {"category": "HARM_CATEGORY_DANGEROUS_CONTENT", "probability": "NEGLIGIBLE"}
 *       ]
 *     }
 *   ],
 *   "usageMetadata": {
 *     "promptTokenCount": 8,
 *     "candidatesTokenCount": 712,
 *     "totalTokenCount": 720
 *   }
 * }
 * }</pre>
 *
 * <p><b>Note:</b> Ensure that the environment variable `GOOGLE_API_KEY` is set with your Google API key before running this code.</p>
 *
 * <p><b>Author:</b> Kunal Vartak (st0rm1O1)</p>
 * <p><b>Date:</b> 28 July 2024</p>
 */
public class GeminiAPI {

    private static final String GEMINI_DEFAULT_MODEL = "gemini-1.5-flash";
    private static final String GEMINI_DEFAULT_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private static final String CANDIDATES_KEY = "candidates";
    private static final String CONTENT_KEY = "content";
    private static final String CONTENTS_KEY = "contents";
    private static final String PARTS_KEY = "parts";
    private static final String TEXT_KEY = "text";

    public static String textPrompt(String apiKey, String textPrompt) {
        HttpURLConnection connection = null;
        try {
            connection = invokeConnection(apiKey);
            sendPrompt(connection, textPrompt);
            return handleResponse(connection);
        } catch (IOException e) {
            return "Could not establish connection : " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



    private static HttpURLConnection invokeConnection(String apiKey) throws IOException {
        String urlString = String.format(GEMINI_DEFAULT_URL, GEMINI_DEFAULT_MODEL, apiKey);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    private static void sendPrompt(HttpURLConnection connection, String textPrompt) throws IOException {
        String jsonInputString = createPayloadTextPrompt(textPrompt).toString();
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    private static String handleResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(connection);
        } else {
            return "Request failed. ( " + responseCode + ") : " + connection.getResponseMessage();
        }
    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return parseJsonAndGetText(response.toString());
        }
    }

    private static JSONObject createPayloadTextPrompt(String textPrompt) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CONTENTS_KEY, new JSONArray()
                    .put(new JSONObject()
                            .put(PARTS_KEY, new JSONArray()
                                    .put(new JSONObject()
                                            .put(TEXT_KEY, textPrompt)
                                    )
                            )
                    )
            );
        } catch (JSONException e) {
            System.err.println("Error creating JSON: " + e.getMessage());
        }
        return jsonObject;
    }

    private static String parseJsonAndGetText(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray candidatesArray = jsonObject.getJSONArray(CANDIDATES_KEY);
            JSONObject firstCandidate = candidatesArray.getJSONObject(0);
            JSONObject contentObject = firstCandidate.getJSONObject(CONTENT_KEY);
            JSONArray partsArray = contentObject.getJSONArray(PARTS_KEY);
            JSONObject firstPart = partsArray.getJSONObject(0);
            return firstPart.getString(TEXT_KEY);
        } catch (JSONException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            return null;
        }
    }
}
