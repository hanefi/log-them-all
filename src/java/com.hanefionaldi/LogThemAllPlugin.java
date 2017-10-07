package com.hanefionaldi;

import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sample plugin for Openfire.
 */
public class LogThemAllPlugin implements Plugin, PacketInterceptor {
    private Logger Log;

    private InterceptorManager interceptorManager;

    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        Log = LoggerFactory.getLogger(LogThemAllPlugin.class);
        interceptorManager = InterceptorManager.getInstance();
        interceptorManager.addInterceptor(this);
    }

    public void destroyPlugin() {
        // Your code goes here
    }

    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
            throws PacketRejectedException
    {
        Log.info("Packet intercepted {}", packet);
        // Ignore any packets that haven't already been processed by interceptors.
        if (!processed) {
            Log.info("Packet was not processed, exiting {}", packet);
            return;
        }
        if (packet instanceof Message) {
            // Ignore any outgoing messages (we'll catch them when they're incoming).
            if (!incoming) {
                return;
            }
            Message message = (Message) packet;
            // Ignore any messages that don't have a body so that we skip events.
            // Note: XHTML messages should always include a body so we should be ok. It's
            // possible that we may need special XHTML filtering in the future, however.
            if (message.getBody() != null) {
                // Only process messages that are between two users, group chat rooms, or gateways.
                Log.info("intercepted message from {} to {}", message.getFrom(), message.getTo());
            } else {
                Log.info("intercepted message without body from {} to {}", message.getFrom(), message.getTo());
            }
        }
    }
}