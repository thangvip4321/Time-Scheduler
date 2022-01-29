package Utilities;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import entities.Event;
import entities.User;
import gradle_tish_embedded.App;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class MailHelper {
    // which is the email that i put this app password?
    static String sender= App.prop.getProperty("email");
	static String password = App.prop.getProperty("mailPassword");
	static String hostname= "https://"+App.prop.getProperty("hostname").concat(":").concat(App.prop.getProperty("port"));
	static Properties mailProps = new Properties();

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
			System.out.println();
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
