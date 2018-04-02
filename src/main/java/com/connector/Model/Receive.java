package com.connector.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Receive extends Thread {
    ObjectInputStream ois;
    ObjectOutputStream oos;

    Call call;

    Socket socket;

    boolean is_running = true;

    public Receive(Socket socket) {
        this.socket = socket;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        TransferedData msg = null;
        try {
            while (is_running) {
                msg = (TransferedData) ois.readObject();
                switch (msg.action) {
                    case "login":
                        this.login(msg.data[0], msg.data[1]);
                        break;
                    case "registration":
                        this.reg(msg.data[0], msg.data[1], msg.data[2], msg.data[3]);
                        break;
                    case "request_call":
                        this.request_call(msg.data[0]);
                        break;
                    case "end_call":
                        System.out.println("Client ended call...........................................");
                        this.end_call();
                        break;
                    case "request_status":
                        this.status();
                        break;
                    case "dialer_connection":
                        System.out.println("Reseived request - dialer_connection");
                        this.dialerConnection();
                        break;
                    case "dialer_waitingForCall":
                        System.out.println("Reseived request - dialer_waitingForCall");
                        this.dialerWaiting();
                        break;
                    case "dialer_endCall":
                        System.out.println("Dialer ended call...........................................");
                        this.end_call();
                        break;
                    default:
                        System.out.println("unknown action");
                }
            }
        } catch (IOException e) {
            System.out.println("user disconnect 1");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                App.calling = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ois.close();
                oos.close();
                System.out.println("user disconnect 2");
            } catch (IOException ex) {
                //   Logger.getLogger(recive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                e.printStackTrace();
                System.out.println("Ничего страшного");
            }
        }
    }


    private void dialerWaiting() {
        while (true) {
            try {
                System.out.println("dialer waits");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //   System.out.println("Dialer awaits call "+App.calling+" - "+App.callingNumber);
            if (App.calling == true && !App.callingNumber.isEmpty()) {
                System.out.println("Calling frm dialerWaiting..........................................");
                try {
                    oos.writeObject(new TransferedData("Call", new String[]{App.callingNumber}));
                    oos.flush();
                    System.out.println("Отправлен номер......................................................................."+App.callingNumber);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void dialerConnection() {
        try {
            App.dialerConnected = true;
            oos.writeObject(new TransferedData("Success"));
            oos.flush();
            System.out.println("Dialer connected...........................");
        } catch (IOException e) {
            App.dialerConnected = false;
            e.printStackTrace();
        }
    }

    private void status() {
        try {
            if (!App.dialerConnected) {
                oos.writeObject(new TransferedData("Ошибка сервера", App.calling));
            } else if(App.calling || Recorder.isBusy){
                oos.writeObject(new TransferedData("Линия занята", App.calling));
            } else {
                oos.writeObject(new TransferedData("Линия свободна", App.calling));
            }
            System.out.println(App.calling);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void end_call() {
        System.out.println("метод end_call");
        App.calling = false;
        App.callingNumber="";
        try {
            oos.writeObject(new TransferedData("Success"));
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void request_call(String phoneNumber) {
        try {
            if (App.calling == true || Recorder.isBusy == true) {
                oos.writeObject(new TransferedData("Fail", new String[]{"Линия занята\nпопробуйте через пару минут"}));
                oos.flush();
                return;
            }
            if (App.dialerConnected == false) {
                oos.writeObject(new TransferedData("Fail", new String[]{"Ошибка сервера\nпопробуйте позже"}));
                oos.flush();
                return;
            }

            oos.writeObject(new TransferedData("Success"));
            oos.flush();
            App.calling = true;
            App.callingNumber = "071"+phoneNumber;
         //   App.callingNumber = "411";
            System.out.println("client ip - " + socket.getInetAddress().toString().substring(1));
            call = new Call();
            call.init_audio();
            call.init_player();
            try {
                call.init_recorder(InetAddress.getByName(socket.getInetAddress().toString().substring(1)));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reg(String phoneNumber, String name, String referal, String password) {
        String queryCheck = "SELECT phoneNumber FROM connector_users WHERE phoneNumber = ?";
        String queryInsert = "INSERT INTO connector_users (phoneNumber, name, referal, password) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement1 = App.connection.prepareStatement(queryCheck);
            preparedStatement1.setString(1, phoneNumber);
            ResultSet rs = preparedStatement1.executeQuery();
            rs.last();
            if (rs.getRow() != 0) {
                // user_name exist;
                oos.writeObject(new TransferedData("Fail", new String[]{"Номер уже используется"}));
                oos.flush();
                return;
            }
            PreparedStatement preparedStatement2 = App.connection.prepareStatement(queryInsert);
            preparedStatement2.setString(1, phoneNumber);
            preparedStatement2.setString(2, name);
            preparedStatement2.setString(3, referal);
            preparedStatement2.setString(4, password);
            preparedStatement2.executeUpdate();
            oos.writeObject(new TransferedData("Success"));
            oos.flush();

        } catch (SQLException e) {
            System.out.println("SQL Error");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(String phoneNumber, String password) {
        String queryCheck = "SELECT phoneNumber, password, approved FROM connector_users WHERE phoneNumber = ?";
        try {
            PreparedStatement preparedStatement1 = App.connection.prepareStatement(queryCheck);
            preparedStatement1.setString(1, phoneNumber);
            ResultSet rs = preparedStatement1.executeQuery();
            rs.last();
            if (rs.getRow() == 0) {
                // phone number doesn't exist;
                oos.writeObject(new TransferedData("Fail", new String[]{"Номер не зарегистрирован"}));
                oos.flush();
                return;
            }
            if (!rs.getString("password").equals(password)) {
                // phone number doesn't exist;
                oos.writeObject(new TransferedData("Fail", new String[]{"Пароль не верный"}));
                oos.flush();
                return;
            }
            if (rs.getInt("approved") == 0) {
                oos.writeObject(new TransferedData("Fail", new String[]{"Регистрация не подтверждена\nпопробуйте позже"}));
                oos.flush();
                return;
            }
            oos.writeObject(new TransferedData("Success"));
            oos.flush();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
