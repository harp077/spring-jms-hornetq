package jennom.hot;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQJMSConnectionFactory;
import org.hornetq.jms.client.HornetQQueue;
import org.springframework.jms.annotation.EnableJms;
import java.util.HashMap;
import java.util.Map;
import javax.jms.ConnectionFactory;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@ComponentScan(basePackages = {"jennom"})
@EnableJms
public class AppContext {
    
    /// ========================= JMS ============================

    @Bean 
    public HornetQQueue harp07() {
	return new HornetQQueue("harp07");
    }
    
    /* without auth - for default user:passw = guest:guest
    @Bean 
    public ConnectionFactory connectionFactory() {
	Map<String, Object> connDetails = new HashMap<>();
	connDetails.put(TransportConstants.HOST_PROP_NAME, "127.0.0.1");
	connDetails.put(TransportConstants.PORT_PROP_NAME, "5445");
	TransportConfiguration transportConfiguration = 
            new TransportConfiguration(NettyConnectorFactory.class.getName(), connDetails);
        // HornetQJMSConnectionFactory(boolean ha, TransportConfiguration[] initialConnectors)
	return new HornetQJMSConnectionFactory(false, transportConfiguration);
    }*/

    // with auth - for user:passw = romka:coldrom
    @Bean 
    public CachingConnectionFactory connectionFactory() {
	Map<String, Object> connDetails = new HashMap<>();
	connDetails.put(TransportConstants.HOST_PROP_NAME, "127.0.0.1");
	connDetails.put(TransportConstants.PORT_PROP_NAME, "5445");
	TransportConfiguration transportConfiguration = 
            new TransportConfiguration(NettyConnectorFactory.class.getName(), connDetails);
        // HornetQJMSConnectionFactory(boolean ha, TransportConfiguration[] initialConnectors)
        ConnectionFactory hornetCF = new HornetQJMSConnectionFactory(false, transportConfiguration);
        UserCredentialsConnectionFactoryAdapter userCF = new UserCredentialsConnectionFactoryAdapter();
        userCF.setTargetConnectionFactory(hornetCF);
        userCF.setUsername("romka");
        userCF.setPassword("romka");
        CachingConnectionFactory mainCF = new CachingConnectionFactory(userCF);
	return mainCF; 
    }

    @Bean
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory() {
	DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	factory.setConnectionFactory(connectionFactory());
	factory.setConcurrency("3-5");
	return factory;
    }

    @Bean 
    public JmsTemplate jmsTemplate() {
	JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        // Default Destination
	//jmsTemplate.setDefaultDestination(harp07());
	return jmsTemplate;
    }   
    
    @Bean(name = "gson")
    public Gson gson() {
        return new Gson();
    }         

}
