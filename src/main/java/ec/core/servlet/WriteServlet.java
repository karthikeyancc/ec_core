package ec.core.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import ec.core.Executor;
import ec.core.bean.Task;
import ec.core.bean.Tasks;
import ec.core.util.ECServerUtil;
import java.util.HashMap;
import org.json.JSONException;

/**
 * Servlet implementation class CacheInitServlet
 */
public class WriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(WriteServlet.class.getName());
    private static HashMap<String,Task> taskCache=new HashMap<>();
    private static DataSource dataSource;
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		ServletContext servletContext=config.getServletContext();
		initCache(servletContext);
        dataSource=(DataSource)servletContext.getAttribute(ECServerUtil.DS);
	}
	private void initCache(ServletContext servletContext) {
        try {
			ECServerUtil.readFiles(servletContext,Tasks.class,"task_file_names",taskCache);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
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
        String sb=ecexecute(request, response);
		response.getWriter().println(sb);
	}

    private String ecexecute(HttpServletRequest request, HttpServletResponse response) {
        //	printRequestParams(request);
        String ecuser=(String)request.getAttribute(ECServerUtil.USER);
        System.out.println("ecuser = "+ecuser);
		Executor execu=new Executor();
        String taskId=request.getParameter("id");
        String data=request.getParameter("data");
        HashMap<String,Object> map=new HashMap();
        map.put(ECServerUtil.DS,dataSource);
        map.put(ECServerUtil.TC,taskCache);
        map.put(ECServerUtil.REQ,request);
        map.put(ECServerUtil.RES,response);
        map.put(ECServerUtil.USER,ecuser);
        String sb="";
        try {
            sb = execu.execute(taskId,data,map);
        } catch (JSONException ex) {
            printRequestParams(request);
            ex.printStackTrace();
            sb="{\"error\":\"System error while constructing response\"}";
        }
        return sb;
    }

	private void printRequestParams(HttpServletRequest req){
		Enumeration< String> en=req.getParameterNames();
		while(en.hasMoreElements()){
			String e=en.nextElement();
			LOGGER.log(Level.INFO, "param "+e+" = "+req.getParameter(e));
			
		}
	}

    @Override
    public void destroy() {
        super.destroy(); 
        taskCache.clear();
        dataSource=null;
    }
    
}
