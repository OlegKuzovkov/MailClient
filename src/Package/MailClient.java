//Main file with application syntax
package Package;
import javax.mail.MessagingException;
import org.ini4j.*;

import java.io.*;

public class MailClient {
	private String UserName;
	private String Password;
	private String Imap;
	private String Folder;
	private long WaitTime;
	private int Num;
	private boolean Seen;
	private boolean MarkAsRead;
	private boolean Delete;
	private String From;
	private String Recipient;
	private String Subject;
	private String Body;
	
	public static void main(String[] args)throws IOException {
		String iniFile = args[0];
		Ini ini = new Ini(new File(iniFile));
		MailClient MailClient = new MailClient();
		String credentialsSection = "Credentials";
		MailClient.UserName=ini.get(credentialsSection,"UserName");
		MailClient.Password=ini.get(credentialsSection,"Password");
		MailClient.Imap=ini.get(credentialsSection,"Imap");
		MailClient.Folder=ini.get(credentialsSection,"Folder");
		MailClient.WaitTime=Long.parseLong(ini.get(credentialsSection,"WaitTime"));
		String searchCriteriasSection = "Search_Criterias";
		MailClient.Num=Integer.parseInt(ini.get(searchCriteriasSection,"Num"));
		MailClient.Seen=Boolean.parseBoolean(ini.get(searchCriteriasSection,"Seen"));
		MailClient.MarkAsRead=Boolean.parseBoolean(ini.get(searchCriteriasSection,"MarkAsRead"));
		MailClient.Delete=Boolean.parseBoolean(ini.get(searchCriteriasSection,"Delete"));
		MailClient.From=ini.get(searchCriteriasSection,"From");
		MailClient.Recipient=ini.get(searchCriteriasSection,"To");
		MailClient.Subject=ini.get(searchCriteriasSection,"Subject");
		MailClient.Body=ini.get(searchCriteriasSection,"Body");
		MailBox mailBox=new MailBox(MailClient.UserName,MailClient.Password,MailClient.Imap);
		MailContent mails[] =null;
		try{
			long Finish=0;
			long Start = System.currentTimeMillis();
			int i=15;
			while ((i-->0) && ((Finish-Start)<(MailClient.WaitTime*1000)) && (mails==null)){
				mails = mailBox.FindMails(MailClient.Num,MailClient.Seen,MailClient.From,MailClient.Subject,MailClient.Body,MailClient.Folder,MailClient.Recipient);
				if (mails==null){
					try {
						long sleep=(MailClient.WaitTime*1000)/15;
					    Thread.sleep(sleep);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
				}
				Finish=System.currentTimeMillis();
				/*System.out.println(System.currentTimeMillis());
				try{
					mails = mailBox.FindMails(MailClient.Num,MailClient.Seen,MailClient.From,MailClient.Subject,MailClient.Body);
				}catch(javax.mail.MessagingException ex1){
					mailBox.Disconnect();
					try {
					    Thread.sleep(500);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
					mailBox=new MailBox(MailClient.UserName,MailClient.Password,MailClient.Imap);
				}
				Finish=System.currentTimeMillis();
				*/
			}
			if (mails!=null){
				mailBox.SetMessagesState(MailClient.MarkAsRead);
				if (MailClient.Delete){
					mailBox.DeleteMails();
				}
			}
		}catch (MessagingException e) {
			   	System.out.println(e);
			}
		if (mails!=null){
			String iniOutFileName;
			if (args.length>1){
				iniOutFileName=args[1];
			}else{
				iniOutFileName="c:/Temp/iniOut.ini";
			}
			File inioutfile = new File(iniOutFileName,"");
			if(inioutfile.exists()) {
				inioutfile.delete();
			}
			if(!inioutfile.createNewFile()) return;
			Ini iniOut = new Ini(inioutfile);
			int i=1;
			for (MailContent mail:mails){
				String sectionName = "EMail_" + i++;
				iniOut.put(sectionName, "From", mail.from);
				iniOut.put(sectionName, "To", mail.to);
				iniOut.put(sectionName, "Subject", mail.subject);
				iniOut.put(sectionName, "ReceivedDate", mail.date);
				iniOut.put(sectionName, "Body", mail.Body);
				iniOut.put(sectionName, "Attachment", mail.attachment);
				Config value = new Config();
				value.setEscape(false);
				iniOut.setConfig(value);
				iniOut.store();
			}
			System.out.println("All mails were successfully found");
		}
	}			
}
	