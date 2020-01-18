import java.io.*;
import java.net.Socket;

public class ClientSendThread extends Thread {
    private String str;
    private Socket socket;
    public ClientSendThread(Socket socket,String str){
        this.socket=socket;
        this.str=str;
    }
    public void run(){
        try {
            OutputStream os;
            os = socket.getOutputStream();
            PrintWriter   pw=new PrintWriter(os,true);
                pw.println(str);
                pw.flush();
                System.out.println("发送："+str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
