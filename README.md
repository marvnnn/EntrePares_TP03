# 📚 Trabalho Prático – AEDs III (TP2)

## 👥 Participantes
- Arthur Campos Pereira
- Felipe Barros Silva
- Mateus Martins Parreiras

---

# 🧾 Descrição do Sistema

O sistema realiza o gerenciamento de usuários, cursos e inscrições.

Os usuários podem:
- realizar cadastro e login;
- criar cursos;
- buscar cursos utilizando código NanoID;
- realizar inscrições em cursos;
- cancelar inscrições;
- visualizar os inscritos em seus cursos.

O projeto utiliza Tabelas Hash Extensíveis e Árvores B+ para armazenamento e indexação dos dados.

---

# 🖥️ Telas do Sistema

## Tela Inicial
![Tela Inicial](imagens/tela-inicial.jpg)

## Cadastro de Usuário
![Cadastro de Usuário](imagens/cadastro-usuario.jpg)

## Menu Meus Cursos
![Menu Meus Cursos](imagens/menu-cursos.jpg)

## Busca de Cursos por NanoID
![Busca NanoID](imagens/busca-nanoid.jpg)

## Menu Minhas Inscrições
![Menu Minhas Inscrições](imagens/minhas-inscricoes.jpg)

## Lista de Inscritos no Curso
![Lista de Inscritos](imagens/lista-inscritos.jpg)

---

# 🧱 Classes Criadas

## Entidades
- `Usuario`
- `Curso`
- `CursoUsuario`

## Arquivos
- `ArquivoUsuario`
- `ArquivoCurso`
- `ArquivoCursoUsuario`

## Menus
- `MenuUsuario`
- `MenuCursos`
- `MenuInscricoes`

## Utilidades
- `NanoIdUtil`

---

# 🔗 Relacionamento N:N

O relacionamento entre usuários e cursos foi implementado utilizando a entidade `CursoUsuario`.

Foram utilizadas duas Árvores B+:
- `(idCurso, idCursoUsuario)`
- `(idUsuario, idCursoUsuario)`

---

# ✅ Checklist

## Há um CRUD da entidade de associação CursoUsuario (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?
Sim.

## A visão de inscrições está corretamente implementada e permite consultas aos cursos em que um usuário está inscrito?
Sim.

## A visão de cursos funciona corretamente e permite a gestão dos usuários inscritos em um curso?
Sim.

## Há uma visualização dos cursos de outras pessoas por meio de um código NanoID?
Sim.

## A integridade do relacionamento entre cursos e usuários está mantida em todas as operações?
Sim.

## O trabalho compila corretamente?
Sim.

## O trabalho está completo e funcionando sem erros de execução?
Sim.

## O trabalho é original e não a cópia de um trabalho de outro grupo?
Sim.

---

# 🎥 Vídeo de Demonstração

https://youtu.be/WSPK65PM4zk
