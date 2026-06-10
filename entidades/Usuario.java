package entidades;

import aed3.InterfaceEntidade;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Usuario implements InterfaceEntidade {

    private int id;
    private String nome;
    private String email;
    private int hashSenha;
    private String perguntaSecreta;
    private int hashRespostaSecreta;

    public Usuario() {
        this(-1, "", "", -1, "", -1);
    }

    public Usuario(String n, String e, int h, String p, int hR) {
        this(-1, n, e, h, p, hR);
    }

    public Usuario(int i, String n, String e, int h, String p, int hR) {
        id = i;
        nome = n;
        email = e;
        hashSenha = h;
        perguntaSecreta = p;
        hashRespostaSecreta = hR;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHashSenha() {
        return hashSenha;
    }

    public void setSenha(String senha) {
        hashSenha = senha.hashCode();
    }


    public String getPerguntaSecreta() {
        return perguntaSecreta;
    }

    public void setPerguntaSecreta(String pergunta) {
        perguntaSecreta = pergunta;
    }

    public int getHashRespostaSecreta() {
        return hashRespostaSecreta;
    }

    public void setRespostaSecreta(String resposta) {
        hashRespostaSecreta = resposta.hashCode();
    }

    public void setPergunta(String p) {
        perguntaSecreta = p;
    }

    public String getPergunta() {
        return perguntaSecreta;
    }

    public void setResposta(String r) {
        this.hashRespostaSecreta = r.hashCode();
    }



    @Override
    public String toString() {
        return "ID......: " + id +
               "\nNome....: " + nome +
               "\nEmail...: " + email +
               "\nPergunta Secreta: " + perguntaSecreta;
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeUTF(nome);
        dos.writeUTF(email);
        dos.writeInt(hashSenha);
        dos.writeUTF(perguntaSecreta);
        dos.writeInt(hashRespostaSecreta);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        nome = dis.readUTF();
        email = dis.readUTF();
        hashSenha = dis.readInt();
        perguntaSecreta = dis.readUTF();
        hashRespostaSecreta = dis.readInt();
    }
}
