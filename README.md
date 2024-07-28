# Gemini API Text Prompt Example

This repository contains a Java example demonstrating how to use the `GeminiAPI` to generate text responses based on a given prompt.

## Dependencies

To use this class, ensure you have the following dependency in your Maven project:

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20240303</version>
</dependency>
```

## Usage

The following example demonstrates how to use the `GeminiAPI` to get a response for a given text prompt.

```java
public class Main {
    public static void main(String[] args) {
        // Fetch the API key from the environment variables
        String apiKey = System.getenv("GOOGLE_API_KEY");
        
        // Define the text prompt
        String textPrompt = "Hello Gemini.";
        
        // Get the response from GeminiAPI
        String response = GeminiAPI.textPrompt(apiKey, textPrompt);
        
        // Print the response
        System.out.println(response);
    }
}
```

## Sample Response

Below is an example of a response returned by the Gemini API:

```json
{
  "candidates": [
    {
      "content": {
        "parts": [
          {
            "text": "The old backpack sat tucked away in the attic, a forgotten relic of a time long past. ..."
          }
        ],
        "role": "model"
      },
      "finishReason": "STOP",
      "index": 0,
      "safetyRatings": [
        {"category": "HARM_CATEGORY_SEXUALLY_EXPLICIT", "probability": "NEGLIGIBLE"},
        {"category": "HARM_CATEGORY_HATE_SPEECH", "probability": "NEGLIGIBLE"},
        {"category": "HARM_CATEGORY_HARASSMENT", "probability": "NEGLIGIBLE"},
        {"category": "HARM_CATEGORY_DANGEROUS_CONTENT", "probability": "NEGLIGIBLE"}
      ]
    }
  ],
  "usageMetadata": {
    "promptTokenCount": 8,
    "candidatesTokenCount": 712,
    "totalTokenCount": 720
  }
}
```

## Notes

Ensure that the environment variable `GOOGLE_API_KEY` is set with your Google API key before running this code.