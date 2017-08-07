package ec.core.servlet;

import ec.core.JSONGenerator;
import ec.core.bean.Queries;
import ec.core.bean.SelectQuery;
import ec.core.util.ECServerUtil;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class DataServlet
 */
public class ReadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = Logger.getLogger(ReadServlet.class.getName());
    protected static HashMap<String, SelectQuery> queryCache = new HashMap<>();
    protected static DataSource dataSource;

    public void init(ServletConfig config) throws ServletException {
        ServletContext servletContext = config.getServletContext();
        initCache(servletContext);
        dataSource = (DataSource) servletContext.getAttribute(ECServerUtil.DS);
    }

    private void initCache(ServletContext servletContext) {
        try {
            ECServerUtil.readFiles(servletContext, Queries.class, "query_file_names", queryCache);
            Pattern p = Pattern.compile("\\b[wW][hH][eE][rR][eE]\\b");
            for (SelectQuery t : queryCache.values()) {
                t.setWhereExists(p.matcher(opfind(t)).find());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    protected void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String res = request.getParameter("res");
        String ecuser = (String) request.getAttribute(ECServerUtil.USER);
        if (ECServerUtil.isNotBlank(res)) {
            request.getRequestDispatcher(res.trim()).forward(request, response);
        } else {
            JSONGenerator jsongen = new JSONGenerator();
            try (Connection con = dataSource.getConnection()) {
                String qu = request.getParameter("qu");
                String start = request.getParameter("start");
                String limit = request.getParameter("limit");
                response.setContentType("application/json");
                if (qu != null) {
                    SelectQuery t = (SelectQuery) queryCache.get(qu);
                    if (t == null) {
                        response.getWriter().println("{\"error\":\"query with name " + qu + " does not exists\"}");
                    } else {
                        int s = -1;
                        int l = -1;
                        try {
                            if (limit != null) {
                                l = Integer.parseInt(limit);
                            }
                            if (start != null) {
                                s = Integer.parseInt(start);
                            }
                        } catch (NumberFormatException nfe) {
                            LOGGER.log(Level.SEVERE, "limit invalid ", nfe);
                        }
                        LOGGER.log(Level.CONFIG, "ecuser " + ecuser);
                        String op=opfind(t);
                        jsongen.process(response.getWriter(), op, jsongen.findParams(t.getOperationParams(), request.getParameterMap(), ecuser), con, s, l);
                    }
                } else {
                    response.getWriter().println("{\"error\":\"query not specified\"}");
                }
                con.close();
            } catch (SQLException nfe) {
                LOGGER.log(Level.SEVERE, "exception ", nfe);
                response.getWriter().println("{\"error\":\"" + nfe.getMessage() + "\"}");
            }
        }
    }
    private String opfind(SelectQuery t){
        String op=t.getOperation();
        if(ECServerUtil.isNotBlank(t.getBaseQuery())){
			SelectQuery tbase = (SelectQuery) queryCache.get(t.getBaseQuery());
            op=tbase.getOperation()+ " "+(op==null?"":op);
		}

		return op;
    }
    @Override
    public void destroy() {
        super.destroy();
        queryCache.clear();
        dataSource = null;
    }

}
