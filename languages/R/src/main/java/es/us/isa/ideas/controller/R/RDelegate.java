package es.us.isa.ideas.controller.R;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;
import org.math.R.RserverConf;
import org.math.R.Rsession;
import org.rosuda.REngine.REXPGenericVector;
import es.us.isa.ideas.common.AppResponse;

import es.us.isa.ideas.common.AppResponse.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPNull;

public class RDelegate {
    public final static String[] PLOT_EXTENSIONS  ={".jpg",".jpeg",".png",".gif",".svg",".bmp",".tiff"};
    public final static String EXECUTE_SCRIPT = "executeScript";
    public final static String EXECUTE_SCRIPT2 = "executeScript2";
    public final static String LINT = "lint";
    public final static String END_SESSION = "endsession";
    public final static String DELETE_TEMP = "deleteTemp";
    public final static String DEFAULT_R_REPO="local({r <- getOption(\"repos\");\n" +
                                                "       r[\"CRAN\"] <- \"http://cran.us.r-project.org\"; \n" +
                                                "       options(repos=r)})";
    public String tempD;
    String host = "R://localhost:6311";
    public String uri;
    public Rsession session;
    public String[] plots;
    public PrintStream ps;
    public ByteArrayOutputStream baos;
    public Boolean isConnected = false;
    public Integer PID;
    public List<String> tempsDirectories;
    Set<String> nonListedVariables = Sets.newHashSet("savegraphs", "?","packs");

    public RDelegate() {
        initialize();
    }

    private void initialize() {
        RserverConf c = RserverConf.parse(host);
        this.baos = new ByteArrayOutputStream();
        this.ps = new PrintStream(this.baos);
        this.session = Rsession.newInstanceTry(this.ps, c);
        tempsDirectories = new ArrayList<String>();
        List<String> setup = new ArrayList<String>();                
        setup.add(DEFAULT_R_REPO);                
        for (String command : setup) {
            this.session.eval(command);
        }
        try {
            PID = session.eval("Sys.getpid()").asInteger();
        } catch (Exception e) {
            e.printStackTrace();
            PID = null;
        }
    }        
    public AppResponse endSession() {
        AppResponse res;
        if (PID != null && !PID.equals(-1)) {
            killPID();
        }
        return closeSession();
    }

    private void killPID() {
        try {
            Rsession killer = Rsession.newInstanceTry(System.out, null);
            killer.eval("tools::pskill(" + this.PID + ")");
            killer.eval("tools::pskill(" + this.PID + ", tools::SIGKILL)");
            killer.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PID = -1;
    }

    public AppResponse closeSession() {
        AppResponse res = new AppResponse();
        try {
            this.session.rmAll();
            this.baos.reset();
            this.session.close();
            deleteTemp();
            res.setMessage("Session correctly ended.");
            res.setStatus(Status.OK);
        } catch (Exception e) {
            res.setMessage("Session couldn't be ended. ERROR:'" + e.getMessage() + "'");
            res.setStatus(Status.ERROR);
            e.printStackTrace();
        }
        return res;
    }

    public AppResponse prepareScriptExecution(String content, String fileUri) {
        AppResponse response = constructBaseResponse(fileUri);
        uri = fileUri;
        //Create the Temporary Directory
        if (!tryConnection(this.session)) {
            closeSession();
            initialize();
        }
        WorkspaceSync ws = new WorkspaceSync(this.session);
        tempD = ws.setTempDirectory();
        this.tempsDirectories.add(tempD);

        if (tempD != null) {
            response.setMessage("Temporary directory created!. Executing R script!");
            response.setContext(tempD);
        } else {
            response.setMessage("Unable to create temporary directory.");
            response.setStatus(Status.ERROR);
        }

        return response;
    }

    public AppResponse executeScript(String content, String fileUri) {
        AppResponse response = constructBaseResponse(fileUri);
        try {
            baos.reset();
            session.eval("jpeg(filename ='"+WorkspaceSync.OUTPUT_FOLDER+"/Rplot%03d.jpeg')");
            session.eval(content);            
            String f = baos.toString("UTF-8");
            session.eval("graphics.off()");
            plots = getPlots(tempD);
            String htmlMessage = "<pre>" + cleanMessage(f, content) + "</pre>";
            response.setHtmlMessage(htmlMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setMessage("ERROR: " + e.getMessage());
            response.setStatus(Status.ERROR);
        }

        return response;
    }

    public AppResponse lintScript(String content, String fileUri) {
        AppResponse response = constructBaseResponse(fileUri);
        try {
            File f = savecontentToTempFile(content);

            if (!session.isPackageInstalled("lintr", "0.2.0")) {
                session.installPackage("lintr", true);
            }
            if (!session.isPackageLoaded("lintr")) {
                session.loadPackage("lintr");
            }
            String command = "lintr::lint(\"" + f.getAbsolutePath().replace("\\", "\\\\\\\\") + "\")";
            REXPGenericVector result = (REXPGenericVector) session.eval(command);
            response.setAnnotations(ErrorBuilder.buildErrorStructure(result.asList()));
            if (result.length() == 0) {
                response.setStatus(Status.OK);
                response.setMessage("Everything is Ok!");
            } else {
                response.setStatus(Status.OK_PROBLEMS);
                response.setMessage(String.valueOf(result.length()) + " issues were found!");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setMessage("ERROR: " + e.getMessage());
            response.setStatus(Status.ERROR);
        }

        return response;
    }

    private AppResponse constructBaseResponse(String fileUri) {
        AppResponse appResponse = new AppResponse();
        appResponse.setFileUri(fileUri);
        appResponse.setStatus(Status.OK);
        return appResponse;
    }

    private File savecontentToTempFile(String content) throws IOException {
        UUID uuid = UUID.randomUUID();
        //create a temp file
        File temp = File.createTempFile(uuid.toString(), ".tmp");

        try ( //write it
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write(content);
        }
        return temp;
    }    

    private String cleanMessage(String f, String content) {
        String f2 = f.replace("[eval] " + content, "");
        f2 = f2.replaceAll("(.eval).{1,}", "");
        String f3 = f2.replaceFirst("(org).{1,}", "");
        f3 = f3.replace("(!!) Rserve R://localhost:6311 is not accessible.\n ! null\r\nTrying to spawn R://localhost:6311\r\nEnvironment variables:\n  R_HOME=C:\\Program Files\\R\\R-3.2.1\\\r\nchecking Rserve is available... \r\n  ok\r\nstarting R daemon... R://localhost:6311\r\n  ok\r\nLocal Rserve started. (Version 103)\r\n", "");
        String f4 = f3.replaceAll(".{1,}(org.rosuda).{1,}", "");
        f4 = f4.replaceAll("(org.).{1,}", "");
        return f4;
    }

    private boolean tryConnection(Rsession s) {
        boolean res;
        try {
            baos.reset();
            REXP rexp = s.eval("getwd()");
            if (rexp != null && !(rexp instanceof REXPNull)) {
                System.out.println(rexp.asString());
            }
            String f = baos.toString("UTF-8");
            res = !f.contains("[exception]");
        } catch (Exception e) {
            e.printStackTrace();
            res = false;
        }

        return res;
    }

    public AppResponse deleteTemp() {
        AppResponse response = new AppResponse();
        boolean noerror = true;
        for (String tem : this.tempsDirectories) {
            noerror = noerror && WorkspaceSync.deleteTemp(tem);
        }
        if (noerror) {
            response.setStatus(Status.OK);

        } else {
            response.setStatus(Status.ERROR);
        }
        response.setMessage("Execution finished");
        return response;
    }

    public Rsession getSession() {
        return session;
    }

    public String[] getEnvironmentVariables() {
        Set<String> vars = Sets.newHashSet(session.ls());
        vars.removeAll(nonListedVariables);
        return vars.toArray(new String[0]);
    }

    public String[] getPlots() {
        return plots;
    }

    public String getUri() {
        return uri;
    }

    /**
     * Gives the values of the variables in HTML with the following format:
     * <div class="value"> first line<br/> second line <br/>..... </div >
     *
     */
    public String[] getVariablesValues(String[] vars) throws REXPMismatchException {
        return getVariablesValues(getSession(), vars);
    }

    

    public String[] getPlots(String temp) {
        List<String> res = Lists.newArrayList();
        try {
            File t = new File(temp);
            File o = t.toPath().resolve(WorkspaceSync.OUTPUT_FOLDER).toFile();

            for (String f : o.list()) {
                for(String extension:PLOT_EXTENSIONS)
                if (f.contains(extension)) {
                    String[] spl = f.split("/");
                    res.add(spl[spl.length - 1]);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return res.toArray(new String[0]);
    }

    public String getFileUri() {
        String res = getUri();
        String[] sp = res.split("/");

        return sp[0] + "/" + sp[1];
    }

    public List<String> getTempsDirectories() {
        return tempsDirectories;
    }

    public boolean isDataFrame(String variable) {
        return isDataFrame(getSession(), variable);
    }

    private boolean isDataFrame(Rsession s, String variable) {

        boolean res = false;
        try {
            res = s.silentlyEval("is.data.frame(" + variable + ")").asString().contains("TRUE");
        } catch (REXPMismatchException e) {
            System.out.println("se elevó excepción en la evaluación del comando de comprobación.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    private String[] getVariablesValues(Rsession s, String[] var) throws REXPMismatchException {
        List<String> res = Lists.newArrayList();
        if (var != null) {
            int index = 0;
            for (String variable : var) {
                if (!nonListedVariables.contains(variable)) {
                    try {
                        StringBuilder r = new StringBuilder();
                        r.append("<div class=\"value\">");
                        if (isDataFrame(variable)) {
                            r.append(dataFrameAsHTML(variable, index));
                        } else {
                            r.append(variableAsHTML(variable, index));
                        }
                        r.append("</div>");
                        res.add(r.toString());
                        index++;
                    } catch (Exception ex) {
                        res.add(ex.getMessage());
                        System.out.println("EXCEPTION in RInspector!:" + ex.getMessage());
                    }
                } else {
                    res.add("<div class=\"extendable\" id=\"value" + index + "\"> no value found.</div>");
                }
            }
        }
        return res.toArray(new String[0]);
    }

    private String dataFrameAsHTML(String variable, int index) {
        StringBuilder result = new StringBuilder();
        String alternativeNEWLINE = "NEWLINE!!";
        result.append("<table class=\"table table-hover extendable\" id=\"value" + index + "\">");
        // EXECUTE R CODE TO GET THE DATAFRAME AS CSV STRING:
        session.silentlyEval("hiddenCon <- textConnection(\"hiddenCSVOutput\", \"w\")");
        session.silentlyEval("write.csv(file=hiddenCon," + variable + ",row.names=F,eol = \"" + alternativeNEWLINE + "\")");
        session.silentlyEval("close(hiddenCon)");
        //s.silentlyEval("hiddenCSVOutput <- capture.output("+variable+", stdout(), row.names=F)");
        String csv = session.asString("hiddenCSVOutput");
        String[] rows = csv.split(alternativeNEWLINE);
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i].trim();
            if (!"\"".equals(row)) {
                if (row.startsWith("[") && row.contains("]")) {
                    row = row.substring(row.indexOf("\"") + 1);
                }
                row = row.replace("\\\"", "");
                result.append("<tr>");
                String[] columns = row.split(",");
                for (String item : columns) {
                    if (i == 0) {
                        result.append("<th>");
                    } else {
                        result.append("<td>");
                    }
                    result.append(item);
                    if (i == 0) {
                        result.append("</th>");
                    } else {
                        result.append("</td>");
                    }
                }
                result.append("</tr>");
            }
        }
        result.append("</table>");
        // REMOVE THE AUXILIARY VARIABLES FROM R ENVIRONMENT
        session.silentlyEval("remove (\"hiddenCSVOutput\")");
        session.silentlyEval("remove (\"hiddenCon\")");
        return result.toString();
    }

    private String variableAsHTML(String variable, int index) {
        String st = session.asHTML("print(" + variable + ")");
        st = st.replace("<html>", "");
        st.replace("</html>", "");
        st = st.substring(0, (st.length() / 2) - 1);
        String[] spl = st.split("(<br/>)");
        StringBuilder result = new StringBuilder();
        result.append("<p>" + spl[0] + "</p>");
        result.append("<div class=\"extendable\" id=\"value" + index + "\">");
        for (int i = 1; i < spl.length; i++) {
            result.append(spl[i] + "<br/>");
        }
        result.append("</div>");
        return result.toString();
    }

}
