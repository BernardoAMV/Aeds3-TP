import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

public class MusicaService {

    protected final File file;

    protected final RandomAccessFile raf;


    public MusicaService(String file) throws IOException {
        this.file = new File(file);
        if(!this.file.exists()){
            this.file.createNewFile();
        }
        this.raf = new RandomAccessFile(this.file,"rw");

    }
    protected void writeInFile(long pos, Musica musica) throws IOException {
        raf.seek(pos);
        raf.writeBoolean(true);
        raf.writeInt(getObjectSize(musica));
        raf.writeInt(musica.getId());
        raf.writeUTF(musica.getTitulo());
        raf.writeUTF(musica.getNome());
        raf.writeLong(musica.getData().getTime());
        raf.writeInt(musica.getRank());
        raf.writeInt(musica.getStreams());
        // Escrever os dados de texto (nome e região) como strings, limitando a regiao a somente 3 caracteres(String fixa)
        String temp = musica.getRegiao().substring(0,Math.min(3, musica.getRegiao().length()));
        byte [] substring = temp.getBytes();
        raf.write(substring);
    }

   protected int getObjectSize(Musica musica){
        return 4+4+4+8+musica.getNome().getBytes().length+musica.getTitulo().getBytes().length+3;

    }

    protected Musica readFromFile(long pos) throws IOException {

        raf.seek(pos);
        Musica temp = new Musica();
        raf.readByte();
        raf.readInt();
        temp.setId(raf.readInt());
        temp.setTitulo(readString(raf.getFilePointer()));
        temp.setNome(readString(raf.getFilePointer()));
        temp.setData(Date.from(Instant.ofEpochMilli(raf.readLong())));
        temp.setRank(raf.readInt());
        temp.setStreams(raf.readInt());
        temp.setRegiao(byteArrayToString(raf.getFilePointer()));


        return temp;
    }
    protected void readAll() throws IOException {
        long pos = 4;
        for(int i = 0; i < 20; i++){
           System.out.println(toString(readFromFile(pos)));
            pos = raf.getFilePointer();
        }


    }



    protected Musica readFromCSV(long pos) throws IOException, ParseException {
        raf.seek(pos);
        String temp = raf.readLine();
        Musica resp = new Musica();
        String [] data = temp.split(",", 4);
        resp.setTitulo(data[0]);
        resp.setRank(Integer.parseInt(data[1]));
        resp.setDataByString(data[2]);
        String base = data[3];

        if(base.charAt(0) == '\"'){
             base = base.substring(1);

             data = base.split("\"", 2);

             resp.setNome(data[0]);

             data = data[1].split(",", 3);
             resp.setRegiao(data[1]);
             resp.setStreams(Integer.parseInt(data[2]));
        }
        else {
            data = base.split(",", 3);
            resp.setNome(data[0]);
            resp.setRegiao(data[1]);
            resp.setStreams(Integer.parseInt(data[2]));
        }

        return resp;

    }

    protected String byteArrayToString(long pos) throws IOException {
        raf.seek(pos);
        byte [] temp = new byte[3];
        for(int i = 0; i < temp.length; i++){
            temp[i] = raf.readByte();
        }
        String resp = new String(temp, StandardCharsets.UTF_8);
        return resp;


    }
    protected String readString(long pos) throws IOException {
        raf.seek(pos);
        short temp = raf.readShort();
        byte [] newString = new byte[temp];
        for(int i = 0; i < temp; i++){
            newString[i] = raf.readByte();
        }
        return new String(newString, StandardCharsets.UTF_8);

    }




   public String toString(Musica musica){
        return "\nID: " + musica.getId() +
                "\nNome: "+musica.getNome()+
                "\nTitulo:"+musica.getTitulo()+
                "\nRegiao: "+musica.getRegiao() +
                "\nData: "+ musica.getData() +
                "\nRank: "+musica.getRank() +
                "\nStreams: "+musica.getStreams();

    }







     }







