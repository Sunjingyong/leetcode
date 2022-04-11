package NIO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioClient {



    public static void main(String[] args) throws Exception{
        //创建SocketChannel 用来请求端口
        SocketChannel socketChannel = SocketChannel.open();
        //配置为非阻塞
        socketChannel.configureBlocking(false);
        //创建Selector
        Selector selector = Selector.open();
        //socketChannel注册到selector 初始时关注向服务端建立连接的事件
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        //向远程发起连接
        socketChannel.connect(new InetSocketAddress("127.0.0.1",8899));

        while (true){
            //阻塞 关注感兴趣的事件
            selector.select();
            //获取关注事件的SelectionKey集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            //根据不同的事件做不同的处理
            for(SelectionKey selectionKey : selectionKeys){
                final SocketChannel channel;
                if(selectionKey.isConnectable()){
                    //与服务端建立好连接 获取通道
                    channel = (SocketChannel) selectionKey.channel();
                    //客户端与服务端是否正处于连接中
                    if(channel.isConnectionPending()){
                        //完成连接的建立
                        channel.finishConnect();
                        //发送连接建立的信息
                        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                        //读入
                        writeBuffer.put((LocalDateTime.now() + "连接成功").getBytes());
                        writeBuffer.flip();
                        //写出
                        channel.write(writeBuffer);
                        //TCP双向通道建立
                        //键盘作为标准输入 避免主线程的阻塞 新启线程来做处理
                        ExecutorService service = Executors.newSingleThreadExecutor(Executors.defaultThreadFactory());
                        service.submit(()->{
                            while (true){
                                writeBuffer.clear();
                                //IO操作
                                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                                BufferedReader reader = new BufferedReader(inputStreamReader);

                                String readLine = reader.readLine();
                                //读入
                                writeBuffer.put(readLine.getBytes());
                                writeBuffer.flip();
                                channel.write(writeBuffer);
                            }
                        });
                    }
                    //客户端也需要监听服务端的写出信息 所以需要关注READ事件
                    channel.register(selector,SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    //从服务端读属事件
                    channel = (SocketChannel) selectionKey.channel();
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);

                    int count = channel.read(readBuffer);
                    if(count > 0){
                        readBuffer.flip();

                        Charset charset = Charset.forName("utf-8");
                        String receiveMessage = String.valueOf(charset.decode(readBuffer).array());

                        System.out.println("client:" + receiveMessage);
                    }
                }
                //处理完成该key后 必须删除 否则会重复处理报错
                selectionKeys.clear();
            }


        }

    }
}
