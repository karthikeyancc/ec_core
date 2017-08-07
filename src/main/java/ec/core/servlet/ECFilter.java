package ec.core.servlet;

import ec.core.util.ECServerUtil;
import static ec.core.util.ECServerUtil.USER;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author karthikeyan
 */

public class ECFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;
        Date d=new Date();
        resp.setHeader("Expires", d.toString());
        resp.setDateHeader("Last-Modified", d.getTime());
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
        resp.setHeader("Pragma", "no-cache");
        
        setECParams((HttpServletRequest)request);
		
        chain.doFilter(request, response);
    }
    public void setECParams(HttpServletRequest request){
        Principal p=request.getUserPrincipal();
        if(p!=null){
            request.setAttribute(USER,p.getName());
        }
        String scheme=request.getScheme();
        String portStr="";
        int port=request.getServerPort();
        if(!((scheme.equalsIgnoreCase("https")&&port==443)||
            (scheme.equalsIgnoreCase("http")&&port==80))){
            portStr=":"+port;
        }
        
        request.setAttribute(ECServerUtil.SERVERCONTEXTPATH,scheme+"://"+request.getServerName()+
                    portStr+request.getContextPath());
        request.setAttribute(ECServerUtil.CONTEXTROOT, ECContextListener.ROOT);
    }
    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
    }
    
}
