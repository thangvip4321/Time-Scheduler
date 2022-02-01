package Utilities;

import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.Event;
import entities.User;
import gradle_tish_embedded.App;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MailHelper {

	/**
	 * logger announcement in runtime showing process in console window
	 */
	private static final Logger logger = LoggerFactory.getLogger(MailHelper.class);

    // which is the email that i put this app password?
    static String sender= App.prop.getProperty("email");
	static String password = App.prop.getProperty("mailPassword");
	static String hostname= "https://"+App.prop.getProperty("hostname").concat(":").concat(App.prop.getProperty("port"));
	static Properties mailProps = new Properties();
	static String fileName = App.prop.getProperty("/home/ngoc/Documents/java-project/img-event-notification/upcoming-event.png");

	// this work
	static {
		mailProps.put("mail.smtp.host", "smtp.gmail.com");
		mailProps.put("mail.smtp.starttls.enable", "true");
		mailProps.put("mail.smtp.port", 587);
		mailProps.put("mail.smtp.auth", true);
	}

	/**
     * <p>
     * Empty constructor for job initialization
     * </p>
     */
	// public MailHelper() {

	// }

    public static void sendMail(String subject,String content,String[] recipients) {

		// create some properties and get the default Session
		Session session = Session.getInstance(mailProps, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(sender, password);
			}
		});


		System.out.println("here" + recipients[0]);
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
	public static void sendVerificationMail(String token,String email) {
		String subroute = "/register";
		String redirectLink = hostname.concat(subroute).concat("?token=").concat(token);
		String content = "Click on this link to verify your email address:\n".concat(redirectLink)
		.concat("\n if it is not you, please ignore this.");
		sendMail("register your email",content, new String[]{email});
	}
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

	/**
	 * Utility method to send email with attachment
	 * @param []recipients
	 * @param subject
	 * @param body
	 */
	public static void sendAttachmentEmail(String subject, String body, String[] recipients){
		//provide recipient's email ID
		// String to = "jakartato@example.com";

		// //provide sender's email ID
		// String from = "jakartafrom@example.com";
		// //provide Mailtrap's username
		// final String username = "a094ccae2cfdb3";
		// //provide Mailtrap's password
		// final String password = "82a851fcf4aa33";
  
		// //provide Mailtrap's host address
		// String host = "smtp.mailtrap.io";
		// //configure Mailtrap's SMTP server details
		// Properties props = new Properties();
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.starttls.enable", "true");
		// props.put("mail.smtp.host", host);
		// props.put("mail.smtp.port", "587");
  
		//create the Session object
		Session session = Session.getInstance(mailProps, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(sender, password);
			}
		});

		System.out.println("here" + recipients[0]);
		// could not cast from Object to InternetAddress, be fucking cause the Object type is loaded by the 'bootstrap' classloader
		// and the Internet address type is loaded by application classloader. But still why cant they be casted?
		// Class.forname("java.lang.Object") would solve the problem, but it seems ugly, no choices left tho
		String addresses =  Arrays.stream(recipients).reduce("",(str,nextRecipient) -> str.concat(",").concat(nextRecipient));
  
		try {
			logger.info("-------instantiate session to message---------");
			//create a MimeMessage object
			Message message = new MimeMessage(session);
		
			//set From email field
			message.setFrom(new InternetAddress(sender));
		
			//set To email field
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresses));
		
			//set email subject field
			message.setSubject("Here comes an attachment!");
		
			//create the message body part
			BodyPart messageBodyPart = new MimeBodyPart();
		
			//set the actual message
			messageBodyPart.setText("Please find the attachment sent using Jakarta Mail");
		
			//create an instance of multipart object
			Multipart multipart = new MimeMultipart();
		
			//set the first text message part
			multipart.addBodyPart(messageBodyPart);
		
			//set the second part, which is the attachment
			messageBodyPart = new MimeBodyPart();
			String filename = fileName;
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);
		
			//send the entire message parts
			message.setContent(multipart);
		
			//send the email message
			Transport.send(message);
		
			System.out.println("Email Message Sent Successfully");
			
			logger.info("-------------Ssent mail message successfully--------------");
		} catch (MessagingException mex) {
			mex.printStackTrace();
			logger.info("Into catching error exceptions");
			System.out.println();
			Exception ex = mex;
			do {
			if (ex instanceof SendFailedException) {
				SendFailedException sfex = (SendFailedException)ex;
				Address[] invalid = sfex.getInvalidAddresses();
				if (invalid != null) {
					logger.error("Meeting invalid address", (Object[]) sfex.getInvalidAddresses());
					System.out.println("    ** Invalid Addresses");
				for (int i = 0; i < invalid.length; i++) 
					System.out.println("         " + invalid[i]);
				}
				Address[] validUnsent = sfex.getValidUnsentAddresses();
				if (validUnsent != null) {
					logger.error("Meeting invalid address", (Object[]) sfex.getValidUnsentAddresses());
					System.out.println("    ** ValidUnsent Addresses");
				for (int i = 0; i < validUnsent.length; i++) 
					System.out.println("         "+validUnsent[i]);
				}
				Address[] validSent = sfex.getValidSentAddresses();
				if (validSent != null) {
					logger.debug("Valid sent message addresses", (Object[]) sfex.getValidSentAddresses());
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
			throw new RuntimeException(mex);
		} 
	}

}
