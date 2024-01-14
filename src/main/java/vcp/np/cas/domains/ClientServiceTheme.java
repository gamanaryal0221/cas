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
@Table(name = "client_service_theme")
public class ClientServiceTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "background_image_url")
    private String backgroundImageUrl;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = new Timestamp(System.currentTimeMillis());
	}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    

    public ClientServiceTheme() {
    }

	public Client getClient() {
		return client;
	}

	public Service getService() {
		return service;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public String getBackgroundImageUrl() {
		return backgroundImageUrl;
	}

	public String getColorCode() {
		return colorCode;
	}

    
    
}
