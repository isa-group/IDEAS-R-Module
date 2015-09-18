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

    public final static String EXECUTE_SCRIPT = "executeScript";
    public final static String EXECUTE_SCRIPT2 = "executeScript2";
    public final static String LINT = "lint";
    public final static String END_SESSION = "endsession";
    public static final Object DELETE_TEMP = "deleteTemp";
    public String tempD;
    String host = "R://localhost:6311";
    public String uri;
    public Rsession s;
    public String[] plots;
    public Rsession copy;
    public PrintStream ps;
    public ByteArrayOutputStream baos;
    public Boolean isConnected = false;
    public Integer PID;
	public List<String> tempsDirectories;
    Set<String> nonListedVariables=Sets.newHashSet("savegraphs","?");

    public RDelegate(Rsession s, PrintStream ps, ByteArrayOutputStream baos) {
        super();
        if (s != null) {
            this.s = s;
            this.ps = ps;
            this.baos = baos;
        } else {
            try {
                RserverConf c = RserverConf.parse(host);                
                this.baos = new ByteArrayOutputStream();
                this.ps = new PrintStream(this.baos);
                this.s = Rsession.newInstanceTry(this.ps, c);
                tempsDirectories= new ArrayList<String>();
                List<String> setup=new ArrayList<String>();
                setup.add("option(error=function() NULL)");
                setup.add("savegraphs <- local({i <- 1; function(){if(dev.cur()>1){filename<- paste('IDEAS-R-OutputFolder/SavedPlot',i,'.jpg',sep=\"\");file.create(filename);jpeg( file=filename ); i <<- i + 1; }}})");
                setup.add("setHook('before.plot.new', savegraphs )");
                setup.add("setHook('before.grid.newpage', savegraphs )");                
                for(String command:setup)
                    this.s.eval(command);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                //TODO: hay que poner gestón de excepciones.
            }

        }
        copy = this.s;
    }
    /* private String readFile(File file) {
     String res=""; 
     File archivo = file;
     FileReader fr = null;
     BufferedReader br = null;
    	 
     try {
     fr = new FileReader (archivo);
     br = new BufferedReader(fr);
    	 
     // Lectura del fichero
     String linea;
     while((linea=br.readLine())!=null)
     res+=linea;
     }
     catch(Exception e){
     e.printStackTrace();
     }finally{  	         
     try{                    
     if( null != fr ){   
     fr.close();     
     }                  
     }catch (Exception e2){ 
     e2.printStackTrace();
     }
     }
    	   
     return res;
     }*/

    public AppResponse endSession() {
        AppResponse res;

        if (PID.equals(-1)) {
            res = endSession2();
        } else {
            try {
                Rsession killer = Rsession.newInstanceTry(System.out, null);
                killer.eval("tools::pskill(" + this.PID + ")");
                killer.eval("tools::pskill(" + this.PID + ", tools::SIGKILL)");
//              Integer suicide=s.eval("Sys.getpid()").asInteger();
//             killer.eval("tools::pskill("+ suicide+ ", tools::SIGKILL)");

                killer.end();
               // killer.connection.shutdown();

            } catch (Exception e) {
                e.printStackTrace();
            }
            res = endSession2();
            PID = -1;
        }
        return res;
    }

    public AppResponse endSession2() {
        AppResponse res = new AppResponse();

        try {
            this.s.rmAll();
            this.baos.reset();
            this.s.close();
            //this.s.end();
            deleteTemp();
            res.setMessage("Session correctly ended.");
            res.setStatus(Status.OK);

        } catch (Exception e) {
            res.setMessage("Session couldn't be ended.");
            res.setStatus(Status.ERROR);
            e.printStackTrace();

        }

        return res;
    }

    public AppResponse executeScript(String content, String fileUri) {
        AppResponse response = constructBaseResponse(fileUri);
        uri = fileUri;
        //Create the Temporary Directory
        if (!tryConnection(this.s)) {
            s.close();
            RDelegate r = new RDelegate(null, null, null);
            this.s = r.s;
            this.baos = r.baos;
            this.ps = r.ps;

        }
        WorkspaceSync ws = new WorkspaceSync(this.s);
        tempD = ws.setTempDirectory();
        this.tempsDirectories.add(tempD);

        if (tempD != null) {
            response.setMessage("executing R script");
            response.setContext(tempD);
        } else {
            response.setMessage("No se pudo crear el directorio temporal.");
            response.setStatus(Status.ERROR);
        }

        return response;
    }

    public AppResponse executeScript2(String content, String fileUri) {
        AppResponse response = constructBaseResponse(fileUri);
        try {
            //Execute Script 

            try {
                PID = s.eval("Sys.getpid()").asInteger();

            } catch (Exception e) {

                e.printStackTrace();
            }
            baos.reset();
            s.eval(content);

            String f = baos.toString("UTF-8");
            s.eval("graphics.off()");
            plots = getPlots(tempD);
            String htmlMessage = "<pre>" + cleanMessage(f, content) + "</pre>";
            response.setHtmlMessage(htmlMessage);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            response.setMessage(e.getMessage());
            response.setStatus(Status.ERROR);
        }

        return response;
    }

    public AppResponse lintScript(String content, String fileUri) {
        AppResponse response = constructBaseResponse(fileUri);

        try {

            File f = savecontentToTempFile(content);

            if (!s.isPackageInstalled("lintr", "0.2.0")) {
                s.installPackage("lintr", true);
            }
            if (!s.isPackageLoaded("lintr")) {
                s.loadPackage("lintr");
            }
            String command = "lintr::lint(\"" + f.getAbsolutePath().replace("\\", "\\\\\\\\") + "\")";
            REXPGenericVector result = (REXPGenericVector) s.eval(command);
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
            response.setMessage(e.getMessage());
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
    /*private File savecontentToTempFile2(String content) throws IOException {        
     UUID uuid=UUID.randomUUID();
     //create a temp file
     File temp = File.createTempFile(uuid.toString(), ".R");

     try ( //write it
     BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
     bw.write(content);
     }
     return temp;            
     }*/

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
            REXP rexp=s.eval("getwd()");
            if(rexp!=null && !(rexp instanceof REXPNull))
                System.out.println(rexp.asString());
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
        boolean noerror=true;
        for(String tem:this.tempsDirectories){
        	noerror=noerror&&WorkspaceSync.deleteTemp(tem);
        }
        if(noerror){
        response.setStatus(Status.OK);
       
        }else{
        	response.setStatus(Status.ERROR); 	
        }
        response.setMessage("Execution finished");
        return response;
    }

    public Rsession getSession() {

        return s;
    }

    public String[] getEnvironmentVariables() {
        Set<String> vars=Sets.newHashSet(s.ls());
        vars.removeAll(nonListedVariables);
        return vars.toArray(new String[0]);
    }

    public  String[] getPlots() {
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

    private String[] getVariablesValues(Rsession s, String[] var) throws REXPMismatchException {
        List<String> res = Lists.newArrayList();        
        if (var != null) {
            int index = 0;
            for (String variable : var) {
                if (!nonListedVariables.contains(variable)) {
                    try
                    {
                        String st = s.asHTML("print(" + variable + ")");
                        st = st.replace("<html>", "");
                        st.replace("</html>", "");
                        st = st.substring(0, (st.length() / 2) - 1);
                        String[] spl = st.split("(<br/>)");
                        String r;
                        if (isDataFrame(variable)) {
                            r = DataFrameHTML(spl, index);
                        } else {

                            r = "<div class=\"value\"><p>" + spl[0] + "</p><div class=\"extendable\" id=\"value" + index + "\">";
                            for (int i = 1; i < spl.length; i++) {
                                r += spl[i] + "<br/>";
                            }
                            r += "</div></div>";
                        }
                        res.add(r);
                        index++;
                    }catch(Exception ex){res.add(ex.getMessage());System.out.println("EXCEPTION in RInspector!:"+ex.getMessage());}
                } else {
                    res.add("<div class=\"extendable\" id=\"value" + index + "\"> no value found.</div>");
                }
            }
        }
        return res.toArray(new String[0]);
    }

    public String DataFrameHTML(String[] lines, int index) {
        String res = "";
        res += "<div class=\"value\">";
        res += "<table class=\"table table-hover extendable\" id=\"value" + index + "\">";

        int i = 0;

        for (String line : lines) {
            if (i != 0) {
                res += "<tr>";
                String[] spl = line.substring(1).trim().replaceAll("( {2,})", ",").split("(,)");
                for (String piece : spl) {
                    res += "<td>" + piece + "</td>";
                }
                res += "</tr>";
            } else {

                res += "<tr>";
                String[] spl = line.replace('\t', '\u0020').trim().split("( )");
                for (String piece : spl) {
                    res += "<th>" + piece + "</th>";
                }
                res += "</tr>";
            }

            i++;
        }

        res += "</table></div>";

        return res;
    }
   

    public String[] getPlots(String temp) {
        List<String> res = Lists.newArrayList();
        try {
            File t = new File(temp);
            File o = t.toPath().resolve("IDEAS-R-OutputFolder").toFile();

            for (String f : o.list()) {
                if (f.contains(".jpg")) {
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
    
}
