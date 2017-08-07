package ec.core.servlet;

import ec.core.util.ECServerUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet implementation class CacheInitServlet
 */
public class WriteUServlet extends WriteServlet {
	private static List<String>VALID_URLS;
    private static Logger LOGGER = Logger.getLogger(WriteUServlet.class.getName());
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        try {
            VALID_URLS=ECServerUtil.listFileContents(config.getServletContext(), "validUWriteIds");
            LOGGER.log(Level.CONFIG,"valid urls to write "+VALID_URLS);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "valid unsecure read id file not processed", ex);
        }
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

	protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	String qu=request.getParameter("id");
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
