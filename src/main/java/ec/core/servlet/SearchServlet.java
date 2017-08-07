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
import ec.core.bean.Param;
import ec.core.bean.Queries;
import ec.core.bean.SelectQuery;
import ec.core.util.ECServerUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Servlet implementation class DataServlet
 */
public class SearchServlet extends ReadServlet {

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
        {
            String ecuser=(String)request.getAttribute(ECServerUtil.USER);
            String qu = request.getParameter("qu");
            response.setContentType("application/json");
            if (qu != null) {
                SelectQuery t = (SelectQuery) queryCache.get(qu);
                if (t == null) {
                    response.getWriter().println("{\"error\":\"query with name " + qu + " does not exists\"}");
                } else {
//                    List<Param> operparams = t.getOperationParams();
//                    Pattern p=Pattern.compile("\\b\\\\b");
                    String op=t.getOperation();
                    if(ECServerUtil.isNotBlank(t.getBaseQuery())){
                        SelectQuery tbase = (SelectQuery) queryCache.get(t.getBaseQuery());
                        op=tbase.getOperation()+ " "+(op==null?"":op);
                    }
                    StringBuilder sb = new StringBuilder(op.replaceAll("\\$\\{ecuser\\}","'"+request.getAttribute("ecuser")+"'"));
                    if(!t.isWhereExists()){
                        sb.append(" where 1=1 ");
                    }
                    Map<String, String[]> params = request.getParameterMap();
                    String[] vals = new String[params.size() - 1];
                    Iterator<Map.Entry<String, String[]>> iter = params.entrySet().iterator();
                    int i = 0;
                    while (iter.hasNext()) {
                        Map.Entry<String, String[]> entry = iter.next();
                        String key = entry.getKey();
                        if (key.equals("qu")) {
                            continue;
                        }
                        if (key.toLowerCase().startsWith("start__")) {
                            key = key.substring(7);
                            vals[i] = entry.getValue()[0];
                            sb.append(" and " + key + " >= ? ");
                        }else if (key.toLowerCase().startsWith("end__")) {
                            key = key.substring(5);
                            vals[i] = entry.getValue()[0];
                            sb.append(" and " + key + " <= ? ");
                        }else{
                            vals[i] = '%'+entry.getValue()[0]+'%';
                            sb.append(" and " + key + " like ? ");
                        }
                        i++;
                    }
                    
                    JSONGenerator jsongen = new JSONGenerator();
                    try (Connection con = dataSource.getConnection()) {
                        LOGGER.log(Level.INFO, " query "+sb.toString()+" \nand params {}"+Arrays.toString(vals));
                        jsongen.process(response.getWriter(), sb.toString(), vals, con);
                        con.close();
                    } catch (SQLException nfe) {
                        LOGGER.log(Level.SEVERE, "exception ", nfe);
                        response.getWriter().println("{\"error\":\"" + nfe.getMessage() + "\"}");
                    }
                }
            } else {
                response.getWriter().println("{\"error\":\"query not specified\"}");
            }

        }
    }

}
