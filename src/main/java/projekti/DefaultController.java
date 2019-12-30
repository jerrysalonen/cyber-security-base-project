package projekti;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DefaultController {
    
    @Autowired
    AccountService userSer;
    
    @Autowired
    AccountRepository userRepo;
    
    @Autowired
    PostRepository postRepo;
    
    @Autowired
    @Lazy
    PasswordEncoder passwordEncoder;
    
    @Autowired
    FollowerRepository followerRepo;
    
    @Autowired
    CommentRepository commentRepo;
    
    /*@Autowired
    ImageService imageSer;
    
    @Autowired
    ImageRepository imageRepo;*/

    @GetMapping("*")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        
        if (u == null) {
            return "index";
        }
        
        List<Account> posters = new ArrayList();
        posters.add(u);
        
        List<Follower> followees = followerRepo.findAllByUser(u);
        followees.forEach((f) -> {
            posters.add(f.getFollowee());
        });
        
        Pageable postPage = PageRequest.of(0, 25, Sort.by("postedAt").descending());
        List<Post> sortedPosts = postRepo.findAllByUserIn(posters, postPage);
        model.addAttribute("posts", sortedPosts);
        model.addAttribute("loggedInUser", u);
        return "index";
    }
    
    @PostMapping("/post")
    public String post(@RequestParam String content) {
        
        Post post = new Post();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        Date date = new Date();
        
        post.setContent(content);
        post.setPostedAt(date);
        post.setUser(u);
        
        postRepo.save(post);
        
        return "redirect:/index";
    }
    
    @PostMapping("/comment/{user}/{id}")
    public String comment(@RequestParam String content, @PathVariable String user, @PathVariable Long id) {
        
        Comment comment = new Comment();
        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Account u = userRepo.findByUsername(auth.getName());
        Account u = userRepo.findByUsername(user);
        Date date = new Date();
        
        comment.setContent(content);
        comment.setPostedAt(date);
        comment.setUser(u);
        
        Post post = postRepo.getOne(id);
        
        comment.setPost(post);
        
        List<Comment> comments = postRepo.getOne(id).getComments();
        comments.add(comment);
        post.setComments(comments);
        
        commentRepo.save(comment);
        postRepo.save(post);
        
        return "redirect:/post/" + id;
    }
    
    @GetMapping("/user/{id}")
    public String user(Model model, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        model.addAttribute("loggedInUser", u);
        model.addAttribute("user", userRepo.getOne(id));
        
        Pageable postPage = PageRequest.of(0, 25, Sort.by("postedAt").descending());
        List<Post> sortedPosts = postRepo.findAllByUser(userRepo.getOne(id), postPage);
        model.addAttribute("posts", sortedPosts);
        
        Follower f = followerRepo.findByUserAndFollowee(u, userRepo.getOne(id));
        if (f != null) {
            model.addAttribute("following", true);
        } else {
            model.addAttribute("following", false);
        }
        
        model.addAttribute("followers", userRepo.getOne(id).getFollowers().size());
        return "user";
    }
    
    @GetMapping("/post/{id}")
    public String onePost(Model model, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        model.addAttribute("loggedInUser", u);
        
        Pageable postPage = PageRequest.of(0, 25, Sort.by("postedAt").descending());
        
        model.addAttribute("post", postRepo.getOne(id));
        model.addAttribute("comments", commentRepo.findAllByPost(postRepo.getOne(id), postPage));
        return "post";
    }
    
    @GetMapping("/followers")
    public String followers(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        model.addAttribute("loggedInUser", u);

        model.addAttribute("followers", followerRepo.findAllByFollowee(u));
        
        return "followers";
    }
    
    @GetMapping("/followees")
    public String followees(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        model.addAttribute("loggedInUser", u);

        model.addAttribute("followees", followerRepo.findAllByUser(u));
        
        return "following";
    }
    
    @GetMapping("/search")
    public String search(Model model, @RequestParam String name) {
        if (name == null) {
            return "index";
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        model.addAttribute("loggedInUser", u);
        
        List<Account> acc = userRepo.findAllByNameContainingIgnoreCase(name);
        model.addAttribute("users", acc);
        
        model.addAttribute("searchParam", name);
        
        if (acc == null) {
            return "index";
        }
        
        return "search";
    }
    
    @PostMapping("/signup")
    public String postSignup(@Valid @ModelAttribute Account user, BindingResult br, Model model) {
        
        if(br.hasErrors()) {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("uniqueUsername", true);
            System.out.println("haserrors");
            return "signup";
        }
        
        if ( !userSer.checkUniqueUsername(user) ){
            model.addAttribute( "isLoggedIn", false );
            model.addAttribute( "uniqueUsername", userSer.checkUniqueUsername(user) );
            System.out.println("unique false");
            return "signup";
        }
        
        String pw = user.getPassword();
        user.setPassword(passwordEncoder.encode(pw));
        userRepo.save(user);
        model.addAttribute(user);
        model.addAttribute("isLoggedIn", false);
        return "redirect:/login";
    }
    
    @GetMapping("/signup")
    public String signup(Model model) {
        if (userSer.loggedInUser() != null) {
            return "redirect:/index";
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account u = userRepo.findByUsername(auth.getName());
        model.addAttribute("loggedInUser", u);
        
        if (u != null) {
            return "redirect:/";
        }
        
        model.addAttribute("user", new Account());
        return "signup";
    }
}
