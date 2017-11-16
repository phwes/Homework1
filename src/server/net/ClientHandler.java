/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.controller.Controller;

/**
 *
 * @author philip
 */

//  Handles one client
// TODO: Create one model.
// Every time a operation is done (change a word, make a guess. Make a controller that takes the model 
class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedWriter toCli;
    private BufferedReader fromCli;
    private Controller controller;
    public boolean inGame = false;
    
    public ClientHandler(Socket clientSocket) {
        controller = new Controller();
        this.clientSocket = clientSocket;
        try {
            this.fromCli = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.toCli = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        //  Hur ska jag göra userface responsive? Ska jag göra en ny thread av controller för varje request? Låter slösaktigt Kolla på hans video bara
        
    }
    // TODO: Se till att clientHandler inte behöver hålla reda på "inGame". Bara controller och model håller reda på spelet.

    @Override
    public void run() {
        
        try {
            boolean connected = true;
            //boolean inGame = false;
            String request;
            
            while(connected){
                
                
                
                if(!inGame){
                    sendResponse("To start a game, type: Start Game");
                    request = readRequest();
                    if(request.toLowerCase().equals("start game")){
                        controller.changeWord();
                        sendResponse("Game started");
                        inGame = true;
                        continue;
                }
                    else{
                        sendResponse("");
                    }
                }
                
                request = readRequest();
                if(request.toLowerCase().equals("bye")){
                    disconnect();
                    connected = false;
                }else if (request.toLowerCase().startsWith("guess ")){
                    String response = controller.guess(request.toUpperCase());
                    sendResponse(response);
                }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //  Reads input from client
    private String readRequest() throws IOException{
        return fromCli.readLine();
    }
    // Sends feedback to client
    private void sendResponse(String response) throws IOException{
        toCli.write(response);
        toCli.newLine();
        toCli.flush();
    }
    
    private void disconnect() throws IOException{
        clientSocket.close();
    }
    
}
