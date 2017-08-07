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
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DataServlet
 */
public class RoleFileServlet extends HttpServlet {
    private static final Logger LOGGER=Logger.getLogger(RoleFileServlet.class.getName());
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sess = request.getSession();
        String role = (String) sess.getAttribute(ECServerUtil.ROLE);
        if (role == null) {
            for(String  r: ECContextListener.ROLE_LIST){
                if (request.isUserInRole(r)) {
                    role = r;
                }
            }
            if (role == null) {
                role = "anon";
            }
            sess.setAttribute(ECServerUtil.ROLE, role);
        }
        LOGGER.log(Level.INFO," user role = "+role +" for "+sess.getAttribute(ECServerUtil.USER));
        String fpath=request.getPathInfo();
        int flindex=fpath.lastIndexOf('/');
        String fpt="";
        if(flindex>0){
            fpt=fpath.substring(0,flindex);
        }else{
            flindex=0;
        }
        fpt=fpt+'/'+role+'.'+fpath.substring(flindex+1);
        LOGGER.log(Level.INFO," fpath = "+fpath+" and fpt = "+fpt);
        request.getRequestDispatcher(fpt).forward(request, response);
    }

}
