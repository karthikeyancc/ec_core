    package ec.core.servlet;


import ec.core.util.ECServerUtil;
import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

/**
 * Servlet implementation class CacheInitServlet
 */
public class ECContextListener implements ServletContextListener {
    private static final long serialVersionUID = 1L;
	
    private static final Logger LOGGER = Logger.getLogger(ECContextListener.class.getName());
    protected static DataSource dataSource;
    public static String ROOT=null;
    protected static final List<String> ROLE_LIST =new ArrayList<>(); 
	
    
    

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");    
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        ServletContext sc=sce.getServletContext();
        String v=sc.getRealPath("/");
        int r=v.lastIndexOf(File.separatorChar);
        ROOT=v.substring(0,r);
        LOGGER.log(Level.INFO,"context root directory = "+ROOT);
        initDataSource(sc);
        ROLE_LIST.clear();
        try {
            ROLE_LIST.addAll(ECServerUtil.listFileContents(sc, "roleNamesFile"));
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(ECServerUtil.DS);
        dataSource=null;
    }
    private void initDataSource(ServletContext servletContext){
		String dbJNDIName=servletContext.getInitParameter("db_jndi_name");
		LOGGER.info("db jndi name "+dbJNDIName);
		try{
			InitialContext context=new InitialContext();
	        dataSource = (DataSource)context.lookup(dbJNDIName);
            servletContext.setAttribute(ECServerUtil.DS, dataSource);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
