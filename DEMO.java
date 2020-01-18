import javax.swing.*;

public class DEMO extends Thread {
    private  String str;
    private  JTextArea jta;
    public DEMO(String str, JTextArea jta){
        this.str=str;
        this.jta=jta;
    }
   public void  run(){
        jta.append("对方："+str+'\n');
    }
}
