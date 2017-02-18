package com.arun.soa.jms.client;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.Message;
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

public class WLSQueueClient {

    private static final String QUEUE_CONNECTION_FACTORY = "jms/requestCF";
    private static final String QUEUE_RECEIVE = "jms/requestQueue";
    private static final String QUEUE_SEND = "jms/requestQueue";
    private static final String PAYLOAD =
        "<?xml version=\"1.0\" ?><ExpenseRecord xmlns=\"http://xmlns.oracle.com/pcbpel/samples/expense\"><EmpId>1</EmpId><Item>Item2</Item><Count>3</Count><Cost>40</Cost></ExpenseRecord>";

    public static void main(String[] args) throws Exception {
        WLSQueueClient client = new WLSQueueClient();

        //trigger bpel process
        client.enqueue();
    }

    private void enqueue() throws Exception {
        this.debug("***Message Payload = " + PAYLOAD);

        QueueConnectionFactory qcf = getQueueConnectionFactory(QUEUE_CONNECTION_FACTORY);
        QueueConnection qc = qcf.createQueueConnection();
        QueueSession qs = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        qc.start();

        Queue initialReceive = null;
        initialReceive = getQueue(QUEUE_RECEIVE);

        QueueSender initialSender = qs.createSender(initialReceive);
        Message initialMsg = null;

        initialMsg = createJmsTextMessage(qs, PAYLOAD);
        initialSender.send(initialMsg);
        System.out.println("Message Send");
        this.debug("***Initial msg to trigger process sent on " + QUEUE_RECEIVE);
    }


    private QueueConnectionFactory getQueueConnectionFactory(String name) throws Exception {
        QueueConnectionFactory ret = null;

        debug("*** looking up Connection Factory\"" + name + "\" via JNDI");

        Context ctx = getInitialContext();
        ret = (QueueConnectionFactory) ctx.lookup(name);
        ctx.close();

        return ret;
    }


    private Queue getQueue(String name) throws Exception {
        Queue ret = null;

        debug("*** looking up Destination \"" + name + "\" via JNDI");

        Context ctx = getInitialContext();
        ret = (Queue) ctx.lookup(name);
        ctx.close();

        return ret;
    }


    private TextMessage createJmsTextMessage(Session s, String payload) throws JMSException {
        TextMessage msg = s.createTextMessage();
        msg.setText(payload);
        return msg;
    }


    private InitialContext getInitialContext() throws NamingException {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        env.put(Context.PROVIDER_URL, "t3://localhost:7101");
        return new InitialContext(env);
    }

    private void debug(String msg) {
        System.out.println(msg);
    }

}
