/*
Matthew Deardorff
12/4/17
This Chat Client is based off the Java Doc's implementation of a KnockKnockClient
https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/networking/sockets/examples/KnockKnockClient.java
It's very simple, sets up the socket info, and then enters a loop of asking for input from the server and then asking for input from the user
 */
package chatroom;
import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) throws IOException {
        //check correct usage
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                //attempt to set up socket with parameters
                Socket kkSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(kkSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                    new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            //this is the main loop of asking for server information, then asking for user information and sending it
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);

                fromUser = stdIn.readLine();
                if(fromUser.equals("exit")) {
                    System.exit(1);
                }
                if (fromUser != null) {
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }
}