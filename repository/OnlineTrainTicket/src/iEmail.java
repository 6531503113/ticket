import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class iEmail {
	private Email e;
    public iEmail(String Sender, String KeyApp) {
        e = new SimpleEmail();
        e.setHostName("smtp.gmail.com");
        e.setSmtpPort(465);
        e.setAuthentication(Sender, KeyApp);
        e.setSSLOnConnect(true);
    }
    public boolean Send(String Receiver, String Subject, String Content) {
    	try {
			e.setFrom(Receiver);
			e.setSubject(Subject);
			e.setMsg(Content);
			e.addTo(Receiver);
			e.send();
			return true;
		} catch (EmailException e) {
			return false;
		}
    }
}