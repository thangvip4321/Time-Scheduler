package Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import entities.Event;
import entities.User;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import com.sun.mail.util.MailLogger;

public class MailHelper {
    // which is the email that i put this app password?
    static String sender= "ducthangnguyen0609@gmail.com";
	static String password = "onqbmgrvefxeliok";
	static String hostname="localhost:8080";
	static Properties props = new Properties();

	// this may not work
	static {
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", 587);
		props.put("mail.smtp.auth", true);
	}

    public static void sendMail(String content,String[] recipients) {

		// create some properties and get the default Session
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(sender, password);
			}
		});

		try {
			Class.forName("java.lang.Object");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		// could not cast from Object to InternetAddress, be fucking cause the Object type is loaded by the 'bootstrap' classloader
		// and the Internet address type is loaded by application classloader. But still why cant they be casted?
		// Class.forname("java.lang.Object") would solve the problem, but it seems ugly, no choices left tho
		String addresses =  Arrays.stream(recipients).reduce("",(str,nextRecipient) -> str.concat(",").concat(nextRecipient));



		try {
			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(sender));
			msg.setRecipients(Message.RecipientType.TO, addresses);
			msg.setSubject("Jakarta Mail APIs Test");
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
	public static void sendVerificationMail(String token,String email) {
		String subroute = "/register";
		String redirectLink = hostname.concat(subroute).concat("?token=").concat(token);
		String content = "Click on this link to verify your email address:".concat(redirectLink)
		.concat("\n if it is not you, please ignore this.");
		sendMail(content, new String[]{email});
	}
	public static void sendInvitationMail(Event e, User recipient) {
		String subroute = "/invite";
		JwtHelper jwt = new JwtHelper().put("eventID", e.eventID).put("username", recipient.username);
		String acceptToken = jwt.put("accept", true).createToken();
		String acceptLink = hostname.concat(subroute).concat("?token=").concat(acceptToken);
		// String denyToken = jwt.put("accept", false).createToken();
		// String denyLink = hostname.concat(subroute).concat("?token=").concat(denyToken);
		String content = String.format("You are invited to a meeting named %s hosted by %s: click here to accept:", e.eventName,e.organizer)
		.concat(acceptLink)//.concat("\n or deny:").concat(denyLink)
		.concat("\n if it is not you, please ignore this.");
		sendMail(content, new String[]{recipient.email});
	}

}
