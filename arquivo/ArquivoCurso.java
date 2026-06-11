package arquivo;

import aed3.*;
import entidades.Curso;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

public class ArquivoCurso extends Arquivo<Curso> {

    private HashExtensivel<ParCodigoId> indiceCodigo;
    private ArvoreBMais<ParNomeId> indiceNome;
    private ArvoreBMais<ParIdUsuarioId> indiceUsuario;
    private ListaInvertida indiceInvertido;

    private static final HashSet<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "o", "as", "os", "um", "uma", "uns", "umas",
        "de", "da", "do", "das", "dos", "d", "em", "no", "na", "nos", "nas",
        "por", "para", "com", "sem", "sob", "sobre", "entre", "ate", "apos",
        "e", "ou", "mas", "que", "se", "ao", "aos", "pela", "pelo", "pelas", "pelos",
        "mais", "menos", "muito", "muita", "muitos", "muitas"
    ));

    public ArquivoCurso() throws Exception {
        super("curso", Curso.class.getConstructor());
        indiceCodigo = new HashExtensivel<>(
            ParCodigoId.class.getConstructor(),
            4,
            "./dados/curso/indiceCodigo.d.db",
            "./dados/curso/indiceCodigo.c.db"
        );
        indiceNome = new ArvoreBMais<>(
            ParNomeId.class.getConstructor(),
            4,
            "./dados/curso/indiceNome.db"
        );
        indiceUsuario = new ArvoreBMais<>(
            ParIdUsuarioId.class.getConstructor(),
            4,
            "./dados/curso/indiceUsuario.db"
        );
        abrirIndiceInvertido();
        if (indiceInvertido.numeroEntidades() != readAll().length) {
            reconstruirIndiceInvertido();
        }
    }

    @Override
    public int create(Curso curso) throws Exception {
        int id = super.create(curso);
        indiceCodigo.create(new ParCodigoId(curso.getCodigoCompartilhavel(), id));
        indiceNome.create(new ParNomeId(curso.getNome(), id));
        indiceUsuario.create(new ParIdUsuarioId(curso.getIdUsuario(), id));
        inserirNoIndiceInvertido(curso);
        indiceInvertido.incrementaEntidades();
        return id;
    }

    public Curso readCodigo(String codigo) throws Exception {
        ParCodigoId pci = indiceCodigo.read(Math.abs(codigo.hashCode()));
        if (pci == null) {
            return null;
        }
        return read(pci.getId());
    }

    public Curso[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(new ParNomeId(nome, -1));
        if (pnis.isEmpty()) {
            return new Curso[0];
        }

        Curso[] cursos = new Curso[pnis.size()];
        int i = 0;
        for (ParNomeId pni : pnis) {
            cursos[i++] = super.read(pni.getId());
        }
        return cursos;
    }

    public Curso[] readPorPalavras(String busca) throws Exception {
        String[] termos = termosUnicos(busca);
        if (termos.length == 0) {
            return new Curso[0];
        }

        int totalEntidades = indiceInvertido.numeroEntidades();
        if (totalEntidades <= 0) {
            reconstruirIndiceInvertido();
            totalEntidades = indiceInvertido.numeroEntidades();
        }
        if (totalEntidades <= 0) {
            return new Curso[0];
        }

        HashMap<Integer, Float> pontuacoes = new HashMap<>();

        for (String termo : termos) {
            ElementoLista[] lista = indiceInvertido.read(termo);
            if (lista.length == 0) {
                continue;
            }

            float idf = (float) (Math.log10((double) totalEntidades / lista.length) + 1);
            for (ElementoLista elemento : lista) {
                float valor = elemento.getFrequencia() * idf;
                pontuacoes.put(elemento.getId(), pontuacoes.getOrDefault(elemento.getId(), 0f) + valor);
            }
        }

        ArrayList<Map.Entry<Integer, Float>> entradas = new ArrayList<>(pontuacoes.entrySet());
        entradas.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        ArrayList<Curso> cursos = new ArrayList<>();
        for (Map.Entry<Integer, Float> entrada : entradas) {
            Curso curso = super.read(entrada.getKey());
            if (curso != null) {
                cursos.add(curso);
            }
        }

        if (cursos.isEmpty()) {
            return readPorPalavrasSemIndice(busca);
        }

        return cursos.toArray(new Curso[0]);
    }

    public Curso[] readPorUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdUsuarioId> piuis = indiceUsuario.read(new ParIdUsuarioId(idUsuario, -1));
        if (piuis.isEmpty()) {
            return new Curso[0];
        }

        Curso[] cursos = new Curso[piuis.size()];
        int i = 0;
        for (ParIdUsuarioId piui : piuis) {
            cursos[i++] = super.read(piui.getIdCurso());
        }

        Arrays.sort(cursos, Comparator.comparing(c -> ParNomeId.transforma(c.getNome())));
        return cursos;
    }

    public Curso[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if (pnis.isEmpty()) {
            return new Curso[0];
        }

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
        if (curso != null && super.delete(id)) {
            removerDoIndiceInvertido(curso);
            indiceInvertido.decrementaEntidades();
            indiceCodigo.delete(Math.abs(curso.getCodigoCompartilhavel().hashCode()));
            indiceNome.delete(new ParNomeId(curso.getNome(), curso.getID()));
            indiceUsuario.delete(new ParIdUsuarioId(curso.getIdUsuario(), curso.getID()));
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Curso novoCurso) throws Exception {
        Curso antigo = read(novoCurso.getID());
        if (antigo == null) {
            return false;
        }

        if (super.update(novoCurso)) {
            if (!antigo.getCodigoCompartilhavel().equals(novoCurso.getCodigoCompartilhavel())) {
                indiceCodigo.delete(Math.abs(antigo.getCodigoCompartilhavel().hashCode()));
                indiceCodigo.create(new ParCodigoId(novoCurso.getCodigoCompartilhavel(), novoCurso.getID()));
            }

            if (!antigo.getNome().equals(novoCurso.getNome())) {
                indiceNome.delete(new ParNomeId(antigo.getNome(), antigo.getID()));
                indiceNome.create(new ParNomeId(novoCurso.getNome(), novoCurso.getID()));
                removerDoIndiceInvertido(antigo);
                inserirNoIndiceInvertido(novoCurso);
            }

            if (antigo.getIdUsuario() != novoCurso.getIdUsuario()) {
                indiceUsuario.delete(new ParIdUsuarioId(antigo.getIdUsuario(), antigo.getID()));
                indiceUsuario.create(new ParIdUsuarioId(novoCurso.getIdUsuario(), novoCurso.getID()));
            }

            return true;
        }
        return false;
    }

    private void abrirIndiceInvertido() throws Exception {
        indiceInvertido = new ListaInvertida(
            4,
            "./dados/curso/indiceInvertido.d.db",
            "./dados/curso/indiceInvertido.c.db"
        );
    }

    private void reconstruirIndiceInvertido() throws Exception {
        indiceInvertido.close();
        new File("./dados/curso/indiceInvertido.d.db").delete();
        new File("./dados/curso/indiceInvertido.c.db").delete();
        abrirIndiceInvertido();

        Curso[] cursos = readAll();
        for (Curso curso : cursos) {
            if (curso != null) {
                inserirNoIndiceInvertido(curso);
                indiceInvertido.incrementaEntidades();
            }
        }
    }

    private void inserirNoIndiceInvertido(Curso curso) throws Exception {
        HashMap<String, Float> frequencias = frequencias(curso.getNome());
        for (Map.Entry<String, Float> entrada : frequencias.entrySet()) {
            indiceInvertido.create(entrada.getKey(), new ElementoLista(curso.getID(), entrada.getValue()));
        }
    }

    private void removerDoIndiceInvertido(Curso curso) throws Exception {
        String[] termos = termosUnicos(curso.getNome());
        for (String termo : termos) {
            indiceInvertido.delete(termo, curso.getID());
        }
    }

    private HashMap<String, Float> frequencias(String texto) {
        String[] termos = termosValidos(texto);
        HashMap<String, Float> frequencias = new HashMap<>();

        if (termos.length == 0) {
            return frequencias;
        }

        for (String termo : termos) {
            frequencias.put(termo, frequencias.getOrDefault(termo, 0f) + 1f);
        }

        for (String termo : new ArrayList<>(frequencias.keySet())) {
            frequencias.put(termo, frequencias.get(termo) / termos.length);
        }

        return frequencias;
    }

    private String[] termosUnicos(String texto) {
        String[] termos = termosValidos(texto);
        ArrayList<String> unicos = new ArrayList<>();

        for (String termo : termos) {
            if (!unicos.contains(termo)) {
                unicos.add(termo);
            }
        }

        return unicos.toArray(new String[0]);
    }

    private String[] termosValidos(String texto) {
        String normalizado = normalizar(texto);
        String[] palavras = normalizado.split("[^a-z0-9]+");
        ArrayList<String> termos = new ArrayList<>();

        for (String palavra : palavras) {
            if (!palavra.isEmpty() && !STOP_WORDS.contains(palavra) && !palavra.matches("\\d+")) {
                termos.add(palavra);
            }
        }

        return termos.toArray(new String[0]);
    }

    private Curso[] readPorPalavrasSemIndice(String busca) throws Exception {
        String[] termosBusca = termosUnicos(busca);
        Curso[] todos = readAll();
        HashMap<Integer, Float> pontuacoes = new HashMap<>();
        HashMap<String, Integer> documentosPorTermo = new HashMap<>();
        HashMap<Integer, HashMap<String, Float>> frequenciasPorCurso = new HashMap<>();

        for (Curso curso : todos) {
            HashMap<String, Float> freq = frequencias(curso.getNome());
            frequenciasPorCurso.put(curso.getID(), freq);

            for (String termoBusca : termosBusca) {
                if (contemTermoCompatível(freq, termoBusca)) {
                    documentosPorTermo.put(termoBusca, documentosPorTermo.getOrDefault(termoBusca, 0) + 1);
                }
            }
        }

        for (Curso curso : todos) {
            HashMap<String, Float> freq = frequenciasPorCurso.get(curso.getID());
            float total = 0;

            for (String termoBusca : termosBusca) {
                Float tf = frequenciaCompatível(freq, termoBusca);
                if (tf != null) {
                    int docs = documentosPorTermo.getOrDefault(termoBusca, 1);
                    float idf = (float) (Math.log10((double) todos.length / docs) + 1);
                    total += tf * idf;
                }
            }

            if (total > 0) {
                pontuacoes.put(curso.getID(), total);
            }
        }

        ArrayList<Map.Entry<Integer, Float>> entradas = new ArrayList<>(pontuacoes.entrySet());
        entradas.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        ArrayList<Curso> cursos = new ArrayList<>();
        for (Map.Entry<Integer, Float> entrada : entradas) {
            Curso curso = super.read(entrada.getKey());
            if (curso != null) {
                cursos.add(curso);
            }
        }

        return cursos.toArray(new Curso[0]);
    }

    private boolean contemTermoCompatível(HashMap<String, Float> frequencias, String termoBusca) {
        return frequenciaCompatível(frequencias, termoBusca) != null;
    }

    private Float frequenciaCompatível(HashMap<String, Float> frequencias, String termoBusca) {
        Float valor = frequencias.get(termoBusca);
        if (valor != null) {
            return valor;
        }

        for (Map.Entry<String, Float> entrada : frequencias.entrySet()) {
            String termoCurso = entrada.getKey();
            if (termoBusca.contains(termoCurso) && termoCurso.length() >= 4) {
                return entrada.getValue();
            }
            if (termoCurso.contains(termoBusca) && termoBusca.length() >= 4) {
                return entrada.getValue();
            }
        }

        return null;
    }

    private String normalizar(String texto) {
        String nfd = Normalizer.normalize(texto, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfd).replaceAll("").toLowerCase();
    }

    @Override
    public void close() throws Exception {
        super.close();
        indiceCodigo.close();
        indiceNome.close();
        indiceUsuario.close();
        indiceInvertido.close();
    }
}
