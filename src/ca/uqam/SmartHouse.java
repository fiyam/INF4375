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
		
                 
                        
		try (ZContext context = new ZContext()) {
			


                        Socket subscriber = context.createSocket(SocketType.SUB); 
                        if (args.length == 1) 
                            subscriber.connect(args[0]); 
                        else subscriber.connect("tcp://localhost:5555"); 
                        
                          Socket publisher = context.createSocket(SocketType.PUB); 
                       if (args.length == 1) 
                           publisher.connect(args[0]); 
                       else publisher.bind("tcp://*:6666"); 
                       
                       
                       Thread.sleep(1000); 

                        subscriber.subscribe("temperature"); 
                        subscriber.subscribe("activity"); 
                        subscriber.subscribe("time"); 
                        subscriber.subscribe("door_lock_sensor"); 


                        while (true) { 
                            String topic = subscriber.recvStr(); 
                           if (topic == null) 
                                break; 
                            String data = subscriber.recvStr(); 
                            
                             if( topic == "temperature")  {
                             int dataInt = Integer.parseInt(data);
                                 if( dataInt  <  19){
                                 publisher.send("heater", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                        
                                    }
                                  if( dataInt  > 23 ){
                                 publisher.send("ac", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                                    }
                             }
                            
                             
                                if( topic == "activity")  {
                                    int dataInt = Integer.parseInt(data);
                                 if( dataInt  == 1 ){
                                 publisher.send("lights", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                        
                                    }
                                  if( dataInt  == 0 ){
                                 publisher.send("lights", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                                    }
                             }
                            
                                if( topic == "time")  {
                                 if( data   == "on" ){
                                 publisher.send("heater", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                        
                                    }
                                  if( dataInt  > 23 ){
                                 publisher.send("ac", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                                    }
                             }
                            
                                if( topic == "door_lock_sensor")  {
                             int dataInt = Integer.parseInt(data);
                                 if( dataInt  <  19){
                                 publisher.send("heater", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                        
                                    }
                                  if( dataInt  > 23 ){
                                 publisher.send("ac", ZMQ.SNDMORE); 
                                  System.out.println(topic + " -> " + data); 
                                    }
                             }
                            
                             
                             
                            System.out.println(topic + " -> " + data); 
                            

                            publisher.send("lights", ZMQ.SNDMORE); 
                            publisher.send("door_lock", ZMQ.SNDMORE); 
                            
                            
                           
                            
                            
                            publisher.send("on"); 
                       }

                   
        
	}
    
    }
}