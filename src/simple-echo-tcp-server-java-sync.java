package com.echoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Example {
    public void start() {
        ExecutorService executor = Executors.newCachedThreadPool();
        int port = 2424;
        ServerSocketChannel server = null;
        try {
            server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            while (true){
                final SocketChannel socket = server.accept();
                // init new client;
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        BufferedReader reader = null;
                        PrintWriter writer = null;
                        try {
                            reader = new BufferedReader(Channels.newReader(socket, "utf-8"));
                            writer = new PrintWriter(Channels.newWriter(socket, "utf-8"));
                            while(socket.isConnected()){
                                String command = reader.readLine();
                                //do some processing
                                writer.println(command);
                                writer.flush();
                            }
                        }
                        catch (IOException ex){
                            if (!socket.isConnected()){
                                ex.printStackTrace();
                            }
                        }
                        finally {
                            if (reader != null) try {reader.close();} catch (Exception ex){}
                            if (writer != null) try {writer.close();} catch (Exception ex){}
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) try {server.close();} catch (Exception ex){}
        }
    }
}
