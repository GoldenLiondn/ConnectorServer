package com.connector.Model;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author duong
 */
public class Player extends Thread {
    public DatagramSocket din;
    public SourceDataLine audio_out;
    byte[] buffer = new byte[2200];

    @Override
    public void run() {
        try {

            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

            System.out.println("Start Playing");
            Long pack = 0l;
            while (App.calling) {
                din.receive(incoming);
                buffer = incoming.getData();
                audio_out.write(buffer, 0, buffer.length);
                System.out.println("receive from: #" + (pack++) + " ip " + incoming.getSocketAddress() + " port"
                        + incoming.getPort());
            }
            audio_out.drain();
            audio_out.close();
            System.out.println("Player stopped");
        } catch (SocketException ex) {
            System.out.println("Socket closed in player");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
