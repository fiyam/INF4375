package ca.uqam;

import java.util.Random;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class SmartHouse {
	
	public static void main(String [] args) throws InterruptedException {
		
		////ZMQ.Socket publisher;
		//ZMQ.Socket subscriber;
		
                    Boolean heater = null;    
                    Boolean ac = null; 
                    Boolean lights = null;
                    Boolean door_lock = null;

    
                        
		try (ZContext context = new ZContext()) {
			

                        Socket subscriber = context.createSocket(SocketType.SUB); 
                        if (args.length == 1) 
                            subscriber.connect(args[0]); 
                        else subscriber.connect("tcp://localhost:5555"); 


                        Random rand = new Random(System.currentTimeMillis()); 
                        String subscription = String.format("%03d", rand.nextInt(1000)); 
                        subscriber.subscribe(subscription.getBytes(ZMQ.CHARSET)); 


                        while (true) { 
                            String topic = subscriber.recvStr(); 
                           if (topic == null) 
                                break; 
                            String data = subscriber.recvStr(); 
                            assert (topic.equals(subscription)); 
                            System.out.println(data); 
                       }

     
              
              
              
              


                        Socket publisher = context.createSocket(SocketType.PUB); 
                       if (args.length == 1) 
                           publisher.connect(args[0]); 
                       else publisher.bind("tcp://*:6666"); 

                       //  Ensure subscriber connection has time to complete 
                       Thread.sleep(1000); 


                       //  Send out all 1,000 topic messages 
                        boolean[] topics = {heater,ac,lights,door_lock} ;

                       int topicNbr; 
                       for (topicNbr = 1; topicNbr <= 4; topicNbr++) { 
                           publisher.send(String.format("%03d", topics[topicNbr]), ZMQ.SNDMORE); 
                           publisher.send("Save Roger"); 
                       } 
                       //  Send one random update per second 
                       while (!Thread.currentThread().isInterrupted()) { 
                           Thread.sleep(1000); 
                           publisher.send( 
                               String.format("%03d", rand.nextInt(1000)), ZMQ.SNDMORE 
                           ); 
                           publisher.send("Off with his head!"); 
                       } 



             
              
              
              
                        
                        
                        
                        
        
	}
    
    }
}