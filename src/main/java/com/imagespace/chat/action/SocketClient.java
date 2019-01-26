package com.imagespace.chat.action;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * @author gusaishuai
 * @since 2019/1/24
 */
public class SocketClient {

    public static void main(String args[]) throws Exception {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);

        new Thread(() -> {
            try {
                List<String> aaList;
//                while ((aaList = IOUtils.readLines(System.in)) != null) {
//                    StringBuilder sb = new StringBuilder();
//                    aaList.forEach(r -> sb.append(r).append("\r\n"));
//                    socket.getOutputStream().write(sb.toString().getBytes("UTF-8"));
//                }
//                BufferedReader br = new BufferedReader();
//                String str;
//                while((str = br.readLine()) != null){
//                    socket.getOutputStream().write((str + "\r\n").getBytes("UTF-8"));
//                }
                String str = FileUtils.readFileToString(new File("C:\\Users\\gusaishuai704\\Desktop\\1.txt"));
                socket.getOutputStream().write((str+"#EOF#\r\n").getBytes("UTF-8"));
                while(true){

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

//        new Thread(() -> {
//            try {
//                InputStream inputStream = socket.getInputStream();
//                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//                StringBuilder sb = new StringBuilder();
//                String line;
//                //发送读到的内容
//                while ((line = br.readLine())!=null) {
////                    if (!line.endsWith("#EOF")) {
////                        sb.append(line);
////                        continue;
////                    }
//                    System.out.println("server用户" + socket.getPort() + "说：" +line);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
    }


}
