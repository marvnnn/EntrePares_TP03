package arquivo;

import aed3.*;
import entidades.CursoUsuario;

import java.util.ArrayList;

public class ArquivoCursoUsuario extends Arquivo<CursoUsuario> {

    ArvoreBMais<ParUsuarioCursoId> indiceUsuario;
    ArvoreBMais<ParCursoUsuarioId> indiceCurso;

    public ArquivoCursoUsuario() throws Exception {
        super("cursoUsuario", CursoUsuario.class.getConstructor());
        indiceUsuario = new ArvoreBMais<>(
                ParUsuarioCursoId.class.getConstructor(),
                4,
                "./dados/cursoUsuario/indiceUsuario.db");
        indiceCurso = new ArvoreBMais<>(
                ParCursoUsuarioId.class.getConstructor(),
                4,
                "./dados/cursoUsuario/indiceCurso.db");
    }

    @Override
    public int create(CursoUsuario cursoUsuario) throws Exception {
        int id = super.create(cursoUsuario);
        indiceUsuario.create(new ParUsuarioCursoId(cursoUsuario.getIdUsuario(), cursoUsuario.getIdCurso()));
        indiceCurso.create(new ParCursoUsuarioId(cursoUsuario.getIdCurso(), cursoUsuario.getIdUsuario()));
        return id;
    }

    /**
     * Retorna todas as associações de um usuário específico, ordenadas por ID do curso.
     */
    public CursoUsuario[] readPorUsuario(int idUsuario) throws Exception {
        ArrayList<ParUsuarioCursoId> pucis = indiceUsuario.read(new ParUsuarioCursoId(idUsuario, -1));
        if (pucis.isEmpty())
            return new CursoUsuario[0];

        CursoUsuario[] assocs = new CursoUsuario[pucis.size()];
        int i = 0;
        for (ParUsuarioCursoId puc : pucis) {
            assocs[i++] = super.read(puc.getIdCurso());
        }
        return assocs;
    }

    /**
     * Retorna todos os usuários associados a um curso específico, ordenados por ID do usuário.
     */
    public CursoUsuario[] readPorCurso(int idCurso) throws Exception {
        ArrayList<ParCursoUsuarioId> pcuis = indiceCurso.read(new ParCursoUsuarioId(idCurso, -1));
        if (pcuis.isEmpty())
            return new CursoUsuario[0];

        CursoUsuario[] assocs = new CursoUsuario[pcuis.size()];
        int i = 0;
        for (ParCursoUsuarioId pcu : pcuis) {
            assocs[i++] = super.read(pcu.getIdUsuario());
        }
        return assocs;
    }

    /**
     * Verifica se existe uma associação específica entre usuário e curso.
     */
    public boolean exists(int idUsuario, int idCurso) throws Exception {
        CursoUsuario[] assocs = readPorUsuario(idUsuario);
        for (CursoUsuario assoc : assocs) {
            if (assoc.getIdCurso() == idCurso) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        CursoUsuario assoc = read(id);
        if (assoc != null) {
            if (super.delete(id)) {
                indiceUsuario.delete(new ParUsuarioCursoId(assoc.getIdUsuario(), assoc.getIdCurso()));
                indiceCurso.delete(new ParCursoUsuarioId(assoc.getIdCurso(), assoc.getIdUsuario()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(CursoUsuario novoAssoc) throws Exception {
        CursoUsuario assoc = read(novoAssoc.getID());
        if (assoc == null)
            return false;
        if (super.update(novoAssoc)) {
            // Atualiza índices se necessário
            if (assoc.getIdUsuario() != novoAssoc.getIdUsuario() || assoc.getIdCurso() != novoAssoc.getIdCurso()) {
                indiceUsuario.delete(new ParUsuarioCursoId(assoc.getIdUsuario(), assoc.getIdCurso()));
                indiceCurso.delete(new ParCursoUsuarioId(assoc.getIdCurso(), assoc.getIdUsuario()));
                indiceUsuario.create(new ParUsuarioCursoId(novoAssoc.getIdUsuario(), novoAssoc.getIdCurso()));
                indiceCurso.create(new ParCursoUsuarioId(novoAssoc.getIdCurso(), novoAssoc.getIdUsuario()));
            }
            return true;
        }
        return false;
    }

    public void close() throws Exception {
        super.close();
        indiceUsuario.close();
        indiceCurso.close();
    }
}