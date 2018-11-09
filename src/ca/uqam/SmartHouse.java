package ca.uqam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class SmartHouse {

    public static void main(String[] args) throws InterruptedException, IOException {

        try (ZContext context = new ZContext()) {

            Socket subscriber = context.createSocket(SocketType.SUB);
            if (args.length == 1) {
                subscriber.connect(args[0]);
            } else {
                subscriber.connect("tcp://localhost:5555");
            }

            Socket publisher = context.createSocket(SocketType.PUB);
            if (args.length == 1) {
                publisher.connect(args[0]);
            } else {
                publisher.bind("tcp://*:6666");
            }

            Thread.sleep(1000);

            subscriber.subscribe("temperature");
            subscriber.subscribe("activity");
            subscriber.subscribe("time");
            subscriber.subscribe("door_lock_sensor");

            System.out.println(" House simulator running [...]");

            while (true) {
                String topic = subscriber.recvStr();
                if (topic == null) {
                    break;
                }
                String data = subscriber.recvStr();

                
                

                

                //topic temperature
                if ("temperature".equals(topic)) {
                    Float dataFloat = Float.parseFloat(data);
                    System.out.println("write to the file -> " + dataFloat);
                    writeFile("./thermostat.json", dataFloat);
                    
                    float thermostatValue;
                    String thermostatVal = readFile("./thermostat_request.json");
                    thermostatValue = Float.parseFloat(thermostatVal);
                    
                    if (thermostatValue < 19) {
                        System.out.println(topic + " -> " + thermostatValue);
                        publisher.send("heater", ZMQ.SNDMORE);
                        System.out.println("Démarrage du chauffage");
                        publisher.send("on");

                    }
                    if (thermostatValue > 19) {
                        System.out.println(topic + " -> " + thermostatValue);
                        publisher.send("heater", ZMQ.SNDMORE);
                        System.out.println("Arrête du chauffage");
                        publisher.send("off");

                    }

                    if (thermostatValue > 23) {
                        publisher.send("ac", ZMQ.SNDMORE);
                        System.out.println("Démarrer Air climatisé");
                        publisher.send("on");

                    }
                    if (thermostatValue < 23) {
                        publisher.send("ac", ZMQ.SNDMORE);
                        System.out.println("Arrêter Air climatisé");
                        publisher.send("off");

                    }

                }

                //topic activity
                if ("activity".equals(topic)) {
                    
                    System.out.println(topic + " -> " + data);
                    
                    int dataInt = Integer.parseInt(data); 
                    writeFile("./status.json", dataInt);
                    if (dataInt == 1) {
                        publisher.send("lights", ZMQ.SNDMORE);
                        System.out.println("Ouvrir Lumières");
                        publisher.send("on");
                    }
                    if (dataInt == 0) {
                        publisher.send("lights", ZMQ.SNDMORE);
                        System.out.println("Éteindre Lumières");
                        publisher.send("off");
                    }
                }

                //topic time  
                if ("time".equals(topic)) {
                     String doorStatus ;
                    System.out.println(topic + " -> " + data);
                    doorStatus = readFile("./door_request.json");
                    if ("23:00:00".equals(data)||"on".equals(doorStatus) )  {
                        publisher.send("door_lock", ZMQ.SNDMORE);
                        System.out.println("Activer Verrouillage des portes");
                        publisher.send("on");
                    }
                    if ("7:00:00".equals(data)||"off".equals(doorStatus)) {
                        publisher.send("door_lock", ZMQ.SNDMORE);
                        System.out.println("Désactiver Verrouillage des portes");
                        publisher.send("off");
                    }
                }

                //topic door_lock_sensor 
                if ("door_lock_sensor".equals(topic)) {
                    String doorStatus;
                    doorStatus = readFile("./door_request.json");
                   
                }

            }

        }

    }

    private static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));

    }

    private static void writeFile(String fileName, float value) {

        String content = Float.toString(value);

        System.out.println("here");

        try {
            System.out.println("here 2 " + Paths.get(fileName).toAbsolutePath().toString());
            Files.write(Paths.get(fileName), content.getBytes());
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }

}
