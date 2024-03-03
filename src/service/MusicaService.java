import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;

public class MusicaService {

    protected final File file; // arquivo a ser manipulado

    protected final RandomAccessFile raf; // atributo do tipo raf para manipulacao do arquivo


    public MusicaService(String file) throws IOException { // construtor da classe
        this.file = new File(file);
        if(!this.file.exists()){
            this.file.createNewFile();  // se o arquivo passado como parametro nao existir, eh criado um outro
        }
        this.raf = new RandomAccessFile(this.file,"rw");

    }
    protected void writeInFile(long pos, Musica musica) throws IOException { // metodo para escrever no arquivo, recebe a posicao para escrita e um objeto musica para escrever
        raf.seek(pos); // seta o ponteiro na posicao desejada
        raf.writeBoolean(true); // escreve a lapide
        raf.writeInt(getObjectSize(musica)); // tamanho do registro
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

    protected Musica readFromFile(long pos) throws IOException { // metodo para ler do arquivo, passando a posicao como paranetro

        raf.seek(pos);
            Musica temp = new Musica(true); // motivo do uso deste construtor esta na classe musica
            raf.readBoolean(); // ignorando lapide, pois nos metodos usados a lapide ja eh considerada a lapide
            raf.readInt(); // lendo tamanho
            temp.setId(raf.readInt());
            temp.setTitulo(readString(raf.getFilePointer()));
            temp.setNome(readString(raf.getFilePointer()));
            temp.setData(Date.from(Instant.ofEpochMilli(raf.readLong())));
            temp.setRank(raf.readInt());
            temp.setStreams(raf.readInt());
            temp.setRegiao(byteArrayToString(raf.getFilePointer()));
            return temp;
        }



    protected void readAll() throws IOException { // metodo para ler todos os registros no arquivo
        long pos = 4;
        raf.seek(pos);
        while(raf.getFilePointer() < raf.length()) { // enquanto o o ponteiro for menor que o tamanho do arquivo, ou seja, enquanto o arquivo nao acabar
            pos = raf.getFilePointer(); // pos = posicao da lapide
            boolean lapide = raf.readBoolean();
            long tamPos = raf.getFilePointer(); // posicao do tamanho do registro
            int tam = raf.readInt(); // tamanho do registro
            if(lapide) {
                System.out.println(toString(readFromFile(pos))); // se registro existir, ler do arquivo na posicao da lapide
                raf.seek(tam + tamPos); // setando o ponteiro para o proximo registro, tamanho do registro mais a posicao do tamanho do registro
            } else{
                raf.seek(tamPos + tam); // se nao, passar para o proximo registro
            }
        }
    }




    protected Musica readFromCSV(long pos) throws IOException, ParseException { // metodo para passar do arquivo csv para o arquivo binario
        raf.seek(pos);
        String temp = raf.readLine(); // traz toda a linha do arquivo
        Musica resp = new Musica();
        String [] data = temp.split(",", 4); // divide ela em 4, usando a virgula como criterio de separacao
        if(temp.charAt(0) == '\"') { // se o primeiro caractere for uma aspas duplas, significa que tem virgulas no campo
            temp = temp.substring(1); // retirando a aspas duplas
            data = temp.split("\"", 2); // dividindo a string original em 2, titulo da musica/resto
            resp.setTitulo(data[0]); // capturando o titulo da musica
            data = data[1].split(",", 4); // separando o resto
            resp.setRank(Integer.parseInt(data[1])); // capturando o rank
            resp.setDataByString(data[2]);//capturando a data
        }   else{
            resp.setTitulo(data[0]);
            resp.setRank(Integer.parseInt(data[1]));
            resp.setDataByString(data[2]);
        }
        if(data[3].charAt(0) == '\"'){ // se o resto da string comecar com aspas duplas, significa uma lista de valores
            data[3] = data[3].substring(1); // retirando a aspas duplas

             data = data[3].split("\"", 2); // dividindo em 2, nome dos artistas/resto

             resp.setNome(data[0]); // capturando o nome dos artistas

             data = data[1].split(",", 3); // dividindo o resto da string em 3
             resp.setRegiao(data[1]); // capturando regiao
            if(data[2] == "") // se o campo streams estiver vazio, preencher com 0
                resp.setStreams(0);
            else
                resp.setStreams(Integer.parseInt(data[2])); // se nao, capturar
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

    protected String byteArrayToString(long pos) throws IOException { // metodo para ler a string de tamanho fixo
        raf.seek(pos);
        byte [] temp = new byte[3]; // cria um array de bytes para armazenar os bytes da string
        for(int i = 0; i < temp.length; i++){ // le os bytes
            temp[i] = raf.readByte();
        }
        String resp = new String(temp, StandardCharsets.UTF_8); // utiliza um construtor de string para construir a string, em UTF-8
        return resp;


    }
    protected String readString(long pos) throws IOException { // metodo para ler string de tamanho variavel
        raf.seek(pos);
        short temp = raf.readShort(); // le o tamanho da string, que e escrito no inicio dela no arquivo
        byte [] newString = new byte[temp]; // cria o array de bytes do tamanho da string
        for(int i = 0; i < temp; i++){ // preenche o array
            newString[i] = raf.readByte();
        }
        return new String(newString, StandardCharsets.UTF_8); // utiliza um construtor de string para construir a string, em UTF-8

    }




   public String toString(Musica musica){ // metodo simples, apenas para transformar os atributos do objeto musica em uma string
       SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        if(musica != null) {
            return "\nID: " + musica.getId() +
                    "\nNome: " + musica.getNome() +
                    "\nTitulo:" + musica.getTitulo() +
                    "\nRegiao: " + musica.getRegiao() +
                    "\nData: " + format.format(musica.getData()) + // format da data para yyyy-MM-dd
                    "\nRank: " + musica.getRank() +
                    "\nStreams: " + musica.getStreams();
        }else {
            return null;
        }

    }

    protected Musica readMusica(int id) throws IOException { // metodo para Read da musica no arquivo binario
        long pos = 4;
        raf.seek(pos);

        while (raf.getFilePointer() < raf.length()) { // enquanto arquivo nao acabar
             pos = raf.getFilePointer(); // pos = posicao da lapide
            if (raf.readBoolean()) { // se arquivo existir
                long actualPos = raf.getFilePointer(); // posicao do tamanho do registro
                int size = raf.readInt(); // tamanho do registro
                if (id == raf.readInt()) { // se o id for o procurado
                    return readFromFile(pos); // retorna o registro lido, na posicao da lapide
                } else { // se nao for o id procurado
                    raf.seek(actualPos + size); // passa para o proximo registro, posicao do tamanho + tamanho
                    pos = raf.getFilePointer();
                }
            } else { // se registro nao existir, passar para o proximo registro
                raf.seek(raf.getFilePointer() + raf.readInt()); // posicao do tamanho + tamanho
                pos = raf.getFilePointer();
            }
        }

        return null;
    }

    protected void deleteMusica(int id) throws IOException {
        raf.seek(4);
        long pos = raf.getFilePointer(); // posicao da lapide
        boolean stop = true; // parada do while, achou e deletou o registro alvo, parou o loop
        while(pos < raf.length() && stop){ // enquanto o arquivo nao acabar
            if(raf.readBoolean()) {
                long posTam = raf.getFilePointer(); // lendo a posicao do tamanho do registro
                int tam = raf.readInt();// lendo o tamanho do registro
                if (raf.readInt() == id) { // se o id for o procurado
                    raf.seek(pos); // seta o ponteiro para a posicao da lapide
                    raf.writeBoolean(false); // escreve como falso
                    stop = false; // para o loop
                } else { // se nao
                    raf.seek(posTam + tam); // passa para o proximo registro, posicao do tamanho + tamanho
                    pos = raf.getFilePointer();
                }
            } else { // se registro nao existir
                raf.seek(raf.getFilePointer() + raf.readInt());// passa para o proximo registro, posicao do tamanho + tamanho
                pos = raf.getFilePointer();
            }
        }
    }
    public Musica createMusica() throws IOException{ // metodo para criar musica


        Scanner scanner = new Scanner(System.in);
        String nome, titulo, regiao, data;
        int  rank, streams;
        // captura as informacoes do usuario
        System.out.println("------- CRIAR MUSICA\n Digite o nome dos artistas: ");
        nome = scanner.nextLine();
        System.out.println("Digite o titulo da musica: ");
        titulo = scanner.nextLine();
        System.out.println("Digite a regiao da musica: ");
        regiao = scanner.nextLine();
        System.out.println("Digite a data da musica(Ex: 1995-12-25): ");
        data = scanner.nextLine();
        System.out.println("Digite o rank da musica: ");
        rank = scanner.nextInt();
        System.out.println("Digite quantas streams tem a musica: ");
        streams = scanner.nextInt();

        try
        {   // cria o novo objeto
            Musica tmp = new Musica(rank, streams, nome, data, regiao, titulo);

            //escreve o registro no final do arquivo
            writeInFile(raf.length(), tmp);
            return tmp;
        }
        catch (Exception e)
        {
            System.out.println("falha ao criar musica!");
        }

        return null;
    }

    protected void updateMusica(int id) throws ParseException, IOException { // metodo protegido para atualizar musica
        Scanner scanner = new Scanner(System.in);
        String nome, titulo, regiao, data;
        int  rank, streams;
        // captura as informacoes do usuario
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
        Musica temp = new Musica(false); // cria um objeto novo
        temp.setId(id);
        temp.setTitulo(titulo);
        temp.setRegiao(regiao);
        temp.setNome(nome);
        temp.setDataByString(data);
        temp.setRank(rank);
        temp.setStreams(streams);
        //chama a funcao privada
        updateMusica(temp);

    }




    private void updateMusica(Musica musica) throws IOException { // funcao privada para atualizar musica
            raf.seek(4);
            long pos = raf.getFilePointer();
            boolean parada = true;
            while(pos < raf.length() && parada){ // enquando nao estiver no final do arquivo
                if(raf.readBoolean()){ // se registro existir
                    long posTam = raf.getFilePointer(); // posicao do tamanho do registro
                    int tamRegistro = raf.readInt(); // tamanho do registro
                    Musica temp = readFromFile(pos); // traz o registro para um objeto
                    if(musica.getId() == temp.getId()){ // se o id do registro no arquivo for o mesmo requisitado pelo usuario
                        if(getObjectSize(musica) == getObjectSize(temp)){ // se o tamanho dos registros for o mesmo
                            writeInFile(pos, musica); // escreve na posicao do registro do arquivo, o registro atualizado
                        } else if(getObjectSize(musica) < getObjectSize(temp)) { // se for menor
                            writeMusicaByTam(pos,musica, tamRegistro); // escreve na posicao do registro do arquivo, mas mantendo o tamanho anterior

                        } else{
                            deleteMusica(musica.getId()); // se for maior, o registro anterior eh deletado
                            writeInFile(raf.length(), musica); // e criado um novo, com o id anterior

                        }
                        parada = false;
                    } // se o id nao for o requisitado
                    pos = posTam + tamRegistro; // passa para o proximo registro
                    raf.seek(pos);

                } else{ // se registro nao existir, passa para o proximo registro
                    pos = raf.getFilePointer() + raf.readInt();
                    raf.seek(pos);
                }

            }

    }
    private void writeMusicaByTam(long pos, Musica musica, int tam) throws IOException { // metodo para escrever a musica, mas mantendo o tamanho do arquivo anterior, metodo exclusivo do update
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







