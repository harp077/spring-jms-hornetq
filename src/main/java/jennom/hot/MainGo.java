package jennom.hot;

import com.google.gson.Gson;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import jennom.jms.MessageSenderObj;
import jennom.jms.MessageSenderTxt;
import jennom.jms.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class MainGo extends javax.swing.JFrame {
    
    private User user;
    @Inject
    private MessageSenderTxt txtSender;
    @Inject
    private MessageSenderObj objSender;
    @Inject
    private Gson gson;      

    @PostConstruct
    public void afterBirn() {
        // JMS-test
        for(int i=1; i < 10; ++i) {
            user=new User();
            user.setLogin("oo"+i+i);
            user.setPassw("oo"+i+i);
            objSender.sendMessage("harp07", user); 
        }      
        for(int i=1; i < 10; ++i) {
            user=new User();
            user.setLogin("tt"+i+i);
            user.setPassw("tt"+i+i);
            txtSender.sendMessage("harp07", gson.toJson(user)); 
        }                 

    }

    public synchronized static void main(String args[]) {
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(AppContext.class);
        ctx.registerShutdownHook();
        System.out.println("Enter 'stop' to close");
        Scanner sc = new Scanner(System.in);
        new Thread(() -> {
            while (true) {
                if (sc.next().toLowerCase().trim().equals("stop")) {
                    System.out.println("Closing");
                    System.exit(0);
                    break;
                }
            }
        }).start();
    }
}
