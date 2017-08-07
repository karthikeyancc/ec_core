package ec.core.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;


import ec.core.JSONGenerator;
import ec.core.bean.Queries;
import ec.core.bean.SelectQuery;
import ec.core.util.ECServerUtil;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Servlet implementation class DataServlet
 */
public class ReadUServlet extends ReadServlet {
	private static List<String>VALID_URLS;
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        try {
            VALID_URLS=ECServerUtil.listFileContents(config.getServletContext(), "validUReadIds");
            LOGGER.log(Level.CONFIG,"valid urls to read "+VALID_URLS);
        } catch (IOException ex) {
            Logger.getLogger(ReadUServlet.class.getName()).log(Level.SEVERE, "valid unsecure read id file not processed", ex);
        }
	}
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadUServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		process(request, response);
	}
	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String qu=request.getParameter("qu");
        PrintWriter out=response.getWriter();
        if(ECServerUtil.isNotBlank(qu)){
            if(VALID_URLS.contains(qu)){
                super.process(request, response);
            }else{
                out.println("{\"error\":\"Un authorized access to resource "+qu+" \"}");
            }
        }else{
            out.println("{\"error\":\"query identifier not provided \"}");
        }
	}
    
}
