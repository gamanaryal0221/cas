package vcp.np.cas.utils.email;


public class MailConfig {
    
    private String senderMailAddress = "";
    private String password = "";


    public MailConfig(String sender, String password) {
        this.senderMailAddress = sender;
        this.password = password;
    }


    public String getSenderMailAddress() {
        return senderMailAddress;
    }


    public String getPassword() {
        return password;
    }

}
