/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package gradle_tish_embedded;

import org.apache.catalina.Context;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;

import reminder.Reminder;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;


//  this is the main class, which is responsible for starting Tomcat server
public class App {
    public static Properties prop;
    public static Reminder reminder;
    public static void main(String[] args) throws Exception {
        easyMain(args);
    }
    
    /** 
     * @param args
     * @throws Exception
     */
    public static void easyMain(String[] args)
            throws Exception {
        Tomcat tomcat = new Tomcat();
        prop = new Properties();
        String fileName = "./src/main/resources/app.properties";
        FileInputStream fis = new FileInputStream(fileName);
        prop.load(fis);
        // TODO: fix the way we retrieve resource 
  
 

        int port = Integer.parseInt(prop.getProperty("port"));
        String hostname = App.prop.getProperty("hostname");
        String keyStorePass = App.prop.getProperty("keyStorePass");
        String keyStoreName = App.prop.getProperty("keyStoreName");
        tomcat.setHostname(hostname);
        // idk why we need this connector, maybe for connecting via http?
        System.out.println(System.getProperty("user.dir"));
        Http11Nio2Protocol protocolHandler = new Http11Nio2Protocol();
        protocolHandler.setPort(port);
        protocolHandler.setSSLEnabled(true);
        // protocolHandler.setSSL(true);
        protocolHandler.setKeystoreFile("../src/main/resources/"+keyStoreName);
        protocolHandler.setKeystorePass(keyStorePass);
        protocolHandler.setKeyAlias("mykey");
        // protocolHandler.setSSLVerifyClient(Boolean.toString(true));
        
        System.out.println(protocolHandler.isSSLEnabled());
        Connector conn = new Connector(protocolHandler);
        tomcat.setConnector(conn);

        Context ctx = tomcat.addWebapp("", new File("./src/main/resources").getAbsolutePath());

        // by default jndi is disabled in tomcat embedded
        tomcat.enableNaming();



        

        tomcat.start();
        tomcat.getServer().await();
    }
 
 
}
