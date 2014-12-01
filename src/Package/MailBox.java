// File with all main functions
package Package;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;
import java.text.*;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.*;
import com.sun.mail.imap.*;

public class MailBox {
	
	protected String LogIn;
	protected String Password;
	protected String Imap;
	private Store store;
	private IMAPFolder folder;
	private Flags seen;
	protected Message[] messages;
	
	public MailBox(String LogIn,String Password,String Imap){
		this.LogIn=LogIn;
		this.Password=Password;
		this.Imap=Imap;
		try{
			Properties props = new Properties();
			props.put("mail.store.protocol","imaps");
			Session session;
			session = Session.getDefaultInstance(props, null);
			this.store = session.getStore("imaps");
			this.store.connect(Imap,LogIn,Password);
			System.out.println("Connected successfully");
		}catch (MessagingException e){
			System.out.println("Error: " + e);
		}
	}

	public void Disconnect(){
			try {
				store.close();
				System.out.println("Disconnected successfully");
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	public MailContent ParseEmailContent(Message message){
		MailContent EmailContent=new MailContent();
		try{
	    	Address[] addr = message.getFrom();
    		EmailContent.from+=addr[0].toString();
    		EmailContent.to+=InternetAddress.toString(message.getRecipients(Message.RecipientType.TO));
    		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
    	    Date date = message.getReceivedDate();//message.getSentDate();
    	    EmailContent.date+=dateFormat.format(date);
    		if (message.getSubject()!=null){
    			EmailContent.subject+=message.getSubject();	
    		}
	    	 try{
	    		if (message.getContent() instanceof Multipart) {
	    			Multipart multipart = (Multipart) message.getContent();
	    			for (int x = 0; x < multipart.getCount(); x++) {
			       	     BodyPart bodyPart = multipart.getBodyPart(x);
			    	     String disposition = bodyPart.getDisposition();
			    	     if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))){
			    	    		 DataHandler handler = bodyPart.getDataHandler();
				    	    	 EmailContent.attachment+=handler.getName();    	 
			    	    	} else {
			    	    		String currentContent = EmailContent.getText(bodyPart);
			    	    		if (currentContent!= null && !EmailContent.Body.contains(currentContent)){
			    	    			EmailContent.Body+=currentContent;
			    	    		}
			    	    	 
			    	    	}
	    			}
	    		}else{
	    			EmailContent.Body+=Jsoup.parse(message.getContent().toString()).text();
	    			}
	    		}catch (IOException e2){
    	        System.out.println(e2);
    			}
	        }catch (MessagingException e){
	    		System.out.println(e.getCause());}
	    return EmailContent;
	}
	
	public MailContent[] FindMails(int num, boolean Seen, String From, String Subject, String Body, String MailFolder, String To) throws MessagingException{
		this.folder = (IMAPFolder) this.store.getFolder(MailFolder);
		Folder[] f = store.getDefaultFolder().list();
		for(Folder fd:f)
		    System.out.println(">> "+fd.getName());
		
		this.folder.open(Folder.READ_ONLY);
		this.seen=new Flags(Flags.Flag.SEEN);
		SearchTerm finalTerm = new FlagTerm(seen,Seen);
		if (From!=null){
			FromStringTerm fromTerm = new FromStringTerm(From);
			finalTerm = new AndTerm(fromTerm, finalTerm);
		}
		if (To!=null){
			RecipientStringTerm recipientTerm = new RecipientStringTerm(Message.RecipientType.TO, To);
			finalTerm = new AndTerm(recipientTerm, finalTerm);
		}
		if (Subject!=null){
			SubjectTerm subjectTerm = new SubjectTerm(Subject);
			finalTerm = new AndTerm(subjectTerm, finalTerm);
		}
		if (Body!=null){
			BodyTerm subjectTerm = new BodyTerm(Body);
			finalTerm = new AndTerm(subjectTerm, finalTerm);
		}
	    Message allMessages[] = folder.search(finalTerm);//search(new FlagTerm(seen,Seen));//search(finalTerm); 
	    MailContent MailsContent[]=null;
	    if (allMessages.length>0){
	    	if (num==-1){
		    	num=allMessages.length;
		    }else if (allMessages.length<num){
		    	num=allMessages.length;
		    }
		    this.messages=new Message[num];
	    	MailsContent=new MailContent[num];
	        for (int i=1; i<=num; i++){
	        	this.messages[i-1]=allMessages[allMessages.length-i];
	        	MailsContent[i-1]=this.ParseEmailContent(this.messages[i-1]);
		    }
	    }else{
	    	System.out.println("No requested messages found");
	    }
	    return MailsContent;
	}
	
	public void SetMessagesState(boolean MarkAsRead) throws MessagingException{
		if (this.folder.isOpen()){
			this.folder.close(false);
		}
		this.folder.open(Folder.READ_WRITE);
	    this.folder.setFlags(this.messages, this.seen, MarkAsRead);
	    this.folder.close(false);
	}
	
	public void DeleteMails()throws MessagingException{
		if (this.folder.isOpen()){
			this.folder.close(false);
		}
		this.folder.open(Folder.READ_WRITE);
		Flags deleted=new Flags(Flags.Flag.DELETED);
		this.folder.setFlags(this.messages, deleted, true);
	    this.folder.close(true);
	}
	
}
