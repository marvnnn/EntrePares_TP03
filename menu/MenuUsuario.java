package menu;


import arquivo.*;
import entidades.Curso;
import entidades.Usuario;


import arquivo.*;
import entidades.Usuario;
import entidades.Curso;
import menu.MenuUsuario;

import java.util.Scanner;

public class MenuUsuario {

    int idUsuario;

    ArquivoUsuario arqUsuario;
    ArquivoCurso arqCurso;
    Scanner console;
    VisaoUsuario visao;

    public void menu() {
        try {
            console = new Scanner(System.in);
            arqUsuario = new ArquivoUsuario();
            arqCurso = new ArquivoCurso();

            visao = new VisaoUsuario(console);
            this.idUsuario = id;

            int opcao;
            do {
                System.out.println("\n\nEntrePares");
                System.out.println("--------");
                System.out.println("\n> Gestão de Usuários\n");
                System.out.println("1 - Inserir Usuário");
                System.out.println("2 - Buscar por Email");
                System.out.println("3 - Buscar por Nome");
                System.out.println("4 - Alterar Usuário");
                System.out.println("5 - Excluir Usuário");
                System.out.println("6 - Listar Todos");

            int opcao;
            do {
                System.out.println("\n\nAEDs III");
                System.out.println(    "--------");
                System.out.println("\n> Início > Pessoas\n");
                System.out.println("1 - Inserir");
                System.out.println("2 - Buscar por Email");
                System.out.println("3 - Buscar por Nome");
                System.out.println("4 - Alterar");
                System.out.println("5 - Excluir");
                System.out.println("8 - Listagem");

                System.out.println("0 - Retornar ao menu anterior");
                System.out.print("\nOpção: ");
                try {
                    opcao = Integer.parseInt(console.nextLine());
                } catch (NumberFormatException e) {
                    opcao = -1;
                }

                switch (opcao) {
                    case 1:
                        inserir();
                        break;
                    case 2:
                        buscarPorEmail();
                        break;
                    case 3:
                        buscarPorNome();
                        break;
                    case 4:
                        alterar();
                        break;
                    case 5:
                        excluir();
                        break;
                    case 6:
                        listagem();
                        break;
                    case 0: break;
                    default:
                        System.out.println("Opção inválida");
                }
            } while (opcao != 0);

            arqUsuario.close();
            arqCurso.close();

        } catch(Exception e) {
            System.err.println("Erro no menu de usuários!");
            e.printStackTrace();
        }
    }

    private void inserir() throws Exception {

        System.out.println("\n=== INCLUSÃO DE USUÁRIO ===");

        String nome;
        String email;
        String senha;
        String pergunta;
        String resposta;
        boolean dadosValidos;                  
        System.out.println("INCLUSÃO");
        System.out.print("Nome: ");


        Usuario usuario = visao.leUsuario();
        if (usuario == null) {
            System.out.println("Operação cancelada.");
            return;
        }


        // Verifica se email já está cadastrado
        Usuario existente = arqUsuario.readEmail(usuario.getEmail());
        if (existente != null) {
            System.out.println("Email já cadastrado!");
            return;
        }

        int id = arqUsuario.create(usuario);
        System.out.println("Usuário incluído com ID: " + id);
    }

    private void buscarPorEmail() throws Exception {
        System.out.println("\n=== BUSCA POR EMAIL ===");
        System.out.print("Email: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;

        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario != null) {
            visao.mostraUsuario(usuario);
        } else {
            System.out.println("Usuário não encontrado!");
        }
    }

    private void buscarPorNome() throws Exception {
        System.out.println("\n=== BUSCA POR NOME ===");
        System.out.print("Nome: ");
        String nome = console.nextLine();
        if (nome.isEmpty()) return;

        Usuario[] usuarios = arqUsuario.readNome(nome);
        if (usuarios.length > 0) {
            System.out.println("\n=== USUÁRIOS ENCONTRADOS ===");
            for (Usuario u : usuarios) {
                visao.mostraUsuario(u);

        // email
        dadosValidos = false;
        do {
            System.out.print("Email: ");
            email = console.nextLine();
            if(email.length()==0)
                return;
    
            Usuario p1 = arqUsuario.readEmail(email); // confere se existe alguem com o email  -- criar o readEmail
            if(p1!=null)
                System.out.println("Email já cadastrado!");
            else
                dadosValidos = true;
        } while(dadosValidos != true);

       // senha
       dadosValidos = false;
       do { 
            System.out.println("Senha: ");
            senha = console.nextLine();
            if(senha.length() < 6)
                System.out.println("Senha deve conter pelo menos 6 caracteres");
            else 
                dadosValidos = true;
       } while (dadosValidos != true);

        // pergunta
       dadosValidos = false;
       do { 
            System.out.println("Pergunta Secreta: ");
            pergunta = console.nextLine();
            if(pergunta.length() < 6)
                System.out.println("Pergunta secreta deve conter pelo menos 6 caracteres");
            else 
                dadosValidos = true;
       } while (dadosValidos != true);

        // resposta
       dadosValidos = false;
       do { 
            System.out.println("Resposta: ");
            resposta = console.nextLine();
            if(resposta.length() < 6)
                System.out.println("Resposta deve conter pelo menos 6 caracteres");
            else 
                dadosValidos = true;
       } while (dadosValidos != true);

        System.out.print("Confirmar inclusão (S/N) ?");
        String confirma = console.nextLine();
        if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
            Usuario p = new Usuario(nome, email, senha.hashCode(), pergunta, resposta.hashCode());
            arqUsuario.create(p);
            System.out.println("Pessoa incluída!");
        }
    }

    private void buscarCPF() throws Exception {     
        System.out.println("BUSCA");
        System.out.print("CPF: ");
        String cpf = console.nextLine();
        if(cpf.length()==0)
            return;
        if(!cpf.matches("\\d{11}")) {
            System.out.println("CPF inválido!");
            return;
        }
        Usuario p = arqUsuario.readEmail(email);
        if(p!=null)
            mostraPessoa(p);
        else
            System.out.println("Pessoa não encontrada!");
    }

    private void buscarNome() throws Exception {     
        System.out.println("BUSCA");
        System.out.print("Nome: ");
        String nome = console.nextLine();
        if(nome.length()==0)
            return;
        Usuario[] pessoas = arqUsuario.readNome(nome);
        if(pessoas.length>0) {
            for(Usuario p : pessoas)
                mostraPessoa(p);
        }
        else
            System.out.println("Nenhuma pessoa encontrada!");
    }

    private void excluir() throws Exception {
        System.out.println("EXCLUSÃO");
        System.out.print("Email: ");
        String email = console.nextLine();
        if(email.length()==0)
            return;
        Usuario p = arqUsuario.readEmail(email);
        if(p!=null) {
            mostraPessoa(p);

            Curso[] cursos = arqCurso.readAutor(p.getID());
            if(cursos.length>0) {
                System.out.println("\nATENÇÃO: Esta pessoa é autora dos seguintes livros:");
                for(Curso l : cursos)
                    System.out.println(" - "+l.getNome() + "(Código: " + l.getCod() + ")");
                System.out.println("Exclusão não permitida!");
                return;
            }

            System.out.print("\nConfirma exclusão (S/N) ?");
            String confirma = console.nextLine();
            if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
                if(arqUsuario.delete(p.getID()))
                    System.out.println("Pessoa excluída!");
                else
                    System.out.println("Erro na exclusão!");
>>>>>>> origin/main
            }
        } else {
            System.out.println("Nenhum usuário encontrado!");
        }
    }

    private void alterar() throws Exception {
        System.out.println("\n=== ALTERAÇÃO DE USUÁRIO ===");
        System.out.print("Email do usuário: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;

        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }


        Usuario p = arqUsuario.readCPF(cpf);


        visao.mostraUsuario(usuario);
        Usuario alterado = visao.leAlteracaoUsuario(usuario);


        if (arqUsuario.update(alterado)) {
            System.out.println("Usuário atualizado!");
        } else {
            System.out.println("Erro na atualização!");
        }
    }

            System.out.println("\nAltere os dados a seguir. Deixe o campo em branco quando não quiser alterar.");
            String novoNome;
            String novoEmail;
            String novaPergunta;
            String novaSenha;
            String novaResposta;
            LocalDate novaDN;


    private void excluir() throws Exception {
        System.out.println("\n=== EXCLUSÃO DE USUÁRIO ===");
        System.out.print("Email do usuário: ");
        String email = console.nextLine();
        if (email.isEmpty()) return;


        Usuario usuario = arqUsuario.readEmail(email);
        if (usuario == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        visao.mostraUsuario(usuario);

        // Verifica cursos vinculados
        Curso[] cursosDoUsuario = arqCurso.readPorUsuario(usuario.getID());
        if (cursosDoUsuario.length > 0) {
            System.out.println("\n⚠️  ATENÇÃO: Este usuário possui " + cursosDoUsuario.length + " curso(s) vinculado(s):");
            int cursosAtivos = 0;
            for (Curso c : cursosDoUsuario) {
                String estadoStr = c.getEstadoDescricao();
                System.out.println("  - " + c.getNome() + " (Estado: " + estadoStr + ")");
                if (c.getEstado() == Curso.ATIVO_INSCRICOES || c.getEstado() == Curso.ATIVO_SEM_INSCRICOES) {
                    cursosAtivos++;
                }

            // Alteração do email
            boolean dadosValidos = false;
            do {
                System.out.print("Email: ");
                novoEmail = console.nextLine();
                if(novoEmail.length()==0) {
                    dadosValidos = true;
                } else {
                    Usuario p1 = arqUsuario.readEmail(novoEmail);
                    if(p1!=null)
                        System.out.println("Email já cadastrado!");
                    else 
                        dadosValidos = true;
                }
            } while(!dadosValidos);
            if(novoEmail.length()>0)
                p.setEmail(novoEmail);

            // Alteração da pergunta
            dadosValidos = false;
            String aux = "";
            do {
                System.out.print("Data de nascimento (dd/mm/aaaa): ");
                aux = console.nextLine();
                if(aux.length()==0) {
                    dadosValidos = true;
                }
                else {
                    try {
                        String[] dadosData = aux.split("/");
                        novaDN = LocalDate.of(
                            Integer.parseInt(dadosData[2]),
                            Integer.parseInt(dadosData[1]),
                            Integer.parseInt(dadosData[0]));
                        dadosValidos = true;
                    } catch(Exception e) {
                        System.out.println("Data inválida!");
                    }
                }
            } while(!dadosValidos);
            if(aux.length()>0)
                p.setDataNascimento(novaDN);


            System.out.print("\nConfirma alteração (S/N) ?");
            String confirma = console.nextLine();
            if(confirma.charAt(0)=='S' || confirma.charAt(0)=='s') {
                if(arqPessoas.update(p))
                    System.out.println("Pessoa atualizada!");
                else
                    System.out.println("Erro na alteração!");

            }

            if (cursosAtivos > 0) {
                System.out.println("\n❌ NÃO É POSSÍVEL EXCLUIR: Existem " + cursosAtivos + " curso(s) ATIVO(S).");
                System.out.println("   Exclua ou inative os cursos ativos primeiro.");
                return;
            }

            System.out.println("\nTodos os cursos estão INATIVOS. Eles também serão excluídos.");
        }

        // Exclui cursos inativos primeiro
        for (Curso c : cursosDoUsuario) {
            arqCurso.delete(c.getID());
        }

        // Exclui o usuário
        if (arqUsuario.delete(usuario.getID())) {
            System.out.println("Usuário e " + cursosDoUsuario.length + " curso(s) excluído(s)!");
        } else {
            System.out.println("Erro na exclusão!");
        }
    }


    private void listagem() throws Exception {
        System.out.println("\n=== LISTAGEM DE USUÁRIOS ===");
        Usuario[] usuarios = arqUsuario.readAll();
        if (usuarios.length > 0) {
            for (Usuario u : usuarios) {
                visao.mostraUsuario(u);
                System.out.println();
            }
        } else {
            System.out.println("Nenhum usuário cadastrado!");
        }
    }

    public void mostraPessoa(Usuario p) {
        System.out.println( 
            "Nome....: " + p.getNome() +
            "\nEmail.....: " + p.getEmail() +
            "\nPergunta: " + p.getPergunta() + "\n"
        );
    }

    public  void popular() throws Exception {
        arqUsuario.close();
        arqUsuario = null;

        (new File("./dados/pessoa/dados.db")).delete();
        (new File("./dados/pessoa/indiceDireto.d.db")).delete();
        (new File("./dados/pessoa/indiceDireto.c.db")).delete();
        (new File("./dados/pessoa/indiceCPF.d.db")).delete();
        (new File("./dados/pessoa/indiceCPF.c.db")).delete();
        (new File("./dados/pessoa/indiceNome.db")).delete();


        arqPessoas = new ArquivoUsuario();

        arqPessoas.create(new Pessoa("Johann Hari", "11111111111", LocalDate.of(1979, 1, 21)));
        arqPessoas.create(new Pessoa( "Brian Traci", "22222222222", LocalDate.of(1944, 1, 5)));
        arqPessoas.create(new Pessoa( "James Clear", "33333333333", LocalDate.of(1986, 1, 22)));
        arqPessoas.create(new Pessoa( "Morgan Housel", "44444444444", LocalDate.of(1986, 7, 20)));
    }

}
