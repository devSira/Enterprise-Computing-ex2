package jmsprimeclient;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class Main {
    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/TempQueue")
    private static Queue queue;
   
    public static void main(String[] args) {
        Connection connection = null;
        TextListener listener = null;
                 
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            listener = new TextListener();
            
            Queue tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            responseConsumer.setMessageListener(listener);
            MessageProducer producer = session.createProducer(queue);
            
            connection.start();
            while(true) {
                System.out.println("Enter two numbers. Use ',' to seperate each number. To end the program press Enter");
                Scanner sc = new Scanner(System.in);
                String input = sc.nextLine();
                if (input.equals("")) {
                    break;
                }
                TextMessage message = session.createTextMessage();
                message.setText(input);
                message.setJMSReplyTo(tempDest);
    //            connection.start();
                System.out.println("Sending message: " + message.getText());
                producer.send(message);
            }
            
        } catch (JMSException e) {
            System.err.println("Exception occurred: " + e.toString());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }
}
