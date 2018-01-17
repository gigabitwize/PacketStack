package io.vawke.packetstack;

import io.nerfnet.packetstack.pipeline2.PipelineEnd;
import io.nerfnet.packetstack.pipeline2.simple.PacketPipeline;
import io.nerfnet.packetstack.pipeline2.simple.PacketPipelineEnd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Optional;

/**
 * Created by Giovanni on 15/01/2018.
 */
@ChannelHandler.Sharable
public abstract class PipelineEndLauncher extends ChannelInboundHandlerAdapter implements Adapter {

    // TODO implement protocol, this is here for testing.

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // $con::eur001::9993::def::5
        // $con::5::9994::pend
        if (msg instanceof String) {
            String string = (String) msg;
            try {
                String[] values = string.split("::");
                if (values[0].equalsIgnoreCase("$con")) {
                    String identifier = values[1];
                    int port = Integer.valueOf(values[2]);

                    if (values[3] == null) {
                        System.out.println("Received an unknown $con type, dismiss.");
                        return;
                    }

                    if (values[3].equalsIgnoreCase("pend")) {
                        PacketPipelineEnd end = new PacketPipelineEnd(0, ctx.channel());
                        service().endingPipelines().add(end);
                    } else if (values[3].equalsIgnoreCase("def")) {
                        int endId = Integer.valueOf(values[4]);

                        Optional<PipelineEnd> pipelineEnd = service().endingPipelines().find(endId);
                        if (!pipelineEnd.isPresent()) {
                            System.out.println("Received $con but no PipelineEnd found with identifier " + endId);
                            return;
                        }

                        PacketPipeline pipeline = new PacketPipeline(identifier, pipelineEnd.get());
                        pipelineEnd.get().connectedPipelines().add(pipeline);
                    }
                }
            } catch (Exception e) {
                System.out.println("An error occurred whilst trying to launch a PipelineEnd!");
                System.out.println(e.getMessage());
            }
        }
    }
}
