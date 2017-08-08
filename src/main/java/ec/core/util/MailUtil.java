/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.core.util;

import ec.core.ECException;
import ec.core.Executor;
import ec.core.QueryRunner;
import ec.core.bean.MailParams;
import ec.core.bean.SelectQuery;
import ec.core.servlet.ECContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;

// import com.amazonaws.AmazonClientException;
// import com.amazonaws.auth.AWSCredentials;
// import com.amazonaws.auth.profile.ProfileCredentialsProvider;
// import com.amazonaws.regions.Region;
// import com.amazonaws.regions.Regions;
// import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
// import com.amazonaws.services.simpleemail.model.Body;
// import com.amazonaws.services.simpleemail.model.Content;
// import com.amazonaws.services.simpleemail.model.Destination;
// import com.amazonaws.services.simpleemail.model.Message;
// import com.amazonaws.services.simpleemail.model.SendEmailRequest;
// import com.amazonaws.auth.BasicAWSCredentials; 
import java.nio.charset.StandardCharsets;

/**
 *
 * @author karthikeyan
 */
public class MailUtil {

    private Properties mailProps;
    private MailParams ins;
    private Executor exec;
    private static final Logger LOG = Logger.getLogger(MailUtil.class.getName());        
    /*private static AmazonSimpleEmailServiceClient client;
    static{
        try {
            client = new AmazonSimpleEmailServiceClient(new ProfileCredentialsProvider().getCredentials());
            client.setRegion(Region.getRegion(Regions.US_WEST_2));
            
        } catch (Exception ex) {
             LOG.log(Level.SEVERE,"aws ses email client instatiation issue",ex);
       }
    }*/
    private MailUtil(){    
    }
    public MailUtil(Executor e, MailParams m) throws IOException {
        ins = m;
        exec = e;
        mailProps = new Properties();
        if ( ECServerUtil.isNotBlank(ins.getPropertyFile())) {
            try (InputStream is = Files.newInputStream(Paths.get(ECContextListener.ROOT + "/" + ins.getPropertyFile()))) {
                mailProps.load(is);
            } catch (IOException ie) {
                throw ie;
            }
        }
        String templateStr = ins.getMessage();
        if (templateStr == null) {
            templateStr = new String(Files.readAllBytes(Paths.get(ECContextListener.ROOT + "/" + ins.getTemplateFile())));
        }
        mailProps.setProperty("templateStr", templateStr);
        LOG.log(Level.INFO, "mail props = " + mailProps);
    }

    public void sendMail(Connection con, JSONObject obj, Map map) throws JSONException, SQLException, ECException {
        addRequestProps(obj, map);
        MailParams send=new MailParams();
        send.setFrom(getMailProperty(obj, ins.getFrom()));
        send.setTo(getMailProperty(obj, ins.getTo()));
        send.setSubject(getMailProperty(obj, ins.getSubject()));
        send.setFormat(getMailProperty(obj, ins.getFormat()));
        send.setMessage(getMailProperty(obj, mailProps.getProperty("templateStr")));
        processGridQueries(con, obj, send);
        //LOG.log(Level.INFO, " mail props = " + p);
        sendMail(send);
        if(ins.getWaitTime()>0){
            try {
                Thread.sleep(ins.getWaitTime());
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, "mail wait exception", ex);
            }
        }
    }

    private void processGridQueries(Connection con, JSONObject obj, MailParams mp) throws JSONException, SQLException {
        List<SelectQuery> gq = ins.getGridQueries();
        if (gq != null && gq.size() > 0) {
            StringBuilder mailcontent = new StringBuilder(mp.getMessage());
            for (SelectQuery s : gq) {
                int ind = mailcontent.indexOf("${" + s.getId() + "}");
                //LOG.log(Level.INFO, " ind = " + ind + " select query = " + s);
                if (ind > 0) {
                    PreparedStatement stmt = con.prepareStatement(s.getOperation());
                    String[] ps = exec.findParams(obj, s.getOperationParams(), 1);
                    QueryRunner.setParams(ps, stmt);
                    ResultSet rs = stmt.executeQuery();
                    StringBuilder sb = new StringBuilder();
                    int rc = 0;
                    while (rs.next()) {
                        rc = 1;
                        String row = s.getDisplaytemplate();
                        for (String dispParam : s.getDispFields().split(",")) {
                            //LOG.log(Level.INFO, " dispParam = " + dispParam + " row = " + row);
                            if (obj.has(dispParam)) {
                                row = row.replaceAll("\\$\\{" + dispParam + "\\}", obj.getString(dispParam));
                            } else if (rs.getString(dispParam) != null) {
                                row = row.replaceAll("\\$\\{" + dispParam + "\\}", rs.getString(dispParam));
                            } else {
                                row = row.replaceAll("\\$\\{" + dispParam + "\\}", "");
                            }
                        }

                        sb.append("<tr>");
                        sb.append(row);
                        sb.append("</tr>");
                        //LOG.log(Level.INFO, "row = " + row);
                    }
                    //LOG.log(Level.INFO, " sb = " + sb);
                    if (rc > 0) {
                        mailcontent.replace(ind, ind + s.getId().length() + 3, sb.toString());
                    }
                }
            }
            mp.setMessage(mailcontent.toString());
        }
    }

/*        private void sendMail(final MailParams props) throws ECException{
        
        LOG.log(Level.FINEST, " props b4 send = " + props.toString());

        Destination destination = new Destination().withToAddresses(new String[]{props.getTo()});
        Content subject = new Content().withData(props.getSubject());
        Content textBody = new Content().withData(props.getMessage());
        Body body = new Body();
        if("text/html".equalsIgnoreCase(props.getFormat())){
            body.withHtml(textBody);
        }else{
            body.withText(textBody);
        }
        Message message = new Message().withSubject(subject).withBody(body);

        SendEmailRequest request = new SendEmailRequest().withSource(props.getFrom()).withDestination(destination).withMessage(message);

        try {
            client.sendEmail(request);
        } catch (Exception ex) {
             throw new ECException(ex);
       }
    }*/
    
    private void sendMail(final MailParams props) throws ECException {
        try {
             Session session = Session.getDefaultInstance(mailProps, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailProps.getProperty("mail.user"), mailProps.getProperty("mail.pwd"));
                }
            }); 
//            Session session = Session.getDefaultInstance(mailProps);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailProps.getProperty("mail.user")));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(props.getTo()));
            message.setSubject(props.getSubject());
            message.setContent(props.getMessage(), props.getFormat());
            message.addHeader("Reply-to", props.getFrom());
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new ECException(e);
        }

    }

    private void addRequestProps(JSONObject obj, Map map) throws JSONException {
        HttpServletRequest request = (HttpServletRequest) map.get(ECServerUtil.REQ);
        if (request != null) {
            addToJSON(request, ECServerUtil.USER, obj);
            addToJSON(request, ECServerUtil.SERVERCONTEXTPATH, obj);
        }
    }

    private void addToJSON(HttpServletRequest req, String prop, JSONObject obj) throws JSONException {
        String p = (String) req.getAttribute(prop);
        if (p != null) {
            obj.put(prop, p);
        }
    }

    private String getMailProperty( JSONObject obj, String initValue) throws JSONException {
        String val = initValue;
        Iterator keys = obj.keys();
        while (keys.hasNext()) {
            String k = (String) keys.next();
            val = val.replaceAll("\\$\\{" + k + "\\}", obj.getString(k));
        }
        return val;
    }
    public static void main(String[] args) throws Exception{
        if(args.length<4){
            LOG.log(Level.INFO,"Usage : java MailUtil <fromAddress> <recipients list file name> <subject file name> <content file name> [<format>]");
            System.exit(1);
        }
        MailUtil m=new MailUtil();
        MailParams p=new MailParams();
        	if(args.length>=5){
                    p.setFormat(args[4]);
		}else{
                    p.setFormat("text/plain");
                }
		p.setSubject(new String(Files.readAllBytes(Paths.get(args[2]))));
        p.setFrom( args[0]);
        p.setMessage(new String(Files.readAllBytes(Paths.get(args[3]))));
        List<String> to= Files.readAllLines(Paths.get(args[1]), StandardCharsets.UTF_8);
        for(String t:to){
            p.setTo(t);
            m.sendMail(p);
            LOG.log(Level.INFO,t);
            Thread.sleep(2000);
        }
    }
}
