package projekti;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
public class Follower extends AbstractPersistable<Long> {
    
    @OneToOne
    private Account user;
    
    @OneToOne
    private Account followee;
    
    private Date followedAt;
    
}
