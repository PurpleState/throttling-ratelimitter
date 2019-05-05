package hello;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.util.concurrent.RateLimiter;
import com.weddini.throttling.Throttling;
import com.weddini.throttling.ThrottlingType;

@RestController
public class GreetingController {
	private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    
    RateLimiter rateLimiter = RateLimiter.create(10.0); // rate is "10 permits per second"

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	rateLimiter.acquire();
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
    /**
     * Throttling configuration:
     * <p>
     * allow 3 HTTP GET requests per minute
     * for each unique {@code javax.servlet.http.HttpServletRequest#getRemoteAddr()}
     */

    @GetMapping("/throttledController")
    @Throttling(limit = 3, timeUnit = TimeUnit.MINUTES, type = ThrottlingType.RemoteAddr)
    public ResponseEntity<String> controllerThrottling() {
        return ResponseEntity.ok().body("ok");
    }

}
