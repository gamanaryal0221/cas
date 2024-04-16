package vcp.np.cas.config.datasource.usermanagement.domains;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "client_settings", uniqueConstraints = @UniqueConstraint(columnNames = "client_id"))
public class ClientSettings {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
    
    
    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "is_sharing_client", columnDefinition = "BIT DEFAULT 1")
    private boolean isSharingClient = true;
    
    @Column(name = "inactivity_period", nullable = false)
    private int inactivityPeriod = 2;
    
    @Column(name = "password_expiration_period", nullable = false)
    private int passwordExpirationPeriod = 2;
    
    @Column(name = "max_login_attempt", nullable = false)
    private int maxLoginAttempt = 3;
    
    @Column(name = "logout_redirect_url", length = 500)
    private String logoutRedirectUrl;
    
    
	@PrePersist
	protected void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    

    public ClientSettings() {
    }
    
	public String getLogoutRedirectUrl() {
		return logoutRedirectUrl;
	}
    
}
