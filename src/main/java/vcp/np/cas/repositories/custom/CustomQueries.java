package vcp.np.cas.repositories.custom;

import java.sql.Timestamp;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomQueries {
	
	@PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Integer getUserInactivityDays(Long userId, Long clientId, Long serviceId, Timestamp loginTimeStamp) {
    	try {
            String queryStr = ""
            		+ "select "
            		+ "	case when timestampdiff(day, COALESCE(uca.last_loggedin_at, now()), :loginTimeStamp) < css.inactivity_period then 0 "
            		+ "	else timestampdiff(day, COALESCE(uca.last_loggedin_at, now()), :loginTimeStamp) end "
            		+ "from user_client_service ucs "
            		+ "	inner join client_service cs on cs.id = ucs.client_service_id "
            		+ "	inner join client_settings css on css.client_id = cs.client_id "
            		+ "where ucs.user_id = :userId "
            		+ "	and cs.client_id = :clientId "
            		+ "	and cs.service_id = :serviceId ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("loginTimeStamp", loginTimeStamp);
            query.setParameter("userId", userId);
            query.setParameter("clientId", clientId);
            query.setParameter("serviceId", serviceId);
            
            Object result = query.getSingleResult();
            if (result instanceof Number) {
            	Integer days = ((Number) result).intValue();
            	System.out.println("Is user[id: " + userId + "] active on client-service[clientId: " + clientId + ", serviceId: " + serviceId + "]?\n>> " + (days == 0));
            	
                return days;
            } else {
                throw new IllegalStateException("Query result is not a number");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        	System.out.println("Error encountered while checking inactivity of user[id: " + userId + "] on client-service[clientId: " + clientId + ", serviceId: " + serviceId + "]");
            return 0;
        }
    }
    
    
    @Transactional
    @Async
    public void updateTheUserLoginTime(Long userId, Long clientId, Long serviceId, Timestamp loginTimeStamp) {
        try {
        	System.out.println("Updating: user[id: " + userId + "]'s logged in time for client-service[clientId: " + clientId + ", serviceId: " + serviceId + "]");

            String updateQueryStr = ""
            		+ "update user_client_service ucs "
            		+ "	inner join client_service cs on cs.id = ucs.client_service_id "
            		+ "set ucs.last_loggedin_at = :loginTimeStamp "
            		+ "where ucs.user_id = :userId "
            		+ "	and cs.client_id = :clientId "
            		+ "	and cs.service_id = :serviceId ";
            
            Query updateQuery = entityManager.createNativeQuery(updateQueryStr);
            updateQuery.setParameter("loginTimeStamp", loginTimeStamp);
            updateQuery.setParameter("userId", userId);
            updateQuery.setParameter("clientId", clientId);
            updateQuery.setParameter("serviceId", serviceId);

        	System.out.println("Has the user[id: " + userId + "]'s logged time updated for client-service[clientId: " + clientId + ", serviceId: " + serviceId + "]?\n>> " + (updateQuery.executeUpdate() > 0));

        } catch (Exception ex) {
            ex.printStackTrace();
        	System.out.println("Error encountered while updating user[id: " + userId + "]'s logged time for client-service[clientId: " + clientId + ", serviceId: " + serviceId + "]");
        }
    }


    @Transactional(readOnly = true)
    public Integer getDaysSinceLastPasswordChange(Long userId, Long clientId, Timestamp loginTimeStamp) {
    	try {
            String queryStr = ""
            		+ "select "
            		+ "	case when timestampdiff(day, COALESCE(u.last_password_changed_at, now()), :loginTimeStamp) < css.password_expiration_period then 0 "
            		+ "	else timestampdiff(day, COALESCE(u.last_password_changed_at, now()), :loginTimeStamp) end "
            		+ "from user_client_service ucs "
            		+ "	inner join client_service cs on cs.id = ucs.client_service_id "
            		+ "	inner join client_settings css on css.client_id = cs.client_id "
            		+ "	inner join user u on u.id = ucs.user_id "
            		+ "where ucs.user_id = :userId "
            		+ "	and cs.client_id = :clientId ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("loginTimeStamp", loginTimeStamp);
            query.setParameter("userId", userId);
            query.setParameter("clientId", clientId);
            
            Object result = query.getSingleResult();
            if (result instanceof Number) {
            	
            	Integer days = ((Number) result).intValue();
            	System.out.println("Has the user[id: " + userId + "]'s password expired on client[id: " + clientId + "]?\n>> " + (days == 0));
            	
                return days;
            	
            } else {
                throw new IllegalStateException("Query result is not a number");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        	System.out.println("Error encountered while checking password expiration of user[id: " + userId + "] on client[id: " + clientId + "]");
            return 0;
        }
    }


    @Transactional(readOnly = true)
    public boolean isItInUserPasswordHistory(Long userId, String rawPassword) {
    	try {
            String queryStr = ""
            		+ "select "
            		+ "	case when exists( "
            		+ "		select * "
            		+ "		from user_password_history "
            		+ "		where user_id = :userId "
            		+ "			and password = sha2(concat(salt_value, :rawPassword) "
            		+ "	) then true "
            		+ "	else false end ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("userId", userId);
            query.setParameter("rawPassword", rawPassword);
            
            Object result = query.getSingleResult();
            System.out.println(result);
            if (result instanceof Number) {
            	
            	Integer days = ((Number) result).intValue();
            	System.out.println("Is it in user[id: " + userId + "]'s password history?\n>> " + (days == 1));
            	
                return (days == 1);
            	
            } else {
                throw new IllegalStateException("Query result is not a number");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        	System.out.println("Error encountered while checking user[id: " + userId + "]'s password history");
            return false;
        }
    }
}
