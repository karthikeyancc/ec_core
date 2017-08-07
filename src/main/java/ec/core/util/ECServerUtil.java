/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.core.util;

import ec.core.bean.HasChildren;
import ec.core.bean.HasId;
import ec.core.bean.Instruction;
import ec.core.bean.SubTask;
import ec.core.bean.Task;
import ec.core.servlet.ECContextListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author karthikeyan
 */
public class ECServerUtil {
    private static final Logger LOGGER = Logger.getLogger(ECServerUtil.class.getName());
    public static final String DS="datasource";
    public static final String TC="taskCache";
    public static final String REQ="request";
    public static final String RES="response";
    public static final String USER="ecuser";
    public static final String SERVERCONTEXTPATH="eccontextpath";
    public static final String CONTEXTROOT="eccontextroot";
    public static final String ROLE="role";
    public static<S extends HasId> void readFiles(ServletContext servletContext, Class c, String listfileName ,HashMap<String,S> cache) throws JAXBException{
		String fnames=servletContext.getInitParameter(listfileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(c);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                jaxbUnmarshaller.setListener(new Unmarshaller.Listener() {
                    @Override
                    public void afterUnmarshal(Object target, Object parent) {
                        super.afterUnmarshal(target, parent);
                    }
                    public void beforeUnmarshal(Object target, Object parent) {
                        super.beforeUnmarshal(target, parent); 
                        if(parent instanceof Task && target instanceof SubTask){
                            ((SubTask)target).setTaskId(((Task)parent).getProcessid());
                        }else if(parent instanceof SubTask && target instanceof Instruction){
                            SubTask st=(SubTask)parent;
                            Instruction ins=(Instruction)target;
                            ins.setSubTaskId(st.getId());
                            ins.setTaskId(st.getTaskId());
//                            LOGGER.log(Level.INFO,"instruction "+ins.getId()+" \t"+ins.getTaskId() +"\t"+ins.getSubTaskId());

                        }
                    }
                    
});
		LOGGER.fine("list of file name "+fnames);
		String []files=fnames.split(",");
		for (String fname : files){
			LOGGER.fine("current file name "+fname);
			HasChildren tasks=(HasChildren)jaxbUnmarshaller.unmarshal(servletContext.getResourceAsStream(fname.trim()));
            List<S> list=tasks.getChildElements();
			for (S xmle : list) {
				String o=xmle.getId();
				cache.put(o, xmle);
				LOGGER.fine("adding to cache "+o);
			}
		}
	}
    
	public static List<String> listFileContents(ServletContext servletContext, String listfileName) throws IOException{
            String fnames=servletContext.getInitParameter(listfileName);
            List<String> ret=null;
            if(fnames!=null){
                ret= listContextFileContents(fnames);
            }
            return ret;
	}
        public static List<String> listContextFileContents(String fnames) throws IOException{
           return  Files.readAllLines(Paths.get(ECContextListener.ROOT+'/'+fnames), StandardCharsets.UTF_8);
        }
    public static boolean isNotBlank(String s){
        return (s!=null&&s.trim().length()>0);
    }
}
