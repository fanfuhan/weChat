import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImServerFinal {
    private ServerSocket serverSocket;
    // 线程池
    private ExecutorService executorService;
    // 存储在线的用户名及对应socket
    private Hashtable<String, Socket> userSocketInfo;
    // 检测是否有新客户端连接
    private boolean haveNewClient = false;
    // 好友列表
    private List<String> friendList;
    // 存储离线消息
    private Hashtable<String, List<String>> offLineMsg;

    public ImServerFinal() {
        try {
            serverSocket = new ServerSocket(23);
            executorService = Executors.newCachedThreadPool();
            userSocketInfo = new Hashtable<>();
            friendList = new ArrayList<>();
            offLineMsg = new Hashtable<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            final Socket socket;
            try {
                socket = serverSocket.accept();
                executorService.execute(new ServerClient(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ImServerFinal().start();
    }

    class ServerClient implements Runnable {
        private Socket socket;
        private String userName;
        private BufferedReader bReader;
        private PrintWriter pWriter;

        public ServerClient(Socket socket) {
            this.socket = socket;
            try {
                bReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), "UTF-8"));
                pWriter = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 更新好友列表
        public void updateFriendList() {
            if (haveNewClient) {
                haveNewClient = false;
                List<String> tempList = new ArrayList<>();
                tempList.addAll(friendList);
                tempList.add(0, "&");
                tempList.add("#");
                String friendListStr = String.join(":", tempList);
                for (Socket socket : userSocketInfo.values()) {
                    PrintWriter pw = null;
                    try {
                        pw = new PrintWriter(
                                new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                        pw.println(friendListStr);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // 读取离线消息
        private void readOffLineMsg(String userName) {
            for (String s : offLineMsg.get(userName)) {
                pWriter.println(s);
            }
            offLineMsg.remove(userName);
        }

        @Override
        public void run() {
            try {
                userName = bReader.readLine();
                if (!friendList.contains(userName)) {
                    friendList.add(userName);
                }
                userSocketInfo.put(userName, socket);
                haveNewClient = true;

                // 向新连接的客户端发送好友列表
                updateFriendList();

                // 用户上线读取离线消息
                if (offLineMsg.containsKey(userName)) {
                    readOffLineMsg(userName);
                }

                String message = null;
                while ((message = bReader.readLine()) != null) {
                    // 客户端发送bye，服务器删除在线用户的登记
                    if (message.trim().equals("bye")) {
                        userSocketInfo.remove(userName);
                        break;
                    }

                    // 读取用户发送的消息
                    String[] msgParts = message.split(":");
                    if (msgParts[0].equals(userName) && userSocketInfo.containsKey(msgParts[1])) {
                        Socket toSocket = userSocketInfo.get(msgParts[1]);
                        PrintWriter toPWriter = new PrintWriter(
                                new OutputStreamWriter(toSocket.getOutputStream(), "UTF-8"), true);
                        toPWriter.println(message);
                        //toPWriter.close();
                    } else {
                        if (!offLineMsg.containsKey(msgParts[1])) {
                            List<String> msgList = new ArrayList<>();
                            msgList.add(message);
                            offLineMsg.put(msgParts[1], msgList);
                        } else {
                            offLineMsg.get(msgParts[1]).add(message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bReader.close();
                    pWriter.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
