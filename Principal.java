import arquivo.ArquivoUsuario;
import entidades.Usuario;
import menu.MenuCursos;
import menu.MenuInscricoes;
import menu.MenuUsuario;

import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        ArquivoUsuario arqUsuario = null;
        Usuario usuarioAtivo = null;
        boolean executando = true;

        try {
            arqUsuario = new ArquivoUsuario();
        } catch (Exception e) {
            System.err.println("Erro ao abrir arquivo de usuários: " + e.getMessage());
            return;
        }

        while (executando) {
            System.out.println("\n\nEntrePares 3.0");
            System.out.println("--------------");

            if (usuarioAtivo == null) {
                System.out.println("\n(A) Login");
                System.out.println("(B) Novo usuário");
                System.out.println("\n(S) Sair");
                System.out.print("\nOpção: ");

                String entrada = console.nextLine().trim().toUpperCase();

                switch (entrada) {
                    case "A":
                        usuarioAtivo = login(console, arqUsuario);
                        if (usuarioAtivo != null) {
                            MenuCursos.setUsuarioAtivo(usuarioAtivo);
                            MenuUsuario.setUsuarioAtivo(usuarioAtivo);
                            System.out.println("Login realizado com sucesso!");
                        } else {
                            System.out.println("Email ou senha inválidos.");
                        }
                        break;

                    case "B":
                        new MenuUsuario().cadastrarNovoUsuario();
                        break;

                    case "S":
                        executando = false;
                        break;

                    default:
                        System.out.println("Opção inválida!");
                }
            } else {
                System.out.println("\n> Início");
                System.out.println("\n(A) Meus dados");
                System.out.println("(B) Meus cursos");
                System.out.println("(C) Minhas inscrições");
                System.out.println("(S) Sair");
                System.out.print("\nOpção: ");

                String entrada = console.nextLine().trim().toUpperCase();

                switch (entrada) {
                    case "A":
                        MenuUsuario.setUsuarioAtivo(usuarioAtivo);
                        new MenuUsuario().menu();
                        usuarioAtivo = MenuUsuario.getUsuarioAtivo();
                        if (usuarioAtivo == null) {
                            MenuCursos.setUsuarioAtivo(null);
                        }
                        break;

                    case "B":
                        MenuCursos.setUsuarioAtivo(usuarioAtivo);
                        new MenuCursos().menu();
                        break;

                    case "C":
                        MenuInscricoes.setUsuarioAtivo(usuarioAtivo);
                        new MenuInscricoes().menu();
                        break;

                    case "S":
                        usuarioAtivo = null;
                        MenuCursos.setUsuarioAtivo(null);
                        MenuUsuario.setUsuarioAtivo(null);
                        System.out.println("Logout realizado!");
                        break;

                    default:
                        System.out.println("Opção inválida!");
                }
            }
        }

        try {
            if (arqUsuario != null) {
                arqUsuario.close();
            }
            console.close();
        } catch (Exception e) {
            System.err.println("Erro ao fechar arquivos: " + e.getMessage());
        }
    }

    private static Usuario login(Scanner console, ArquivoUsuario arqUsuario) {
        try {
            System.out.print("Email: ");
            String email = console.nextLine();
            if (email.isEmpty()) {
                return null;
            }

            System.out.print("Senha: ");
            String senha = console.nextLine();
            if (senha.isEmpty()) {
                return null;
            }

            Usuario usuario = arqUsuario.readEmail(email);
            if (usuario == null) {
                return null;
            }

            if (usuario.getHashSenha() == senha.hashCode()) {
                return usuario;
            }
        } catch (Exception e) {
            System.err.println("Erro no login: " + e.getMessage());
        }
        return null;
    }
}
