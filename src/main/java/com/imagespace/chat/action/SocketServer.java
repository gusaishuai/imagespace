package com.imagespace.chat.action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/24
 */
public class SocketServer {

//    public static List<Socket> socketList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // 监听指定的端口
        int port = 55533;
        ServerSocket server = new ServerSocket(port);

        // server将一直等待连接的到来
        System.out.println("server将一直等待连接的到来");
        while (true) {
            Socket socket = server.accept();
            System.out.println("上线通知： " + socket.getInetAddress() + ":" +socket.getPort());
//            socketList.add(socket);
            new Thread(() -> {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String str;
                    while((str = br.readLine()) != null){
                        socket.getOutputStream().write((str + "\r\n").getBytes("UTF-8"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try {
                    InputStream inputStream = socket.getInputStream();
//                    byte[] bytes = new byte[1024];
//                    int len;
//                    StringBuilder sb = new StringBuilder();
//                    while ((len = inputStream.read(bytes)) != -1) {
//                        //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
//                        sb.append(new String(bytes, 0, len,"UTF-8"));
//                    }


                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    //发送读到的内容
                    while ((line = br.readLine())!=null) {
                        if (!line.endsWith("#EOF#")) {
                            sb.append(line).append("\r\n");
                            continue;
                        }
                        sb.append(line).delete(sb.length() - 5, sb.length());
                        System.out.println("client用户" + socket.getPort() + "说：" + sb.toString());
                        sb.delete(0, sb.length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        }
        // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
//        InputStream inputStream = socket.getInputStream();
//        byte[] bytes = new byte[1024];
//        int len;
//        StringBuilder sb = new StringBuilder();
//        while ((len = inputStream.read(bytes)) != -1) {
//            //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
//            sb.append(new String(bytes, 0, len,"UTF-8"));
//        }
//        System.out.println("get message from client: " + sb);
//        inputStream.close();
//        socket.close();
//        server.close();
    }

}
