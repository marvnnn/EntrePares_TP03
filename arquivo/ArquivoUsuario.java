
package arquivo;

import aed3.*;
import entidades.Usuario;

import java.util.ArrayList;

public class ArquivoUsuario extends Arquivo<Usuario> {

    private HashExtensivel<ParEmailId> indiceEmail;
    private ArvoreBMais<ParNomeId> indiceNome;

    public ArquivoUsuario() throws Exception {
        super("usuario", Usuario.class.getConstructor());

        indiceEmail = new HashExtensivel<>(
            ParEmailId.class.getConstructor(),
            4,
            "./dados/usuario/indiceEmail.d.db",
            "./dados/usuario/indiceEmail.c.db"
        );

        indiceNome = new ArvoreBMais<>(
            ParNomeId.class.getConstructor(),
            4,
            "./dados/usuario/indiceNome.db"
        );
    }

    @Override
    public int create(Usuario usuario) throws Exception {
        int id = super.create(usuario);
        indiceEmail.create(new ParEmailId(usuario.getEmail(), id));
        indiceNome.create(new ParNomeId(usuario.getNome(), id));
        return id;
    }

    public Usuario readEmail(String email) throws Exception {
        ParEmailId pei = indiceEmail.read(Math.abs(email.hashCode()));
        if (pei == null) return null;
        return super.read(pei.getId());
    }

    public Usuario[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> lista = indiceNome.read(new ParNomeId(nome, -1));
        Usuario[] usuarios = new Usuario[lista.size()];
        int i = 0;
        for (ParNomeId p : lista) {
            usuarios[i++] = super.read(p.getId());
        }
        return usuarios;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario u = read(id);
        if (u != null && super.delete(id)) {
            indiceEmail.delete(Math.abs(u.getEmail().hashCode()));
            indiceNome.delete(new ParNomeId(u.getNome(), u.getID()));
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Usuario novo) throws Exception {
        Usuario antigo = read(novo.getID());
        if (antigo == null) return false;

        if (super.update(novo)) {
            if (!antigo.getEmail().equals(novo.getEmail())) {
                indiceEmail.delete(Math.abs(antigo.getEmail().hashCode()));
                indiceEmail.create(new ParEmailId(novo.getEmail(), novo.getID()));
            }
            if (!antigo.getNome().equals(novo.getNome())) {
                indiceNome.delete(new ParNomeId(antigo.getNome(), antigo.getID()));
                indiceNome.create(new ParNomeId(novo.getNome(), novo.getID()));
            }
            return true;
        }
        return false;
    }
}
