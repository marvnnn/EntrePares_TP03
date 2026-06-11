package menu;

import arquivo.ArquivoCurso;
import arquivo.ArquivoCursoUsuario;
import arquivo.ArquivoUsuario;
import entidades.Curso;
import entidades.CursoUsuario;
import entidades.Usuario;
import util.NanoIdUtil;

import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class MenuCursos {

    private ArquivoCurso arqCurso;
    private ArquivoUsuario arqUsuario;
    private ArquivoCursoUsuario arqCursoUsuario;
    private Scanner console;

    private static Usuario usuarioAtivo = null;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void setUsuarioAtivo(Usuario usuario) {
        usuarioAtivo = usuario;
    }

    public static Usuario getUsuarioAtivo() {
        return usuarioAtivo;
    }

    public void menu() {
        try {
            console = new Scanner(System.in);
            arqCurso = new ArquivoCurso();
            arqUsuario = new ArquivoUsuario();
            arqCursoUsuario = new ArquivoCursoUsuario();

            String entrada;
            do {
                System.out.println("\n\nEntrePares 1.0");
                System.out.println("--------------");
                System.out.println("> Início > Meus cursos\n");

                listarMeusCursosResumo();

                System.out.println("\n(A) Novo curso");
                System.out.println("(B) Buscar por código");
                System.out.println("(C) Buscar por nome");
                System.out.println("(R) Retornar");
                System.out.print("\nOpção: ");

                entrada = console.nextLine().trim().toUpperCase();

                if (entrada.equals("A")) {
                    inserir();
                } else if (entrada.equals("B")) {
                    buscarPorCodigo();
                } else if (entrada.equals("C")) {
                    buscarPorNome();
                } else if (entrada.matches("\\d+")) {
                    abrirCursoPorNumero(Integer.parseInt(entrada));
                } else if (!entrada.equals("R")) {
                    System.out.println("Opção inválida!");
                }

            } while (!entrada.equals("R"));

            arqCurso.close();
            arqUsuario.close();
            arqCursoUsuario.close();

        } catch (Exception e) {
            System.err.println("Erro no menu de cursos!");
            e.printStackTrace();
        }
    }

    private void listarMeusCursosResumo() throws Exception {
        if (usuarioAtivo == null) {
            System.out.println("Nenhum usuário ativo.");
            return;
        }

        Curso[] cursos = arqCurso.readPorUsuario(usuarioAtivo.getID());
        if (cursos.length == 0) {
            System.out.println("CURSOS");
            System.out.println("(nenhum curso cadastrado)");
            return;
        }

        System.out.println("CURSOS");
        for (int i = 0; i < cursos.length; i++) {
            System.out.println("(" + (i + 1) + ") " + cursos[i].getNome() + " - " +
                    cursos[i].getDataInicio().format(FORMATO_DATA));
        }
    }

    private void abrirCursoPorNumero(int numero) throws Exception {
        if (usuarioAtivo == null) {
            System.out.println("Nenhum usuário ativo.");
            return;
        }

        Curso[] cursos = arqCurso.readPorUsuario(usuarioAtivo.getID());
        if (numero < 1 || numero > cursos.length) {
            System.out.println("Curso inválido!");
            return;
        }

        Curso curso = cursos[numero - 1];
        String entrada;

        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Meus cursos > " + curso.getNome() + "\n");

            mostraCursoDetalhado(curso);

            System.out.println("\n(A) Gerenciar inscritos no curso");
            System.out.println("(B) Corrigir dados do curso");
            System.out.println("(C) Encerrar inscrições");
            System.out.println("(D) Concluir curso");
            System.out.println("(E) Cancelar curso");
            System.out.println("(X) Excluir curso");
            System.out.println("(R) Retornar");
            System.out.print("\nOpção: ");

            entrada = console.nextLine().trim().toUpperCase();

            switch (entrada) {
                case "A":
                    gerenciarInscritos(curso);
                    break;
                case "B":
                    alterar(curso);
                    curso = arqCurso.read(curso.getID());
                    break;
                case "C":
                    curso.setEstado(Curso.ATIVO_SEM_INSCRICOES);
                    arqCurso.update(curso);
                    curso = arqCurso.read(curso.getID());
                    System.out.println("Inscrições encerradas.");
                    break;
                case "D":
                    curso.setEstado(Curso.CONCLUIDO);
                    arqCurso.update(curso);
                    curso = arqCurso.read(curso.getID());
                    System.out.println("Curso concluído.");
                    break;
                case "E":
                    curso.setEstado(Curso.CANCELADO);
                    arqCurso.update(curso);
                    curso = arqCurso.read(curso.getID());
                    System.out.println("Curso cancelado.");
                    break;
                case "X":
                    excluir(curso);
                    entrada = "R";
                    break;
                case "R":
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (!entrada.equals("R"));
    }

    private void gerenciarInscritos(Curso curso) throws Exception {
        String entrada;

        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Meus cursos > " + curso.getNome() + " > Inscrições\n");

            CursoUsuario[] inscricoes = arqCursoUsuario.readPorCurso(curso.getID());
            Arrays.sort(inscricoes, Comparator.comparing(i -> i.getDataInscricao()));

            for (int i = 0; i < inscricoes.length; i++) {
                Usuario usuario = arqUsuario.read(inscricoes[i].getIdUsuario());
                String nome = usuario == null ? "Usuário não encontrado" : usuario.getNome();
                System.out.println("(" + (i + 1) + ") " + nome + " (" +
                        inscricoes[i].getDataInscricao().format(FORMATO_DATA) + ")");
            }

            if (inscricoes.length == 0) {
                System.out.println("(nenhum inscrito)");
            }

            System.out.println("\n(A) Exportar lista");
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            entrada = console.nextLine().trim().toUpperCase();

            if (entrada.equals("A")) {
                exportarLista(curso, inscricoes);
            } else if (entrada.matches("\\d+")) {
                int numero = Integer.parseInt(entrada);
                if (numero >= 1 && numero <= inscricoes.length) {
                    abrirInscrito(curso, inscricoes[numero - 1]);
                } else {
                    System.out.println("Inscrito inválido!");
                }
            } else if (!entrada.equals("R")) {
                System.out.println("Opção inválida!");
            }

        } while (!entrada.equals("R"));
    }

    private void abrirInscrito(Curso curso, CursoUsuario inscricao) throws Exception {
        Usuario usuario = arqUsuario.read(inscricao.getIdUsuario());
        String entrada;

        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Meus cursos > " + curso.getNome() + " > Inscrições\n");

            if (usuario == null) {
                System.out.println("Usuário não encontrado.");
            } else {
                System.out.println("Nome...............: " + usuario.getNome());
                System.out.println("Email..............: " + usuario.getEmail());
                System.out.println("Data da inscrição..: " + inscricao.getDataInscricao().format(FORMATO_DATA));
            }

            System.out.println("\n(A) Cancelar inscrição deste usuário");
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            entrada = console.nextLine().trim().toUpperCase();

            if (entrada.equals("A")) {
                if (arqCursoUsuario.delete(inscricao.getID())) {
                    System.out.println("Inscrição cancelada.");
                    entrada = "R";
                } else {
                    System.out.println("Erro ao cancelar inscrição.");
                }
            } else if (!entrada.equals("R")) {
                System.out.println("Opção inválida!");
            }

        } while (!entrada.equals("R"));
    }

    private void exportarLista(Curso curso, CursoUsuario[] inscricoes) throws Exception {
        String nomeArquivo = "inscritos_curso_" + curso.getID() + ".csv";

        FileWriter fw = new FileWriter(nomeArquivo);
        fw.write("nome,email,data_inscricao\n");

        for (CursoUsuario inscricao : inscricoes) {
            Usuario usuario = arqUsuario.read(inscricao.getIdUsuario());
            if (usuario != null) {
                fw.write(csv(usuario.getNome()) + "," + csv(usuario.getEmail()) + "," +
                        inscricao.getDataInscricao().format(FORMATO_DATA) + "\n");
            }
        }

        fw.close();
        System.out.println("Lista exportada para " + nomeArquivo);
    }

    private String csv(String valor) {
        String texto = valor == null ? "" : valor.replace("\"", "\"\"");
        return "\"" + texto + "\"";
    }

    private void inserir() throws Exception {
        System.out.println("\n=== NOVO CURSO ===");

        if (usuarioAtivo == null) {
            System.out.println("Nenhum usuário ativo. Faça login primeiro.");
            return;
        }

        System.out.print("Nome do curso: ");
        String nome = console.nextLine();
        if (nome.isEmpty()) return;

        System.out.print("Descrição detalhada: ");
        String descricao = console.nextLine();
        if (descricao.isEmpty()) return;

        LocalDate dataInicio = null;
        while (dataInicio == null) {
            System.out.print("Data de início (dd/mm/aaaa): ");
            String data = console.nextLine();
            if (data.isEmpty()) return;

            try {
                String[] dadosData = data.split("/");
                dataInicio = LocalDate.of(
                    Integer.parseInt(dadosData[2]),
                    Integer.parseInt(dadosData[1]),
                    Integer.parseInt(dadosData[0])
                );
            } catch (Exception e) {
                System.out.println("Data inválida!");
            }
        }

        String codigoCompartilhavel = gerarCodigoCompartilhavelUnico();
        Curso curso = new Curso(usuarioAtivo.getID(), nome, dataInicio, descricao, codigoCompartilhavel, Curso.ATIVO_INSCRICOES);

        arqCurso.create(curso);
        System.out.println("Curso incluído com sucesso!");
        System.out.println("Código compartilhável: " + codigoCompartilhavel);
    }

    private void buscarPorCodigo() throws Exception {
        System.out.print("Código: ");
        String codigo = console.nextLine();
        if (codigo.isEmpty()) return;

        Curso curso = arqCurso.readCodigo(codigo);
        if (curso != null) {
            mostraCursoDetalhado(curso);
        } else {
            System.out.println("Curso não encontrado!");
        }
    }

    private void buscarPorNome() throws Exception {
        System.out.print("Nome: ");
        String nome = console.nextLine();
        if (nome.isEmpty()) return;

        Curso[] cursos = arqCurso.readNome(nome);
        if (cursos.length == 0) {
            System.out.println("Nenhum curso encontrado!");
            return;
        }

        for (Curso curso : cursos) {
            mostraCursoDetalhado(curso);
            System.out.println();
        }
    }

    private void alterar(Curso curso) throws Exception {
        System.out.println("\n=== CORRIGIR DADOS DO CURSO ===");

        System.out.print("Nome (" + curso.getNome() + "): ");
        String nome = console.nextLine();
        if (!nome.isEmpty()) {
            curso.setNome(nome);
        }

        System.out.print("Descrição (" + curso.getDescricao() + "): ");
        String descricao = console.nextLine();
        if (!descricao.isEmpty()) {
            curso.setDescricao(descricao);
        }

        System.out.print("Data de início (dd/mm/aaaa) [" +
                curso.getDataInicio().format(FORMATO_DATA) + "]: ");
        String data = console.nextLine();
        if (!data.isEmpty()) {
            try {
                String[] dadosData = data.split("/");
                curso.setDataInicio(LocalDate.of(
                    Integer.parseInt(dadosData[2]),
                    Integer.parseInt(dadosData[1]),
                    Integer.parseInt(dadosData[0])
                ));
            } catch (Exception e) {
                System.out.println("Data inválida, mantendo a anterior.");
            }
        }

        if (arqCurso.update(curso)) {
            System.out.println("Curso atualizado!");
        } else {
            System.out.println("Erro na atualização!");
        }
    }

    private void excluir(Curso curso) throws Exception {
        System.out.print("Confirma exclusão (S/N)? ");
        String confirma = console.nextLine();

        if (confirma.isEmpty() || (confirma.charAt(0) != 'S' && confirma.charAt(0) != 's')) {
            System.out.println("Exclusão cancelada.");
            return;
        }

        arqCursoUsuario.deletePorCurso(curso.getID());

        if (arqCurso.delete(curso.getID())) {
            System.out.println("Curso excluído!");
        } else {
            System.out.println("Erro na exclusão!");
        }
    }

    private void mostraCursoDetalhado(Curso curso) throws Exception {
        System.out.println("CÓDIGO........: " + curso.getCodigoCompartilhavel());
        System.out.println("NOME..........: " + curso.getNome());
        System.out.println("DESCRIÇÃO.....: " + curso.getDescricao());
        System.out.println("DATA DE INÍCIO: " + curso.getDataInicio().format(FORMATO_DATA));
        System.out.println();
        System.out.println(curso.getEstadoDescricao());
    }

    private String gerarCodigoCompartilhavelUnico() throws Exception {
        String codigo;
        do {
            codigo = NanoIdUtil.generate();
        } while (arqCurso.readCodigo(codigo) != null);
        return codigo;
    }
}
