import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.*;
import java.util.*;

public class EmailSender {

    public static Map<String, String> loadEnv(String path) throws IOException {
        Map<String, String> env = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    env.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return env;
    }

    public static void send(String jsonFile, String csvFile, String from, String password, String to) throws Exception {
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(from, password);
        }
    });

    Message msg = new MimeMessage(session);
    msg.setFrom(new InternetAddress(from));
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
    msg.setSubject("Wikipedia Data");

    Multipart multipart = new MimeMultipart();

    MimeBodyPart textPart = new MimeBodyPart();
    textPart.setText("Wikipedia data scraped successfully. See attachments.");
    multipart.addBodyPart(textPart);

    for (String path : new String[] { jsonFile, csvFile }) {
        MimeBodyPart attachment = new MimeBodyPart();
        attachment.attachFile(new File(path));
        multipart.addBodyPart(attachment);
    }

    msg.setContent(multipart);
    Transport.send(msg);
}
}
