package com.hanefionaldi;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

/**
 * A sample plugin for Openfire.
 */
public class LogThemAllPlugin implements Plugin, PacketInterceptor {
    private Logger Log;

    private InterceptorManager interceptorManager;

    private void saveInDB(String packet, String session, int incoming, int processed) {
        Connection con = null;
        Statement stmt = null;
        String sql = String.format("INSERT INTO ofLogThemAll (packet, session, incoming, processed) VALUES ('%s', '%s', %s, %s)", packet, session, incoming, processed);
        try {
            con = DbConnectionManager.getConnection();
            stmt = con.createStatement();
            stmt.execute(sql);
        } catch (SQLException ex) {
            Log.error(ex.toString());
        }
        finally {
            DbConnectionManager.closeConnection(con);
            DbConnectionManager.closeStatement(stmt);
        }
    }

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
        saveInDB(packet.toString(), session.toString(), incoming ? 1 : 0, processed ? 1 : 0);

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
