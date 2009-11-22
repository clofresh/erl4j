package com.syntacticbayleaves.erl4j;

// Erlang's jinterface
import com.ericsson.otp.erlang.OtpSelf;
import com.ericsson.otp.erlang.OtpPeer;
import com.ericsson.otp.erlang.OtpConnection;
import com.ericsson.otp.erlang.OtpErlangPid;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangTuple;

// Apache's Daemon
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

// Exceptions
import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpErlangExit;

public class Erl4j implements Daemon {
    private OtpSelf me;
    private OtpPeer other;
    private OtpConnection connection;
    
    public Erl4j() {}
    
    public void init(DaemonContext context) {
        try {
            me = new OtpSelf("jruby_node@ccabanilla-mac");
            other = new OtpPeer("client@ccabanilla-mac");
            connection = me.connect(other);
        } catch (java.io.IOException e) {
        } catch (OtpAuthException e) {
        }
    }
    
    public void start() {
        OtpErlangObject ref, response;
        OtpErlangPid pid;
        OtpErlangTuple msg, sender;
        
        while(true) {
            try {
                msg = (OtpErlangTuple) connection.receive();
                sender = (OtpErlangTuple) msg.elementAt(1);
                pid = (OtpErlangPid) sender.elementAt(0);
                ref = sender.elementAt(1);
            
                response = new OtpErlangTuple(new OtpErlangObject[] {ref, new OtpErlangLong(10)});
                connection.send(pid, response);
            } catch (java.io.IOException e) {
                
            } catch (OtpAuthException e) {
                
            } catch (OtpErlangExit e) {
                
            }
            
        }
        
    }
    public void stop() {}
    public void destroy() {}
    
    
}

