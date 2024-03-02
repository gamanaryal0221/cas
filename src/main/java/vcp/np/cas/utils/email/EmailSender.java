package vcp.np.cas.utils.email;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSender {

	@Autowired
    private JavaMailSender emailSender;

	@Async
    public void trigger(Email emailData) {
        try {
        	
        	if (emailData == null) throw new Exception("Received null email data");
        	
        	List<String> receiverAddressList = emailData.getReceiverAddressList();
        	if (receiverAddressList == null) throw new Exception("Received null receivers to send email");
        	if (receiverAddressList.size() < 1) throw new Exception("Did not found any receivers to send email");
        	
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            String totalReceivers = "";
            String[] receiverAddressArray = StringUtils.toStringArray(receiverAddressList);
            totalReceivers = StringUtils.arrayToCommaDelimitedString(receiverAddressArray);
            mimeMessageHelper.setTo(receiverAddressArray);
            
            String[] ccAddressArray = StringUtils.toStringArray(emailData.getCcAddressList());
            totalReceivers = totalReceivers + " | " + StringUtils.arrayToCommaDelimitedString(ccAddressArray);
            if (ccAddressArray.length > 0) mimeMessageHelper.setCc(ccAddressArray);
            
            String[] bccAddressArray = StringUtils.toStringArray(emailData.getBccAddressList());
            totalReceivers = totalReceivers + " | " + StringUtils.arrayToCommaDelimitedString(bccAddressArray);
            if (bccAddressArray.length > 0) mimeMessageHelper.setBcc(bccAddressArray);
            
            
            mimeMessageHelper.setSubject(emailData.getSubject());
            mimeMessageHelper.setText(emailData.getContent(), true);
            
            System.out.println("Sending email[title: " + emailData.getSubject() + ", receivers: " + totalReceivers + "]");
            emailSender.send(mimeMessage);
            System.out.println("Email[title: " + emailData.getSubject() + ", receivers: " + totalReceivers + "] sent successfully.");
            
        } catch (MailException mex) {
            System.out.println("Failed to send email[title: " + emailData.getSubject() + "] : " + mex.getMessage());
        	mex.printStackTrace();
        	
        } catch (Exception ex) {
            System.out.println("An unexpected error occurred sending email[title: " + emailData.getSubject() + "] : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
}
