package ca.uqam;


import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class SmartHouse {
	
	public static void main(String [] args) throws InterruptedException {
		

                 
                        
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
                            

                            //topic temperature
                             if( "temperature".equals(topic))  {
                                 Float dataFloat = Float.parseFloat(data);
                                                  
                                 if( dataFloat  <  19){
                                     System.out.println(topic + " -> " + data);
                                     publisher.send("heater", ZMQ.SNDMORE);
                                     System.out.println("Démarrage du chauffage");
                                     publisher.send("on");

                                    }
                                 if( dataFloat  > 19){
                                     System.out.println(topic + " -> " + data);
                                     publisher.send("heater", ZMQ.SNDMORE);
                                     System.out.println("Arrête du chauffage");
                                     publisher.send("off"); 
                        
                                    }
                                 
                                  if( dataFloat  > 23 ){
                                 publisher.send("ac", ZMQ.SNDMORE); 
                                 System.out.println("Démarrer Air climatisé");
                                 publisher.send("on");
                                 
                                    }
                                  if( dataFloat  < 23 ){
                                 publisher.send("ac", ZMQ.SNDMORE); 
                                 System.out.println("Arrêter Air climatisé");
                                 publisher.send("off");
                                 
                                    }
                                  
                             }
                            
                             
                             
                             
                             //topic activity
                                if( "activity".equals(topic))  {
                                    int dataInt = Integer.parseInt(data);
                                    System.out.println(topic + " -> " + data);
                              
                                 if( dataInt  == 1 ){
                                 publisher.send("lights", ZMQ.SNDMORE); 
                                 System.out.println("Ouvrir Lumières");
                                 publisher.send("on");
                                    }
                                  if( dataInt  == 0 ){
                                 publisher.send("lights", ZMQ.SNDMORE); 
                                 System.out.println("Éteindre Lumières");
                                 publisher.send("off");
                                    }
                             }
                            
                                
                                
                                
                              //topic time  
                                if( "time".equals(topic))  {
                                    System.out.println(topic + " -> " + data);
                                 if( "23:00:00".equals(data) ){
                                  publisher.send("door_lock", ZMQ.SNDMORE);
                                 System.out.println("Activer Verrouillage des portes");
                                  publisher.send("on");
                                    }
                                  if( "7:00:00".equals(data) ){
                                  publisher.send("door_lock", ZMQ.SNDMORE);
                                 System.out.println("Désactiver Verrouillage des portes");
                                  publisher.send("off");
                                    }
                             }
                                
                                
                               
                            //topic door_lock_sensor 
                                if( "door_lock_sensor".equals(topic))  {
                                    
                                  
                             }
                            
                                 
                       }
                                       
                   
        
	}
    
    }
}