package chatroom;

import java.lang.reflect.Array;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatServer {

    private HashMap<String, String> userInfo;
    private static final String FILENAME = "userInfo.txt";
    private ArrayList<String> clientNames;


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ChatServer <port number>");
            System.exit(1);
        }
        ArrayList clientNames = new ArrayList<>();
        HashMap userInfo = new HashMap();
        BufferedReader br = null;
        FileReader fr = null;

        int portNumber = Integer.parseInt(args[0]);

        try {
            File file = new File(FILENAME);
            if (file.exists()) {
                fr = new FileReader(FILENAME);
            }
            else {
                file.createNewFile();
                fr = new FileReader(file);
            }

            br = new BufferedReader(fr);
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                String splitLine[] = currentLine.split(",");
                userInfo.put(splitLine[0], splitLine[1]);
            }
            fr.close();

            System.out.println(userInfo);

        } catch (IOException e) {
            System.out.println(e);
        }


        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;

            out.println("Server: Please login with UserID Password");
            boolean loggedIn = false;

            while (true) {
                inputLine = in.readLine();
                String tokenizedLine[] = inputLine.split("\\s+");
                String[] strippedLine = Arrays.copyOfRange(tokenizedLine, 1, tokenizedLine.length);
                String returnedLine = String.join(" ", strippedLine);

                //Command is login
                if (tokenizedLine[0].equals("login") && tokenizedLine.length == 3) {
                    System.out.println("Login message received");
                    if (loggedIn) {
                        out.println("Already logged in, idiot.");
                    } else if (userInfo.containsKey(tokenizedLine[1]) && userInfo.get(tokenizedLine[1]).equals(tokenizedLine[2])) {
                        loggedIn = true;
                        clientNames.add(tokenizedLine[1]);
                        out.println("Server: Valid login");
                    } else {
                        loggedIn = false;
                        out.println("Server: Invalid credentials");
                    }
                }
                //Command is send
                else if (tokenizedLine[0].equals("send")) {
                    System.out.println("Send message received");
                    if (!loggedIn) {
                        out.println("Server: Denied. Please login first.");
                    } else {
                        out.println(clientNames.get(0) + ": " + returnedLine);
                    }
                }
                //Command is new user
                else if (tokenizedLine[0].equals("newuser") && tokenizedLine.length == 3) {
                    if (!userInfo.containsKey(tokenizedLine[1])) {
                        userInfo.put(tokenizedLine[1], tokenizedLine[2]);
                        FileWriter fw = new FileWriter(FILENAME, true);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.append(tokenizedLine[1] + "," + tokenizedLine[2]);
                        bw.newLine();
                        bw.close();
                        System.out.println("Created user " + tokenizedLine[1]);
                        out.println("Server: Created user " + tokenizedLine[1]);
                    } else {
                        out.println("Server: Could not create new user.");
                    }
                }
                //Command is logout
                else if(tokenizedLine[0].equals("logout")) {
                    if(loggedIn) {
                        loggedIn = false;
                        out.println("Logged out from " + clientNames.get(0));
                        clientNames.remove(0);
                    } else {
                        out.println("Server: Not Logged in.");
                    }
                }
                //Command not found
                else {
                    out.println("Server: Command not recognized.");
                }
            }


        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }


}
