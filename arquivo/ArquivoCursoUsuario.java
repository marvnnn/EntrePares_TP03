package arquivo;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ParIdId;
import entidades.CursoUsuario;

import java.util.ArrayList;

public class ArquivoCursoUsuario extends Arquivo<CursoUsuario> {

    private ArvoreBMais<ParIdId> indiceCurso;
    private ArvoreBMais<ParIdId> indiceUsuario;

    public ArquivoCursoUsuario() throws Exception {
        super("cursoUsuario", CursoUsuario.class.getConstructor());

        indiceCurso = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            4,
            "./dados/cursoUsuario/indiceCurso.db"
        );

        indiceUsuario = new ArvoreBMais<>(
            ParIdId.class.getConstructor(),
            4,
            "./dados/cursoUsuario/indiceUsuario.db"
        );
    }

    @Override
    public int create(CursoUsuario cursoUsuario) throws Exception {
        CursoUsuario existente = readPorUsuarioCurso(cursoUsuario.getIdUsuario(), cursoUsuario.getIdCurso());
        if (existente != null) {
            return existente.getID();
        }

        int id = super.create(cursoUsuario);
        indiceCurso.create(new ParIdId(cursoUsuario.getIdCurso(), id));
        indiceUsuario.create(new ParIdId(cursoUsuario.getIdUsuario(), id));
        return id;
    }

    public CursoUsuario[] readPorCurso(int idCurso) throws Exception {
        ArrayList<ParIdId> pares = indiceCurso.read(new ParIdId(idCurso, -1));
        CursoUsuario[] inscricoes = new CursoUsuario[pares.size()];

        int i = 0;
        for (ParIdId par : pares) {
            inscricoes[i++] = super.read(par.getId2());
        }

        return inscricoes;
    }

    public CursoUsuario[] readPorUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> pares = indiceUsuario.read(new ParIdId(idUsuario, -1));
        CursoUsuario[] inscricoes = new CursoUsuario[pares.size()];

        int i = 0;
        for (ParIdId par : pares) {
            inscricoes[i++] = super.read(par.getId2());
        }

        return inscricoes;
    }

    public CursoUsuario readPorUsuarioCurso(int idUsuario, int idCurso) throws Exception {
        CursoUsuario[] inscricoes = readPorUsuario(idUsuario);

        for (CursoUsuario inscricao : inscricoes) {
            if (inscricao != null && inscricao.getIdCurso() == idCurso) {
                return inscricao;
            }
        }

        return null;
    }

    @Override
    public boolean delete(int id) throws Exception {
        CursoUsuario inscricao = read(id);
        if (inscricao != null && super.delete(id)) {
            indiceCurso.delete(new ParIdId(inscricao.getIdCurso(), id));
            indiceUsuario.delete(new ParIdId(inscricao.getIdUsuario(), id));
            return true;
        }
        return false;
    }

    public void deletePorCurso(int idCurso) throws Exception {
        CursoUsuario[] inscricoes = readPorCurso(idCurso);
        for (CursoUsuario inscricao : inscricoes) {
            if (inscricao != null) {
                delete(inscricao.getID());
            }
        }
    }

    public void deletePorUsuario(int idUsuario) throws Exception {
        CursoUsuario[] inscricoes = readPorUsuario(idUsuario);
        for (CursoUsuario inscricao : inscricoes) {
            if (inscricao != null) {
                delete(inscricao.getID());
            }
        }
    }

    @Override
    public boolean update(CursoUsuario novo) throws Exception {
        CursoUsuario antigo = read(novo.getID());
        if (antigo == null) {
            return false;
        }

        if (super.update(novo)) {
            if (antigo.getIdCurso() != novo.getIdCurso()) {
                indiceCurso.delete(new ParIdId(antigo.getIdCurso(), antigo.getID()));
                indiceCurso.create(new ParIdId(novo.getIdCurso(), novo.getID()));
            }

            if (antigo.getIdUsuario() != novo.getIdUsuario()) {
                indiceUsuario.delete(new ParIdId(antigo.getIdUsuario(), antigo.getID()));
                indiceUsuario.create(new ParIdId(novo.getIdUsuario(), novo.getID()));
            }

            return true;
        }

        return false;
    }

    @Override
    public void close() throws Exception {
        super.close();
        indiceCurso.close();
        indiceUsuario.close();
    }
}
