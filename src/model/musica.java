public class musica{
    private int id;
    private int rank;
    private int streams;
    private String nome;
    private Date data;
    private String regiao;

    private boolean lapide;

    // getters e setters
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

    public void setData(Date data) {
        this.data = data;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public musica(){
        data = new Date();
    }
    public musica(int rank, int streams, String nome, String data, String regiao) {
        this.rank = rank;
        this.streams = streams;
        this.nome = nome;
        this.regiao = regiao;
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
    }
}