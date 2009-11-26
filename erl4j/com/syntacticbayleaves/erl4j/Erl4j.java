package com.syntacticbayleaves.erl4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.security.SecureClassLoader;

// Erlang's jinterface
import com.ericsson.otp.erlang.OtpSelf;
import com.ericsson.otp.erlang.OtpPeer;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpConnection;

// Apache's Daemon
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;

// Exceptions
import java.io.IOException;
import com.ericsson.otp.erlang.OtpAuthException;
import com.ericsson.otp.erlang.OtpErlangExit;

public class Erl4j implements Daemon {
    private Class dispatcherClass;
    private ExecutorService threadPool;
    private OtpSelf me;
    private int timeout;
    
    public Erl4j() {}
    
    public void init(DaemonContext context) throws IOException, ClassNotFoundException {
        String[] args = context.getArguments();

        this.timeout = Integer.parseInt(args[2]);
        this.threadPool = Executors.newFixedThreadPool(Integer.parseInt(args[3]));

        ClassLoader loader = Erl4j.class.getClassLoader();
        this.dispatcherClass = loader.loadClass(args[1]);
        
        this.me = new OtpSelf(args[0]);
        this.me.publishPort();
    }
    
    public void start() {
        OtpConnection connection;
        Erl4jRunnable runnable;
        Erl4jHandler dispatcher;
        
        try {
            while(this.me != null) {
                try {
                    dispatcher = (Erl4jHandler) this.dispatcherClass.newInstance();
                    connection = this.me.accept();
                    runnable = new Erl4jRunnable(connection, dispatcher, this.timeout);
                    this.threadPool.execute(runnable);
                } catch (IOException e) {
                    // Remote node tried to connect but couldn't find a common protocol
                } catch (OtpAuthException e) {
                    // Unauthorized connection 
                }
            
            }
        } catch (IllegalAccessException e) {
            // Something wrong with the dispatcherClass
        } catch (InstantiationException e) {
            // Something wrong with the dispatcherClass
        }
    }
    
    public void stop() {
        this.threadPool.shutdown();
    }
    
    public void destroy() {
        this.me.unPublishPort();
        this.me = null;
    }
    
}

