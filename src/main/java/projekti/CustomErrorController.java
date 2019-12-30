package projekti;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author salon
 */
public class CustomErrorController implements ErrorController {
    @GetMapping("/error")
    public String error() {
        return "error";
    }
    
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
