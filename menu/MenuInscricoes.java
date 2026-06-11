package menu;

import arquivo.ArquivoCurso;
import arquivo.ArquivoCursoUsuario;
import arquivo.ArquivoUsuario;
import entidades.Curso;
import entidades.CursoUsuario;
import entidades.Usuario;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class MenuInscricoes {

    private ArquivoCurso arqCurso;
    private ArquivoCursoUsuario arqCursoUsuario;
    private ArquivoUsuario arqUsuario;
    private Scanner console;

    private static Usuario usuarioAtivo = null;
    private static final int TAMANHO_PAGINA = 10;
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
            arqCursoUsuario = new ArquivoCursoUsuario();
            arqUsuario = new ArquivoUsuario();

            String entrada;
            do {
                System.out.println("\n\nEntrePares 1.0");
                System.out.println("--------------");
                System.out.println("> Início > Minhas inscrições\n");

                listarMinhasInscricoesResumo();

                System.out.println("\n(A) Buscar curso por código");
                System.out.println("(B) Buscar curso por palavras-chave");
                System.out.println("(C) Listar todos os cursos");
                System.out.println("(R) Retornar ao menu anterior");
                System.out.print("\nOpção: ");

                entrada = console.nextLine().trim().toUpperCase();

                if (entrada.equals("A")) {
                    buscarCursoPorCodigo();
                } else if (entrada.equals("B")) {
                    buscarCursoPorPalavras();
                } else if (entrada.equals("C")) {
                    listarTodosOsCursos();
                } else if (entrada.matches("\\d+")) {
                    abrirInscricaoPorNumero(Integer.parseInt(entrada));
                } else if (!entrada.equals("R")) {
                    System.out.println("Opção inválida!");
                }

            } while (!entrada.equals("R"));

            arqCurso.close();
            arqCursoUsuario.close();
            arqUsuario.close();

        } catch (Exception e) {
            System.err.println("Erro no menu de inscrições!");
            e.printStackTrace();
        }
    }

    private void listarMinhasInscricoesResumo() throws Exception {
        System.out.println("INSCRIÇÕES");

        if (usuarioAtivo == null) {
            System.out.println("(nenhum usuário ativo)");
            return;
        }

        CursoUsuario[] inscricoes = arqCursoUsuario.readPorUsuario(usuarioAtivo.getID());

        if (inscricoes.length == 0) {
            System.out.println("(nenhuma inscrição cadastrada)");
            return;
        }

        Curso[] cursos = cursosDasInscricoes(inscricoes);
        Arrays.sort(cursos, Comparator.comparing(c -> c.getDataInicio()));

        for (int i = 0; i < cursos.length; i++) {
            System.out.println("(" + (i + 1) + ") " + cursos[i].getNome() + " - " +
                    cursos[i].getDataInicio().format(FORMATO_DATA) + textoEstadoResumo(cursos[i]));
        }
    }

    private void abrirInscricaoPorNumero(int numero) throws Exception {
        CursoUsuario[] inscricoes = arqCursoUsuario.readPorUsuario(usuarioAtivo.getID());
        Curso[] cursos = cursosDasInscricoes(inscricoes);
        Arrays.sort(cursos, Comparator.comparing(c -> c.getDataInicio()));

        if (numero < 1 || numero > cursos.length) {
            System.out.println("Inscrição inválida!");
            return;
        }

        abrirCursoInscrito(cursos[numero - 1]);
    }

    private Curso[] cursosDasInscricoes(CursoUsuario[] inscricoes) throws Exception {
        Curso[] cursos = new Curso[inscricoes.length];
        int quantidade = 0;

        for (CursoUsuario inscricao : inscricoes) {
            if (inscricao != null) {
                Curso curso = arqCurso.read(inscricao.getIdCurso());
                if (curso != null) {
                    cursos[quantidade++] = curso;
                }
            }
        }

        return Arrays.copyOf(cursos, quantidade);
    }

    private void buscarCursoPorCodigo() throws Exception {
        System.out.print("Código: ");
        String codigo = console.nextLine().trim();
        if (codigo.isEmpty()) {
            return;
        }

        Curso curso = arqCurso.readCodigo(codigo);
        if (curso == null) {
            System.out.println("Curso não encontrado!");
            return;
        }

        abrirCursoEncontrado(curso);
    }


    private void buscarCursoPorPalavras() throws Exception {
        System.out.print("Palavras-chave: ");
        String palavras = console.nextLine().trim();
        if (palavras.isEmpty()) {
            return;
        }

        Curso[] cursos = arqCurso.readPorPalavras(palavras);
        if (cursos.length == 0) {
            System.out.println("Nenhum curso encontrado!");
            return;
        }

        listarCursosPaginado(cursos);
    }

    private void listarTodosOsCursos() throws Exception {
        Curso[] cursos = arqCurso.readAll();
        if (cursos.length == 0) {
            System.out.println("Nenhum curso encontrado!");
            return;
        }

        Arrays.sort(cursos, Comparator.comparing(Curso::getDataInicio));
        listarCursosPaginado(cursos);
    }

    private void listarCursosPaginado(Curso[] cursos) throws Exception {
        int pagina = 0;
        int totalPaginas = (int) Math.ceil(cursos.length / (double) TAMANHO_PAGINA);
        String entrada;

        do {
            int inicio = pagina * TAMANHO_PAGINA;
            int fim = Math.min(inicio + TAMANHO_PAGINA, cursos.length);

            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Minhas inscrições > Lista de cursos\n");
            System.out.println("Página " + (pagina + 1) + " de " + totalPaginas + "\n");

            for (int i = inicio; i < fim; i++) {
                int opcao = (i - inicio + 1) % 10;
                System.out.println("(" + opcao + ") " + cursos[i].getNome() + " - " +
                        cursos[i].getDataInicio().format(FORMATO_DATA) + textoEstadoResumo(cursos[i]));
            }

            System.out.println("\n(A) Página anterior");
            System.out.println("(B) Próxima página");
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            entrada = console.nextLine().trim().toUpperCase();

            if (entrada.equals("A")) {
                if (pagina > 0) {
                    pagina--;
                } else {
                    System.out.println("Você já está na primeira página.");
                }
            } else if (entrada.equals("B")) {
                if (pagina < totalPaginas - 1) {
                    pagina++;
                } else {
                    System.out.println("Você já está na última página.");
                }
            } else if (entrada.matches("\\d")) {
                int opcao = Integer.parseInt(entrada);
                int posicao = opcao == 0 ? inicio + 9 : inicio + opcao - 1;

                if (posicao >= inicio && posicao < fim) {
                    abrirCursoEncontrado(cursos[posicao]);
                } else {
                    System.out.println("Curso inválido!");
                }
            } else if (!entrada.equals("R")) {
                System.out.println("Opção inválida!");
            }

        } while (!entrada.equals("R"));
    }

    private void abrirCursoEncontrado(Curso curso) throws Exception {
        String entrada;

        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Minhas inscrições > Lista de cursos > " + curso.getNome() + "\n");

            mostraCursoDetalhado(curso);

            if (curso.getEstado() == Curso.ATIVO_INSCRICOES) {
                System.out.println("\n(A) Fazer minha inscrição no curso");
            } else {
                System.out.println("\nEste curso não está aberto para inscrições.");
            }

            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            entrada = console.nextLine().trim().toUpperCase();

            if (entrada.equals("A") && curso.getEstado() == Curso.ATIVO_INSCRICOES) {
                CursoUsuario inscricaoExistente = arqCursoUsuario.readPorUsuarioCurso(usuarioAtivo.getID(), curso.getID());
                if (inscricaoExistente != null) {
                    System.out.println("Você já está inscrito neste curso.");
                } else {
                    arqCursoUsuario.create(new CursoUsuario(curso.getID(), usuarioAtivo.getID(), LocalDate.now()));
                    System.out.println("Inscrição realizada com sucesso!");
                }
            } else if (!entrada.equals("R")) {
                System.out.println("Opção inválida!");
            }

        } while (!entrada.equals("R"));
    }

    private void abrirCursoInscrito(Curso curso) throws Exception {
        String entrada;

        do {
            System.out.println("\n\nEntrePares 1.0");
            System.out.println("--------------");
            System.out.println("> Início > Minhas inscrições > " + curso.getNome() + "\n");

            mostraCursoDetalhado(curso);

            System.out.println("\n(A) Cancelar minha inscrição no curso");
            System.out.println("(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");

            entrada = console.nextLine().trim().toUpperCase();

            if (entrada.equals("A")) {
                CursoUsuario inscricao = arqCursoUsuario.readPorUsuarioCurso(usuarioAtivo.getID(), curso.getID());
                if (inscricao != null && arqCursoUsuario.delete(inscricao.getID())) {
                    System.out.println("Inscrição cancelada com sucesso!");
                    entrada = "R";
                } else {
                    System.out.println("Erro ao cancelar inscrição.");
                }
            } else if (!entrada.equals("R")) {
                System.out.println("Opção inválida!");
            }

        } while (!entrada.equals("R"));
    }

    private void mostraCursoDetalhado(Curso curso) throws Exception {
        Usuario autor = arqUsuario.read(curso.getIdUsuario());

        System.out.println("CÓDIGO........: " + curso.getCodigoCompartilhavel());
        System.out.println("CURSO.........: " + curso.getNome());
        System.out.println("AUTOR.........: " + (autor == null ? "Autor não encontrado" : autor.getNome()));
        System.out.println("DESCRIÇÃO.....: " + curso.getDescricao());
        System.out.println("DATA DE INÍCIO: " + curso.getDataInicio().format(FORMATO_DATA));
        System.out.println("ESTADO........: " + curso.getEstadoDescricao());
    }

    private String textoEstadoResumo(Curso curso) {
        if (curso.getEstado() == Curso.ATIVO_SEM_INSCRICOES) {
            return " (INSCRIÇÕES ENCERRADAS)";
        } else if (curso.getEstado() == Curso.CONCLUIDO) {
            return " (CURSO CONCLUÍDO)";
        } else if (curso.getEstado() == Curso.CANCELADO) {
            return " (CURSO CANCELADO)";
        }
        return "";
    }
}
