package projekti;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author salon
 */
@RestController
public class RestControllers {
    
    @Autowired
    PostRepository postRepo;
    
    @Autowired
    AccountRepository accRepo;
    
    @Autowired
    FollowerRepository followerRepo;
    
    @Transactional
    @PostMapping("/post/{postId}/like")
    public Map<String, String> likePost(@PathVariable String postId) {
        HashMap<String, String> map = new HashMap<>();
        Long id = Long.parseLong(postId);
        Post p = postRepo.getOne(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account a = accRepo.findByUsername(auth.getName());
        
        if (p.getLikes().contains(a)) {
            p.getLikes().remove(a);
            a.getLikedPosts().remove(p);
            map.put("likeStatus", "unlike");
        } else {
            p.getLikes().add(a);
            a.getLikedPosts().add(p);
            map.put("likeStatus", "like");
        }
        postRepo.save(p);
        accRepo.save(a);
        
        return map;
    }
    
    @Transactional
    @PostMapping("/user/{userId}/follow")
    public Map<String, String> follow(@PathVariable String userId) {
        HashMap<String, String> map = new HashMap<>();
        Long id = Long.parseLong(userId);
        
        Account followee = accRepo.getOne(id);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account a = accRepo.findByUsername(auth.getName());
        
        Follower f = followerRepo.findByUserAndFollowee(a, followee);
        
        if (f != null) {
            followee.getFollowers().remove(f);
            followerRepo.delete(f);
            map.put("followStatus", "unfollow");
        } else {
            Follower fl = new Follower();
            fl.setFollowedAt(new Date());
            fl.setUser(a);
            fl.setFollowee(followee);
            
            List<Follower> currFollowers = followee.getFollowers();
            currFollowers.add(fl);
            followee.setFollowers(currFollowers);
            
            map.put("followStatus", "follow");
            followerRepo.save(fl);
        }
        accRepo.save(a);
        accRepo.save(followee);
        
        return map;
    }
}
