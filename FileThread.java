import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.Socket;

public class FileThread extends Thread {
    private Socket socket;
    private String filename;
    private OutputStream os;
    private File sendfile;
    private FileInputStream fis;

    public FileThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        //缓存数据
        //选择文件,获取文件对象
        byte[] buffer = new byte[4096 * 5];
        ChooseFile chooseFile = new ChooseFile();
        chooseFile.choose();
        sendfile = new File(chooseFile.FilePath);

        //发送文件准备信息
        try {
            //提取出文件对象
            fis = new FileInputStream(sendfile);

            os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            filename = sendfile.getName();
            System.out.println(filename);
            //发送内容：文件协议码+文件名+文件大小
            ps.println("111/#" + filename + "/#" + fis.available());
            ps.flush();
        } catch (IOException e) {
            System.out.println("文件准备信息发送出错");
            e.printStackTrace();
        }

        try {
            FileThread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int size = 0;
        //发送文件，循环发送数据包
        try {
            while (((size = fis.read(buffer)) != -1)) {
                System.out.println("发送数据包，大小为：" + size);
                os.write(buffer, 0, size);
            }

        } catch (IOException e) {
            System.out.println("文件输出出错");
            e.printStackTrace();
        } finally {
            //关闭文件
            try {
                fis.close();
            } catch (IOException e) {
                System.out.println("文件关闭出错");
                e.printStackTrace();
            }
        }

    }
}

class ChooseFile {
    public String FileName;
    public String FilePath;
    void choose() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG&GIF Images", "jpg", "gif");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(chooser);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FileName = chooser.getSelectedFile().getName();
            FilePath = chooser.getSelectedFile().getPath();
        }
    }

}
