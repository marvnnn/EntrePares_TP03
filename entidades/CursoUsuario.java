package entidades;

import aed3.InterfaceEntidade;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;

public class CursoUsuario implements InterfaceEntidade {

    private int id;
    private int idCurso;
    private int idUsuario;
    private LocalDate dataInscricao;

    public CursoUsuario() {
        this(-1, -1, -1, LocalDate.now());
    }

    public CursoUsuario(int idCurso, int idUsuario, LocalDate dataInscricao) {
        this(-1, idCurso, idUsuario, dataInscricao);
    }

    public CursoUsuario(int id, int idCurso, int idUsuario, LocalDate dataInscricao) {
        this.id = id;
        this.idCurso = idCurso;
        this.idUsuario = idUsuario;
        this.dataInscricao = dataInscricao;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDate getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(LocalDate dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idCurso);
        dos.writeInt(idUsuario);
        dos.writeInt((int) dataInscricao.toEpochDay());
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idCurso = dis.readInt();
        idUsuario = dis.readInt();
        dataInscricao = LocalDate.ofEpochDay(dis.readInt());
    }
}
