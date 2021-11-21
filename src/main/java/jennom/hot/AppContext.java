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
import javax.jms.Queue;
import javax.jms.Topic;
import jennom.jms.JmsExceptionListener;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.jms.client.HornetQTopic;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.UserCredentialsConnectionFactoryAdapter;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

@Configuration
@ComponentScan(basePackages = {"jennom"})
@EnableJms
public class AppContext {
    
    private String concurrency = "1-8";
    
    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }   
    
    //=========== new 21-11-2021
    
    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(JmsExceptionListener jmsExceptionListener) {
    	DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
	factory.setConnectionFactory(jmsConnectionFactory(jmsExceptionListener));
	factory.setDestinationResolver(destinationResolver());
	factory.setConcurrency(concurrency);
	factory.setPubSubDomain(false);
	return factory;
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory(JmsExceptionListener jmsExceptionListener) {
    	return createJmsConnectionFactory(jmsExceptionListener);
    }

    private ConnectionFactory createJmsConnectionFactory(JmsExceptionListener jmsExceptionListener) {
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
	CachingConnectionFactory connectionFactory = new CachingConnectionFactory(userCF);
        connectionFactory.setExceptionListener(jmsExceptionListener);
	return connectionFactory;
    }

	@Bean(name = "jmsQueueTemplate")
	public JmsTemplate createJmsQueueTemplate(ConnectionFactory jmsConnectionFactory) {
		return new JmsTemplate(jmsConnectionFactory);
	}

	@Bean(name = "jmsTopicTemplate")
	public JmsTemplate createJmsTopicTemplate(ConnectionFactory jmsConnectionFactory) {
		JmsTemplate template = new JmsTemplate(jmsConnectionFactory);
		template.setPubSubDomain(true);
		return template;
	}

	@Bean
	public DestinationResolver destinationResolver() {
		return new DynamicDestinationResolver();
	}    
    
    /// ========================= JMS ============================

    /*@Bean 
    public Queue queue() {
	return new HornetQQueue("harp07qq");
                //HornetQJMSClient.createQueue("harp07qq");
                //new HornetQQueue("harp07qq");
    }
    
    @Bean 
    public Topic topic() {
	return new HornetQTopic("harp07tt");
                //HornetQJMSClient.createTopic("harp07tt");
                //new HornetQTopic("harp07tt");
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
    }

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
        //factory.setPubSubDomain(true);
	return factory;
    }

    @Bean 
    public JmsTemplate jmsTemplate() {
	JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
        // Default Destination
	//jmsTemplate.setPubSubDomain(true);
        //jmsTemplate.setDefaultDestination(queue());
	return jmsTemplate;
    }  */
        
    //==================================
    
    @Bean(name = "gson")
    public Gson gson() {
        return new Gson();
    }         

}
