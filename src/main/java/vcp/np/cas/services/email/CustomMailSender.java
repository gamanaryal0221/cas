package vcp.np.cas.services.email;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.mail.internet.MimeMessage;
import vcp.np.cas.utils.Helper;
import vcp.np.cas.utils.enums.JwtTokenPurpose;

@Service
public class CustomMailSender {

	
	@Autowired
    private JavaMailSenderImpl mailSender;
	
	private final MailCredential mailCredential;
	
	    
    public CustomMailSender(MailCredential emailConfig) {
        this.mailCredential = emailConfig;
    }


	@Async
    private void trigger(MailModel emailModel) {
        try {
        	
        	if (emailModel == null) throw new Exception("Received null email data");
        	
        	List<String> receiverAddressList = emailModel.getReceiverAddressList();
        	if (receiverAddressList == null) throw new Exception("Received null receivers to send email");
        	if (receiverAddressList.size() < 1) throw new Exception("Did not found any receivers to send email");
        	
			// Putting the sender credentials
			mailSender.setUsername(mailCredential.getSenderMailAddress());
			mailSender.setPassword(mailCredential.getPassword());

			// Creating mime message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            String totalReceivers = "";
			// COllecting all to addresses
            String[] receiverAddressArray = StringUtils.toStringArray(receiverAddressList);
            totalReceivers = StringUtils.arrayToCommaDelimitedString(receiverAddressArray);
            mimeMessageHelper.setTo(receiverAddressArray);
            
			// COllecting all cc addresses
            String[] ccAddressArray = StringUtils.toStringArray(emailModel.getCcAddressList());
            totalReceivers = totalReceivers + " | " + StringUtils.arrayToCommaDelimitedString(ccAddressArray);
            if (ccAddressArray.length > 0) mimeMessageHelper.setCc(ccAddressArray);
            
			// COllecting all bcc addresses
            String[] bccAddressArray = StringUtils.toStringArray(emailModel.getBccAddressList());
            totalReceivers = totalReceivers + " | " + StringUtils.arrayToCommaDelimitedString(bccAddressArray);
            if (bccAddressArray.length > 0) mimeMessageHelper.setBcc(bccAddressArray);
            
            // Putting contents
            mimeMessageHelper.setSubject(emailModel.getSubject());
            mimeMessageHelper.setText(emailModel.getContent(), true);
            
			// Sending mail
            System.out.println("Sending email[ " + emailModel.getSubject() + "] to receivers: " + totalReceivers);
            mailSender.send(mimeMessage);
            System.out.println("Email[" + emailModel.getSubject() + "] sent successfully to: " + totalReceivers);
            
        } catch (MailException mex) {
            System.out.println("Failed to send mail[title: " + emailModel.getSubject() + "] : " + mex.getMessage());
        	mex.printStackTrace();
        	
        } catch (Exception ex) {
            System.out.println("An unexpected error occurred sending mail[title: " + emailModel.getSubject() + "] : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
	
	@Async
	public void trigerForgotPasswordEmail(String userMailAddress, Date expirationAt, String passwordResetUrl) {
        System.out.println("Sending 'Forgot Password' mail to userMailAddress:'" + userMailAddress + "'");

        if (userMailAddress == null || userMailAddress.isEmpty()) {
            System.out.println("Aborted! -> Sending 'Forgot Password' mail due to invalid mail address");
            return;
        }
        
    	MailModel emailModel = new MailModel();
    	emailModel.setReceiverAddressList(List.of(userMailAddress));
    	emailModel.setSubject("Forgot Password");
    	
    	String content = ""
    			+ "<!DOCTYPE html> "
    			+ "<html lang=\"en\"> "
    			+ "<head> "
    			+ "    <meta charset=\"UTF-8\"> "
    			+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> "
    			+ "    <title>Forgot Password</title> "
    			+ "</head> "
    			+ "<body style=\"font-family: Arial, sans-serif;\"> "
    			+ " "
    			+ "    <h2>Forgot Password</h2> "
    			+ " "
    			+ "    <p>We have received a request to reset your password. If you did not make this request, you can ignore this email.</p> "
    			+ " "
    			+ "    <p>To reset your password, please click the following link:</p> "
    			+ " "
    			+ "    <p><a href=\"" + passwordResetUrl + "\">Reset Password</a></p> "
    			+ " "
    			+ "    <p>This link will expire after <b>" + Helper.dateToTimestamp(expirationAt).toLocaleString() + "</b> for security reasons.</p> "
    			+ " "
    			+ "    <p>If you're having trouble clicking the \"Reset Password\" button, copy and paste the URL below into your web browser:</p> "
    			+ " "
    			+ "    <p>" + passwordResetUrl + "</p> "
    			+ " "
    			+ "    <p>Thank you,</p> "
    			+ "    <p>VCP</p> "
    			+ " "
    			+ "</body> "
    			+ "</html> ";
    	
    	emailModel.setContent(content);
    	
    	trigger(emailModel);
    	
	}
    
	
	@Async
	public void trigerPasswordResetSuccessEmail(String jwtTokenPurposeCode, String userMailAddress, Timestamp passwordResetTimeStamp) {
		boolean isPasswordReset = JwtTokenPurpose.PASSWORD_RESET.getCode().equals(jwtTokenPurposeCode);
		String subject = isPasswordReset? "Password Reset Success":"Password Updated";
        System.out.println("Sending " + subject + " email to userMailAddress:'" + userMailAddress + "'");

        if (userMailAddress == null || userMailAddress.isEmpty()) {
            System.out.println("Aborted! -> Sending " + subject + " mail due to invalid mail address");
            return;
        }
        
    	MailModel emailModel = new MailModel();
    	emailModel.setReceiverAddressList(List.of(userMailAddress));
    	emailModel.setSubject(subject);
    	
    	String content = ""
    			+ "<!DOCTYPE html> "
    			+ "<html lang=\"en\"> "
    			+ "<head> "
    			+ "    <meta charset=\"UTF-8\"> "
    			+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> "
    			+ "    <title>" + subject + "</title> "
    			+ "</head> "
    			+ "<body style=\"font-family: Arial, sans-serif;\"> "
    			+ " "
    			+ "    <p>Your password has been successfully " + (isPasswordReset? "reset":"updated") + " at <b>" + passwordResetTimeStamp + "</b>.</p> ";


		if (isPasswordReset) {
			content = content
    			+ "    <p>If you did not initiate this password reset, please click the following link to lock your account:</p> "
    			+ " "
    			+ "    <p><a href=\"http://example.com/lock-account\">Lock My Account</a></p> "
    			+ " ";
		}
		

    	content = content
				+ "    <p>Thank you,</p> "
    			+ "    <p>VCP</p> "
    			+ " "
    			+ "</body> "
    			+ "</html> "
    			+ "";
    	

    	emailModel.setContent(content);
    	trigger(emailModel);
    	
	}
	
}
