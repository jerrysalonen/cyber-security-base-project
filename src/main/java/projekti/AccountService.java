package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 *
 * @author salon
 */
@Service
public class AccountService {
    
    @Autowired
    private AccountRepository userRepo;
    
    public boolean checkUniqueUsername(Account profile){
        boolean unique = true;
        if (userRepo.findByUsername(profile.getUsername()) != null ){
            unique = false;
        }
        return unique;
    }
    
    public void addProfile(Account user){
        if (userRepo.findByUsername(user.getUsername()) == null) {
            userRepo.save(user);
        }
    }
    
    public Account loggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        return u;
    }
}
