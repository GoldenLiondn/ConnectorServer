package com.connector.Model;

import javax.sound.sampled.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Call {

    Player p;
    Recorder r;

    DatagramSocket datagramSocket;


    SourceDataLine audio_out;
    TargetDataLine audio_in;

    public int my_port = 50002;
    public int portToSend = 50003;
    public String my_ip;


    public void init_player() {
        try {
            p = new Player();
            p.din = datagramSocket;
            p.audio_out = audio_out;
            my_ip = InetAddress.getLocalHost().getHostAddress();
            p.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void init_recorder(InetAddress ia) {

            r = new Recorder();
            r.dout = datagramSocket;
            r.audio_in = audio_in;
            r.fr_ip = ia;
            r.fr_port = portToSend;
            r.start();

    }

    public void init_audio() {
        try {
            datagramSocket = new DatagramSocket(my_port);
            AudioFormat format = getAudioFormat();
            DataLine.Info info_in = new DataLine.Info(TargetDataLine.class, format);
            DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info_in)) {
                System.out.println("Line for in not supported");
                System.exit(0);
            }
            if (!AudioSystem.isLineSupported(info_out)) {
                System.out.println("Line for out not supported");
                System.exit(0);
            }
            audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
            audio_out.open(format);
            audio_out.start();

            audio_in = (TargetDataLine) AudioSystem.getLine(info_in);
            audio_in.open(format);
            audio_in.start();
        } catch (LineUnavailableException ex) {
          ex.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public static AudioFormat getAudioFormat() {

        float sampleRate = 44100.0F;

        int sampleSizeInBits = 16;

        int channels = 1;

        boolean signed = true;

        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }


}
