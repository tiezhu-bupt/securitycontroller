/** 
*    Copyright 2014 BUPT. 
**/ 
/**
*    Copyright 2011, Big Switch Networks, Inc. 
*    Originally created by David Erickson, Stanford University
* 
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package com.sds.securitycontroller.core.internal;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.ExternalResourceReleasable;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import com.sds.securitycontroller.core.internal.MessageChannelState.HandshakeState;

/**
 * Trigger a timeout if a switch fails to complete handshake soon enough
 */
public class HandshakeTimeoutHandler 
    extends SimpleChannelUpstreamHandler
    implements ExternalResourceReleasable {
    static final HandshakeTimeoutException EXCEPTION = 
            new HandshakeTimeoutException();
    
    final MessageChannelState state;
    final Timer timer;
    final long timeoutNanos;
    volatile Timeout timeout;
    
    public HandshakeTimeoutHandler(MessageChannelState state, Timer timer,
                                   long timeoutSeconds) {
        super();
        this.state = state;
        this.timer = timer;
        this.timeoutNanos = TimeUnit.SECONDS.toNanos(timeoutSeconds);

    }
    
    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        if (timeoutNanos > 0) {
            timeout = timer.newTimeout(new HandshakeTimeoutTask(ctx), 
                                       timeoutNanos, TimeUnit.NANOSECONDS);
        }
        ctx.sendUpstream(e);
    }
    
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        if (timeout != null) {
            timeout.cancel();
            timeout = null;
        }
    }

    @Override
    public void releaseExternalResources() {
        timer.stop();
    }
    
    private final class HandshakeTimeoutTask implements TimerTask {

        private final ChannelHandlerContext ctx;

        HandshakeTimeoutTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (timeout.isCancelled()) {
                return;
            }

            if (!ctx.getChannel().isOpen()) {
                return;
            }
            if (!state.hsState.equals(HandshakeState.READY))
                Channels.fireExceptionCaught(ctx, EXCEPTION);
        }
    }
}
