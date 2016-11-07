package TCPServerTest.ChatRoom;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

/**
 * Created by geyao on 16/9/8.
 * 客户端程序，实现将用户在文本框中输入的信息发送至服务器端
 * 并将文本框中输入的信息显示在客户端的文本域中
 */
public class MyClien extends JFrame {
    private PrintWriter writer;              //写管道
    private BufferedReader reader;           //读管道
    private Socket socket;                           //套接字
    private JTextArea ta = new JTextArea();  //聊天记录
    private JTextField tf = new JTextField();//输入框
    private Container cc;                            //Swing容器

    public MyClien(String title){ // Construct
        super(title);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cc = this.getContentPane();
        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new BevelBorder(BevelBorder.RAISED));
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(ta);
        cc.add(tf, "South"); // 将输入框放置在窗体的下部
        ta.setEnabled(false);
        tf.addActionListener(new ActionListener() {
            // 绑定事件
            @Override
            public void actionPerformed(ActionEvent e) {//输入框的事件函数
                // 将文本框中的信息写入流
                writer.println(tf.getText());
                tf.setText(""); // 输入框置空
            }
        });
    }
    private void connect(){ // this methed connect the Socket
        ta.append("尝试连接\n");
        try{
            socket = new Socket("127.0.0.1", 27999);
            writer = new PrintWriter(socket.getOutputStream(), true);
	        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            ta.append("完成连接\n");
            /**接收第一条消息，是服务器发送的，告知客户端是第多少台连接的机器*/
            String Id = reader.readLine();
            this.setTitle(Id + "号机器");
        }catch (Exception e){
            e.printStackTrace();
        }
        while (true){
	        try {
                System.out.print(1);
		        String message = reader.readLine();// 从客户端读消息
                System.out.println(message + "1");
                if (!message.equals(""))
			        ta.append(message + "\n");     //把消息写到聊天记录里
	        } catch (IOException e) {
		        e.printStackTrace();
	        }

        }
    }
    public static void main(String[] args){
        MyClien clien = new MyClien("向服务器传送数据");
        clien.setSize(200, 200);
        clien.setVisible(true);
        clien.connect();
    }
}
