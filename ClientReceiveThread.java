import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ClientReceiveThread extends Thread {
    private Socket socket;
    public String str;
    private JTextArea jta;

    public ClientReceiveThread(Socket socket, JTextArea jta)  {
        this.socket = socket;
        this.jta = jta;
    }

    public void run() {
        try {
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            while (true) {
                str = br.readLine();
                int index = str.indexOf("/#");
                String xieyi;
                System.out.println("index=" + index);

                //“/#”不存在时是-1
                if (index > 0) {
                    xieyi = str.substring(0, index);
                    //判断是否是文件协议111
                    if (xieyi.equals("111")) {
                        str = str.substring(index + 2);
                        index = str.indexOf("/#");
                        //获取文件名及大小
                        String filename = str.substring(0, index).trim();
                        String filesize = str.substring(index + 2).trim();


                        //建立一个空文件，准备接受传递的文件
                        File file = new File(filename);
                        if (!file.exists()) {
                            file.createNewFile();
                        } else {
                            System.out.println("存在相同文件");
                        }
                        byte[] buffer = new byte[4096 * 5];
                        FileOutputStream fos = new FileOutputStream(file);
                        long file_size = Long.parseLong(filesize);
                        is = socket.getInputStream();
                        int size = 0;//数据包大小
                        long count = 0;//文件大小

                        //开始接受文件
                        while (count < file_size) {
                            size = is.read(buffer);

                            //将数据包写入新的文件
                            fos.write(buffer, 0, size);
                            fos.flush();
                            count += size;
                           //Debug: System.out.println("服务器端接收到数据包，大小为" + size);
                        }

                        //反馈
                        DEMO thread = new DEMO("对方给您发送了：" + filename+" 大小为："+(count/1026)+"MB", jta);
                        thread.start();

                        //打开刚接收的文件
                        // ？？怎么打开文件所在文件夹呢
                        java.awt.Desktop.getDesktop().open(file);
                        fos.close();
                    }
                } else {
                    //正常的文字消息
                    System.out.println("收到" + str);
                    DEMO thread = new DEMO(str, jta);
                    thread.start();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}