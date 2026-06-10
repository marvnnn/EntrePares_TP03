package arquivo;

import aed3.*;
import entidades.Usuario;

import java.util.ArrayList;

public class ArquivoUsuario extends Arquivo<Usuario> {

    HashExtensivel<ParEmailId> indiceEmail;
    ArvoreBMais<ParNomeId> indiceNome;

    public ArquivoUsuario() throws Exception {
        super("usuario", Usuario.class.getConstructor());
        indiceEmail = new HashExtensivel<>(
                ParEmailId.class.getConstructor(),
                4,
                "./dados/usuario/indiceEmail.d.db",
                "./dados/usuario/indiceEmail.c.db");
        indiceNome = new ArvoreBMais<>(
                ParNomeId.class.getConstructor(),
                4,
                "./dados/usuario/indiceNome.db");
    }

    @Override
    public int create(Usuario p) throws Exception {
        int id = super.create(p);
        indiceEmail.create(new ParEmailId(p.getEmail(), id));
        indiceNome.create(new ParNomeId(p.getNome(), id));
        return id;
    }

    public Usuario readEmail(String email) throws Exception {
        ParEmailId pei = indiceEmail.read(Math.abs(email.hashCode()));
        if (pei == null)
            return null;
        return read(pei.getId());
    }

    public Usuario[] readNome(String nome) throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(new ParNomeId(nome, -1));
        if (pnis.isEmpty())
            return new Usuario[0];

        Usuario[] usuarios = new Usuario[pnis.size()];
        int i = 0;
        for (ParNomeId pni : pnis) {
            usuarios[i++] = super.read(pni.getId());
        }
        return usuarios;
    }

    public Usuario[] readAll() throws Exception {
        ArrayList<ParNomeId> pnis = indiceNome.read(null);
        if (pnis.isEmpty())
            return new Usuario[0];

        Usuario[] usuarios = new Usuario[pnis.size()];
        int i = 0;
        for (ParNomeId pni : pnis) {
            usuarios[i++] = super.read(pni.getId());
        }
        return usuarios;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Usuario p = read(id);
        if (p != null) {
            if (super.delete(id)) {
                indiceEmail.delete(Math.abs(p.getEmail().hashCode()));
                indiceNome.delete(new ParNomeId(p.getNome(), p.getID()));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Usuario novoUsuario) throws Exception {
        Usuario p = read(novoUsuario.getID());
        if (p == null)
            return false;

        if (super.update(novoUsuario)) {
            if (!p.getEmail().equals(novoUsuario.getEmail())) {
                indiceEmail.delete(Math.abs(p.getEmail().hashCode()));
                indiceEmail.create(new ParEmailId(novoUsuario.getEmail(), novoUsuario.getID()));
            }
            if (!p.getNome().equals(novoUsuario.getNome())) {
                indiceNome.delete(new ParNomeId(p.getNome(), p.getID()));
                indiceNome.create(new ParNomeId(novoUsuario.getNome(), novoUsuario.getID()));
            }
            return true;
        }
        return false;
    }

    public void close() throws Exception {
        super.close();
        indiceEmail.close();
        indiceNome.close();
    }
}