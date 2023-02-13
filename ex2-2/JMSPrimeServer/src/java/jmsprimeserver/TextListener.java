package jmsprimeserver;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class TextListener implements MessageListener {

    private MessageProducer replyProducer;
    private Session session;

    private boolean isPrime(int n) {
        int i;
        for (i = 2; i * i <= n; i++) {
            if ((n % i) == 0) {
                return false;
            }
        }
        return true;
    }

    public TextListener(Session session) {

        this.session = session;
        try {
            replyProducer = session.createProducer(null);
        } catch (JMSException ex) {
            Logger.getLogger(TextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;

        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                System.out.println("Reading message: " + msg.getText());
            } else {
                System.err.println("Message is not a TextMessage");
            }

            String[] arrOfRange = msg.getText().split(",");
            int num1 = Integer.parseInt(arrOfRange[0]);
            int num2 = Integer.parseInt(arrOfRange[1]);
            int temp = 0;
            for(int i=num1+1; i<num2; i++) {
                if(isPrime(i)) temp++;
            }
            String prime = Integer.toString(temp);
            TextMessage response = session.createTextMessage("The number of primes between " 
                    + arrOfRange[0] + " and " + arrOfRange[1] + " is " + prime);
            System.out.println("sending message The number of primes between " + arrOfRange[0] +
                    " and " + arrOfRange[1] + " is " + prime);
            replyProducer.send(message.getJMSReplyTo(), response);
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }

    }
}
