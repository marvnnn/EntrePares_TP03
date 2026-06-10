import menu.MenuCursos;
import menu.MenuUsuario;
import arquivo.ArquivoUsuario;
import arquivo.ArquivoCurso;
import entidades.Usuario;

import java.util.Scanner;

public class Principal {

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        ArquivoUsuario arqUsuario = null;
        Usuario usuarioAtivo = null;

        try {
            arqUsuario = new ArquivoUsuario();
        } catch (Exception e) {
            System.err.println("Erro ao abrir arquivo de usuários: " + e.getMessage());
            return;
        }

        int opcao;
        do {
            System.out.println("\n\n=== EntrePares 1.0 ===");
            System.out.println("------------------");
            if (usuarioAtivo != null) {
                System.out.println("\n> Conectado como: " + usuarioAtivo.getNome() + " (" + usuarioAtivo.getEmail() + ")");
                System.out.println("1 - Menu Usuario");
                System.out.println("2 - Menu Cursos");
                System.out.println("0 - Logout");
            } else {
                System.out.println("\n> Desconectado");
                System.out.println("1 - Login");
                System.out.println("2 - Registrar");
                System.out.println("0 - Sair");
            }

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.parseInt(console.nextLine());
            } catch (NumberFormatException e) {
                opcao = -1;
            }
            if (usuarioAtivo != null) {
                switch (opcao) {
                    case 1:
                        (new MenuUsuario()).menu();
                        break;
                    case 2:
                        MenuCursos.setUsuarioAtivo(usuarioAtivo);
                        (new MenuCursos()).menu();
                        break;
                    case 0:
                        usuarioAtivo = null;
                        MenuCursos.setUsuarioAtivo(null);
                        System.out.println("Logout realizado!");
                        break;
                    default:
                        System.out.println("Opção inválida");
                }
            } else {
                switch (opcao) {
                    case 1:
                        // Login
                        System.out.print("\nEmail: ");
                        String email = console.nextLine();
                        if (email.isEmpty()) {
                            System.out.println("Email não pode estar vazio!");
                            break;
                        }
                        System.out.print("Senha: ");
                        String senha = console.nextLine();
                        if (senha.isEmpty()) {
                            System.out.println("Senha não pode estar vazia!");
                            break;
                        }

                        Usuario usuario = arqUsuario.readEmail(email);
                        if (usuario != null) {
                            // Verifica senha (simplificado - em um sistema real usaríamos hash)
                            if (senha.hashCode() == usuario.getHashSenha()) {
                                usuarioAtivo = usuario;
                                System.out.println("\nLogin realizado com sucesso!");
                            } else {
                                System.out.println("Senha incorreta!");
                            }
                        } else {
                            System.out.println("Usuário não encontrado!");
                        }
                        break;
                    case 2:
                        // Registrar
                        (new MenuUsuario()).menu();
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida");
                }
            }
        } while (opcao != 0);

        // Fecha os arquivos para garantir persistência dos dados
        try {
            if (arqUsuario != null) {
                arqUsuario.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar arquivo de usuários: " + e.getMessage());
        }
    }
}