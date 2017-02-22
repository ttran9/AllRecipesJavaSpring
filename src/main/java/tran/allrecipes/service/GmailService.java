package tran.allrecipes.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

/**
 * @author Todd
 * A helper class to send users notification emails.
 * Slightly modified from the G-Mail Java Quick start Guide.
 */
@Service
public class GmailService {
	/** Application name. */
    private static final String APPLICATION_NAME = "Gmail API Java E-mail Sender";

    /** Global instance of the {@link FileDataStoreFactory}. */
    private FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private HttpTransport HTTP_TRANSPORT;
    
    private final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_SEND);
    
    private static final String USER_TO_AUTHENTICATE_WITH = "me";
    
    /** Directory to store user credentials for this application. */
    private File data_store_dir;

    public GmailService() {
		// TODO Auto-generated constructor stub
	}
        
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public Credential getCredentials() {
        InputStream in = null;
        in = GmailService.class.getClassLoader().getResourceAsStream("/gmailAPIFiles/client_secret.json");
        GoogleClientSecrets clientSecrets = null;
        GoogleAuthorizationCodeFlow flow = null;
        Credential credential = null;
        if(in != null) {
        	/* 
        	 * without a data story factory (StoredCredentials file) using an 
        	 * AuthorizationCodeInstalledApp will require a user to manually allow 
        	 * or deny permission to use the Gmail API. 
        	 */
			try {
				clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
				HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
				data_store_dir = new File(GmailService.class.getClassLoader().getResource("/gmailAPIFiles/java-gmail-credentials/").getFile());
	            DATA_STORE_FACTORY = new FileDataStoreFactory(data_store_dir);
	            if(clientSecrets != null) {
		            flow = new GoogleAuthorizationCodeFlow.Builder(
			                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
			        .setDataStoreFactory(DATA_STORE_FACTORY)
			        .setAccessType("offline")
			        .build();
		            credential = new AuthorizationCodeInstalledApp(
 		                    flow, new LocalServerReceiver()).authorize(USER_TO_AUTHENTICATE_WITH);
	            }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Throwable t) {
	            t.printStackTrace();
	            System.exit(1);
	        }
        }

        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public Gmail getGmailService() {    	
    	Credential credential = null;
    	Gmail service = null;

    	credential = getCredentials();

    	if (credential != null) {
	        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
	                .setApplicationName(APPLICATION_NAME)
	                .build();
    	}
    	return service;
    }
    
    /**
     * Create a message from an email.
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     */
    public Message createMessageWithEmail(MimeMessage emailContent) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Message message = new Message();
        try {
	        emailContent.writeTo(buffer);
	        byte[] bytes = buffer.toByteArray();
	        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
	        message.setRaw(encodedEmail);
        } catch(MessagingException e) {
        	e.printStackTrace();
        } catch(IOException e ) {
        	e.printStackTrace();
        }
        return message;
    }
    
    /**
     * Create a MimeMessage using the parameters provided.
     * @param to email address of the receiver(s)
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the emails
     * @return the MimeMessage to be used to send email
     */
    public MimeMessage createEmail(List<String> recepients, String from, String subject,
    		String bodyText) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);	
		MimeMessage email = new MimeMessage(session);
		try {
			email.setFrom(new InternetAddress(from));
			for(String recipient : recepients)
				email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipient));
			email.setSubject(subject);
			email.setText(bodyText);
		} catch(AddressException e) {
			e.printStackTrace();
		} catch(MessagingException e) {
			e.printStackTrace();
		}
		return email;
	}
        
    /**
     * Send an email from the user's mailbox to its recipient.
     * @param emailContent Email to be sent.
     * @param usersToSendTo Recipient(s) of the email.
     * @param emailSubject Subject of the email.
     * @return The sent message 
     */
    public Message sendEmail(String emailContent, List<String> usersToSendTo, String emailSubject) {
    	Gmail service = getGmailService();
    	if(service != null) {
        MimeMessage emailBody = createEmail(usersToSendTo, USER_TO_AUTHENTICATE_WITH, 
        		emailSubject, emailContent);
        Message emailMessage = createMessageWithEmail(emailBody);
        try {
			emailMessage = service.users().messages().send(USER_TO_AUTHENTICATE_WITH, emailMessage).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return emailMessage;
    	}
    	else
    		return null;
    }

}
