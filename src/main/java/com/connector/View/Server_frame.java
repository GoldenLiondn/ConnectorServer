package com.connector.View;


import com.connector.Model.Listen_connect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Maks Ovcharenko
 */
public class Server_frame extends JFrame {

    private JScrollPane jScrollPane1;
    public static JButton btn_start;
    public static JTextArea txt_log;

    private static Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

    public Server_frame() {
        super("Server");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();

        this.setSize((int) (dim.getWidth() / 2), (int) (dim.getHeight() / 2));
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setResizable(false);
        setVisible(true);
    }

    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        txt_log = new JTextArea();
        btn_start = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txt_log.setColumns(20);
        txt_log.setRows(5);
        jScrollPane1.setViewportView(txt_log);

        btn_start.setText("Start");

        getContentPane().setLayout(new BorderLayout());
        add(txt_log, "Center");
        add(btn_start, "South");

        btn_start.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_btnActionPerformed(evt);
            }
        });
    }

    private void start_btnActionPerformed(ActionEvent evt) {
        new Listen_connect().start();

    }


}
