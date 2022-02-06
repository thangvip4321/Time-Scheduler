package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.Event;
import entities.User;
<<<<<<< HEAD
import gradle_tish_embedded.BackendApp;
=======
// import gradle_tish_embedded.App;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
>>>>>>> 4aa66fc8371520680a6706428e8a293fd183f1e3
import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.NoArgsConstructor;

public class MailHelper {

	/**
	 * logger announcement in runtime showing process in console window
	 */
	private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);
	private static Properties appProp;
	static{
		appProp = new Properties();
		String fileName = "./src/main/resources/app.properties";
		try (FileInputStream fis = new FileInputStream(fileName)) {
			try {
				appProp.load(fis);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    // which is the email that i put this app password?
<<<<<<< HEAD
    static String sender= BackendApp.prop.getProperty("email");
	static String password = BackendApp.prop.getProperty("mailPassword");
	static String hostname= "https://"+BackendApp.prop.getProperty("hostname").concat(":").concat(BackendApp.prop.getProperty("port"));
=======
    static String sender= appProp.getProperty("email");
	static String password = appProp.getProperty("mailPassword");
	static String hostname= "https://"+appProp.getProperty("hostname").concat(":").concat(appProp.getProperty("port"));
>>>>>>> 4aa66fc8371520680a6706428e8a293fd183f1e3
	static Properties mailProps = new Properties();
	static String fileName = appProp.getProperty("/home/ngoc/Documents/java-project/img-event-notification/upcoming-event.png");

	// this work
	static {
		mailProps.put("mail.smtp.host", "smtp.gmail.com");
		mailProps.put("mail.smtp.starttls.enable", "true");
		mailProps.put("mail.smtp.port", 587);
		mailProps.put("mail.smtp.auth", true);
	}

    
	/** 
	 * send an email with these subject and content to a list of email addresses.
	 * Unfortunately the sender is hardcoded in our app.
	 * @param subject
	 * @param content
	 * @param recipients
	 */
	public static void sendMail(String subject,String content,String[] recipients) {
		if(recipients.length==0) return;
		// create some properties and get the default Session
		Session session = Session.getInstance(mailProps, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(sender, password);
			}
		});


		// could not cast from Object to InternetAddress, be fucking cause the Object type is loaded by the 'bootstrap' classloader
		// and the Internet address type is loaded by application classloader. But still why cant they be casted?
		// Class.forname("java.lang.Object") would solve the problem, but it seems ugly, no choices left tho
		String addresses =  Arrays.stream(recipients).reduce("",(str,nextRecipient) -> str.concat(",").concat(nextRecipient));


		try {
			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(sender));
			msg.setRecipients(Message.RecipientType.TO, addresses);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			// If the desired charset is known, you can use
			// setText(text, charset)
			msg.setText(content);
			System.out.println(msg.getFrom());
			Transport.send(msg);
		} catch (MessagingException mex) {
			mex.printStackTrace();
			Exception ex = mex;
			do {
			if (ex instanceof SendFailedException) {
				SendFailedException sfex = (SendFailedException)ex;
				Address[] invalid = sfex.getInvalidAddresses();
				if (invalid != null) {
				System.out.println("    ** Invalid Addresses");
				for (int i = 0; i < invalid.length; i++) 
					System.out.println("         " + invalid[i]);
				}
				Address[] validUnsent = sfex.getValidUnsentAddresses();
				if (validUnsent != null) {
				System.out.println("    ** ValidUnsent Addresses");
				for (int i = 0; i < validUnsent.length; i++) 
					System.out.println("         "+validUnsent[i]);
				}
				Address[] validSent = sfex.getValidSentAddresses();
				if (validSent != null) {
				System.out.println("    ** ValidSent Addresses");
				for (int i = 0; i < validSent.length; i++) 
					System.out.println("         "+validSent[i]);
				}
			}
			System.out.println();
			if (ex instanceof MessagingException)
				ex = ((MessagingException)ex).getNextException();
			else
				ex = null;
			} while (ex != null);
		}
	}
	
	/** send a mail to verify a user email with a link 
	 * @param token an token string 
	 * @param email the email to verify
	 */
	public static void sendVerificationMail(String token,String email) {
		String subroute = "/register";
		String redirectLink = hostname.concat(subroute).concat("?token=").concat(token);
		String content = "Click on this link to verify your email address:\n".concat(redirectLink)
		.concat("\n if it is not you, please ignore this.");
		sendMail("register your email",content, new String[]{email});
	}
	
	/** send a mail to invite a user to an event
	 * @param e an Event object which must include : eventID,eventName, organizer
	 * @param recipient an User object which must include username and email
	 */
	public static void sendInvitationMail(Event e, User recipient) {
		String subroute = "/invite";
		JwtHelper jwt = new JwtHelper().put("eventID", e.eventID).put("username", recipient.username);
		String acceptToken = jwt.put("accept", true).createToken();
		String acceptLink = hostname.concat(subroute).concat("?token=").concat(acceptToken);
		// String denyToken = jwt.put("accept", false).createToken();
		// String denyLink = hostname.concat(subroute).concat("?token=").concat(denyToken);
		String content = String.format("You are invited to a meeting named %s hosted by %s: click here to accept:\n", e.eventName,e.organizer)
		.concat(acceptLink)//.concat("\n or deny:").concat(denyLink)
		.concat("\n if it is not you, please ignore this.");
		sendMail("invitation to an event",content, new String[]{recipient.email});
	}

}
