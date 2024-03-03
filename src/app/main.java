import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class main {
       protected MusicaService service;
       protected Musica musica;


    public static void main(String[] args) throws IOException, ParseException {

        MusicaService serviceDB = new MusicaService("DB/teste.db");

        MusicaService serviceCSV = new MusicaService("DB/csv_teste_tratado.csv");
        Scanner scanner = new Scanner(System.in);
        serviceCSV.raf.readLine();
        long posCSV = serviceCSV.raf.getFilePointer();
        long posBD = 4;
        int id;

        System.out.println("Olá usuário! " +
                "Por favor, tecle 1 para carregar o arquivo binário com os registros, caso ja tenha carregado tecle 2");
        int escolha = scanner.nextInt();

                if(escolha == 1) {
                    System.out.println("Por favor, entre com o numero de registros que gostaria que fossem passados. (obs: o numero maximo e 148983 : ");
                    int n = scanner.nextInt();
                    for (int i = 0; i < n; i++) {
                        serviceDB.writeInFile(posBD, serviceCSV.readFromCSV(posCSV));
                        posCSV = serviceCSV.raf.getFilePointer();
                        posBD = serviceDB.raf.getFilePointer();
                    }
                }

                        while (escolha != 6) {
                            System.out.println("""
                                    Por favor, escolha a operação desejada:
                                    1 - Criar
                                    2 - Ler
                                    3 - Atualizar
                                    4 - Deletar
                                    5 - Listar todos os registros(CUIDADO)
                                    6 - Sair
                                    """);
                            escolha = scanner.nextInt();
                            switch (escolha) {
                                case 1:
                                    System.out.println(serviceDB.toString(serviceDB.createMusica()));
                                    break;

                                case 2:
                                    System.out.println("Por favor, entre com o ID do registro requisitado");
                                    id = scanner.nextInt();
                                    System.out.println(serviceDB.toString(serviceDB.readMusica(id)));
                                    break;
                                case 3:
                                    System.out.println("Por favor, entre com o ID do registro requisitado");
                                    id = scanner.nextInt();
                                    serviceDB.updateMusica(id);
                                    break;
                                case 4:
                                    System.out.println("Por favor, entre com o ID do registro requisitado");
                                    id = scanner.nextInt();
                                    serviceDB.deleteMusica(id);
                                    break;
                                case 5:
                                    serviceDB.readAll();
                                    break;
                                case 6:
                                    break;
                            }
                        }


    }
}