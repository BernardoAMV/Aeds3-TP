import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
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
            Musica temp = new Musica(true);
            raf.readBoolean();
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
        raf.seek(pos);
        while(raf.getFilePointer() < raf.length()) {
            pos = raf.getFilePointer();
            boolean resp = raf.readBoolean();
            long tamPos = raf.getFilePointer();
            int tam = raf.readInt();
            if(resp) {
                System.out.println(toString(readFromFile(pos)));
                raf.seek(tam + tamPos);
            } else{
                raf.seek(tamPos + tam);
            }
        }
    }




    protected Musica readFromCSV(long pos) throws IOException, ParseException {
        raf.seek(pos);
        String temp = raf.readLine();
        Musica resp = new Musica();
        String [] data = temp.split(",", 4);
        if(temp.charAt(0) == '\"') {
            temp = temp.substring(1);
            data = temp.split("\"", 2);
            resp.setTitulo(data[0]);
            data = data[1].split(",", 4);
            resp.setRank(Integer.parseInt(data[1]));
            resp.setDataByString(data[2]);
        }   else{
            resp.setTitulo(data[0]);
            resp.setRank(Integer.parseInt(data[1]));
            resp.setDataByString(data[2]);
        }
        if(data[3].charAt(0) == '\"'){
            data[3] = data[3].substring(1);

             data = data[3].split("\"", 2);

             resp.setNome(data[0]);

             data = data[1].split(",", 3);
             resp.setRegiao(data[1]);
            if(data[2] == "")
                resp.setStreams(0);
            else
                resp.setStreams(Integer.parseInt(data[2]));
        }
        else {
            data = data[3].split(",", 3);
            resp.setNome(data[0]);
            resp.setRegiao(data[1]);
            if(data[2] == "")
                resp.setStreams(0);
            else
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

    protected Musica readMusica(int id) throws IOException {
        long pos = 4;
        raf.seek(pos);

        while (raf.getFilePointer() < raf.length()) {
             pos = raf.getFilePointer();
            if (raf.readBoolean()) {
                long actualPos = raf.getFilePointer();
                int size = raf.readInt();
                if (id == raf.readInt()) {
                    return readFromFile(pos);
                } else {
                    raf.seek(actualPos + size);
                    pos = raf.getFilePointer();
                }
            } else {
                raf.seek(raf.getFilePointer() + raf.readInt());
                pos = raf.getFilePointer();
            }
        }

        return null;
    }


    /*private Musica readMusica(int id, long pos) throws IOException {
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
                return readMusica(id,raf.getFilePointer());
            }
        }else {
            raf.seek(raf.getFilePointer() + raf.readInt());
            return readMusica(id, raf.getFilePointer());
        }
        }else
            return null;
    }*/
    protected void deleteMusica(int id) throws IOException {
        raf.seek(4);
        long pos = raf.getFilePointer(); // posicao da lapide
        boolean stop = true; // parada do while, achou e deletou o registro alvo, parou o loop
        while(pos < raf.length() && stop){ // enquanto o arquivo nao acabar
            if(raf.readBoolean()) {
                long posTam = raf.getFilePointer();
                int tam = raf.readInt();// lendo o tamanho do registro
                if (raf.readInt() == id) {
                    raf.seek(pos);
                    raf.writeBoolean(false);
                    stop = false;
                } else {
                    raf.seek(posTam + tam);
                    pos = raf.getFilePointer();
                }
            } else {
                raf.seek(raf.getFilePointer() + raf.readInt());
                pos = raf.getFilePointer();
            }
        }
    }
    public Musica createMusica(String fileName) throws IOException{


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
            return tmp;
        }
        catch (Exception e)
        {
            System.out.println("falha ao criar musica!");
            resp = false;
        }

        return null;
    }

    protected void updateMusica(int id) throws ParseException, IOException {
        Scanner scanner = new Scanner(System.in);
        boolean resp = true;
        String nome, titulo, regiao, data;
        int  rank, streams;
        System.out.println("------- ATUALIZAR MUSICA\n Digite o nome dos artistas: ");
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
        Musica temp = new Musica(false);
        temp.setId(id);
        temp.setTitulo(titulo);
        temp.setRegiao(regiao);
        temp.setNome(nome);
        temp.setDataByString(data);
        temp.setRank(rank);
        temp.setStreams(streams);
        updateMusica(temp);

    }




    private void updateMusica(Musica musica) throws IOException {
            raf.seek(4);
            long pos = raf.getFilePointer();
            boolean parada = true;
            while(pos < raf.length() && parada){
                if(raf.readBoolean()){
                    long posTam = raf.getFilePointer();
                    int tamRegistro = raf.readInt();
                    Musica temp = readFromFile(pos);
                    if(musica.getId() == temp.getId()){
                        if(getObjectSize(musica) == getObjectSize(temp)){
                            writeInFile(pos, musica);
                        } else if(getObjectSize(musica) < getObjectSize(temp)) {
                            writeMusicaByTam(pos,musica, tamRegistro);

                        } else{
                            deleteMusica(musica.getId());
                            writeInFile(raf.length(), musica);

                        }
                        parada = false;
                    }
                    pos = posTam + tamRegistro;
                    raf.seek(pos);

                } else{
                    pos = raf.getFilePointer() + raf.readInt();
                    raf.seek(pos);
                }

            }

    }
    private void writeMusicaByTam(long pos, Musica musica, int tam) throws IOException {
            raf.seek(pos);
            raf.writeBoolean(true);
            raf.writeInt(tam);
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










     }







