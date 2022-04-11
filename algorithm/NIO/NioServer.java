package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NioServer {

    //通过map来记录客户端连接信息
    private static Map<String, SocketChannel> clientMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        //创建ServerSocketChannel 用来监听端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        //获取服务端的socket
        ServerSocket serverSocket = serverSocketChannel.socket();
        //监听8899端口
        serverSocket.bind(new InetSocketAddress(8899));
        //创建Selector
        Selector selector = Selector.open();
        //serverSocketChannel注册到selector 初始时关注客户端的连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true){
            try{
                //阻塞 关注感兴趣的事件
                selector.select();
                //获取关注事件的SelectionKey集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                //根据不同的事件做不同的处理
                selectionKeys.forEach(selectionKey -> {
                    final SocketChannel client;
                    try{
                        //连接建立起来之后 开始监听客户端的读写事件
                        if(selectionKey.isAcceptable()){
                            //如何监听客户端读写事件 首先需要将客户端连接注册到selector
                            //如何获取客户端建立的通道 可以通过selectionKey.Channel()
                            //前面只注册了ServerSocketChannel 所以进入这个分支的通道必定是
                            //ServerSocketChannel
                            ServerSocketChannel server = (ServerSocketChannel)selectionKey.channel();
                            //获取到真实的客户端
                            client = server.accept();
                            client.configureBlocking(false);
                            //客户端连接注册到selector
                            client.register(selector, SelectionKey.OP_READ);
                            //selector已经注册上ServerSocketChannel(关注连接)和SocketChannel(关注读写)
                            //UUID代表客户端 此处为业务信息
                            String key = "[" + UUID.randomUUID().toString() + "]";
                            clientMap.put(key, client);
                        }else if(selectionKey.isReadable()){
                            //处理客户端写过来的数据 对于服务端是可读数据 此处必定是和SocketChannel
                            client = (SocketChannel)selectionKey.channel();
                            //Channel不能读写数据 必须通过Buffer来读写数据
                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                            //服务端读数据到buffer
                            int count = client.read(byteBuffer);
                            if(count > 0){
                                //读写转换
                                byteBuffer.flip();
                                //写数据到其他客户端
                                Charset charset = Charset.forName("utf-8");
                                String receiveMessage = String.valueOf(charset.decode(byteBuffer).array());
                                System.out.println("client:" + client + receiveMessage);

                                String sendKey = null;
                                for(Map.Entry<String, SocketChannel> entry : clientMap.entrySet()){
                                    if(client == entry.getValue()){
                                        //拿到发送者的UUID 用于模拟客户端的聊天发送信息
                                        sendKey = entry.getKey();
                                        break;
                                    }
                                }
                                for(Map.Entry<String, SocketChannel> entry : clientMap.entrySet()){
                                   //拿到所有建立连接的客户端对象
                                    SocketChannel value = entry.getValue();

                                    ByteBuffer writerBuffer = ByteBuffer.allocate(1024);
                                    //这个put操作是Buffer的读操作
                                    writerBuffer.put((sendKey + ":" + receiveMessage).getBytes());

                                    //write之前需要读写转换
                                    writerBuffer.flip();
                                    //写出去
                                    value.write(writerBuffer);
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
                //处理完成该key后 必须删除 否则会重复处理报错
                selectionKeys.clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
