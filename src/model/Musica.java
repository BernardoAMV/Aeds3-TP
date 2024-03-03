
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
public class Musica{
    private int id;
    private int rank;
    private int streams;
    private String nome;
    private Date data;
    private String regiao;

    private String titulo;

    public Musica(int rank, int streams, String nome, String data, String regiao, String titulo) throws IOException {
        this.rank = rank;
        this.streams = streams;
        this.nome = nome;
        String pad = "   ";
        this.regiao = (regiao + pad).substring(0,3);
        this.titulo = titulo;
        MusicaService temp = new MusicaService("DB/teste.db");

        temp.raf.seek(0);
        if(temp.file.length() > 0)
            this.id = temp.raf.readInt() + 1;
        else
            this.id = 14;

        temp.raf.seek(0);
        temp.raf.writeInt(this.id);
        // aqui eu estou formatando a estrutura da data, como eu quero que ela apareca. EX: 2017-12-31
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //  transformando a string recebida como parametro em uma do tipo Date
            this.data = format.parse(data);
        }
        catch(ParseException e){
            //capturando a excecao, se houver
            e.printStackTrace();
        }
        temp = null;
    }
    public Musica() throws IOException {

        MusicaService temp = new MusicaService("DB/teste.db");

        temp.raf.seek(0);
        if(temp.file.length() > 0)
            this.id = temp.raf.readInt() + 1;
        else
            this.id = 1;

        temp.raf.seek(0);
        temp.raf.writeInt(this.id);
        this.nome ="";
        String pad = "   ";
        this.regiao = (regiao + pad).substring(0,3);
        this.streams = 0;
        data = new Date();
    }

    public Musica(boolean vazio){} // construtor vazio, em alguns casos tivemos que criar uma musica, mas sem capturar o id do arquivo, ai criamos esse construtor vazio

    // getters e setters


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getRank() {
        return rank;
    }

    public int getStreams() {
        return streams;
    }

    public String getNome() {
        return nome;
    }

    public Date getData() {
        return data;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setStreams(int streams) {
        this.streams = streams;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDataByString(String data) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.data = format.parse(data);
        format = null;
    }
    public void setData(Date data){
        this.data = data;
    }

    public void setRegiao(String regiao) {
        String pad = "   ";
        this.regiao = (regiao + pad).substring(0,3);
    }



}