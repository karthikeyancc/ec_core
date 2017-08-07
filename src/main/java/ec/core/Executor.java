package ec.core;

import ec.core.bean.DownloadFileParams;
import ec.core.bean.HttpClientParams;
import ec.core.bean.Instruction;
import ec.core.bean.Param;
import ec.core.bean.SubTask;
import ec.core.bean.Task;
import ec.core.bean.UploadFileParams;
import ec.core.bean.type.CallType;
import ec.core.servlet.ECContextListener;
import ec.core.util.ECServerUtil;
import ec.core.util.MailUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Executor {

    private static final Logger LOGGER = Logger.getLogger(Executor.class.getName());

    private ScriptEngineManager scriptManager = new ScriptEngineManager();
    private ScriptEngine engine = scriptManager.getEngineByName("JavaScript");

    private QueryRunner runner = new QueryRunner();
    private boolean subTaskCondition = true;
    private long subTaskLoopCount = 1;
    private int subTaskLoopIndex = 0;
    private boolean instructionCondition = true;
    private long instructionLoopCount = 1;
    private int instructionLoopIndex = 0;
//    private static final String DS = "datasource";
//    private static final String TC = "taskCache";
    private JSONObject jsondata;
    private Connection con;

    public String execute(String taskId,final String data,final Map<String, Object> map) throws JSONException {
        JSONObject sb = new JSONObject();
        Object dso = map.get(ECServerUtil.DS);
        if (dso == null) {
            sb.put("error", "data source is invalid");
        } else {
            try (final Connection con = ((DataSource) dso).getConnection()) {
                LOGGER.log(Level.INFO, "task id from client is " + taskId);
                Object tco = map.get(ECServerUtil.DS);
                if (tco == null) {
                    sb.put("error", "task list is invalid");
                } else {
                    final Task t = (Task) ((HashMap<String, Task>) map.get(ECServerUtil.TC)).get(taskId);
                    if (t == null) {
                        sb.put("error", "error - no task with id " + taskId);
                    } else {
                        if(t.isAsync()){
                            new AsyncRun(t, data, map).start();
                        }else{
                            run(con, t, data, map);
                        }
                        generateResponse(t, sb);
                    }
                }
            } catch (NumberFormatException | SQLException | ECException nfe) {
                LOGGER.log(Level.SEVERE, "exception ", nfe);
                sb.put("error", nfe.getMessage());
            }
        }
        return sb.toString();
    }

    private JSONObject loadRequestParameter(HttpServletRequest req) throws JSONException, IOException, ServletException, ScriptException {
        JSONObject jso= new JSONObject();
        if(req!=null){
            Enumeration<String> iter=req.getParameterNames();
            while(iter.hasMoreElements()){
                String e=iter.nextElement();
                jso.put(e, req.getParameter(e));
                engine.eval("data['"+e+"']='"+req.getParameter(e)+"'");
            }
            if(req.getContentType()!=null && req.getContentType().toUpperCase().startsWith("MULTIPART")){
                for(Part p:req.getParts()){
                    String fn=p.getSubmittedFileName();
                    if(fn!=null){
                        jso.put(p.getName()+"_filename",fn);
                        engine.eval("data['"+p.getName()+"_filename']='"+fn+"'");
                    }
                }
            }
        }
        return jso;
    }
    private class AsyncRun extends Thread{
        Task task; 
        String data;
        Map<String, Object> map;
        AsyncRun(Task ta, String da, Map<String, Object> ma)
        {
            this.task=ta;
            this.data=da;
            this.map=ma;
        }
        public void run(){
            Object dso = map.get(ECServerUtil.DS);
            try (final Connection con = ((DataSource) dso).getConnection()) {
                Executor.this.run(con,task,data,map);
            }catch(Exception e){
                LOGGER.log(Level.SEVERE, "exception ", e);
            }
        }
    }
    private void run(final Connection con1, final Task t, final String data, final Map<String, Object> map) throws SQLException, ECException {
        con1.setAutoCommit(false);
        process(t, data, con1, map);
        con1.commit();
        con1.close();
    }

    private void generateResponse(Task t, JSONObject res) throws JSONException {
        if (t.getQuitMessage() != null) {
            res.put("result", t.getQuitMessage());
        } else {
            res.put("result", "success");
        }
        String qsp = t.getResponseParams();
        if (qsp != null) {
            JSONObject resp = new JSONObject();
            String qp[] = qsp.split(",");
            for (String q : qp) {
                resp.put(q, jsondata.get(q));
            }
            res.put("shared", resp);
        }
        if (t.getReloadURL() != null) {
            res.put("reloadURL", t.getReloadURL());
        }
        if (t.getForwardURL() != null) {
            res.put("forwardURL", t.getForwardURL());
        }

    }

    public void process(Task t, String data, Connection c, Map<String, Object> map) throws ECException {
        try {
            HttpServletRequest req=(HttpServletRequest)map.get(ECServerUtil.REQ);
            ScriptContext context=engine.getContext();
            if (data == null) {
                context.setAttribute("data", engine.eval("eval({})"), ScriptContext.GLOBAL_SCOPE);
                jsondata = loadRequestParameter(req);
            } else {
                jsondata = new JSONObject(data);
                context.setAttribute("data", engine.eval("eval("+data+")"), ScriptContext.GLOBAL_SCOPE);
            }
            String user = (String) map.get(ECServerUtil.USER);
            jsondata.put(ECServerUtil.USER, user);
            jsondata.put(ECServerUtil.CONTEXTROOT, ECContextListener.ROOT);
            con = c;
            LOGGER.log(Level.INFO, "data object type = " + jsondata);
            int stindex = 0;
            for (SubTask st : t.getSubTasks()) {
                LOGGER.log(Level.INFO, "sub task id = " + st.getId());
                subTaskCondition = eval(st.getStCondition(), st.getStConditionType(), st.getSubTaskConditionParams(), stindex, true);
                LOGGER.log(Level.INFO, "sub task can proceed  = " + subTaskCondition);
                if (subTaskCondition) {
                    subTaskLoopCount = eval(st.getStLoop(), st.getStLoopType(), st.getSubTaskLoopParams(), stindex, 1L);
                    LOGGER.log(Level.FINEST, "sub task loop count  = " + subTaskLoopCount);
                    for (subTaskLoopIndex = 0; subTaskLoopIndex < subTaskLoopCount; subTaskLoopIndex++) {
                        jsondata.put("subTaskLoopIndex", subTaskLoopIndex);
                        processInstructions(st.getInstructions(), subTaskLoopIndex, map);
                    }
                } else if (st.isQuitOnInvalidCondition()) {
                    throw new ECException(st.getQuitMessage());
                }
                stindex++;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "error while processing task " + t.getId(), e);
            if(e instanceof SQLException){
                SQLException se=(SQLException)e;
                throw new ECException("sql error code"+se.getErrorCode()+':'+se.getMessage());
            }else{
                throw new ECException(e);
            }
        }
    }

    private void processInstructions(List<Instruction> insList, int subtaskloopindex, Map<String, Object> map) throws ScriptException, JSONException, SQLException, ECException, IOException, ServletException {
        for (Instruction ins : insList) {
            LOGGER.log(Level.FINEST, "instruction id " + ins.getId());
            try {
                instructionCondition = eval(ins.getOpCondition(), ins.getOpConditionType(), ins.getOperationConditionParams(), subtaskloopindex, true);
                LOGGER.log(Level.FINEST, "instructionCondition = " + instructionCondition);
                if (instructionCondition) {
                    instructionLoopCount = eval(ins.getOpLoop(), ins.getOpLoopType(), ins.getOperationLoopParams(), subtaskloopindex, 1L);
                    LOGGER.log(Level.FINEST, "instruction loop count = " + instructionLoopCount);
                    for (instructionLoopIndex = 0; instructionLoopIndex < instructionLoopCount; instructionLoopIndex++) {
                        jsondata.put("loopIndex", instructionLoopIndex);
                        evaluateInstruction(ins, instructionLoopIndex, map);
                    }
                } else if (ins.isQuitOnInvalidCondition()) {
                    throw new ECException(ins.getQuitMessage());
                }
            } catch (ScriptException | ECException | JSONException | SQLException | IOException | ServletException e) {
                if (ins.isQuitOnException()) {
                    throw e;
                } else {
                    LOGGER.log(Level.SEVERE, "error while executing instruction " + ins, e);
                }
            }

        }
    }

    private void evaluateInstruction(Instruction ins, int loopindex, Map<String, Object> map)
            throws ScriptException, ECException, JSONException, SQLException, IOException, ServletException {
        LOGGER.log(Level.INFO, "ins =  " + ins.getOperation() + "  and its type is " + ins.getType());
        List<Param> opParams = ins.getOperationParams();
        String save = null;
        switch (ins.getType()) {
            case JS_EXPRESSION: {
                save = evalJS(ins.getOperation(), opParams, loopindex);
                break;
            }
            case JAVA_EXPRESSION: {
                CustomProcess cp = null;
                try {
                    Class z = Class.forName(ins.getOperation().trim());
                    cp = (CustomProcess) z.newInstance();
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new ECException(e);
                }
                if (cp != null) {
                    cp.processData(con, jsondata, loopindex, map);
                }
                break;
            }
            case SQL: {
                save = runner.query(con, ins.getOperation(), findParams(jsondata, opParams, loopindex));
                break;
            }
            case SQL_RECORD: {
                runner.query(con, ins.getOperation(), findParams(jsondata, opParams, loopindex), jsondata,engine);
                LOGGER.log(Level.FINEST,"json data = "+engine.eval("JSON.stringify(data)"));
                break;
            }
            case INSERT: {
                save = runner.insert(con, ins.getOperation(), findParams(jsondata, opParams, loopindex));
                break;
            }
            case UPD_DEL: {
                runner.update(con, ins.getOperation(), findParams(jsondata, opParams, loopindex));
                break;
            }
            case SEL_INS: {
                runner.update(con, ins.getOperation(), findParams(jsondata, opParams, loopindex));
                break;
            }
            case MAIL: {
                sendMail(con, ins, map, loopindex);
                break;
            }
            case HTTP: {
                httpCall(con, ins, map, loopindex);
                break;
            }
            case UPLOAD_FILE: {
                uploadFile(con, ins, map, loopindex);
                break;
            }
            case DOWNLOAD_FILE: {
                downloadFile(con, ins, map, loopindex);
                break;
            }
        }
        if (ins.isSaveResult() && save != null) {
            String id=ins.getSaveResultAs();
            if(id==null){
                id=ins.getSubTaskId() + "." + ins.getId();
            }
            jsondata.put(id, save); 
            engine.eval("data['"+id+"']='"+save+"'");
            LOGGER.log(Level.FINEST,"json data = "+engine.eval("JSON.stringify(data)"));
        }
    }

    private <T> T eval(String expression, CallType expressionType, List<Param> params, int loopindex, T dValue) throws ScriptException, SQLException, JSONException {
        Object val = null;
        Object ret = dValue;
        if (ECServerUtil.isNotBlank(expression)) {
            switch (expressionType) {
                case JS_EXPRESSION: {
                    val = evalJS(expression, params, loopindex);
                    break;
                }
                case SQL: {
                    val = runner.query(con, expression, findParams(jsondata, params, loopindex));
                }
            }
        }
        LOGGER.log(Level.INFO, "exp "+expression+" can proceed for  evaluated value = " + val);
        if (val != null) {
            if (dValue instanceof Boolean) {
                ret = Boolean.parseBoolean(val.toString());
            } else if (dValue instanceof Long) {
                ret = Long.parseLong(val.toString());
            }
        }
        LOGGER.log(Level.INFO, " find count = " + ret);
        return (T) ret;
    }

    private String evalJS(String expression, List<Param> opParams, int loopindex) throws ScriptException, JSONException {
        String ex =replaceParameterValues(expression,opParams, jsondata, loopindex);
        Object val = engine.eval(ex);
        LOGGER.log(Level.INFO," val = " + val + " expression = "+expression);
        String save = null;
        if (val != null) {
            save = val.toString();
        }
        return save;
    }

    public String[] findParams(JSONObject jd, List<Param> params, int loopind) throws JSONException {
        LOGGER.log(Level.FINEST, "find params = " + params);
        String[] s = new String[params.size()];
        int index = 0;
        for (Param param : params) {
            s[index] = getParamValue(jd, param, loopind);
            index++;
        }
        LOGGER.log(Level.INFO, "find param values = " + Arrays.toString(s));
        return s;
    }

   
    private String getParamValue(JSONObject jd, Param param, int loopindex) throws JSONException {
        String val = param.getValue();
        String pname = param.getName();
        if (jd.has(pname)) {
            Object para = jd.get(pname);
            if (para instanceof JSONArray) {
                val = ((JSONArray) para).getString(loopindex);
            } else if (para != null) {
                val = para.toString();
            }
        }
        return val;
    }

    private void sendMail(Connection con, Instruction ins, Map<String, Object> map, int loopindex) throws SQLException, JSONException, IOException, ECException, ScriptException {
        LOGGER.log(Level.INFO, " mailins = " + ins);
        if (ins.getMailParams().isCommitAndStart()) {
            con.commit();
        }
        MailUtil mailer = new MailUtil(this, ins.getMailParams());
        if (ins.getOperation() != null) {
            try (PreparedStatement stmt = con.prepareStatement(ins.getOperation())) {
                runner.setParams(findParams(jsondata, ins.getOperationParams(), loopindex), stmt);
                ResultSet rs = stmt.executeQuery();
                java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                while (rs.next()) {
                    JSONObject obj = new JSONObject();
                    copyJsonObject(jsondata, obj);
                    runner.getRecordJSON(rsmd, obj, rs,engine);
                    LOGGER.log(Level.FINEST, " json obj = " + obj);
                    mailer.sendMail(con, obj, map);
                }
            } catch (SQLException sqle) {
                throw sqle;
            }
        } else {
            mailer.sendMail(con, jsondata, map);
        }
    }

    private void httpCall(Connection con, Instruction ins, Map<String, Object> map, int loopindex) throws ECException {
        HttpClientParams hcp = ins.getHttpClientParams();
        HttpClientBuilder hcb = HttpClientBuilder.create();

        try (CloseableHttpClient hc = hcb.build()) {
            LOGGER.log(Level.FINEST, " httpins = " + ins);
            if (hcp.isCommitAndStart()) {
                con.commit();
            }
            if (hcp.getUsernameParam() != null && hcp.getPasswordParam() != null) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                String us = getParamValue(jsondata, hcp.getUsernameParam(), loopindex);
                String pa = getParamValue(jsondata, hcp.getPasswordParam(), loopindex);
                LOGGER.log(Level.FINEST, "user is  = " + us);
                Credentials credentials = new UsernamePasswordCredentials(us, pa);
                credsProvider.setCredentials(AuthScope.ANY, credentials);
                hcb.setDefaultCredentialsProvider(credsProvider);
            }
            String url = replaceParameterValues(hcp.getUrl(), hcp.getUrlParams(), jsondata, loopindex);
            HttpRequestBase req = null;
            if ("post".equalsIgnoreCase(hcp.getMethod())) {
                HttpPost s = new HttpPost(url);
                if (hcp.getMessage() != null) {
                    String content = replaceParameterValues(hcp.getMessage(), hcp.getMessageParams(), jsondata, loopindex);
                    s.setEntity(new StringEntity(content));
                }
                req = s;
            } else {
                req = new HttpGet(url);
            }
            for (Param p : hcp.getHeaderParams()) {
                req.setHeader(p.getName(), getParamValue(jsondata, p, loopindex));
            }
            CloseableHttpResponse chr = hc.execute(req);
            if (ins.isSaveResult()) {
                String s = EntityUtils.toString(chr.getEntity());
                JSONObject jso = new JSONObject(s);
                copyJsonObject(jso, jsondata);
                LOGGER.log(Level.FINEST, " json obj = " + jsondata);
            }
        } catch (Exception e) {
            throw new ECException(e);
        }
    }

    private void uploadFile(Connection con, Instruction ins, Map<String, Object> map, int loopindex) throws ECException {
        LOGGER.log(Level.INFO, " upload ins = " + ins);
        UploadFileParams ufp=ins.getUploadFileParams();
        try {
            if (ufp.isCommitAndStart()) {
                con.commit();
            }
            HttpServletRequest req=(HttpServletRequest)map.get(ECServerUtil.REQ);
            final Part part=req.getPart(ufp.getField());
            if(part.getInputStream()!=null){
                String dfilename=replaceParameterValues(ufp.getDestFile(), ufp.getDestFileNameParams(), jsondata, loopindex);
                part.write(dfilename);
            }
        } catch (Exception e) {
            throw new ECException(e);
        }
    }

    private void downloadFile(Connection con, Instruction ins, Map<String, Object> map, int loopindex) throws ECException {
        LOGGER.log(Level.INFO, " upload ins = " + ins);
        DownloadFileParams dfp=ins.getDownloadFileParams();
        try {
            if (dfp.isCommitAndStart()) {
                con.commit();
            }
            String dfilename=replaceParameterValues(dfp.getDestFile(), dfp.getDestFileNameParams(), jsondata, loopindex);
            String sfilename=replaceParameterValues(dfp.getSourceFile(), dfp.getSourceFileNameParams(), jsondata, loopindex);
            
            HttpServletResponse res=(HttpServletResponse)map.get(ECServerUtil.RES);
            res.setContentType(dfp.getContentType());
            res.setHeader("Content-Disposition", "attachment; filename=\"" + dfilename+'"');
            Files.copy(Paths.get(sfilename), res.getOutputStream());
        } catch (Exception e) {
            throw new ECException(e);
        }
    }

    private void copyJsonObject(JSONObject src, JSONObject dest) throws JSONException {
        Iterator jsokeys = src.keys();
        while (jsokeys.hasNext()) {
            String k = jsokeys.next().toString();
            dest.put(k, src.getString(k));
        }
    }

    private String replaceParameterValues(String src, List<Param> params, JSONObject obj, int loopindex) throws JSONException {
        LOGGER.log(Level.FINEST, " json obj = " + obj);
        String dest = src;
        if (src != null && params != null) {
            for (Param p : params) {
                dest = dest.replaceAll("\\$\\{" + p.getName() + "\\}", getParamValue(obj, p, loopindex));
            }
        }
        return dest;
    }
}
