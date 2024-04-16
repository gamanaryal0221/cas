package vcp.np.cas;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import vcp.np.cas.config.datasource.usermanagement.domains.User;
import vcp.np.cas.config.datasource.usermanagement.repositories.UserRepository;

@SpringBootTest
class CasApplicationTests {


	@Autowired
	private UserRepository userRepository;

	@Test
	void dbTest() {
		System.out.println("................... Testing ...................");

		List<User> userList = userRepository.findAll();
		System.out.println("userList size:" + userList.size());
		
		for (User user : userList) {
			System.out.println("user: " + user.getUsername());
		}

	}

}
