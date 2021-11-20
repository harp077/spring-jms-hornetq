package jennom.jms;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component("txtSender")
public class MessageSenderTxt { //implements MessageSender {
    
    @Inject
    private JmsTemplate jmsTemplate;

    //@Override
    public void sendMessage(String destinationNameQ, String message) {
        jmsTemplate.setDeliveryDelay(500L);
        this.jmsTemplate.send(destinationNameQ, new MessageCreator() {
            @Override
            public Message createMessage(Session session)
                    throws JMSException {
                TextMessage jmsMessage = session.createTextMessage(message);
                System.out.println(">>> Sending txt user: " + jmsMessage.getText());
                return jmsMessage;
            }
        });
    }
}
