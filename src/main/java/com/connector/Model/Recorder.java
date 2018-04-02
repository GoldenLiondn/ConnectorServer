package com.connector.Model;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * @author duong
 */
public class Recorder extends Thread {
    public TargetDataLine audio_in = null;
    public DatagramSocket dout;
    byte buff[] = new byte[2200];
    public InetAddress fr_ip;
    public int fr_port;
    public static boolean isBusy;

    @Override
    public void run() {
        try {
            System.out.println("Start Recording");
            isBusy =true;
            Long pack = 0L;
            while (App.calling) {
                audio_in.read(buff, 0, buff.length);
                DatagramPacket data = new DatagramPacket(buff, buff.length, fr_ip, fr_port);
                System.out.println("send: #" + (pack++) + "ip " + fr_ip + " port" + fr_port);
                dout.send(data);

            }

            audio_in.drain();
            audio_in.close();
            dout.close();
            System.out.println("Socket closed");
            System.out.println("Recorder stopped");
            isBusy = false;

        } catch (SocketException e) {
            System.out.println("socket closed in rec");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

