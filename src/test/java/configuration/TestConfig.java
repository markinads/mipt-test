package configuration;

public class TestConfig {
    private static final String BASE_URL = "https://petstore.swagger.io/v2/";
    private static final String DEFAULT_API_KEY = "special-key";

    public String getBaseUrl() {
        return System.getProperty("baseUrl", BASE_URL);
    }

    public String getApiKey() {
        return System.getProperty("apiKey", DEFAULT_API_KEY);
    }
}
