import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.security.Key;

public class CLIENT extends Component implements ActionListener, KeyListener {
    String IP;
    public String msg;
    public JFrame jf;
    public JFrame connect;
    public Socket scoket;
    public JTextField in_ip;
    public JTextArea show;
    public JTextField input;


    public static void main(String[] args) throws IOException {
        //  用户端与服务器端连接
        CLIENT aa = new CLIENT();

    }

    void ShowMessage(String str) {
        show.append("对方说：" + str + '\n');
    }

    void DengLuUI() {
        connect.setVisible(false);
    }

    void ChatUI() {
        jf.setVisible(true);

    }

    public CLIENT() throws IOException {
        addKeyListener(this);
        connect = new JFrame("客户端");
        connect.setLocation(400, 300);
        connect.setSize(400, 250);
        JPanel panel = new JPanel();
        JLabel label2 = new JLabel();

        panel.add(label2);
        panel.setLayout(new GridLayout(6, 1, 2, 2));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        connect.add(panel);
        JLabel label1 = new JLabel("IP:");
        panel.add(label1);
        label1.setLocation(10, 10);
        in_ip = new JTextField(10);

        panel.add(in_ip);

        JLabel label3 = new JLabel();
        panel.add(label3);

        JButton conBtn = new JButton("连接");
        conBtn.setActionCommand("connect");
        JPanel pbt = new JPanel();
        pbt.add(conBtn);
        panel.add(pbt);
        conBtn.addActionListener(this);
        connect.setVisible(true);

        connect.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void UI() throws IOException {

        jf = new JFrame("客户端");
        jf.setLocation(400, 0);
        jf.setSize(800, 600);

        final JPanel panel = new JPanel();
        panel.setBackground(Color.lightGray);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        jf.add(panel);

        //顶部按钮
        final JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        panel.add(jp, BorderLayout.NORTH);
        final JButton tool = new JButton("绘图");
        tool.setActionCommand("tool");
        tool.addActionListener(this);
        jp.add(tool);
//        final JButton info = new JButton("关于我");
//        info.setActionCommand("info");
//        info.addActionListener(this);
//        jp.add(info);

        //主体
        final JPanel body = new JPanel();
        body.setLayout(null);
        body.setSize(500, 400);
        panel.add(body, BorderLayout.CENTER);

        show = new JTextArea("");
        show.setLineWrap(true);
        show.setWrapStyleWord(true);
        show.setEnabled(false);
        show.setFont(new java.awt.Font("微软雅黑", 0, 14));
        show.setBounds(10, 10, 745, 300);
        body.add(show);

        input = new JTextField("");
        input.setBounds(10, 320, 745, 100);
        body.add(input);
        input.addKeyListener(this);

        final JButton clear = new JButton("清除");
        clear.setActionCommand("clear");
        clear.addActionListener(this);
        clear.setBounds(510, 440, 60, 30);
        body.add(clear);

        final JButton file = new JButton("文件");
        file.setActionCommand("file");
        file.addActionListener(this);
        file.setBounds(600, 440, 60, 30);
        body.add(file);

        final JButton summit = new JButton("发送");
        summit.setActionCommand("summit");
        summit.addActionListener(this);
        summit.setBounds(690, 440, 60, 30);
        body.add(summit);
        IP = in_ip.getText();
        scoket = null;
        try {
            scoket = new Socket(IP, 5555);
            jf.setVisible(true);
            connect.setVisible(false);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ClientReceiveThread thread = new ClientReceiveThread(scoket, show);
        thread.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("tool")) {
            Tool tool = new Tool();
            tool.tool();
        } else if (e.getActionCommand().equals("summit")) {
            msg = input.getText();
            if (msg.length() != 0) {
                ClientSendThread SendThread = new ClientSendThread(scoket, msg);
                SendThread.start();
                input.setText("");
                show.append("我：" + msg + '\n');

            } else {
                JOptionPane.showMessageDialog(this, "您发送的消息为空", "提示信息", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("connect")) {
            try {
                UI();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getActionCommand().equals("file")) {
            System.out.println("send flie");
            FileThread fileThread = new FileThread(scoket);
            fileThread.start();
        } else if (e.getActionCommand().equals("clear")) {
            show.setText("");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            msg = input.getText();
            if (msg.length() != 0) {
                ClientSendThread SendThread = new ClientSendThread(scoket, msg);
                SendThread.start();
                input.setText("");
                show.append("我：" + msg + '\n');

            } else {
                JOptionPane.showMessageDialog(this, "您发送的消息为空", "提示信息", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

class Tool extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
    private JButton current;//当前颜色
    private Color color;//要设置的颜色
    private Graphics G;//Draw类对象;
    private JPanel jp;
    private Graphics g;
    int x1, x2, y1, y2;
    int x, y;

    void tool() {
        tool_UI();
    }

    void tool_UI() {

        setTitle("画图");
        this.setLocation(20, 0);
        this.setSize(380, 600);
        this.setLayout(new FlowLayout());
        this.setResizable(false);
        setDefaultCloseOperation(1);

        jp = new JPanel();
        jp.setPreferredSize(new Dimension(360, 480));

        Color[] color = {Color.black, Color.red, Color.BLUE, Color.CYAN, Color.darkGray, Color.MAGENTA, Color.orange, Color.yellow, Color.PINK};
        JPanel jpC = new JPanel(new GridLayout(1, color.length, 3, 3));
        for (int i = 0; i < color.length; i++) {
            JButton but = new JButton();
            but.setBackground(color[i]);
            but.setPreferredSize(new Dimension(30, 20));
            but.addActionListener(this);
            jpC.add(but);
        }
        this.add(jp);
        this.add(jpC);
        JButton cur = new JButton();
        cur.setPreferredSize(new Dimension(40, 40));
        cur.setBackground(Color.black);
        this.add(cur);
        this.setVisible(true);

        JButton jbt = new JButton("清除");
        jbt.addActionListener(this);
        jbt.setActionCommand("clear");
        this.add(jbt);
        g = jp.getGraphics();
        this.setG(g);
        this.setCurColor(cur);
        jp.addMouseListener(this);
        jp.addMouseMotionListener(this);
    }

    public void setCurColor(JButton cur) {
        this.current = cur;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("clear")) {
            g.clearRect(0, 0, 360, 480);

        } else {
            JButton jb = (JButton) e.getSource();
            color = jb.getBackground();
            current.setBackground(color);
        }

    }

    public void setG(Graphics g) {
        this.G = g;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        G.setColor(color);


    }

    @Override
    public void mouseReleased(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        G.drawLine(x1, y1, x, y);
        x1 = x;
        y1 = y;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
