
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Schickt eine Bestaetigungsemail an einen Veranstalter.
 * @author marcel
 *
 */
public class Mail {
    /**
     * Gmail-Nutzername.
     */
    private String username; //= "hg17b.zukunftsdiplom";
    /**
     * Gmail-Pw.
     */
    private String password; //= "F3u\"9Snl";
    /**
     * Betreffzeile.
     */
    private String betreff = "Bestaetigungsmail fuer das Zukunftsdiplom";
    /**
     * Nachricht der Email.
     */
    private String nachricht = "Guten Tag, \n"
            + "Bitte klicken sie zur bestaetigung auf den Link: "
            + "pcai042.informatik.uni-leipzig.de/~hg17b/ \n \n"
            + "Mit freundlichen Gruessen \n"
            + "hg17b";
    /**
     * Konstruktor zur Initialisierung des Email-Kontos.
     * @param user Nutzername
     * @param pw Passwort
     * @param message Nachricht der Email
     */
    public Mail(final String user, final String pw, final String message) {
        this.username = user;
        this.password = pw;
        this.nachricht = message;
    }
    /**
     * Konstruktor zur Initialisierung des Email-Kontos ohne Nachricht.
     * @param string Nutzername
     * @param string2 Passwort
     */
    public Mail(final String string, final String string2) {
        this.username = string;
        this.password = string2;
        // TODO Auto-generated constructor stub
    }
    /**
     * Sendet eine Bestaetigungsemail an den Empfaenger.
     * @param empfaenger Email des Veranstalters.
     */
    public void senden(final String empfaenger) {
        Properties properties = System.getProperties();
        String host = "smtp.gmail.com";
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", username);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            InternetAddress empfaengeraddress = new InternetAddress(empfaenger);
            message.addRecipient(Message.RecipientType.TO, empfaengeraddress);
            message.setSubject(betreff);
            message.setText(nachricht);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Email an: " + empfaenger + " gesendet.");
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
