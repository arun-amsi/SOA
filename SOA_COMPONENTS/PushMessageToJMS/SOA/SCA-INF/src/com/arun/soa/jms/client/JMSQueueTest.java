package com.arun.soa.jms.client;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSQueueTest {
    public JMSQueueTest() {
        super();
    }

    public static void main(String[] args) throws Exception {
        
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        props.put(Context.PROVIDER_URL, "t3://localhost:7101");

        Context ctx = new InitialContext(props);

        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup("jms/testCF"); //Connection Factory JNDI

        QueueConnection con = factory.createQueueConnection();

        QueueSession session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = (Queue) ctx.lookup("jms/testQueue"); //Find the Queue

        QueueSender sender = session.createSender(queue); //Instantiating the message sender

        TextMessage message = session.createTextMessage("Message sent");

        con.start();

        sender.send(message);

        //Now receiving the message:

        QueueReceiver receiver = session.createReceiver(queue);

        TextMessage receivedMessage = (TextMessage) receiver.receive();

        System.out.println("Received message = " + receivedMessage);
    }

}
