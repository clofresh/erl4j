package com.syntacticbayleaves.erl4j;

// Erlang's jinterface
import com.ericsson.otp.erlang.OtpSelf;
import com.ericsson.otp.erlang.OtpPeer;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;

// Apache's Daemon
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

// Exceptions
import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpErlangExit;

public abstract class Erl4j implements Daemon {
    private Erl4jConnection connection;
    
    public Erl4j() {}
    
    public void init(DaemonContext context) {
        OtpSelf me = null; 
        OtpPeer other = null; 
        
        try {
            me = new OtpSelf("jruby_node@ccabanilla-mac");
            other = new OtpPeer("client@ccabanilla-mac");
        } catch (java.io.IOException e) {
        }
        this.connection = new Erl4jConnection(me, other);
    }
    
    public void start() {
        Erl4jRequest request;
        OtpErlangObject response;
        
        while(true) {
            request = this.connection.receive();
            response = this.respond(request);
            this.connection.reply(request, response);
        }
        
    }
    public void stop() {}
    public void destroy() {}
    
    abstract OtpErlangObject respond(Erl4jRequest request);
    
}

