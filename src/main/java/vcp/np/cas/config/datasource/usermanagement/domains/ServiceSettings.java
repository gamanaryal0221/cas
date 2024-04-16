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
@Table(name = "service_settings", uniqueConstraints = @UniqueConstraint(columnNames = "service_id"))
public class ServiceSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
    

    @OneToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Column(name = "login_success_uri", length = 500)
    private String loginSuccessUri;
    
    @Column(name = "logout_success_path", length = 500)
    private String logoutSuccessPath;
   
    
	@PrePersist
	protected void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    

    public ServiceSettings() {
    }

	public String getLoginSuccessUri() {
		return loginSuccessUri;
	}

	public String getLogoutSuccessPath() {
		return logoutSuccessPath;
	}
    
}
