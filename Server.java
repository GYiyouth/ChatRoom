package TCPServerTest.ChatRoom;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by geyao on 16/9/10.
 * 聊天室 服务器端代码
 * 从客户端获取信息，再发给所有客户端
 */
public class Server {
	private HashMap<Integer, BufferedReader> readers =new HashMap<>();               //缓冲输入流
//	private ArrayList<PrintWriter> writers = new ArrayList<>();              //缓冲输出流
	private ServerSocket serverSocket;
	private HashMap<Integer, Socket> clientSockets = new HashMap<>();                //套接字组
	private HashMap<Integer, Thread> threads = new HashMap<>();                      //线程
	private int totalClients = 0;
	private ArrayList<Integer> onlineClients = new ArrayList<>();

	public Server(){ // 构造函数
		try {
			serverSocket = new ServerSocket(27999);
			System.out.println("聊天室已经创建成功");
			threads.put(totalClients, new Thread(new Task()));
			threads.get(totalClients).start();

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		new Server();
	}

	//每个线程的任务
	class Task implements Runnable{
		@Override
		public void run(){

			System.out.println("房间正在等待第" + (clientSockets.size() + 1) + "位客户连接");
			try {
				Socket socket = serverSocket.accept();
				clientSockets.put(totalClients, socket);                                      //添加到数组
				System.out.println(socket.getInetAddress());
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//				PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
				readers.put(totalClients, reader);                                            //添加到数组
				onlineClients.add(totalClients);

				PrintWriter writer = new PrintWriter(
				//发送第一条消息，告知用户是今天总共第几号
				socket.getOutputStream(), true);
				writer.println((totalClients + 1));     /**这里必须是println，不然不会发送*/
				writer.flush();

				totalClients++;

				sendMessage(totalClients + "号客户加入了聊天室");

				//做好本线程以后，开启下一个等待线程
				threads.put(totalClients,new Thread(new Task()));
				threads.get(totalClients).start();

				getClientMessage(totalClients);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		private void getClientMessage(int clientID) {                                       //从客户端获取信息，并转发
			final int num = clientID -1 ;
			try {
				while (true){                                                   //一直接收
					String message = readers.get(num).readLine();
					System.out.println("getMessage = " + message);

					if (!message.equals(null))  //如果没有这一行语句，会被null刷屏
						sendMessage((num + 1) + "号客户:" + message);
				}// while
			}catch (Exception e){   //客户端关闭后，message会有空索引异常，catch它即可通知
				clientSockets.remove(num);
				threads.remove(num);
				onlineClients.remove(onlineClients.indexOf(num));
				sendMessage((num + 1 ) + "号机器离开了聊天室");
				return;
			}

		}
		private void sendMessage(String message){                               //给聊天室的每个人发送信息
			for (int i = 0; i < onlineClients.size(); i++ ){
				try {
				PrintWriter writer = new PrintWriter(clientSockets.get(
						onlineClients.get(i)            //获取当前在线客户端的编号
				).getOutputStream(), true);
				System.out.println("sendMessage" + message);
				writer.println(message );
					writer.flush();                                     //强制写入

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
