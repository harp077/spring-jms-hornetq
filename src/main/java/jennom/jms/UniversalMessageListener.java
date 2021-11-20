package jennom.jms;

import com.google.gson.Gson;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.springframework.stereotype.Component;
import org.hornetq.jms.client.HornetQObjectMessage;
import org.hornetq.jms.client.HornetQTextMessage;
import org.springframework.jms.annotation.JmsListener;

@Component
public class UniversalMessageListener {

    @Inject
    private Gson gson;  

    // ONLY 1-listener for 1-queue !!!
    @JmsListener(destination = "harp07", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(Object message) {
        //if (message.getClass().getName().equals("jennom.jms.User")) {
        //if (message instanceof User) { 
        if (message.getClass() == HornetQObjectMessage.class) {
            try {
                HornetQObjectMessage hornetQObjectMessage = (HornetQObjectMessage) message;
                User user = (User) hornetQObjectMessage.getObject();
                System.out.println(" >>> Listener INFO: Received object user GSON = " + gson.toJson(user));
            } catch (JMSException | NullPointerException je) {
                System.out.println("Listener WARNING: JMS error = " + je.getMessage());                
            }
        } 
        //
        if (message.getClass() == HornetQTextMessage.class) {
            TextMessage textMessage = (TextMessage) message;
            try {
                User user = gson.fromJson(textMessage.getText(), User.class );
                //System.out.println("objListener INFO: >>> Received: " + textMessage.getText());
                System.out.println(" >>> Listener INFO: Received text user GSON = " + gson.toJson(user));
            } catch (JMSException ex) {
                System.out.println("Listener WARNING: JMS error = " + ex.getMessage());
            }                 
        }
    }

}
