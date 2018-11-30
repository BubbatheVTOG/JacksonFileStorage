
import java.util.*;
import java.io.*;
import java.util.zip.CRC32;


public class Jackson {

	private HashMap<Long,Object> FileMap = new HashMap<>();

	public static void main(String[] args) throws Exception{
		new Jackson();
	}

	Jackson() throws Exception {
		//Step 0 fill it.
		this.fill();
		this.print();
		//Step 1 query it.
		this.containsFile(new File("."));
		//Step 2 mark and sweep to clean?
	}

	public void fill() throws Exception{
		File StartingDir = new File(".");
		//Sanity check...
		if(StartingDir.isDirectory()){
			File[] Files = StartingDir.listFiles();
			//Place files in map.
			for(File file : Files){
				//check that it isn't already added.
				if(!FileMap.containsKey(file.length())){
					FileMap.put(file.length(),file);
				}else{
					//Collisions of file lengths are unavoidable.
					//Need to do this:
					//Generate CRC32 and put it in map of map.
					HashMap<Long,Object> secondLayer = new HashMap<>();
					long fileHash = this.getFileHash(file);
					secondLayer.put(fileHash,file);
					FileMap.put(fileHash, secondLayer);
				}
			}
		}
	}

	public boolean containsFile(File file) throws Exception{
		if(FileMap.containsKey(file.length())){
			return true;
		}else if(FileMap.containsKey(this.getFileHash(file))){
			return true;
		}else{
			return false;
		}
	}

	public void print(){
		for(Long id1 : FileMap.keySet()){
			if(FileMap.get(id1) instanceof File){
				File fileLayer1 = (File)FileMap.get(id1);
				System.out.println("First layer file: "+fileLayer1.getName());
			} else if(FileMap.get(id1) instanceof HashMap){
				HashMap<Long,Object> curMap = (HashMap<Long,Object>)FileMap.get(id1);
				for(Long id2 : curMap.keySet()){
					File fileLayer2 = (File)curMap.get(id2);
					System.out.println("\tSecond layer file: "+fileLayer2.getName());
				}
			}
		}
	}

	public long getFileHash(File file) throws Exception {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		CRC32 digest = new CRC32();
		int buffer;
		while((buffer = in.read()) != -1){
			digest.update(buffer);
		}
		return digest.getValue();
	}
}


