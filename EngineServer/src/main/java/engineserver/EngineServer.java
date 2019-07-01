package engineserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.SucceededFuture;
import nullengine.client.player.PlayerClient;

public class EngineServer{
	private ServerBootstrap serverBootstrap=new ServerBootstrap();
	public void initServer(){
		serverBootstrap.group(new NioEventLoopGroup()).channel(NioServerSocketChannel.class);
	}
	public void onPacket(ByteBuf byteBuf){

	}
}
