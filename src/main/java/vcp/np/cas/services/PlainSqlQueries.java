package vcp.np.cas.services;

import java.sql.Timestamp;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import vcp.np.cas.config.datasource.usermanagement.domains.User;
import vcp.np.cas.services.AuthenticationService.PasswordDetails;

@Repository
public class PlainSqlQueries {
	
	@PersistenceContext
    private EntityManager entityManager;

    
    @Transactional(readOnly = true)
    public Long getClientServiceByRequestHost(String requestHost) {
    	
    	if (requestHost == null || requestHost.isEmpty()) {
            System.out.println("requestHost:" + requestHost + " is null or empty in getClientServiceByRequestHost");
            return null;
    	}
    	
    	try {
    		String queryStr = ""
            		+ "select * "
            		+ "from client_service "
            		+ "where cs.request_host = :requestHost ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("requestHost", requestHost);
            
            Object result = query.getResultList();
            System.out.println("result:" + result);
            // if (result instanceof Long) {
            	
            // 	Long userClientServiceId = ((Long) result).longValue();
            // 	System.out.println("Does user[username:'" + username + "'] exists and have access on client-service[id:" + clientServiceId + "]?\n >> " + (userClientServiceId != null));
            	
            //     return userClientServiceId;
            	
            // } else {
            //     throw new IllegalStateException("Query result is not a number");
            // }

            return null;
            
        } catch (Exception ex) {
            ex.printStackTrace();
        	// System.out.println("Error encountered while fetching UserClientService by credential user[username:'" + username + "'] on client-service[id:" + clientServiceId + "]");
            return null;
        }
    }


    @Transactional(readOnly = true)
    public Long getUserClientServiceIdByCredential(String username, Long clientServiceId) {
    	
    	if (username == null || username.isEmpty() || clientServiceId == null) {
            System.out.println("user:" + username + " or clientServiceId:" + clientServiceId + " is null or empty in getUserClientServiceByCredential");
            return null;
    	}
    	
    	try {
    		String queryStr = ""
            		+ "select ucs.id "
            		+ "from user_client_service ucs "
            		+ "	inner join user u on u.id = ucs.user_id "
            		+ "where ucs.client_service_id = :clientServiceId "
            		+ "	and ( "
            		+ "		u.username = :username or "
            		+ "		u.mail_address = :username or "
            		+ "		u.number = :username "
            		+ "	) ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("username", username);
            query.setParameter("clientServiceId", clientServiceId);
            
            Object result = query.getSingleResult();
            if (result instanceof Long) {
            	
            	Long userClientServiceId = ((Long) result).longValue();
            	System.out.println("Does user[username:'" + username + "'] exists and have access on client-service[id:" + clientServiceId + "]?\n >> " + (userClientServiceId != null));
            	
                return userClientServiceId;
            	
            } else {
                throw new IllegalStateException("Query result is not a number");
            }
            
        } catch (NoResultException ex) {
            System.out.println("Does user[username:'" + username + "'] exists and have access on client-service[id:" + clientServiceId + "]?\n >> false");
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
        	System.out.println("Error encountered while checkig access of user[username:'" + username + "'] on client-service[id:" + clientServiceId + "]");
            return null;
        }
    }


    @Transactional(readOnly = true)
    public Integer getUserInactivityDays(Long userId, Long clientId, Long serviceId, Timestamp loginTimeStamp) {
    	
    	if (userId == null || clientId == null || serviceId == null || loginTimeStamp == null) {
            System.out.println("userId:" + userId + "or clientId:" + clientId + "or serviceId:" + serviceId + " or loginTimeStamp:" + loginTimeStamp + " is null in getUserInactivityDays");
            return 0;
    	}
    	
    	try {

            String queryStr = ""
            		+ "with max_last_loggedin_at as ( "
            		+ "	select coalesce(max(ucs.last_loggedin_at), now()) as last_loggedin_at "
            		+ "	from user_client_service ucs "
            		+ "		inner join client_service cs on cs.id = ucs.client_service_id "
            		+ "	where ucs.user_id = :userId "
            		+ "		and cs.client_id = :clientId "
            		+ ") "
            		+ "select case "
            		+ "           when timestampdiff(day, mlla.last_loggedin_at, :loginTimeStamp) < css.inactivity_period then 0 "
            		+ "           else timestampdiff(day, mlla.last_loggedin_at, :loginTimeStamp) "
            		+ "       end "
            		+ "from max_last_loggedin_at mlla "
            		+ "	join client_settings css on css.client_id = :clientId ";

            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("loginTimeStamp", loginTimeStamp);
            query.setParameter("userId", userId);
            query.setParameter("clientId", clientId);
            
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


    @Transactional(readOnly = true)
    public Integer getDaysSinceLastPasswordChange(Long userId, Long clientId, Timestamp loginTimeStamp) {
    	
    	if (userId == null || clientId == null || loginTimeStamp == null) {
            System.out.println("userId:" + userId + "or clientId:" + clientId + " or loginTimeStamp:" + loginTimeStamp + " is null in getDaysSinceLastPasswordChange");
            return 0;
    	}
    	
    	try {
            
            String queryStr = ""
            		+ "with max_last_password_changed_at as ( "
            		+ "	select coalesce((select max(changed_at) from user_password_history where user_id = u.id), u.created_at) as last_password_changed_at "
            		+ "	from user_client_service ucs "
            		+ "		inner join client_service cs on cs.id = ucs.client_service_id "
            		+ "		inner join client_settings css on css.client_id = cs.client_id "
            		+ "		inner join user u on u.id = ucs.user_id "
            		+ "	where ucs.user_id = :userId "
            		+ "		and cs.client_id = :clientId "
            		+ ") "
            		+ "select case "
            		+ "           when timestampdiff(day, mlpca.last_password_changed_at, :loginTimeStamp) < css.password_expiration_period then 0 "
            		+ "           else timestampdiff(day, mlpca.last_password_changed_at, :loginTimeStamp) "
            		+ "       end "
            		+ "from max_last_password_changed_at mlpca "
            		+ "	join client_settings css on css.client_id = :clientId ";
            
            
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
    	
    	if (userId == null || rawPassword == null || rawPassword.isEmpty()) {
            System.out.println("userId:" + userId + " or rawPassword:" + rawPassword + "is null or empty in isItInUserPasswordHistory");
            return false;
    	}
    	
    	try {
            String queryStr = ""
            		+ "select count(*) "
            		+ "from user_password_history "
            		+ "where user_id = :userId "
            		+ "	and password = sha2(concat(salt_value, :rawPassword), 512) ";
            
            Query query = entityManager.createNativeQuery(queryStr);
            query.setParameter("userId", userId);
            query.setParameter("rawPassword", rawPassword);
            
            Object result = query.getSingleResult();
            if (result instanceof Number) {
            	
            	boolean isInUserPasswordHistory = (((Number) result).intValue() == 1);
            	System.out.println("Is it in user[id: " + userId + "]'s password history?\n>> " + isInUserPasswordHistory);
            	
                return isInUserPasswordHistory;
            	
            } else {
                throw new IllegalStateException("Query result is not a number");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        	System.out.println("Error encountered while checking user[id: " + userId + "]'s password history");
            return false;
        }
    }

    
    @Transactional
    @Async
    public void updateTheUserLoginTime(Long userId, Long clientId, Long serviceId, Timestamp loginTimeStamp) {
    	
    	if (userId == null || clientId == null || serviceId == null || loginTimeStamp == null) {
            System.out.println("userId:" + userId + "or clientId:" + clientId + "or serviceId:" + serviceId + " or loginTimeStamp:" + loginTimeStamp + " is null in updateTheUserLoginTime");
    	}
    	
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

        	System.out.println("Is user[id: " + userId + "]'s logged time for client-service[clientId: " + clientId + ", serviceId: " + serviceId + "] update success?\n>> " + (updateQuery.executeUpdate() > 0));

        } catch (Exception ex) {
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        	System.out.println("Error encountered while updating user[id: " + userId + "]'s logged time for client-service[clientId: " + clientId + ", serviceId: " + serviceId + "]");
        }
    }
    
    
    @Transactional
    public boolean isUserPasswordUpdated(User user, PasswordDetails newPasswordDetail, Timestamp passwordResetTimeStamp) {
    	
    	if (user == null || newPasswordDetail == null) {
            System.out.println("user:" + user + " or newPasswordDetail:" + newPasswordDetail + " is null in IsUserPasswordUpdated");
            return false;
    	}
    	
    	Long userId = user.getId();

    	try {
    		System.out.println("Updating: user[id: " + userId + "]'s password ...");

            // Update user password
            String updateOnUserTableQueryStr = ""
            		+ "update user u "
            		+ "set u.salt_value = :saltValue, "
            		+ "	u.password = :password "
            		+ "where u.id = :userId ";
            
            Query updateOnUserTableQuery = entityManager.createNativeQuery(updateOnUserTableQueryStr);
            updateOnUserTableQuery.setParameter("userId", userId);
            updateOnUserTableQuery.setParameter("saltValue", newPasswordDetail.getSaltValue());
            updateOnUserTableQuery.setParameter("password", newPasswordDetail.getHashedPassword());
            
            if (updateOnUserTableQuery.executeUpdate() <= 0) {
                throw new Exception("Failed to update user [id: " + userId + "]'s password on user table");
            }
            System.out.println("User [id: " + userId + "]'s password updated on user table");
            
            
            // Insert into user_password_history
            String insertIntoUserPasswordHistoryQueryStr = ""
        	        + "insert into user_password_history (user_id, salt_value, password, changed_at) "
        	        + "VALUES (:userId, :saltValue, :password, :passwordResetTimeStamp)";

        	Query insertIntoUserPasswordHistoryQuery = entityManager.createNativeQuery(insertIntoUserPasswordHistoryQueryStr);
        	insertIntoUserPasswordHistoryQuery.setParameter("userId", userId);
        	insertIntoUserPasswordHistoryQuery.setParameter("saltValue", user.getSaltValue());
        	insertIntoUserPasswordHistoryQuery.setParameter("password", user.getPassword());
        	insertIntoUserPasswordHistoryQuery.setParameter("passwordResetTimeStamp", passwordResetTimeStamp);

            if (insertIntoUserPasswordHistoryQuery.executeUpdate() <= 0) {
                throw new Exception("Failed to insert user [id: " + userId + "]'s password into user_password_history table");
            }

            System.out.println("User[id:" + userId + "]'s password inserted into user_password_history table");
            return true;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
        	System.out.println("Error encountered while updating user[id: " + userId + "]'s password");
            return false;
        }
		
    }


    public boolean isPasswordUpdatedAfterTokenIssue(Long userId, Timestamp jwtTokenIssuetAt) {
        
    	if (userId == null || jwtTokenIssuetAt == null) {
            System.out.println("userId:" + userId + " or jwtTokenIssuetAt:" + jwtTokenIssuetAt + " is null in isTokenAlreadyUsed");
            return false;
    	}
    	
    	
        String queryStr = ""
	            + "select case "
	            + "	when exists(select * from user_password_history where user_id = :userId and changed_at >= :jwtTokenIssuetAt) then 1 "
	            + "	else 0 "
	            + "end ";

        Query query = entityManager.createNativeQuery(queryStr);
        query.setParameter("jwtTokenIssuetAt", jwtTokenIssuetAt);
        query.setParameter("userId", userId);

        Object result = query.getSingleResult();
        if (result instanceof Number) {
            
        	boolean isUpdated = (((Number) result).intValue() == 1);
        	System.out.println("Is user[id: " + userId + "]'s password updated after token issue?\n>> " + isUpdated);
        	
            return isUpdated;
            
        } else {
            throw new IllegalStateException("Query result is not a number");
        }
    }
}
