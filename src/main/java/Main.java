import java.util.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
           
            System.out.print("Enter Wikipedia page titles: ");
            String input = scanner.nextLine();

            List<String> pages = Arrays.stream(input.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            if (pages.isEmpty()) {
                System.out.println("No valid pages entered. Exiting...");
                return;
            }

            System.out.print("Enter recipient's email address: ");
            String recipientEmail = scanner.nextLine().trim();

            Map<String, String> env = EmailSender.loadEnv(".env");
            String senderEmail = env.get("EMAIL");
            String senderPassword = env.get("PASSWORD");

            String timestamp = new Date().toInstant().toString().replaceAll("[:.]", "-");
            String jsonFile = "output_" + timestamp + ".json";
            String csvFile = "output_" + timestamp + ".csv";

            System.out.println("Scraping pages...");
            Scraper.scrapePages(pages, jsonFile, csvFile);
            System.out.println("Scraping complete. Files ready.");

            System.out.println("Sending email in background...");
            new Thread(() -> {
                try {
                    EmailSender.send(jsonFile, csvFile, senderEmail, senderPassword, recipientEmail);
                    System.out.println("Email sent to " + recipientEmail + "!");
                } catch (Exception e) {
                    System.out.println("Failed to send email.");
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}