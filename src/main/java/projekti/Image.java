package projekti;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class Image extends AbstractPersistable<Long> {
    
    @OneToOne
    private Account user;
    
    @NotEmpty
    @Size(min=2, max=100)
    private String fileName;
    
    @NotEmpty
    private String fileType;
    
    @Lob
    private byte[] image;
    
    private boolean profilePic;
    
    //@OneToMany
    //private List<Account> likes;
    
    private Date postedAt;
}
