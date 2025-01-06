package ChatDBG;

public class ChatbotTest {
    public static void main(String[] args) {
        // Create a ChatBot instance
        ChatBot chatBot = ChatBot.getInstance();
        // Get a response from the ChatBot
        String response = chatBot.getResponse("Hello");
        // Print the response
        System.out.println(response);
    }
}
