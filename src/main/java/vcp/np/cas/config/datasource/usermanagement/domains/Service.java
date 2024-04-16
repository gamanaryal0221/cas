package vcp.np.cas.config.datasource.usermanagement.domains;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "service", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
    

    @Column(name = "soft_deleted", columnDefinition = "BIT DEFAULT 0")
    private boolean softDeleted = false;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "display_name", length = 500)
    private String displayName;
    

	@PrePersist
	protected void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }


    public Service() {
    }

    

	public Service(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public Long getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}
    
}
