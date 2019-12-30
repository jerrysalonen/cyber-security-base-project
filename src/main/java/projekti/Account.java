package projekti;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author salon
 */

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Account extends AbstractPersistable<Long> {

    @Size(min=4, max=50)
    private String username;

    @Size(min=2, max=50)
    private String name;

    @Size(min=6, max=100)
    private String password;
    
    @OneToMany(mappedBy="user")
    private List<Image> images;
    
    @OneToMany
    private List<Post> posts;
    
    @OneToMany
    private List<Follower> followers;
    
    //@OneToMany
    //private List<Account> followees;
    
    @ManyToMany
    @JoinTable(
        name = "post_like", 
        joinColumns = @JoinColumn(name = "account_id"), 
        inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<Post> likedPosts;
}
