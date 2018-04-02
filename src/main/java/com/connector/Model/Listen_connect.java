package com.connector.Model;

import com.connector.View.Server_frame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listen_connect extends Thread{

    public ServerSocket ss = null;
    public Socket socket = null;
    public int port = 1010;
    @Override
    public void run() {
        try {
            ss = new ServerSocket(port);
            Server_frame.txt_log.append("server running at port " + port + " \n");
            Server_frame.btn_start.setEnabled(false);
            while (true) {
                socket = ss.accept();
                if (socket != null && socket.getRemoteSocketAddress() != null) {
                    Server_frame.txt_log.append("Established a new connection to a remote socket address: " + socket.getRemoteSocketAddress()+"\n");
                }
                Receive r = new Receive(socket);
                r.start();
            }

        } catch (IOException ex) {
            Server_frame.txt_log.append("server error \n");
            return;
        }
    }

}
