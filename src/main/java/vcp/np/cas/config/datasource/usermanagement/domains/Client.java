package vcp.np.cas.config.datasource.usermanagement.domains;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "client", uniqueConstraints = { @UniqueConstraint(columnNames = "name"),
		@UniqueConstraint(columnNames = "vcp_id") })
public class Client {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "soft_deleted", columnDefinition = "BIT DEFAULT 0")
	private boolean softDeleted = false;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
    

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@Column(name = "display_name", length = 500)
	private String displayName;

	@Column(name = "vcp_id", nullable = false, unique = true, length = 20)
	private String vcpId;

	@ManyToOne
	@JoinColumn(name = "parent_client_id", referencedColumnName = "id")
	private Client parentClient;

	@Column(name = "is_google_authentication_enabled", nullable = false, columnDefinition = "BIT DEFAULT 0")
	private boolean isGoogleAuthenticationEnabled = false;

	@Column(name = "is_credential_authentication_enabled", nullable = false, columnDefinition = "BIT DEFAULT 1")
	private boolean isCredentialAuthenticationEnabled = true;


	@PrePersist
	protected void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    
	public Client() {
	}

	public Long getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	

}
