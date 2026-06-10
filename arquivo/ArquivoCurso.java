package arquivo;

import aed3.*;
import entidades.Curso;
import entidades.CursoUsuario;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ArquivoCurso extends Arquivo<Curso> {

    HashExtensivel<ParCodigoId> indiceCodigo;
    ArvoreBMais<ParNomeId> indiceNome;
    ListaInvertida indiceNomeInvertido; // Inverted index for course name words
    ArquivoCursoUsuario arqCursoUsuario;

    public ArquivoCurso() throws Exception {
        super("curso", Curso.class.getConstructor());
        indiceCodigo = new HashExtensivel<>(
                ParCodigoId.class.getConstructor(),
                4,
                "./dados/curso/indiceCodigo.d.db",
                "./dados/curso/indiceCodigo.c.db");
        indiceNome = new ArvoreBMais<>(
                ParNomeId.class.getConstructor(),
                4,
                "./dados/curso/indiceNome.db");
        // Inverted index for course name words (using words from nome)
        indiceNomeInvertido = new ListaInvertida(
                10, // quantidadeDadosPorBloco
                "./dados/curso/indiceNomeInvertido.d.db", // dicionario
                "./dados/curso/indiceNomeInvertido.c.db"); // blocos
        arqCursoUsuario = new ArquivoCursoUsuario();
    }

    /**
     * Tokeniza o nome do curso em palavras individuais
     * Remove pontuação, converte para lowercase e ignora palavras muito pequenas
     */
    private ArrayList<String> tokenizarNome(String nome) {
        ArrayList<String> palavras = new ArrayList<>();
        if (nome == null || nome.isEmpty()) {
            return palavras;
        }

        // Divide por espaços e pontuação básica
        StringTokenizer st = new StringTokenizer(nome, " .,;:!?()-");
        while (st.hasMoreTokens()) {
            String palavra = st.nextToken().trim().toLowerCase();
            // Ignora palavras muito pequenas (menos de 2 caracteres) ou vazias
            if (palavra.length() >= 2) {
                palavras.add(palavra);
            }
        }
        return palavras;
    }

    /**
     * Conta a frequência de cada palavra no nome do curso
     */
    private void indexarNomeCurso(String nome, int idCurso) throws Exception {
        ArrayList<String> palavras = tokenizarNome(nome);
        // Conta frequência de cada palavra
        java.util.Map<String, Integer> frequencias = new java.util.HashMap<>();
        for (String palavra : palavras) {
            frequencias.put(palavra, frequencias.getOrDefault(palavra, 0) + 1);
        }

        // Indexa cada palavra com sua frequência
        for (java.util.Map.Entry<String, Integer> entry : frequencias.entrySet()) {
            String palavra = entry.getKey();
            int frequencia = entry.getValue();
            indiceNomeInvertido.create(palavra, new ElementoLista(idCurso, frequencia));
        }
    }

    /**
     * Remove todas as ocorrências de um curso do índice invertido
     */
    private void removerNomeCursoDoIndice(String nome, int idCurso) throws Exception {
        ArrayList<String> palavras = tokenizarNome(nome);
        for (String palavra : palavras) {
            indiceNomeInvertido.delete(palavra, idCurso);
        }
    }

    @Override
    public int create(Curso curso) throws Exception {
        int id = super.create(curso);
        indiceCodigo.create(new ParCodigoId(curso.getCodigoCompartilhavel(), id));
        indiceNome.create(new ParNomeId(curso.getNome(), id));
        // Indexa o nome do curso no índice invertido
        indexarNomeCurso(curso.getNome(), id);
        return id;
    }

    public Curso readCodigo(String codigo) throws Exception {
        ParCodigoId pci = indiceCodigo.read(Math.abs(codigo.hashCode()));
        if (pci == null)
            return null;
        return read(pci.getId());
    }

    public Curso[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(new ParNomeId(nome, -1));
        if (pnis.isEmpty())
            return new Curso[0];

        Curso[] cursos = new Curso[pnis.size()];
        int i = 0;
        for (ParNomeId pni : pnis) {
            cursos[i++] = super.read(pni.getId());
        }
        return cursos;
    }

    /**
     * Busca cursos por palavras-chave no nome usando TF-IDF para ranking
     * @param consulta String contendo as palavras-chave para busca
     * @return Array de cursos ordenados por relevância TF-IDF (decrescente)
     * @throws Exception
     */
    public Curso[] buscarPorPalavrasChave(String consulta) throws Exception {
        if (consulta == null || consulta.trim().isEmpty()) {
            return new Curso[0];
        }

        // Tokeniza a consulta
        ArrayList<String> termosConsulta = tokenizarNome(consulta);
        if (termosConsulta.isEmpty()) {
            return new Curso[0];
        }

        // Coleta todos os cursos que contêm pelo menos um termo da consulta
        java.util.Map<Integer, Curso> cursosCandidatos = new java.util.HashMap<>();
        java.util.Map<Integer, Double> scoresTFIDF = new java.util.HashMap<>();

        // Primeiro, obtenha o total de cursos para cálculo do IDF
        int totalCursos = 0;
        try {
            totalCursos = this.readAll().length;
        } catch (Exception e) {
            totalCursos = 0;
        }

        if (totalCursos == 0) {
            return new Curso[0];
        }

        // Para cada termo na consulta, obtenha os cursos que o contêm
        for (String termo : termosConsulta) {
            ElementoLista[] elementos = indiceNomeInvertido.read(termo);
            if (elementos != null && elementos.length > 0) {
                // Calcula IDF para este termo
                int docsComTermo = elementos.length;
                double idf = Math.log((double) totalCursos / docsComTermo);

                // Para cada curso que contém este termo, acumule o score TF-IDF
                for (ElementoLista elemento : elementos) {
                    int idCurso = elemento.getId();
                    double tf = elemento.getFrequencia(); // Frequência do termo no curso
                    double tfidf = tf * idf;

                    // Acumula o score (soma dos TF-IDF de todos os termos da consulta presentes no curso)
                    double scoreAtual = scoresTFIDF.getOrDefault(idCurso, 0.0);
                    scoresTFIDF.put(idCurso, scoreAtual + tfidf);

                    // Guarda o curso candidato se ainda não estiver na lista
                    if (!cursosCandidatos.containsKey(idCurso)) {
                        Curso curso = super.read(idCurso);
                        if (curso != null) {
                            cursosCandidatos.put(idCurso, curso);
                        }
                    }
                }
            }
        }

        // Converte o mapa de cursos candidatos para array e ordena por score TF-IDF (decrescente)
        java.util.List<Curso> listaCursos = new ArrayList<>(cursosCandidatos.values());
        listaCursos.sort((c1, c2) -> {
            double score1 = scoresTFIDF.getOrDefault(c1.getID(), 0.0);
            double score2 = scoresTFIDF.getOrDefault(c2.getID(), 0.0);
            // Ordenação decrescente (maior score primeiro)
            return Double.compare(score2, score1);
        });

        // Converte para array e retorna
        Curso[] resultado = new Curso[listaCursos.size()];
        return listaCursos.toArray(resultado);
    }

    /**
     * Associa um usuário a um curso com um determinado papel.
     * @param idUsuario ID do usuário
     * @param idCurso ID do curso
     * @param papel Papel do usuário no curso (INSTRUTOR, PARTICIPANTE)
     * @return ID da associação criada
     * @throws Exception
     */
    public int associarUsuario(int idUsuario, int idCurso, String papel) throws Exception {
        // Verifica se a associação já existe
        if (arqCursoUsuario.exists(idUsuario, idCurso)) {
            throw new Exception("Usuário já está associado a este curso");
        }

        CursoUsuario assoc = new CursoUsuario(idUsuario, idCurso, papel);
        return arqCursoUsuario.create(assoc);
    }

    /**
     * Remove a associação entre um usuário e um curso.
     * @param idUsuario ID do usuário
     * @param idCurso ID do curso
     * @return true se a associação foi removida, false caso contrário
     * @throws Exception
     */
    public boolean desassociarUsuario(int idUsuario, int idCurso) throws Exception {
        // Primeiro encontramos a associação
        CursoUsuario[] assocs = arqCursoUsuario.readPorUsuario(idUsuario);
        for (CursoUsuario assoc : assocs) {
            if (assoc.getIdCurso() == idCurso) {
                return arqCursoUsuario.delete(assoc.getID());
            }
        }
        return false;
    }

    /**
     * Retorna todos os usuários associados a um curso específico.
     */
    public CursoUsuario[] getUsuariosDoCurso(int idCurso) throws Exception {
        return arqCursoUsuario.readPorCurso(idCurso);
    }

    /**
     * Retorna todos os cursos associados a um usuário específico.
     */
    public CursoUsuario[] getCursosDoUsuario(int idUsuario) throws Exception {
        return arqCursoUsuario.readPorUsuario(idUsuario);
    }

    /**
     * Verifica se um usuário tem um determinado papel em um curso.
     */
    public boolean usuarioTemPapel(int idUsuario, int idCurso, String papel) throws Exception {
        CursoUsuario[] assocs = arqCursoUsuario.readPorUsuario(idUsuario);
        for (CursoUsuario assoc : assocs) {
            if (assoc.getIdCurso() == idCurso && assoc.getPapel().equals(papel)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna o ID do instrutor de um curso (usuário com papel INSTRUTOR).
     */
    public Integer getInstrutorDoCurso(int idCurso) throws Exception {
        CursoUsuario[] assocs = arqCursoUsuario.readPorCurso(idCurso);
        for (CursoUsuario assoc : assocs) {
            if (assoc.getPapel().equals(CursoUsuario.INSTRUTOR)) {
                return assoc.getIdUsuario();
            }
        }
        return null; // Nenhum instrutor encontrado
    }

    public Curso[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if (pnis.isEmpty())
            return new Curso[0];

        Curso[] cursos = new Curso[pnis.size()];
        int i = 0;
        for (ParNomeId pni : pnis) {
            cursos[i++] = super.read(pni.getId());
        }
        return cursos;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Curso curso = read(id);
        if (curso != null) {
            // Primeiro remove todas as associações relacionadas a este curso
            CursoUsuario[] assocs = arqCursoUsuario.readPorCurso(curso.getID());
            for (CursoUsuario assoc : assocs) {
                arqCursoUsuario.delete(assoc.getID());
            }

            if (super.delete(id)) {
                indiceCodigo.delete(Math.abs(curso.getCodigoCompartilhavel().hashCode()));
                indiceNome.delete(new ParNomeId(curso.getNome(), curso.getID()));
                // Remove o nome do curso do índice invertido
                removerNomeCursoDoIndice(curso.getNome(), curso.getID());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Curso novoCurso) throws Exception {
        Curso curso = read(novoCurso.getID());
        if (curso == null)
            return false;

        boolean atualizado = super.update(novoCurso);
        if (!atualizado) {
            return false;
        }

        // Atualiza índices se o código ou nome mudou
        if (!curso.getCodigoCompartilhavel().equals(novoCurso.getCodigoCompartilhavel())) {
            indiceCodigo.delete(Math.abs(curso.getCodigoCompartilhavel().hashCode()));
            indiceCodigo.create(new ParCodigoId(novoCurso.getCodigoCompartilhavel(), novoCurso.getID()));
        }
        if (!curso.getNome().equals(novoCurso.getNome())) {
            indiceNome.delete(new ParNomeId(curso.getNome(), curso.getID()));
            indiceNome.create(new ParNomeId(novoCurso.getNome(), novoCurso.getID()));

            // Atualiza o índice invertido: remove o nome antigo, adiciona o novo
            removerNomeCursoDoIndice(curso.getNome(), curso.getID());
            indexarNomeCurso(novoCurso.getNome(), novoCurso.getID());
        }

        return true;
    }

    public void close() throws Exception {
        super.close();
        indiceCodigo.close();
        indiceNome.close();
        indiceNomeInvertido.close();
        arqCursoUsuario.close();
    }
}