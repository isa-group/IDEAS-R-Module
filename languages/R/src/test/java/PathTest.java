import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;



public class PathTest {

	public static void main(String[] args) {
		String content= "print(2+2)";
		try {
		File f=	savecontentToTempFile(content);
		System.out.println(f.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static File savecontentToTempFile(String content) throws IOException {        
        UUID uuid=UUID.randomUUID();
    //create a temp file
    File temp = File.createTempFile(uuid.toString(), ".tmp");

        try ( //write it
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            bw.write(content);
        }
        return temp;            
}
}
