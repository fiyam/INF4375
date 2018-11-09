
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import jdk.nashorn.internal.parser.JSONParser;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import sun.misc.IOUtils;

public class Demo {

    private static final String THERMOSTAT_URI = "/thermostat";
    private static final String TEMPERATURE_URI = "/temperature";
    private static final String DOOR_LOCK_URI = "/door_lock";
    private static final String PRESENCE_URI = "/presence";

    public static void main(String[] args) throws Exception {
        try (ZContext context = new ZContext()) {

            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
            if (args.length == 1) {
                subscriber.connect(args[0]);
            } else {
                subscriber.connect("tcp://localhost:5555");
            }

            ZMQ.Socket publisher = context.createSocket(SocketType.PUB);
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

            /**
             * On utilise le port 8000 en localhost. 0 par dÃ©faut pour le
             * socket backlog, nul besoin de le changer dans le cadre du TP.
             */
            HttpServer server = HttpServer.create(new InetSocketAddress(4444), 0);
            /**
             * On dÃ©finit un endpoint "/test" ainsi qu'un gestionnaire qui va
             * effectuer le traitement associÃ©.
             */
            server.createContext("/", new MyHandler());
            /**
             * On utilise un executeur qui prend en charge un ensemble de
             * threads fixe, pour le besoin 15 sera suffisant, si votre machine
             * possÃ¨de moins de ressource, vous pouvez descendre jusqu'Ã  5.
             */
            server.setExecutor(Executors.newFixedThreadPool(15));
            /**
             * On dÃ©marre le serveur.
             */

            System.out.println("will start the server");
            server.start();
        } catch (Exception ex) {
            /**
             * Traitement d'erreur...
             */
        }
    }

    /**
     * Gestionnaire de endpoint.
     */
    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            /**
             * RÃ©cupÃ¨re le type de mÃ©thode de la requÃªte : GET, PUT, ...
             */

            String requestUri = he.getRequestURI().getPath();

            String requestMethod = he.getRequestMethod();

            System.out.println("uri : " + requestUri);
            System.out.println("method : " + requestMethod);

            /**
             * Traitement dÃ©pendant de la mÃ©thode de la requÃªte, agit comme
             * un controleur.
             */
            if (requestUri.equalsIgnoreCase(THERMOSTAT_URI) && requestMethod.equalsIgnoreCase("GET")) {
                System.out.println("will call handleGetThermostat ...");
                handleGetThermostat(he);
            } else if (requestUri.equalsIgnoreCase(THERMOSTAT_URI) && requestMethod.equalsIgnoreCase("PUT")) {
                System.out.println("will call handlePutThermostat ...");
                handlePutThermostat(he);
            } else if (requestUri.equalsIgnoreCase(TEMPERATURE_URI) && requestMethod.equalsIgnoreCase("GET")) {
                System.out.println("will call handleGetTemperature ...");
                handleGetTemperature(he);
            } else if (requestUri.equalsIgnoreCase(DOOR_LOCK_URI) && requestMethod.equalsIgnoreCase("GET")) {
                System.out.println("will call handleGetDoorLock ...");
                handleGetDoorLock(he);
            } else if (requestUri.equalsIgnoreCase(DOOR_LOCK_URI) && requestMethod.equalsIgnoreCase("PUT")) {
                System.out.println("will call handlePutDoorLock ...");
                handlePutDoorLock(he);
            } else if (requestUri.equalsIgnoreCase(PRESENCE_URI) && requestMethod.equalsIgnoreCase("GET")) {
                System.out.println("will call handleGetPresence ...");
                handleGetPresence(he);
            }

        }
    }

    private static void handleGetThermostat(HttpExchange he) throws IOException {

        String response;
        try {
            response = readFile("./thermostat.json");
        } catch (IOException ex) {
            he.sendResponseHeaders(400, 0);
            return;
        }

        addResponseHeader(he);
        he.sendResponseHeaders(200, response.length());
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }

    }

    private static void handlePutThermostat(HttpExchange he) throws IOException {

        String response = "";
        String payload = readPayload(he);
        System.out.println("inside handlePutThermostat payload = " + payload);

        writeFile("./thermostat_request.json", payload);

        addResponseHeader(he);
        he.sendResponseHeaders(200, response.length());
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

        private static void handleGetTemperature(HttpExchange he) throws IOException {

        String response;
        try {
            response = readFile("./thermostat_request.json");
        } catch (IOException ex) {
            he.sendResponseHeaders(400, 0);
            return;
        }

        addResponseHeader(he);
        he.sendResponseHeaders(200, response.length());
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }

    }
        
    private static void handleGetDoorLock(HttpExchange he) throws IOException {

        System.out.println("inside handlePutThermostat 1");
        
        String response;
        try {
            response = readFile("./door_request.json");
        } catch (IOException ex) {
            System.out.println("inside handlePutThermostat excep ");
            he.sendResponseHeaders(400, 0);
            return;
        }

        System.out.println("inside handlePutThermostat 2 " + response);
        
        //addResponseHeader(he);
        he.sendResponseHeaders(200, response.length());
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static void handlePutDoorLock(HttpExchange he) throws IOException {

        String response = "";
        
        System.out.println("inside hereeeeeeeeeeee= ");
        
        String payload = readPayload(he);
        System.out.println("inside handlePutThermostat payload = " + payload);

        writeFile("./door_request.json", payload);

        //addResponseHeader(he);
        he.sendResponseHeaders(200, response.length());
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static void handleGetPresence(HttpExchange he) throws IOException {

        String response;
        try {
            response = readFile("./status.json");
        } catch (IOException ex) {
            he.sendResponseHeaders(400, 0);
            return;
        }

        addResponseHeader(he);
        he.sendResponseHeaders(200, response.length());
        try (OutputStream os = he.getResponseBody()) {
            os.write(response.getBytes());
        }

    }

    private static String readPayload(HttpExchange he) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(he.getRequestBody());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }



    private static void addResponseHeader(HttpExchange httpExchange) {
        List<String> contentTypeValue = new ArrayList<>();
        contentTypeValue.add("application/json");
        httpExchange.getResponseHeaders().put("Content-Type", contentTypeValue);
    }

    private static String readFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));

    }

    private static void writeFile(String fileName, String content) {

        try {
            Files.write(Paths.get(fileName), content.getBytes());
        } catch (IOException ex) {
            System.out.println("Error: " + ex);
        }
    }

    /**
     * Exemple de thread.
     *
     * Ci-dessous, un gestionnaire de Thread.
     */
    static class ThreadManager {

        /**
         * Ici, un seul thread, mais pour le TP, vous aurez besoin de 2 Threads,
         * un pour le subscriber (ci-dessous) et un autre pour le serveur.
         */
        Thread subThread;

        /**
         * Constructeur initialisant la routine que le processus va utiliser,
         *
         * @param subRunnable Routine pour le processus Subscriber
         */
        public ThreadManager(Runnable subRunnable) {
            this.subThread = new Thread(subRunnable);
        }
    }

    /**
     * Ci-dessous un exemple de routine pour le subscriber, le plus important
     * est que dans la mÃ©thode run, vous lanciez le processus d'execution
     * d'Ã©coute de message ou d'attente de reception de topic.
     *
     * Dans ce cas-ci c'est pour le Subscriber, vous aurez Ã©galement une autre
     * routine qui va Ãªter dÃ©dier au processus serveur, qui quand Ã  lui se
     * charger va dÃ©marrer le serveur. (la mÃ©thode start citÃ© plus haut).
     */
//     static class SubscriberRunnable implements Runnable {
//        private AppSubscriber subscriber;
//
//        public SubscriberRunnable(AppSubscriber subscriber) {
//            this.subscriber = subscriber;
//        }
//
//        @Override
//        public void run() {
//            this.subscriber.subscribe();
//        }
//
//    }
}
