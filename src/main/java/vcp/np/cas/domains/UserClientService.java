package vcp.np.cas.domains;

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
@Table(name = "user_client_service",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "client_service_id"}, name = "user_client_service_unique")
		}
)
public class UserClientService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "client_service_id", nullable = false)
    private ClientService clientService;
    
    @Column(name = "last_loggedin_at", columnDefinition = "TIMESTAMP")
    private Timestamp lastLoggedInAt;

	@Column(name = "is_active", columnDefinition = "BIT DEFAULT 1")
	private boolean isActive = true;


	@PrePersist
	protected void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
		lastLoggedInAt = new Timestamp(System.currentTimeMillis());
	}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }


    // Constructors, getters, and setters

    public UserClientService() {
        // Default constructor
    }
    
    
    public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public ClientService getClientService() {
		return clientService;
	}


	public Client getClient() {
    	return clientService.getClient();
    }

    public Service getService() {
    	return clientService.getService();
    }

	public boolean isActive() {
		return isActive;
	}
    
}
