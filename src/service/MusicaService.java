import java.io.*;
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
import java.util.Scanner;

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

   protected int getObjectSize(Musica musica) throws UnsupportedEncodingException {
        /* Aqui nos criamos a funcao que retorna o tamanho do arquivo:
        4 bytes do id
        4 bytes do rank
        4 bytes das streams
        e o tamanho das strings nome(artistas) e titulo da musica que sao variaveis
        ao pegar o tamanho do array de bytes retornado da funcao "getBytes()" ele retorna o
        numero de bytes da string
        3 bytes da string fixa(regiao)
        4 bytes dos shorts indicando o tamanho das strings variaveis
        e 4 bytes
        */
        return 4+4+4+8+musica.getNome().getBytes("UTF-8").length+musica.getTitulo().getBytes("UTF-8").length+3+4+4;

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
        for(int i = 0; i < 21; i++){
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
        if(musica != null) {
            return "\nID: " + musica.getId() +
                    "\nNome: " + musica.getNome() +
                    "\nTitulo:" + musica.getTitulo() +
                    "\nRegiao: " + musica.getRegiao() +
                    "\nData: " + musica.getData() +
                    "\nRank: " + musica.getRank() +
                    "\nStreams: " + musica.getStreams();
        }else {
            return null;
        }

    }


    protected Musica Read(int id, long pos) throws IOException {
        raf.seek(pos);
        if(raf.getFilePointer() < raf.length()){
        if(raf.readBoolean()){
            long actualPos = raf.getFilePointer();
            int size = raf.readInt();
            if(id == raf.readInt()){
                return readFromFile(pos);
            }
            else{
                raf.seek(actualPos + size);
                return Read(id,raf.getFilePointer());
            }
        }else {
            raf.seek(raf.getFilePointer() + raf.readInt());
            return Read(id, raf.getFilePointer());
        }
        }else
            return null;



    }
    public boolean createMusica(String fileName) throws IOException{


        Scanner scanner = new Scanner(System.in);
        boolean resp = true;
        String nome, titulo, regiao, data;
        int  rank, streams;

        System.out.println("------- CRIAR MUSICA\n Digite o nome da musica: ");
        nome = scanner.nextLine();
        System.out.println("Digite o titulo da musica: ");
        titulo = scanner.nextLine();
        System.out.println("Digite a regiao da musica: ");
        regiao = scanner.nextLine();
        System.out.println("Digite a data da musica: ");
        data = scanner.nextLine();
        System.out.println("Digite o rank da musica: ");
        rank = scanner.nextInt();
        System.out.println("Digite quantas streams tem a musica: ");
        streams = scanner.nextInt();

        /*nome = titulo = regiao = "aaa";
        data = "0000-00-00";
        rank = streams = 1;
        */


        scanner.close();

        try
        {
            MusicaService tmpService = new MusicaService(fileName);
            Musica tmp = new Musica(rank, streams, nome, data, regiao, titulo);

            writeInFile(raf.length(), tmp);
        }
        catch (Exception e)
        {
            System.out.println("falha ao criar musica!");
            resp = false;
        }


        return resp;
    }








     }







