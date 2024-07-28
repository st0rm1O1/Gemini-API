public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("GOOGLE_API_KEY");
        String textPrompt = "Hello Gemini.";
        String response = GeminiAPI.textPrompt(apiKey, textPrompt);
        System.out.println(response);
    }
}
