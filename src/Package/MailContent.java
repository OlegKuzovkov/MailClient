//Output mail structure
package Package;
import javax.mail.*;
import java.io.*;
import org.jsoup.*;

public class MailContent {
	protected String from="";
	protected String to="";
	protected String subject="";
	protected String date="";
	protected String Body="";
	protected String attachment="";
	
	
	public void PrintContent(){
		System.out.println(from+";");
		System.out.println(to);
		System.out.println(subject);
		System.out.println(date);
		System.out.println(Body);
		System.out.println(attachment);
		System.out.println("=====================================================");
	}
	
	protected String getText(Part p)throws MessagingException, IOException{
		if (p.isMimeType("text/*")) {
		String s = (String)p.getContent();
		s=Jsoup.parse(s).text();
		return s;
		}
		if (p.isMimeType("multipart/alternative")) {
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                    	s= Jsoup.parse(s).text();
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }
        return null;
    }
}
