package usecases;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Stream;

import entities.Event;
import jakarta.mail.*;
import jakarta.mail.internet.*;


public class MailHelper {
    // which is the email that i put this app password?
    static String sender= "ducthangnguyen0609@gmail.com";
	static String password = "onqbmgrvefxeliok";
	static String hostname="192.168.0.102:8080";

    private static void sendMail(String content,String[] recipients) {

		// create some properties and get the default Session
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", 587);
		props.put("mail.smtp.auth", true);


		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(sender, password);
			}
		});
		
		try {
			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(sender));
			
			InternetAddress[] addresses =  (InternetAddress[]) Arrays.stream(recipients).map(InternetAddress::new).toArray();
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
			if(ex instanceof AddressException){
				// do nothing
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
		String address = "/verify";
		String redirectLink = hostname.concat(address).concat("?token=").concat(token);
		String content = "Click on this link to verify your address:".concat(redirectLink)
		.concat("\n if it is not you, please ignore this.");
		sendMail(content, new String[]{email});
	}
	public static void sendInvitationMail(Event e, String[] recipients) {
		String subroute = "/invite";
		String acceptLink = hostname.concat(subroute).concat("?token=").concat(acceptToken);
		String denyLink = hostname.concat(subroute).concat("?token=").concat(denyToken);
		String content = "You are invited to a meeting named ${} by ...".concat(acceptLink).concat(denyLink)
		.concat("\n if it is not you, please ignore this.");
		sendMail(content, recipients);
	}
}
